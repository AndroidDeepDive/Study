# KSP란 무엇인가
Kotlin으로 이루어진 프로젝트에 JAVA Annotation Processor API 가 구성되어있는 경우, Java Compile가 Kotlin코드를 인지할 수 없어 우리는 `kapt` 라는 compile plugin을 이용해 Annotation Processor 를 사용해왔다.

Kapt는 수정되지 않은 Annotation Processor를 실행하기 위해 Kotlin으로 짜여진 코드의 모든 곳을 확인 후, Kotlin 코드를 Java Stubs으로 변환하도록 도와준다. 이 때 Stub 생성은 대략 파일을 분석하고 생성하는 모든 과정 중 `1/3`을 차지하게 된다.

전체 컴파일타임 중 1/3은 꽤나 많은 비율이며, Annotation Processor를 많이 사용할수록 시간이 길어질 수 있음을 짐작할 수 있다. 이러한 문제를 해결하기 위해 Kotlin 전용 Annotation Processor 인 `KSP(Kotlin Symbol Processor)`가 등장하게 되었다. 

> 실제로 Glide의 간이버전을 KSP를 이용해 구현했는데, 총 Kotlin 컴파일 시간은 21.55초이지만, KAPT에서 코드를 생성하는 데 8.67초가 걸렸으며, KSP 구현에서 코드를 생성하는 데 1.15초가 걸렸다고 한다. KSP에서 가장 강점으로 내세우는 점 중 하나가, KAPT에 비해 2배이상 빠르다는 것이다. 
> <br>
> For performance evaluation, we implemented a [simplified version](https://github.com/google/ksp/releases/download/1.4.10-dev-experimental-20200924/miniGlide.zip) of [Glide](https://github.com/bumptech/glide) in KSP to make it generate code for the [Tachiyomi](Tachiyomi) project. While the total Kotlin compilation time of the project is 21.55 seconds on our test device, it took 8.67 seconds for KAPT to generate the code, and it took 1.15 seconds for our KSP implementation to generate the code.

📌 KSP는 Kotlin언어와 기존의 Annotation Processing을 알고 있다면, 진입장벽이 높지 않을 것이다.

# How KSP Looks At Source Files

[KSP](https://github.com/google/ksp) Overview를 살펴보면, KSP 관점에서 파일이 어떻게 보이는지 볼 수 있다.


``` kotlin
// 코틀린 소스파일
KSFile
  packageName: KSName
  fileName: String
  annotations: List<KSAnnotation>  (File annotations)
  declarations: List<KSDeclaration>
  
  // 클래스, 인터페이스, 오브젝트를 볼 수 있다.
    KSClassDeclaration
      simpleName: KSName
      qualifiedName: KSName
      containingFile: String
      typeParameters: KSTypeParameter
      parentDeclaration: KSDeclaration
      classKind: ClassKind
      primaryConstructor: KSFunctionDeclaration
      superTypes: List<KSTypeReference>
      // KSClassDeclaration 내부에 있는 클래스, 멤버함수, 프로퍼티등을 볼 수 있다.
      declarations: List<KSDeclaration>
      
      // 상위레벨의 함수를 볼 수 있다. 
    KSFunctionDeclaration
      simpleName: KSName
      qualifiedName: KSName
      containingFile: String
      typeParameters: KSTypeParameter
      parentDeclaration: KSDeclaration
      functionKind: FunctionKind
      extensionReceiver: KSTypeReference?
      returnType: KSTypeReference
      parameters: List<KSValueParameter>
      // KSFunctionDeclaration 내부에 있는 클래스, 멤버함수, 프로퍼티등을 볼 수 있다.
      declarations: List<KSDeclaration>
      
      // 글로벌 변수
    KSPropertyDeclaration 
      simpleName: KSName
      qualifiedName: KSName
      containingFile: String
      typeParameters: KSTypeParameter
      parentDeclaration: KSDeclaration
      extensionReceiver: KSTypeReference?
      type: KSTypeReference
      getter: KSPropertyGetter
      returnType: KSTypeReference
      setter: KSPropertySetter
      parameter: KSValueParameter

```

Annotation이 붙여진 `어떠한 것`이 변수인지, 함수인지, 클래스인지를 알고 싶다면, 위 구성을 이해하면 된다. 아래와 같이 Activity를 구성했다고 가정해보자.

``` kotlin
// KSFile
package com.jshme.kspsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

@TestAnnotation //KClassDeclaration 
class SampleActivity : AppCompatActivity() {

    @Test //KSPropertyDeclaration
    var number: Int = 0

    @Test //KSPropertyDeclaration
    var str: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        
        number = 2
        str = "Test Sample"
    }
}
```

`@TestAnnotation` 이 달린 곳은 클래스 타입이므로 KClassDeclaration으로 구성될 것이고, `@Test` 가 달린 곳은 KSPropertyDeclaration으로 구성될 것이다. 파일 내부를 돌면서 클래스, 함수, 변수타입을 구별해내 `KSDeclaration` 으로 정의하는 것이다. 

> KSClassDeclaration, KSFunctionDeclaration, KSPropertyDeclaration은 KSDeclaration을 상속받고 있기 때문에 casting해서 사용할 수 있어, list(declarations) 형태로 관리할 수 있다.
> 관련된 내용은 [KSP Model](https://github.com/google/ksp/blob/main/docs/ksp-additional-details.md)에 들어가면 자세한 구조를 볼 수 있다.


# KSP로 간단한 Annotation 만들어보기
KSP를 이용해 WebView Setting과 Load할 수 있는`WebViewBuilder`를 만들어보자.

### 1. `WebViewBuilder.kt` 생성
우선 WebViewBuilder Annotation을 생성한다. 
이 어노테이션은 프로퍼티에만 붙여질 것이기 때문에 `AnnotationTarget.PROPERTY`로 타겟을 설정한다.
``` kotlin
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY)
annotation class WebViewBuilder(
    //webview load 할 url
    val url: String, 
    
    //webview auto setting 여부, 
    // true 시 기본적인 webviewSetting을 자동화 해주는 것이 목표이다.
    val autoSet: Boolean 
)
```

<br>

### 2. `WebViewBuilderProcessorProvider` 생성
`WebViewBuilderProcessorProvider` 를 생성한다. SymbolProcessor 인스턴스화를 제공해준다.

``` kotlin
class WebViewBuilderProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return WebViewBuilderProcessor(environment.codeGenerator, environment.logger)
    }
}
```

<br>

### 3. SymbolProcessor를 상속한 `WebViewBuilderProcessor` 구성
SymbolProcessor 는 Java Annotation Processing API에서 AbstractProcessor 와 동일한 역할을 한다.

``` kotlin
class WebViewBuilderProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {

    // 정의된 Annotation Property 값을 담는 역할.
    private var propertyUrl: MutableMap<KSPropertyDeclaration, String> = mutableMapOf()
    private var propertyBridgeClass: MutableMap<KSPropertyDeclaration, String> = mutableMapOf()
    private var propertyAutoSet: MutableMap<KSPropertyDeclaration, Boolean> = mutableMapOf()
    private var parentClassDeclaration: KSDeclaration? = null

    private lateinit var packageName: String
    private lateinit var fileName: String
    
    override fun process(resolver: Resolver): List<KSAnnotated> {
    	// WebViewBuilder Annotation이 달린 모든 Symbol들을 가져온다.
        val symbols = resolver.getSymbolsWithAnnotation(WebViewBuilder::class.java.canonicalName)

        val ret = symbols.filterNot { it.validate() }.toList()

	// Symbol 중 WebViewBuilder Annotation이 달린 프로퍼티에 대해 PropertyVisitor() 를 실행한다.
        symbols
            .filter { it.validate() }
            .forEach {
                it.accept(PropertyVisitor(), Unit)
            }

        return ret
    }

    override fun finish() {
    	// 파일을 생성하기 위해 codeGenerator 를 사용한다.
        // singleton으로 생성하기 위해 finish() 때 파일을 생성함이 목적이다.
        val outputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(true),
            packageName = packageName,
            fileName = fileName
        )

        outputStream.appendText(
            """
                |package $packageName
                |
                |import android.webkit.WebView
                |import android.util.Log
                |import android.content.Context
                |
                |object $fileName {
                |    var context: Context? = null
                |
                |    fun init() {
            """.trimMargin()
        )

        propertyUrl.map { variable ->
            outputStream.appendText("\t\t(context as? ${parentClassDeclaration?.simpleName?.asString()})?.${variable.key}?.loadUrl(\"${variable.value}\")")
        }

	// authset이 true이면 기본적인 webview setting을 자동으로 추가한다.
        propertyAutoSet
            .filter { property -> property.value }
            .map { variable ->
                outputStream.appendText(
                    """
                    |        (context as? ${parentClassDeclaration?.simpleName?.asString()})?.${variable.key}?.settings?.apply {
                    |            javaScriptEnabled = true
                    |            domStorageEnabled = true
                    |            useWideViewPort = true
                    |            loadWithOverviewMode = true
                    |            setSupportMultipleWindows(true)
                    |        }
                """.trimMargin()
                )
            }

        outputStream.appendText(
            """
            |    }
            |}
            """.trimMargin()
        )

        outputStream.close()
        logger.warn("finish")
    }

```
<br>


### 4. `PropertyVisitor()` 생성
`PropertyVisitor()`는 방문한 프로퍼티에 대해 내가 원하는 값을 구해 Generated class 를 만들 수 있다.

``` kotlin
  class PropertyVisitor : KSVisitorVoid() {

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            logger.warn("visite Property Declearation : ${property} and ${property.parentDeclaration}")
            
            parentClassDeclaration = property.parentDeclaration
            
            //WebViewBuilder Annotation의 argument 값을 Map으로 저장한다. 
            property.annotations.firstOrNull { annotation ->
                annotation.shortName.asString() == "WebViewBuilder"
            }?.arguments?.map { argument ->
                if (argument.name?.asString() == "url") 
                	propertyUrl[property] = argument.value.toString()
                    
                if (argument.name?.asString() == "autoSet") 
                	propertyAutoSet[property] = argument.value as Boolean
            }
        }
    }

    fun OutputStream.appendText(str: String) {
        this.write("$str\n".toByteArray())
    }
    
```

<br>

### 5. META-INF.service 생성
`WebViewBuilderProcessorProvider` 이 구성된 프로젝트 내 resources > META-INF > services 디렉토리를 생성하고, `com.google.devtools.ksp.processing.SymbolProcessorProvider` 이름의 파일을 만든 후, `WebViewBuilderProcessorProvider` 의 패키지 경로를 적는다.
![](https://images.velog.io/images/jshme/post/e31c18b8-a6ff-4fb2-8319-7cd0e65b0a26/image.png)

> 📌 파일 생성시 resources 내부에 새로운 디렉토리를 만들기 위해 아래 사진처럼 `META-INF.service` 로 만드는 경우가 있을 것이다. 이렇게 만드는 경우 종종 디렉토리를 META-INF 하위의 services 디렉토리를 만드는 것이 아닌 META-INF.service 라는 하나의 디렉토리로 만드는 경우가 있으니 조심하자. 하나의 디렉토리로 만들어지는 경우, Annotation Processor가 작동하지 않을 것이다.
> ![](https://images.velog.io/images/jshme/post/41014c17-0a9b-4317-95b8-6def245187b4/image.png)

<br>

### 6. MainActivity에 WebView를 구성
MainActivity에 WebView를 배치하고 load할 url 과 autoset을 설정 후, Build를 시도하면 동일한 패키지 명 안에 Generated Class가 생성될 것이다.

``` kotlin
class MainActivity : AppCompatActivity() {

    @WebViewBuilder(
        url = "https://www.google.com/",
        autoSet = true
    )
    lateinit var webView: WebView

    @WebViewBuilder(
        url = "https://www.naver.com/",
        autoSet = false
    )
    lateinit var webView2: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
```

![](https://images.velog.io/images/jshme/post/161653bb-1ef2-4f86-a785-d8f8d03a11f2/image.png)

### 7. `WebViewLoader` 구성 
클래스가 자동으로 완성되었어도 현재 실행될 수 없다. webView를 실행시키기 위한 context가 null이기 때문이다. 
context를 설정하기 위해 Loader를 구성해야 하므로 WebViewLoader 는 아래와 같이 구성한다.

``` kotlin
object WebViewLoader {
    fun onInitialize(activity: Activity) {

        val parserName = "WebViewBuilder"
        val kClass = Class.forName("${activity.packageName}.$parserName").kotlin
        
        //자동으로 생성된 WebViewBuilder 를 싱글톤으로 생성하고, 생성하지 못하는 상태라면 newInstance()로 생성한다.
        val instance = kClass.objectInstance ?: kClass.java.newInstance()

	//WebViewBuilder 내에 멤버변수를 조회한다.
       	//멤버변수가 mutable하고, 변수이름이 context라면,
        //WebViewBuilder를 생성하고, context에 activity를 set한다.
        kClass.memberProperties
            .filterIsInstance<KMutableProperty<*>>()
            .firstOrNull { kProperty -> kProperty.name == "context" }
            .run { this?.setter?.call(instance, activity) }

	//WebViewBuilder 내에 첫번째 멤버함수를 조회한다.
    	//멤버함수의 이름이 init이라면,
        //함수를 호출한다.
        kClass.memberFunctions
            .firstOrNull { kFunction -> kFunction.name == "init" }
            .run { this?.call(instance) }
    }
}
```
<br>

마지막으로 웹뷰를 선언한 후, WebViewLoader를 Initialize하면 WebView가 정상적으로 작동할 것이다. 
``` kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById<WebView>(R.id.webView)
        webView2 = findViewById<WebView>(R.id.webView2)

        WebViewLoader.onInitialize(this)
    }
```

<br>

소스코드는 [Github](https://github.com/jsh-me/KSPWebViewLoader)에서 확인할 수 있다. 
# Part 2 - Kotlin Symbol Processing API2

## Let's inspect in KSP Structure ๐

KSP๋ ์ ๋ฒ์ ์ค๋ชํ๋ค์ํผ KAPT์ ์ ์ฌํ ๊ตฌ์กฐ๋ฅผ ๊ฐ์ง๊ณ  ์์ง๋ง, ํจ์ฌ ๋ ์ฌ์ฉํ๊ธฐ ์ฝ๋ค. ์ฐ๋ฆฌ๊ฐ ๊ธฐ์กด์ KAPT ๊ตฌ์กฐ๋ฅผ ํ์ํ๊ณ  ์๋ค๋ฉด, ์ข ๋ ๋นจ๋ฆฌ ์ ์ํ  ์ ์์๊ฒ์ด๋ค. KSP์ Class Diagram์ ์๋ ์ด๋ฏธ์ง๋ฅผ ์ฐธ๊ณ ํ๋ฉด ์ข๋ค.

![KSP Class Diagram](https://imgur.com/L6VwN9Q.jpg)



### How KSP looks at source files

Source File ๊ตฌ์กฐ๋ ์๋ ํํ๋ก ์ด๋ฃจ์ด์ ธ์๋๋ฐ, KSP์ API๋ฅผ ์ ์ฉํ๊ฒ ์ฌ์ฉํ๊ธฐ ์ํด์๋ ์๋ ๊ตฌ์กฐ๋ฅผ ์ ์ฐธ๊ณ  ํ  ํ์๊ฐ ์๋ค.

``` kotlin
 // ์ฝํ๋ฆฐ ์์คํ์ผ
 KSFile
   packageName: KSName
   fileName: String
   annotations: List<KSAnnotation>  (File annotations)
   declarations: List<KSDeclaration>
   
   // ํด๋์ค, ์ธํฐํ์ด์ค, ์ค๋ธ์ ํธ๋ฅผ ๋ณผ ์ ์๋ค.
     KSClassDeclaration
       simpleName: KSName
       qualifiedName: KSName
       containingFile: String
       typeParameters: KSTypeParameter
       parentDeclaration: KSDeclaration
       classKind: ClassKind
       primaryConstructor: KSFunctionDeclaration
       superTypes: List<KSTypeReference>
       // KSClassDeclaration ๋ด๋ถ์ ์๋ ํด๋์ค, ๋ฉค๋ฒํจ์, ํ๋กํผํฐ๋ฑ์ ๋ณผ ์ ์๋ค.
       declarations: List<KSDeclaration>
       
       // ์์๋ ๋ฒจ์ ํจ์๋ฅผ ๋ณผ ์ ์๋ค. 
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
       // KSFunctionDeclaration ๋ด๋ถ์ ์๋ ํด๋์ค, ๋ฉค๋ฒํจ์, ํ๋กํผํฐ๋ฑ์ ๋ณผ ์ ์๋ค.
       declarations: List<KSDeclaration>
       
       // ๊ธ๋ก๋ฒ ๋ณ์
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



## Let's Implement Proejct by using KSP ๐

์ฐ๋ฆฌ๊ฐ KSP๋ฅผ ์ด์ฉํ ํ๋ก์ ํธ๋ฅผ ๊ตฌํํ๊ธฐ ์ํด, ๊ฐ์ฅ ์ข์ ๋ฐฉ๋ฒ์ [google/ksp์ QucikStart ๊ฐ์ด๋](https://github.com/google/ksp/blob/main/docs/quickstart.md)๋ฅผ ์ฐธ๊ณ ํ๋ ๊ฒ์ด๋ค.



์ผ๋จ ์ฐ๋ฆฌ๊ฐ KSP๋ฅผ ๊ตฌํํ๊ธฐ ์ํ ํ๊ฒฝ์ ๊ธฐ๋ณธ์ ์ผ๋ก Kotlin DSL์ ๊ธฐ๋ฐ์ผ๋กํ Gradle ํ๋ก์ ํธ ์ธํ์ ํด์ฃผ๋ ๊ฒ์ด๋ค. ์ด๋ฅผ ์ํด ์ฐธ๊ณ ํ๊ธฐ ์ข์ ๊ธ์ ๋งํฌ๋ก ๋จ๊ธด๋ค.

[Gradle ์คํฌ๋ฆฝํธ ์ธ์ด๋ฅผ Groovy DSL์์ Kotlin DSL๋ก ๋ณ๊ฒฝ ํ๊ธฐ](https://www.charlezz.com/?p=45140)



๊ฒฐ๊ณผ์ ์ผ๋ก ์ฐ๋ฆฌ๊ฐ ํ๋ก์ ํธ๋ฅผ ๊ตฌํํ์ ๋ ๋ณด์ฌ์ง๋ ๊ตฌ์กฐ๋ ๋ค์๊ณผ ๊ฐ๋ค.

![Android Proejct Structure](https://imgur.com/vwNLaEe.jpg)



์๋๋ก์ด๋ ํ๋ก์ ํธ์๋ ์ฐ๋ฆฌ๊ฐ KSP๋ก Generated๋ ํ์ผ์ ์ฌ์ฉ ํ  `app` ๋ชจ๋, Kotlin Gradle ๋น๋ ํ๋ก์ ํธ๋ฅผ ๊ด๋ฆฌ ํ  `buildSrc` ๋ชจ๋, `ksp-example`๋ก ๊ตฌ์ฑ์ด ๋์ด ์๋ค. ํด๋น ๋ชจ๋์ ๋ํ ์์กด์ฑ ๊ตฌ์กฐ๋ฅผ ์ ๋ฆฌํ๋ฉด `app` <- `ksp-example` ์ด ๋๋ค. ์ฐ๋ฆฌ๊ฐ ์ฌ๊ธฐ์ ์ง์ค์ ์ผ๋ก ๋ณด์์ผ ํ๋๊ฒ์ `ksp-example`  ๋ชจ๋์ด๋ค.



`settings.gradle.kts` ํ์ผ์๋ ์๋์ ๊ฐ์ด ํ๋ก์ ํธ์ ๋ชจ๋์ด ์ถ๊ฐ๋์ด ์๋ ์ํฉ์ด์ด์ผ ํ๋ค.

```kotlin
rootProject.name = "KSPImplementationExample"
include(":app")
include(":ksp-example")
```



### Implement KSP Module

KSP๋ฅผ ๊ตฌํํ๊ธฐ ์ํ ๋ชจ๋์ ์ถ๊ฐํ์๊ณ , ์์กด์ฑ ๊ด๋ฆฌ๋ฅผ ์ํด `buildSrc` ๋ชจ๋์ Dependency๋ฅผ ๊ด๋ฆฌํ๊ธฐ ์ํ ํด๋์ค๋ฅผ ์ถ๊ฐํ๋ค. ํด๋น ํด๋์ค๋ `app` ๋ชจ๋, `ksp-example` ๋ชจ๋์์ ์์กด์ฑ์ ๊ณต๋์ผ๋ก ๊ด๋ฆฌํ๊ธฐ ์ํด ์ถ๊ฐํ๋ค. (๊ฐ๋ฐ์๊ฐ ์ํ๋ ๋ฐฉ์๋๋ก ํด๋ ์ ํ ๋ฌธ์ ๋ ์๋ค.)

```kotlin
...

object Dependencies {

  ...

  object Kotlin {
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    const val poet = "com.squareup:kotlinpoet:1.7.2" // Kotlin API, Java API๋ฅผ ์ด์ฉํด ktํ์ผ์ ์์ฑํ๊ธฐ ์ํ ๋ผ์ด๋ธ๋ฌ๋ฆฌ, kapt, ksp ๋๋ค ์ฌ์ฉ์ด ๊ฐ๋ฅํ๋ค.
  }

  ...

  object Ksp {
    const val symbolProcessingApi = "com.google.devtools.ksp:symbol-processing-api:$kspVersion"
  }

  ...

}
```



`ksp-example` ๋ชจ๋์์๋ KSP Processor๋ฅผ ๊ตฌํํ๊ธฐ ์ํด ํ์ํ ๋ผ์ด๋ธ๋ฌ๋ฆฌ๋ฅผ ์ถ๊ฐํ  ๊ฒ์ด๋ค. ์๋๋ `build.gradle.kts` ์ค์  ํ์ผ์ด๋ค.

```kotlin
...

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(Dependencies.Kotlin.stdlib)
    implementation(Dependencies.Ksp.symbolProcessingApi)

    implementation(Dependencies.Kotlin.poet)
}
```



์ฌ๊ธฐ์ ์ฐ๋ฆฌ๊ฐ ๋ณด์ง๋ชปํ ๋ผ์ด๋ธ๋ฌ๋ฆฌ๊ฐ ํ๋ ๋ณด์ด๋๋ฐ, Square์ฌ์์ ๋ง๋  ๋ผ์ด๋ธ๋ฌ๋ฆฌ์ธ [KotlinPoet](https://square.github.io/kotlinpoet/)์ด๋ค. ํด๋น ๋ผ์ด๋ธ๋ฌ๋ฆฌ์ ๋ํ ๋ด์ฉ์ ํ์ธํ๋ ค๋ฉด ๋งํฌ๋ฅผ ํ์ธํ์.

์ฐ๋ฆฌ๋ Generated๋๋ ํ์ผ์์ ์ข ๋ ์ฌ์ด ๋ฐฉ๋ฒ์ผ๋ก ์ฝ๋ ์์ฑ์ ํ๊ธฐ ์ํด ํ๋์ฝ๋ฉ์ ์ต๋ํ ์ค์ด๊ณ , KotlinPoet์ ์ด์ฉํ์ฌ ํ์ผ์ ์์ฑ ํ  ๊ฒ์ด๋ค.



### Expected InterfaceImplementation Business Logic

```kotlin
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class InterfaceImplementation
```

๋จผ์  ์ด๋ธํ์ด์ ํด๋์ค๋ฅผ ์์ฑํ๋ค. ํด๋น ์ด๋ธํ์ด์์ ํด๋์ค์๋ง ๋ถ์ฌ์ง๋ค๋ ๊ฒ์ ์์ํ๋ค.

์์ ๋ฅผ ๋ง๋ค ๋ ๋๋ `InterfaceImplementation` ์ด๋ฆ์ผ๋ก ํด๋์ค๋ฅผ ๊ตฌํํ๋๋ฐ, ๋ก์ง์ ๊ตฌํํ๋ฉด, ๋ก์ง์ ๋ํ ์ธํฐํ์ด์ค๋ฅผ ์์ฑํ๊ณ , ์ธํฐํ์ด์ค์ ๋ํ ๊ตฌํ์ฒด ํด๋์ค๋ฅผ ์์ฑํ๋ ์ฝ๋๋ฅผ ๊ตฌํํ  ๊ฒ์ด๋ค.



์๋ ์ฝ๋๋ ์์ ๋ก ๊ตฌํ๋ ๊ฒ์ธ๋ฐ, ์ฐ๋ฆฌ๊ฐ ์ผ๋ฐ์ ์ผ๋ก ์๋๋ก์ด๋์์ ๋ฐ์ดํฐ๋ฅผ ๋ถ๋ฌ ์ฌ ๋ ์ฌ์ฉํ๋ Repository๋ฅผ ์์ ๋ก ๊ตฌํํด๋ณด์๋ค.

```kotlin
@InterfaceImplementation
class ExampleRepository {

  fun getData(a: Int, b: Int): Int {
    return a + b // Very simple Business Logic
  }

}
```



์ฌ๊ธฐ์ ์ฐ๋ฆฌ๊ฐ ๊ธฐ๋ํ๋ ์ฝ๋๋ ๋ค์๊ณผ ๊ฐ๋ค.

```kotlin
public interface IExampleRepository { // Interface
  public fun getData(a: Int, b: Int): Int
}

public class ExampleRepositoryImpl( // Implementation Class
  private val examplerepository: ExampleRepository
) : IExampleRepository {
  public override fun getData(a: Int, b: Int): Int = examplerepository.getData(a, b)
}
```

ExampleRepository๋ ์ถ์ํ๋ ํจ์์ ๋ํ ๊ตฌํ๋ ํด๋์ค๋ฅผ ๋ชฉ์ ์ผ๋ก ๋ง๋ค์ด์ง๋ค. ์ฐ๋ฆฌ๊ฐ Repository๋ฅผ ์ค๊ณํ  ๋, ์ธํฐํ์ด์ค๋ก ์ถ์ํํ ๊ฒ์ ๊ตฌํ์ฒด์ ์ธ์คํด์ค๋ก ์ฃผ์ํ์ฌ ๊ด๋ฆฌํ๋ค.

์ด ๋ ๊ตฌํ์ฒด ์ธ์คํด์ค์์๋ ๋น์ฆ๋์ค ๋ก์ง์ ๊ฐ๊ณ  ์๋ Repository ์ธ์คํด์ค๋ฅผ ํตํด ํจ์๋ฅผ ํธ์ถํ๋ ํํ๋ก ๋ง๋ค๊ฒ์ด๋ค.

์ค์ ๋ก ํจ์๋ฅผ ํธ์ถํ์ฌ ์ฌ์ฉ ์ ๋ค์๊ณผ ๊ฐ์ด ์ฃผ์๋ฐ์ ์ธ์คํด์ค๋ฅผ ๊ฐ๊ณ  ์ฌ์ฉํ๊ฒ ๋๋ค.

```kotlin
val exampleRepository: IExampleRepository = ExampleRepositoryImpl(ExampleRepository())

repository.getData(1, 2).toString() // 3
```



### Analyze `InterfaceImplementation Processor`

๋ณธ๊ฒฉ์ ์ผ๋ก **KSP**์ **`SymbolProcessor`** ๋ฅผ ๊ตฌํํด๋ณด์. ์ฐ๋ฆฌ๋ `InterfaceImplementationProcessor` ๋ฅผ ํตํด `InterfaceImplementation` ์ด๋ธํ์ด์์ ๊ฐ์ง ํด๋์ค์ ํจ์, ํจ์๋ด ์ธ์๋ฅผ ๋ถ์ํ์ฌ ๊ตฌํํจ์๋ฅผ ๊ฐ์ง ์ธ์คํด์ค์ ํจ์๋ฅผ ํธ์ถํ๋ ์ฝ๋๋ฅผ ๊ตฌํํ  ๊ฒ์ด๋ค.



**`SymbolProcessor`** ์ธํฐํ์ด์ค์ ํจ์๋ฅผ ๋จผ์  ๋ณด์.

```kotlin
interface SymbolProcessor {
    /**
     * ์ฒ๋ฆฌ ์์์ ํธ์ถํ๊ธฐ ์ํด KSP์ ์ํด ํธ์ถ๋๋ค.
     *
     * @param resolver๋ `Symbol`๊ณผ ๊ฐ์ด ์ปดํ์ผ๋ฌ๊ฐ ์ ๊ทผ์ด ๊ฐ๋ฅํ [SymbolProcessor]๋ฅผ ์ ๊ณตํ๋ค.
     * @return [KSAnnotated] ๋ฆฌ์คํธ๋ฅผ ๋ฐํํ๋๋ฐ, ํ๋ก์ธ์๊ฐ ์ฒ๋ฆฌํ  ์ ์๋ ๊ฑฐ์น๋ Symbol์ ์ ๊ณตํ๋ค.
     */
    fun process(resolver: Resolver): List<KSAnnotated>

    /**
     * ์ฒ๋ฆฌ์ ๋ํ ํธ์ง์ด ๋ง๋ฌด๋ฆฌ๊ฐ ๋๋ฉด KSP์ ์ํด ํธ์ถ๋๋ค.
     */
    fun finish() {}

    /**
     * ์ฒ๋ฆฌํ๋ ๊ณผ์ ์์ ์๋ฌ๊ฐ ๋ฐ์ํ ์ดํ KSP์ ์ํด ํธ์ถ๋๋ค.
     */
    fun onError() {}
}
```



์ฌ๊ธฐ์ ์๋ SymbolProcessor๋ฅผ ํตํด ์ฐ๋ฆฌ๋ ์ปดํ์ผ ์ ํด๋์ค ํ์ผ์ ์์ฑํ  ์ ์๊ฒ๋๋ค. ์๋๋ ๊ตฌํํ  ํด๋์ค์ธ `InterfaceImplementationProcessor` ์ด๋ค.

```kotlin
/**
 * @author SODA1127
 * KSP๋ฅผ ์ด์ฉํ SymbolProcessor ๊ตฌํ์ฒด์ด๋ค.
 */
class InterfaceImplementationProcessor : SymbolProcessor {

  private lateinit var codeGenerator: CodeGenerator
  private lateinit var logger: KSPLogger

  ...

  override fun process(resolver: Resolver): List<KSAnnotated> {
    ...
  }

  override fun finish() {
    logger.warn("Processor ๋")
  }

  override fun onError() {
    logger.error("Processor ์๋ฌ")
  }

}
```



์ฐ๋ฆฌ๊ฐ ํด๋น Processor๋ฅผ ๋์์ํค๊ธฐ ์ํด์๋ `SymbolProcessorProvider`๋ฅผ ํตํด ์์ฑ์ ํด์ฃผ์ด์ผํ๋ค.

```kotlin
/**
 * [SymbolProcessorProvider]์ KSP๋ฅผ ํตํฉํ๊ธฐ ์ํ ํ๋ฌ๊ทธ์ธ์์ ์ฌ์ฉ๋๋ ์ธํฐํ์ด์ค์ด๋ค.
 */
fun interface SymbolProcessorProvider {
    /**
     * ํ๋ก์ธ์๋ฅผ ์์ฑํ๊ธฐ ์ํด KSP์ ์ํด ํธ์ถ๋๋ค.
     */
    fun create(environment: SymbolProcessorEnvironment): SymbolProcessor
}
```

์ธ์๋ก ๋ฃ์ด์ฃผ๋ `SymbolProcessorEnvironment` ์ธ์คํด์ค์๋ ๋ค์๊ณผ ๊ฐ์ ํ๋กํผํฐ๋ฅผ ์ ๊ณตํ๋ค.

```kotlin
class SymbolProcessorEnvironment(
    /**
     * Gradle๋ฑ์ ๋ช๋ น์ด๋ฅผ ํต๊ณผํ๋ค.
     */
    val options: Map<String, String>,
    /**
     * ํธ์ง ํ๊ฒฝ์์ ์ฐ์ด๋ ์ธ์ด์ ๋ฒ์ ์ด๋ค.
     */
    val kotlinVersion: KotlinVersion,
    /**
     * ๊ด๋ฆฌ๋  ํ์ผ์ ์์ฑํ๋ ์ญํ ์ด๋ค.
     */
    val codeGenerator: CodeGenerator,
    /**
     * ๋น๋ ์ ๊ฒฐ๊ณผ์ ๋ํด ๋ก๊นํ๋ ์ญํ ์ด๋ค.
     */
    val logger: KSPLogger
)
```



๋๋ `InterfaceImplementationProcessor` ๋ฅผ ์์ฑํ๊ธฐ ์ํด `InterfaceImplementationProvider` ๋ฅผ ๋ง๋ค์ด ์ฃผ์๋ค.

```kotlin
class InterfaceImplementationProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return InterfaceImplementationProcessor().apply {
            init(environment.codeGenerator, environment.logger)
        }
    }

}
```

environment์ธ์์ ํ๋กํผํฐ์ ์ ๊ทผํ์ฌ `InterfaceImplementationProcessor` ์ธ์คํด์ค ์์ฑ ํ ๋ณ์์ ํ ๋นํ๋ค. ์ด๋ฅผ ํตํด ์ฐ๋ฆฌ๋ ์ฝ๋ ์์ฑ ๋ฐ ํธ์ง, ๋น๋์ ๋ํ ๊ฒฐ๊ดด์ ๋ํด ๋ก๊นํ  ์ ์๊ฒ๋๋ค.



### Register KSP Provider

`InterfaceImplementationProvider`์ ์คํํ  ์ ์๋๋ก ํ๊ธฐ ์ํด์๋ ํ๋ก์ ํธ ๋ด resources/META-INF/services์ `com.google.devtools.ksp.processing.SymbolProcessorProvider` ์ด๋ฆ์ ํ์ผ ์์ฑ ํ ํจํค์ง๋ช์ ๊ฐ๋ ํด๋์ค๋ช์ ๋ฑ๋กํด์ค๋ค.

![Register KSP Providier](https://imgur.com/aojz47R.jpg)



### Implement `InterfaceImplementation Processor`

๊ทธ๋ฌ๋ฉด ๋ณธ๊ฒฉ์ ์ผ๋ก processํจ์์์ ์ด๋ค ๋์์ ํ๋์ง ๋ณด์.

```kotlin
/**
 * @author SODA1127
 * KSP๋ฅผ ์ด์ฉํ SymbolProcessor ๊ตฌํ์ฒด์ด๋ค.
 */
class InterfaceImplementationProcessor : SymbolProcessor {

  ...
  companion object {
    private val annotationName = InterfaceImplementation::class.java.canonicalName // ํจํค์ง๋ช์ ํฌํจํ ์ด๋ธํ์ด์ ํด๋์ค ๋ช
    private val filteringKeywords = arrayOf("equals", "hashCode", "toString", "<init>") // ์ถํ ์ฌ์ฉ ๋  ํค์๋ ๋ค
  }

  ...

  override fun process(resolver: Resolver): List<KSAnnotated> {
    logger.warn("Processor ์์")

    val symbols = resolver.getSymbolsWithAnnotation(annotationName) // ์ด๋ธํ์ด์ ํด๋์ค๋ฅผ ์ฃผ์์ผ๋ก ์ฌ์ฉํ๊ณ  ์๋ Symbol์ ๊ฐ์ ธ์จ๋ค.

    val ret = symbols.filter { !it.validate() }

    symbols
    .filterIsInstance<KSClassDeclaration>() // ํด๋์ค์ ์ ์ธ๋ ํ์์ธ์ง ์ฒดํฌํ๋ค.
    .filter { it.validate() }  // ํ์ฉ๋๋ ํ์์ธ์ง๋ฅผ ์ฒดํฌํ๋ค.
    .forEach {
      logger.warn("์ฌ์ฉํ๋ ํด๋์ค : $it")
      it.accept(InterfaceImplementationVisitor(codeGenerator, logger, annotationName, filteringKeywords), Unit) // Visitor ์ธ์คํด์ค๋ฅผ ์์ฑํ์ฌ ๋ด๋ถ์ ์ผ๋ก ํด๋์ค ํ์ผ์ ์์ฑํ๋ค.
    }

    return ret.toList()
  }

  ...

}
```

์ค์ ๋ก Processor๋ `process()` ํจ์๊ฐ ํธ์ถ๋ ์ดํ๋ถํฐ ๋์ํ๋ค๊ณ  ๋ด๋ ๋ฌด๋ฐฉํ๋ค. 

์์ธํ ๋์ ๋ฐฉ์์ ์ฃผ์์ ์ฐธ๊ณ ํ์.



### Implement `InterfaceImplementation KS Visitor`

`KSVIsitor`๋ ๊ธฐ๋ณธ์ ์ผ๋ก ๋๊ฐ์ง์ ์ ๋๋ฆญ ํ์์ ๊ฐ์ง๋ค. ํ๋๋ D: Data Type์ด๊ณ , ๋๋ค๋ฅธ ํ๋๋ R: Return Type์ด๋ค. ๋๊ฐ์ง ํ์์ ์ธ์๋ ํ๋์ Visitor์์ Input์ ๋ง๋ค๊ณ  ๋๋ค๋ฅธ Visitor๋ก Output์ ๋ง๋๋ ํ์ดํ๋ผ์ธ์ ๋ง๋ค ๋ ์ฌ์ฉ๋๋ค.

```kotlin
interface KSVisitor<D, R> {

  fun visitNode(node: KSNode, data: D): R

  fun visitAnnotated(annotated: KSAnnotated, data: D): R
  
  ...
}
```

์ฐ๋ฆฌ๊ฐ ๊ตฌํํ Visitor์ ๊ฒฝ์ฐ ๋ค๋ฅธ Visitor์ Input๊ณผ Output์ ์ฐ๊ฒฐํ  ์ด์ ๊ฐ ์์ผ๋ฏ๋ก, `KSVisitor<Unit, Unit>`๋ฅผ ์ฌ์ฉํ  ๊ฒ์ด๋ค. KSP๋ ์ด์ ๋ํด `KSVisitorVoid`๋ผ๋ ํด๋์ค๋ฅผ ์ ๊ณตํ๋ค. ์ด๋ฅผ ์์๋ฐ์ ๊ตฌํ ํ  ๊ฒ์ด๋ค.

```kotlin
/**
 * @author SODA1127
 * [InterfaceImplementationProcessor]์์ ์ฌ์ฉ๋๋ KSVisitor ํด๋์ค.
 */
class InterfaceImplementationVisitor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val annotationName: String,
    private val filteringKeywords: Array<String>
) : KSVisitorVoid() {
  
  ...
  
  override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
    ...
  }
}
```



์ฒซ๋ฒ์งธ๋ก, ์ฐ๋ฆฌ๊ฐ ๊ตฌํํ Visitor์์ ๋์ํ๋ ํจ์๋ `visitClassDeclaration`ํจ์์ด๋ค. ์ฐ๋ฆฌ๊ฐ ์ฌ๊ธฐ์ ์ฐ์ ์ ์ผ๋ก ์ฒดํฌํด์ผ ํ  ๋ก์ง์ ํด๋น Class๊ฐ ์จ์ ํ Class์ธ์ง๋ฅผ ํ์ธํ๋ ๊ฒ์ด๋ค. ๊ตฌํ ์ `data class` ๋ฅผ ์ฌ์ฉํ๋ ๊ฒฝ์ฐ, ๊ตฌํ์ฒด ์์ฑ์ ์ ํฉํ์ง ์์ผ๋ฏ๋ก ์๋ฌ๋ฅผ ๋ฐ์์ํจ๋ค. 

ํด๋น ํด๋์ค๊ฐ `data class` ์ธ์ง๋ ์ ๊ทผ์ ์ด์๋ก data๊ฐ ํฌํจ๋์ด์๋์ง ์ฒดํฌํ๋ฉด ์ ์ ์๋ค.

```kotlin
...

override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
  logger.warn("@${annotationName} -> $classDeclaration ๋ฐ๊ฒฌ")

  if (classDeclaration.isDataClass()) {
    logger.error(
      "$annotationName can not target data class $classDeclaration",
      classDeclaration
    )
    return
  }
  ...
}

...

private fun KSClassDeclaration.isDataClass() = modifiers.contains(Modifier.DATA)
```



์ปดํ์ผ ์ ์๋์ ๊ฐ์ด ์๋๋๋ก ๋น๋ ์๋ฌ๋ฅผ ๋ฐ์์ํค๋ ๊ฒ์ ์ ์ ์๋ค.

![Build Error](https://imgur.com/g6L7OHH.jpg)



ํ์ผ์ ์์ฑ์ ์ํด ์ด๋ ํ ํ์ผ์ ์์กด์ฑ์ ๊ฐ๋์ง, ํจํค์ง๋ช๊ณผ ํด๋์ค๋ฅผ ์ ์ํ๋ค. ์ผ๋จ ๊ฐ๋จํ๊ฒ ํ๋์ ํ์ผ์ ์ธํฐํ์ด์ค์ ๊ตฌํ์ฒด ํด๋์ค๋ฅผ ์์ฑํ๊ธฐ๋ก ํ๋ค.

```kotlin
...

override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
  ...
  val packageName = classDeclaration.packageName.asString()
  val className = classDeclaration.simpleName.asString()

  /**
         * [KSClassDeclaration.simpleName]๊ณผ ๋์ผํ ์ด๋ฆ์ผ๋ก ํ์ผ ์์ฑํ์ฌ
         * Interface, Implemtation ํด๋์ค๋ฅผ ๋๊ฐ ์์ฑํ๋ ํ์ผ
         */
  val file = codeGenerator.createNewFile(
    dependencies = Dependencies(true, classDeclaration.containingFile!!),
    packageName = packageName,
    fileName = className
  )
  ...
}
```



์ดํ ์์ฑ๋ ํ์ผ์ ์ธํฐํ์ด์ค ์ ์๋ฅผ ํ๋ค. ์ธํฐํ์ด์ค๋ Prefix๋ก `I`ํค์๋๊ฐ ๋ถ๋๋ก ํ๋ค.

ํจ์๋ ๊ธฐ์กด์ ์ ์ธ๋์ด ์๋ ์ด๋ธํ์ด์์ ํ๊ฒ ํด๋์ค์ ํจ์๋ฅผ ๊ฐ์ ธ์ ํจ์๋ช, ์ธ์๋ช, ์ธ์ํ์, ๋ด๋ถ์ ์ธ์์ ํจ์ ํธ์ถ, ๋ฐํ ํ์ ์ ์ธ์ํ๋ ์ฝ๋ ์์ฑ์ KotlinPoet์ ํตํด ํ๋ค.

```kotlin
...

override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
  /**
  * Interface ์ ์๋ฅผ ํ๋ค.
  * ex) [IExampleRepository]์ ๊ฐ์ด `I`ํค์๋๊ฐ Prefix๋ก ๋ถ๊ฒ๋์ด ํด๋์ค๊ฐ ์์ฑ๋๋ค.
  */
  val interfaceName = "I$className"
  val interfaceType = ... // KotlinPoet์ ์ด์ฉํ ์ธํฐํ์ด์ค ๋ด ํจ์ ์์ฑ
  logger.warn("$interfaceName ์์ฑ ์๋ฃ")
  ...
}
...
```



๋ง์ฐฌ๊ฐ์ง๋ก, ๊ตฌํ์ฒด๋ ํด๋น ํ์ผ ๋ด ํจ์๋ก ์์ฑ์ ํ๋ค.

```kotlin
...

override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
  ...
  /**
  * Implementation ์ ์๋ฅผํ๋ค.
  * ex) [ExampleRepositoryImpl]์ ๊ฐ์ด `Impl`ํค์๋๊ฐ Suffix๋ก ๋ถ๊ฒ๋์ด ํด๋์ค๊ฐ ์์ฑ๋๋ค.
  */
  val implementationName = "${className}Impl"
  val implementsType = ... // KotlinPoet์ ์ด์ฉํ ์ธํฐํ์ด์ค ๋ด ํจ์ ์์ฑ
  logger.warn("$implementationName ์์ฑ ์๋ฃ")
  ...
}
...
```



์ต์ข์ ์ผ๋ก ๋๊ฐ์ง ํด๋์ค์ ๋ํ ๊ตฌํ๋ฐฉ์์ ๋ํด ํ์ผ์ ์์ฑํ๊ธฐ ์ํด FileSpec์ ๋น๋ํจ์๋ฅผ ์ด์ฉํ์ฌ ์์ฑ์ ํด์ฃผ๋ฉด ๋ง๋ฌด๋ฆฌ๋๋ค.



```kotlin
...

override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
  ...
  FileSpec.builder(packageName, className)
  .addType(interfaceType)
  .addType(implementsType)
  .build()
  .writeTo(file.toAppendable())
}
...
```



KSPLogger๋ฅผ ํตํ ๊ฒฐ๊ณผ๋ ๋ค์๊ณผ ๊ฐ๋ค.

![KSP Log](https://imgur.com/X6AmEG6.jpg)



[์์ค์ฝ๋๋ ๊นํ๋ธ ๋ ํฌ์งํ ๋ฆฌ](https://github.com/SODA1127/KSPImplementationExample)์ ์ฌ๋ ค๋์์ผ๋, ์ฐธ๊ณ ํด๋ณด์๊ธฐ ๋ฐ๋๋ค.



์ด์์ผ๋ก [KSP์ ๋ํ ๊ฐ๋, KAPT์ ๋น๊ตํ์ฌ ์ข์ ์ ](https://soda1127.github.io/introduce-kotlin-symbol-processing/)๊ณผ, KSP๋ฅผ ๊ตฌํํ ์์ ์ ๋ํด ๋ถ์ํ๊ณ  ์ ๋ฆฌํด๋ณด์๋ค.

### ์ฐธ๊ณ ์๋ฃ

- https://github.com/google/ksp
- https://proandroiddev.com/ksp-fact-or-kapt-7c7e9218c575
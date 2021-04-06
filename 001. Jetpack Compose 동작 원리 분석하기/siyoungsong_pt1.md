
## Jetpack Compose 체험기

* *Jetpack Compose 는 안드로이드스튜디오 카나리 버전에서 사용이 가능합니다. (Android Studio 2020.3.1 Canary 9 기준)*

## Compose 프로젝트 생성

[File] - [New] - [New Project…] 를 눌러 새로운 프로젝트를 선택하고(Preview) Empty Compose Activity 를 선택합니다.

![](https://cdn-images-1.medium.com/max/3592/1*5gfrQWfxyEC7Oq6b2xlJ9w.png)

Compose 를 사용하기 위해서 gradle 설정에서 다음과 같은지 확인합니다.

    kotlinOptions {**
        **jvmTarget = '1.8'
        useIR = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    
    composeOptions {
        kotlinCompilerVersion '1.4.21'
        kotlinCompilerExtensionVersion '1.0.0-alpha10'
    }

** composeOption 설정에서 kotlinCompilerVersion 에 따라 compose 가 다르게 동작할 수 있습니다.*

프로젝트를 생성하면 아래와 같이 기본 Activity 와 Theme, UI 등을 설정하는 파일이 생성됩니다.

## MainActivity.kt

    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent **{**
                **MyFirstComposeApplicationTheme **{**
                    **// A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {**
                        **Greeting("Android")
                    }**
     **           }
            }**
        **}
    }
    
    @Composable
    fun Greeting(name: String) {
        *Text*(text = "Hello $name!")
    }
    
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MyFirstComposeApplicationTheme **{**
            **Greeting("Android")
        }**
    **}

## ui/theme/Theme.kt

    package com.passionvirus.myapplication.ui.theme
    
    import androidx.compose.foundation.isSystemInDarkTheme
    import androidx.compose.material.MaterialTheme
    import androidx.compose.material.darkColors
    import androidx.compose.material.lightColors
    import androidx.compose.runtime.Composable
    
    private val DarkColorPalette = darkColors(
        primary = Purple200,
        primaryVariant = Purple700,
        secondary = Teal200*
    *)
    
    private val LightColorPalette = lightColors(
        primary = Purple500,
        primaryVariant = Purple700,
        secondary = Teal200*
    
        */* Other default colors to override
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
        */
    )
    
    @Composable
    fun MyFirstComposeApplicationTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
        val colors = if (darkTheme) {
            DarkColorPalette*
        *} else {
            LightColorPalette*
        *}
    
        MaterialTheme(
                colors = colors,
                typography = *Typography*,
                shapes = *Shapes*,
                content = content
        )
    }

Preview 와 Compose 를 사용해서 다음과 같이 구성할 수 있습니다.

![](https://cdn-images-1.medium.com/max/5556/1*ybDWG4W2bPYNq79vr1gE8w.png)

Split(design/code) 을 선택에 따라 코드 및 디자인의 패널을 변경할 수 있습니다.

![](https://cdn-images-1.medium.com/max/2000/1*XVvr-CB2pn88Te7Gg52GPA.png)

미리보기에서 인터렉티브 모드를 설정할 수 있습니다. 인터렉티브 모드는 실제 디바이스에서 상호작용하는것과 같이 미리보기에서 클릭이나 드래그 등의 상호 작용을 확인해 볼 수 있습니다. 네트워크나 파일에 접근 또는 일부 Context API 는 인터렉티브 모드를 지원하지 않습니다.
** 간헐적으로 interactive 모드가 작동하지 않는 경우가 있습니다.*

![](https://cdn-images-1.medium.com/max/3096/1*9IA2k2s5hAS9pSyzpmQ6Pw.png)

미리보기에서 디바이스 혹은 에뮬레이터로 배포하여 결과를 확인할 수 있습니다.

![](https://cdn-images-1.medium.com/max/3234/1*5RZOM7sWV0sXctJtTPdshQ.png)

## @Composable

Composable 어노테이션을 사용하면 Declarative(선언형) UI 를 사용할 수 있습니다.

    @Composable
    fun Greeting(names: List<String>) {
        for (name in names) {
            Text("Hello $name")
        }
    }

## @Preview

Preview 어노테이션을 사용하면 디바이스나 에뮬레이터를 실행하지 않고 실시간으로 Compose UI 를 볼 수 있으며, Preview class 는 아래와 같이 구성되어 있으며 설정에 따라서 미리보기를 다양하게 구성할 수 있습니다.

    annotation class Preview(
        val name: String = "",
        val group: String = "",
        @IntRange(from = 1) val apiLevel: Int = -1,
        // TODO(mount): Make this Dp when they are inline classes
        val widthDp: Int = -1,
        // TODO(mount): Make this Dp when they are inline classes
        val heightDp: Int = -1,
        val locale: String = "",
        @FloatRange(from = 0.01) val fontScale: Float = 1f,
        val showSystemUi: Boolean = false,
        val showBackground: Boolean = false,
        val backgroundColor: Long = 0,
        @UiMode val uiMode: Int = 0,
        @Device val device: String = Devices.DEFAULT
    )

* name : Preview 의 이름을 지정하며, 기본 값은 function 이름으로 설정됩니다.

* group : Preview 의 그룹을 지정한다, 기본 값은 function 이름으로 설정됩니다.

* apiLevel : api level 설정에 따라 Composable 을 렌더링 할 수 있습니다.

* widthDp : Preview 의 너비를 설정할 수 있으며 단위는 dp 이고 별도로 단위를 정의할 필요는 없습니다.

* heightDp : Preview 의 높이를 설정할 수 있으며 단위는 dp 이고 별도로 단위를 정의할 필요는 없습니다.

* locale : 사용자 locales 에 따라 보여지는 UI 를 테스트 하기 위해 사용합니다.

* fontScale : 기본 density 애 배율을 적용해서 폰트 사이즈를 변경할 수 있습니다.

* showSystemUi : true 로 설정하면 status bar 와 action bar 를 같이 보여줍니다.

* showBackground : true 로 설정하면 기본 배경색을 보여지도록 설정할 수 있습니다.

* backgroundColor : 미리보기의 배경색을 설정할 수 있으며, showBackground 설정에 따라 보여집니다.

* uiMode : uiMode 를 설정해서 쓸 수 있습니다.

* device : 정의된 디바이스를 프리뷰에 적용할 수 있으며 Devices object 에 정의된 값을 선택해서 사용할 수 있습니다. (Devices.NEXUS_9)

## Layout

* Column : 아이템을 세로로 배치합니다.

    @Composable
    fun ComposeColumn() {
        Column ****{
            **Text(text = "My First Compose")
            Text(text = "My First Compose")
        **}
    **}

![](https://cdn-images-1.medium.com/max/2000/1*bSyX8yT7H2HevBNw9fbyYw.png)

* Row : 아이템을 가로로 배치합니다.

    @Composable
    fun ComposeRow() {
        Row ****{
            **Text(text = "My First Compose")
            Text(text = "My First Compose")
        **}
    **}

![](https://cdn-images-1.medium.com/max/2000/1*T9Pww7JyPMPhCJ_6yU8fyQ.png)

* Box : 구성 요소를 다른 구성 요소 위에 배치합니다.

    @Composable
    fun ComposeBox() {
        Box ****{
            **Text(text = "My First Compose 1")
            Text(text = "My First Compose 2")
        **}
    **}

![](https://cdn-images-1.medium.com/max/2000/1*wFM4jF71V5aJog3lALaRKQ.png)

* Modifier : 구성 요소의 크기, 마진등을 변경하거나 클릭이나 스크롤 등의 이벤트를 제어할 수 있도록 합니다.

    @Composable
    fun ComposeModifier() {
        Box(modifier = Modifier
            .padding(5.*dp*)**
        **) {**
            **Text(text = "Compose Modifier")
        }**
    **}

![](https://cdn-images-1.medium.com/max/2000/1*tPAf-Akf2IpDRrrZo1f1dg.png)

* LazyColumn / LazyRow : Recyclerview 유사하게 화면에 보여지는 구성 요소만을 렌더링하며 큰 데이터셋을 다루기에 용이합니다.

    @Composable
    fun ComposeLazyColumn() {
        val itemsList = (0..100).*toList*()
    
        *LazyColumn *{**
            ***items*(items = itemsList, itemContent = { ****item ->**
                ***Text*(text = "Item : $item", style = TextStyle(fontSize = 80.*sp*))
            })
        }**
    **}

![](https://cdn-images-1.medium.com/max/2000/1*m7mSalYCza-SoJjmlYeWdA.png)

* ConstraintLayout : 기존 ContraintLayout 과 같이 여러 제약 참조를 설정해서 사용할 수 있으며, createRefs / createRefFor 를 통해 참조를 생성하며, constrainAs 를 통해 제약 조건을 설정할 수 있습니다. 
Jetpack Compose 의 ConstraintLayout 을 사용하기 위해서는 dependencies 추가가 필요합니다

    implementation "androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha05"

    @Composable
    fun ComposeConstraintLayout() {
        ConstraintLayout(modifier = Modifier.size(100.dp, 200.dp)) {
            val (text1, image, text3) = createRefs()
    
            Text("Text Item 1", Modifier.constrainAs(text1) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
    
            Image(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().constrainAs(image) {
                    top.linkTo(text1.bottom)
                    bottom.linkTo(text3.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
    
            Text("Text Item 3", Modifier.constrainAs(text3) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
        }
    }

![](https://cdn-images-1.medium.com/max/2000/1*UoLVl1dbUjKh9kd4uY0ROA.png)

이 외에 Compose 에 더 자세한 내용은 사항은 아래 사이트에서 확인할 수 있습니다.

[https://developer.android.com/jetpack/compose/](https://developer.android.com/jetpack/compose/)

[https://developer.android.com/courses/pathways/compose](https://developer.android.com/courses/pathways/compose)

[https://github.com/android/compose-samples](https://github.com/android/compose-samples)

[https://foso.github.io/Jetpack-Compose-Playground/](https://foso.github.io/Jetpack-Compose-Playground/)
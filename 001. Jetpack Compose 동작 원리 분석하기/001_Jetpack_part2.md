# Jetpack Compose Part 2 - ???

## Jetpack Compose란 무엇인가?

![](https://drive.google.com/uc?export=view&id=1vXOGJD7inmGfDYiPu6wMVrpu5xFnov38)

> **출처** [Android Developers#Jetpack Compse](https://developer.android.com/jetpack/compose)

Compose는 Native UI를 코드레벨로 구현할 수 있는 최신 툴킷이다. 

기존의 뷰를 업데이트하는 방식과 달리 Compose를 사용하면 필요한 영역의 뷰를 다시 그려주는 방식으로 작업할 수 있다.

아래는 아주 간단한 예제이다.

```kotlin
@Composable
fun Greeting(name: String) {
	Text("Hello $name")
}
```

위의 예제처럼 UI를 구성하는 것이 아니라 화면을 구성하는 뷰의 State 설명하는 것이므로 아무것도 반환하지 않는다.

아래는 공식 서비스 소개 영상이다.

<div style="width: 560px; height: 315px; float: none; clear: both;">
  <embed
    src="https://www.youtube.com/embed/U5BwfqBpiWU"
    wmode="transparent"
    type="video/mp4"
    width="100%" height="100%"
    allow="autoplay; encrypted-media; picture-in-picture"
    allowfullscreen
  >
</div>

### Compose의 4가지 특징

구글에서 설명하는 Compose를 이용시 얻을 수 있는 장점은 아래와 같다.

- `Less Code` - 코드 감소
  - 적은 수의 코드로 더 많은 작업을 하고 전체 버그 클래스를 방지할 수 있으므로 코드가 간단하며 유지 관리하기 쉽습니다.
- `Intuitive` - 직관적
  - UI만 설명하면 나머지는 Compose에서 처리합니다. 앱 상태가 변경되면 UI가 자동으로 업데이트됩니다.
- `Accelerate Development` - 빠른 개발 과정
  - 기존의 모든 코드와 호환되므로 언제 어디서든 원하는 대로 사용할 수 있습니다. 실시간 미리보기 및 완전한 Android 스튜디오 지원으로 빠르게 반복할 수 있습니다.
- `Powerful` - 강력한 성능
  - Android 플랫폼 API에 직접 액세스하고 머티리얼 디자인, 어두운 테마, 애니메이션 등을 기본적으로 지원하는 멋진 앱을 만들 수 있습니다.

늘 그렇듯 구글에서 말하는 설명만 보면 안 쓸 이유가 없어보이고, 대세가 된다면 Android 개발자에게 또 하나의 러닝커브로 작용할 것이다.

## Codelab - Jetpack Compose basics

### 1. Before you begin

Compose는 아직 정식으로 릴리즈되지 않은 기능이므로 Android Studio Canary에서 프로젝트를 구성하며 몇 가지 제한사항이 존재한다.

1. Android Studio Canary 

Canary는 아래 링크에서 다운 받을 수 있다.

> [Android Studio Preview](https://developer.android.com/studio/preview)

2. 최신 버전의 Kotlin plugin 

```groovy
ext.kotlin_version = '1.4.31'
```

3. buildFeatures 및 composeOption 활성화

```groovy
android {
    ...
    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion 1.0.0-beta02
    }
}

dependencies {
    ...
    implementation "androidx.compose.ui:ui:1.0.0-beta02"
    implementation "androidx.activity:activity-compose:1.3.0-alpha03"
    implementation "androidx.compose.material:material:1.0.0-beta02"
    implementation "androidx.compose.ui:ui-tooling:1.0.0-beta02"
    ...
}
```

### 2. Compose 프로젝트 생성

[File] - [New] - [New Project…] 를 눌러 새로운 프로젝트를 선택하고(Preview) Empty Compose Activity 를 선택한다.

![](https://cdn-images-1.medium.com/max/3592/1*5gfrQWfxyEC7Oq6b2xlJ9w.png)

선택 이후 Next를 클릭하고, Compose를 구현할 수 있는 최소 API 레벨인 21을 선택해야한다.

프로젝트를 생성하면 아래와 같이 app/build.gradle에 의존성 설정 및 추가가 되어 있는것을 알 수 있다.

```groovy
android {
    ...
    kotlinOptions {
        jvmTarget = '1.8'
        useIR = true
    }
    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion compose_version
    }
}

dependencies {
    ...
    implementation "androidx.compose.ui:ui:$compose_version"
    implementation "androidx.activity:activity-compose:1.3.0-alpha03"
    implementation "androidx.compose.material:material:$compose_version"
    implementation "androidx.compose.ui:ui-tooling:$compose_version"
    ...
}
```

이때 composeOptions 설정에서 kotlinCompilerVersion 에 따라 compose 가 다르게 동작할 수 있음을 유의하자.

프로젝트를 생성하면 아래와 같은 기본 파일들이 생성된다.

**MainActivity.kt**
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstComposeApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyFirstComposeApplicationTheme {
        Greeting("Android")
    }
}
```

**ui/theme/Theme.kt**
```kotlin
private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200*

    /* Other default colors to override
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
    } else {
        LightColorPalette*
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

기본 생성된 코드를 보았을 때, Compose는 총 3가지의 구성 요소를 가지는 것으로 추측할 수 있다.

1. 위젯을 포함하는 Composable 함수
2. Preview를 하기 위한 Preview Composable 함수
3. setContent 람다 표현식으로 실제 화면에 노출하는 코드

일반적으로 우리가 아는 Activity의 라이프사이클 콜백 `onCreate()`에서   `setContentView(Int)` 함수를 호출하던것이 `setContent()` 함수로 바뀐것이 가장 큰 특징으로 보여진다.

이때 Preivew와 Compose를 이용해 아래와 같이 IDE 화면을 구성할 수 있다.

![](https://cdn-images-1.medium.com/max/5556/1*ybDWG4W2bPYNq79vr1gE8w.png)

Split(design/code) 을 선택에 따라 코드 및 디자인의 패널을 변경할 수 있다.

![](https://cdn-images-1.medium.com/max/2000/1*XVvr-CB2pn88Te7Gg52GPA.png)

Preview에서도 인터렉티브 모드를 설정할 수 있다.

인터렉티브 모드는 실제 디바이스에서 상호작용하는것과 같이 미리보기에서 클릭이나 드래그 등의 상호 작용을 확인해 볼 수 있다.

다만, 네트워크나 파일에 접근 또는 일부 Context API는 인터렉티브 모드를 지원하지 않는다.

> 아직은 간헐적으로 작동하지 않는 경우가 있다.

![](https://cdn-images-1.medium.com/max/3096/1*9IA2k2s5hAS9pSyzpmQ6Pw.png)

Preview에서 디바이스 혹은 에뮬레이터로 배포하여 결과를 확인할 수 있다.

![](https://cdn-images-1.medium.com/max/3234/1*5RZOM7sWV0sXctJtTPdshQ.png)

### 3. Getting started with Compose

Composable Function은 어노테이션을 이용한 기술이다. 함수위에 `@Composable` 어노테이션을 붙이게 되면 함수 안 다른 함수를 호출할 수 있게된다. 아래 코드를 보자.

```kotlin
@Composable
fun Greeting(names: List<String>) {
    for (name in names) {
        Text("Hello $name")
    }
}
```

단순하게 내부에는 Text라는 함수가 존재하는데, 이를 통해 UI계층 별 요구하는 컴포넌트를 생성해준다. 기본적으로 보이는 text 파라미터는 내부 속성에서 받는 일부 중 하나이다.

아래 코드를 실행시켜보면 당연하게도 Hello로 시작하는 TextView가 화면에 그려질것을 암시한다.

![output](https://imgur.com/aO6Jlsg.jpg)


#### `@Preview` 구성 요소
Preview 어노테이션을 사용하면 디바이스나 에뮬레이터를 실행하지 않고 실시간으로 Compose UI 를 볼 수 있으며, Preview class 는 아래와 같이 구성되어 있으며 설정에 따라서 미리보기를 다양하게 구성할 수 있다.

```kotlin
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
```

- **name** : Preview 의 이름을 지정하며, 기본 값은 function 이름으로 설정된다.
- **group** : Preview 의 그룹을 지정한다, 기본 값은 function 이름으로 설정된다.
- **apiLevel** : api level 설정에 따라 Composable을 렌더링한다.
- **widthDp** : Preview 의 너비를 설정한다. (dp 단위, 별도 단위의 정의는 필요없음)
- **heightDp** : Preview 의 높이를 설정한다. (dp 단위, 별도 단위의 정의는 필요없음)
- **locale** : 사용자 locales 에 따라 보여지는 UI 를 테스트할때 사용
- **fontScale** : 기본 density 애 배율을 적용해서 폰트 사이즈를 변경한다.
- **showSystemUi** : true 로 설정하면 status bar 와 action bar 를 같이 보여준다.
- **showBackground** : true 로 설정하면 기본 배경색을 보여지도록 설정한다.
- **backgroundColor** : 미리보기의 배경색을 설정할 수 있으며, showBackground 설정에 따라 보여준다.
- **uiMode** : uiMode 를 설정해서 쓸 수 있다.
- **device** : 정의된 디바이스를 프리뷰에 적용할 수 있으며 Devices object 에 정의된 값을 선택해서 사용할 수 있다. (Devices.NEXUS_9)

### 4. Declarative UI

노란색 배경을 입혀 기존 TextView에 추가해보았다. 또한, Greeting에는 Modifier라는 것을 이용하여 Padding을 추가했다. 아래와 같은 결과가 나오게 되었다.

```kotlin
BasicsCodelabTheme {
  // A surface container using the 'background' color from the theme
  Surface(color = Color.Yellow) {
    Greeting("Android")
  }
}
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicsCodelabTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}
```


```kotlin
@Composable
fun Greeting(name: String) {
    var isSelected by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(if (isSelected) Color.Red else Color.Transparent)

    Text(
        text = "Hello $name!",
        modifier = Modifier
            .padding(24.dp)
            .background(color = backgroundColor)
            .clickable(onClick = { isSelected = !isSelected })
    )
}
```

![결과](https://imgur.com/qgQ6oY4.jpg)

선언형 UI의 장점은 말 그대로 내가 UI를 정의한대로 시각적으로 표현이 가능하다는 장점이 있다. 기존에는 속성을 매번 On/Off와 같은 옵션을 통해 변경하는 것이 다반사였지만, 이제는 매번 속성에 변경이 생길때마다 새로 그려주게 되는것이다.




### State in Compose
### Flexible layouts
### Animating your list
### Theming your app
### Congurations


---

# Jetpack Compose Part 1 - Compose 소개 및 코드랩 따라하기

**Author in [AndroidDeepDive Study](https://github.com/AndroidDeepDive/Study)**
- 김남훈 @Naver
- 배희성 @RocketPunch
- 송시영 @SmartStudy
- 이기정 @BankSalad

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

### 1. 시작하기전에

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

### 2. Empty Compose 프로젝트 생성

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

### 3. Composable Function

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

### 4. TextView 만들기

위 코드를 실행시켜보면 당연하게도 Hello로 시작하는 TextView가 화면에 그려질것을 암시한다.

```kotlin
setContent {
  BasicsCodelabTheme {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
      Greeting("Android")
    }
  }
}
```

![output](https://imgur.com/aO6Jlsg.jpg)

### 5. `@Preview`

말 그대로 어노테이션을 이용하여 IDE에서 Preview를하기 위한 용도이다. 아래 코드와 같이 @Preview 어노테이션을 추가하면 다음 결과를 볼 수 있다.

```kotlin
@Preview("Greeting Preview")
@Composable
fun GreetingPreview() {
    BasicsCodelabTheme {
        Surface(color = MaterialTheme.colors.background) {
            Greeting("Android")
        }
    }
}
```

![Greeting Preview](https://imgur.com/WprDTs1.jpg)

### 6. setContent / Theme / Surface

```kotlin
setContent {
  BasicsCodelabTheme {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
      Greeting("Android")
    }
  }
}
```

기존에 onCreate시점에 화면을 그려주기 위한 필수적인 요소를 정리해보자면

- **setContent** : Activity에서 setContentView함수를 사용하는 것과 동일한 동작을 하는 확장함수이다. 다만, setContent의 경우 (@Composable) -> Unit 타입의 컴포즈 UI를 구현해주어야한다.

- **XXXTheme** : Theme정보를 의미한다. 해당 프로젝트에서는 Theme.kt에 여러 테마에 필요한 정보를 정리하고, 컴포즈 UI 구현을 위한 코드를 작성해두었다.

```kotlin
private val DarkColorPalette = darkColors(
    primary = purple200,
    primaryVariant = purple700,
    secondary = teal200
)

private val LightColorPalette = lightColors(
    primary = purple500,
    primaryVariant = purple700,
    secondary = teal200

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
fun BasicsCodelabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
```

- **Surface** : Greeting을 감싸는 뷰에 해당한다. 여기서는 크기를 정하지 않고, background 색상을 정의하고 있다. 역시 람다 표현식이다. 색상에 대한 Paramter로 `color` 라는 값을 사용하여 부여가 가능하다. 내부코드를 보면

```kotlin
@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(color),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val elevationPx = with(LocalDensity.current) { elevation.toPx() }
    val elevationOverlay = LocalElevationOverlay.current
    val absoluteElevation = LocalAbsoluteElevation.current + elevation
    val backgroundColor = if (color == MaterialTheme.colors.surface && elevationOverlay != null) {
        elevationOverlay.apply(color, absoluteElevation)
    } else {
        color
    }
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAbsoluteElevation provides absoluteElevation
    ) {
        Box(
            modifier.graphicsLayer(shadowElevation = elevationPx, shape = shape)
                .then(if (border != null) Modifier.border(border, shape) else Modifier)
                .background(
                    color = backgroundColor,
                    shape = shape
                )
                .clip(shape),
            propagateMinConstraints = true
        ) {
            content()
        }
    }
}
```

### 7. Declarative UI - 선언형 UI

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


### 8. 재사용

Compose의 장점 중 하나는 재사용성이 뛰어난것인데, XML에서 우리가 include 태그를 통해 여러곳에서 갖다쓸 수 있던것처럼, 함수를 통해 여러곳에서 정의하여 사용이 가능하다.

참고해야할 점은 Compose 컴포넌트 확장 시 `@Composable` 어노테이션을 붙여야 한다.

### 9. Container 작성

MyApp이라는 이름으로 컴포즈 컴포넌트를 구횬하여 여러곳에서 공통으로 사용할 수 있는 Composable을 구현하였다. 내부적으로 Container내 내가 원하는 컴포넌트를 넣어주려면 아래와 같이 인자로 `@Composable () -> Unit` 타입을 넘겨받아 처리해주면 된다.

```kotlin
@Composable
fun MyApp(content: @Composable () -> Unit) {
    BasicsCodelabTheme {
        Surface(color = Color.Yellow) {
            content()
        }
    }
}
```

위 함수를 통해 이제는 어디서든 반복해서 사용할 수 있는 Container를 구현하게 되어 아래와 같이 코드를 활용할 수 있게되었다.

```kotlin
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MyApp {
        Greeting("Android")
      }
    }
    ...
  }
```

### 10. 레이아웃을 활용한 Compose function의 다중 호출

지금까지는 하나의 컴포넌트만을 갖고 사용했지만, 여러개의 컴포넌트를 넣는것도 가능하다.

```kotlin
@Composable
fun MyScreenContent() {
    Column {
        Greeting("Android")
        Divider(color = Color.Black)
        Greeting("there")
    }
}
```

 `Column`과 위에서부터 사용하던 `Greeting` 함수를 사용하고, 라인을 그어주기 위한 `Divider`를 추가한 결과물은 다음과 같다.

![multiple component](https://imgur.com/VLTxB8C.jpg)

위 컴포넌트 중 못보던 컴포저블이 있는데, 아래와 같이 설명이 가능하다.

- Column : 항목을 순서대로 배치하기 위해 사용한다.
- Divider : 선 긋기 가능한 Compose 함수이다.

이를 리스트 형태로도 구현이 가능하다.

```kotlin
@Composable
fun MyColumnScreen(names: List<String> = listOf("Line One", "Line Two")) {
    Column {
        names.forEach {
            Greeting(name = it)
            Divider(color = Color.Black)
        }
    }
}
```

### 11. State in Compose - Compose에서의 상태값 관리


컴포넌트에 버튼을 클릭했을 때 클릭한 카운트를 집계하는 간단한 컴포넌트를 만들어보았다.

```kotlin
@Composable
fun MyColumnScreen(names: List<String> = listOf("Line One", "Line Two")) {
    val counterState = remember { mutableStateOf(0) } // 

    Column {
        names.forEach {
            Greeting(name = it)
            Divider(color = Color.Black)
        }
        Counter(
            count = counterState.value,
            updateCount = { newCount ->
                counterState.value = newCount
            }
        )
    }
}
```

remember라는 함수를 사용하여 기존에 존재하는 컴포넌트의 상태값을 기억하게 하는 함수가 있다. 

`remember` 함수의 내부를 살펴보자.

```kotlin
/**
 * Remember the value produced by [calculation]. [calculation] will only be evaluated during the composition.
 * Recomposition will always return the value produced by composition.
 */
@OptIn(ComposeCompilerApi::class)
@Composable
inline fun <T> remember(calculation: @DisallowComposableCalls () -> T): T =
    currentComposer.cache(false, calculation)
```

매 호출마다 Recomposition(재조합)하게되는 경우 컴포넌트에 값을 다시 제공하는 것을 알 수 있다. @Composable 어노테이션에 들어간 함수는 매번 해당 상태를 구독하고, 상태가 변경될때마다 알림을 받아 기존 화면을 갱신해준다.

그리고, 아래 Counter를 보면 Button을 이용하여 이벤트를 받아 처리하도록 했다.

```kotlin
@Composable
fun Counter(count: Int, updateCount: (Int) -> Unit) {
    Button(
        onClick = { updateCount(count + 1) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (count > 5) Color.Green else Color.White
        )
    ) {
        Text("I've been clicked $count times")
    }
}
```

`updateCount(Int)` 함수릉 통해 매번 값을 업데이트 해주는데, 이를 통해 counterState에 값을 넣어주면서 해당 컴포넌트가 매번 변경이 되는것이다.

따라서 결과를 보면, 다음과 같다. Count가 5가 넘어가면 초록색으로 바뀐다.

![count](https://imgur.com/Ju6BSg2.gif)

그 외에도 여러형태의 모양을 구성할수 있도록 옵션이 제공되어 있다. 자세한 정보는 나중에 [Codelabs](https://developer.android.com/codelabs/jetpack-compose-basics)에 더 나와 있으니 보도록하고, 이번에 setContent에 대한 동작원리를 함께 고민해보자.

### 12. Activity에서의 View 생성 방식과의 비교

Compose를 안드로이드 앱에서 사용하려면 Activity, Fragment와 같은곳에서 contentView로 뿌려줘야한다. 기존에 우리가 사용하던 함수를 보자.

```java
/**
* Set the activity content from a layout resource.  The resource will be
* inflated, adding all top-level views to the activity.
*
* @param layoutResID Resource ID to be inflated.
*
* @see #setContentView(android.view.View)
* @see #setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
*/
public void setContentView(@LayoutRes int layoutResID) {
  getWindow().setContentView(layoutResID);
  initWindowDecorActionBar();
}
```

UI 컴포넌트에서 화면을 붙일 수 있는 Window라는 녀석에서 Layout Resource Id를 통해 기존에 등록되어있던 Layout XML 파일을 로드하여 인플레이터에서 파싱하고, 이를통해 레이아웃 계층에 있는 뷰객체를 생성하여 순차적으로 ViewGroup, View를 만들어 넣어주게 된다.

`PhoneWindow`를 보면 자세하게 알 수 있는데, Window를 구현한 setContentView에서 처음에 생성되는 최상위 레이아웃 그 위에 따로 없다면 `installDecor()` 함수를 통해 mContentParent(레이아웃 리소스가 붙게될 ViewGroup)를 생성하고, 하위에 넣어주게 된다.

그러면 기존 방식은 이정도로 설명을하고, 이번엔 Compose에서 `setContent()` 라는 함수를 어떻게 사용하는지 보자.

```kotlin
/**
 * Composes the given composable into the given activity. The [content] will become the root view
 * of the given activity.
 *
 * This is roughly equivalent to calling [ComponentActivity.setContentView] with a [ComposeView]
 * i.e.:
 *
 * ```
 * setContentView(
 *   ComposeView(this).apply {
 *     setContent {
 *       MyComposableContent()
 *     }
 *   }
 * )
 * ```
 *
 * @param parent The parent composition reference to coordinate scheduling of composition updates
 * @param content A `@Composable` function declaring the UI contents
 */
public fun ComponentActivity.setContent(
    parent: CompositionContext? = null,
    content: @Composable () -> Unit
) {
    val existingComposeView = window.decorView
        .findViewById<ViewGroup>(android.R.id.content)
        .getChildAt(0) as? ComposeView

    if (existingComposeView != null) with(existingComposeView) {
        setParentCompositionContext(parent)
        setContent(content)
    } else ComposeView(this).apply {
        // Set content and parent **before** setContentView
        // to have ComposeView create the composition on attach
        setParentCompositionContext(parent)
        setContent(content)
        // Set the view tree owners before setting the content view so that the inflation process
        // and attach listeners will see them already present
        setOwners()
        setContentView(this, DefaultActivityContentLayoutParams)
    }
}
```

이녀석도 마찬가지로 `window.decorView.findViewById<ViewGroup>(android.R.id.content)`  함수를 호출하여 decorView를 가져온다. 만약 compose를 통해 만들어진 최상위 레이아웃이 존재하면, 기존에 inflator에서 ViewGroup, View를 생성해서 넣어주던것 처럼 `setContent()` => window가 Activity/Fragment에 붙으면 `createComposition()`를 호출하여 검증 후 `ensureCompsositionCreated()` 함수를 호출한다. 현재는 내부적으로 `ViewGroup.setContent()` 를 사용하고 있는데, 곧 교체 될 예정이라고 한다. 이코드도 보면 기존에 있는 ViewGroup에 확장함수로 구현한 녀석인데, 쉽게 말해 ViewGroup에 하위 View, ViewGroup에 Composable로 구현된 함수로 컴포넌트를 넣어줄 때 AndroidComposeView라는 객체를 꺼내오거나 없다면 새로 생성하여 넣어준다.

```kotlin
/**
 * Composes the given composable into the given view.
 *
 * The new composition can be logically "linked" to an existing one, by providing a
 * [parent]. This will ensure that invalidations and CompositionLocals will flow through
 * the two compositions as if they were not separate.
 *
 * Note that this [ViewGroup] should have an unique id for the saved instance state mechanism to
 * be able to save and restore the values used within the composition. See [View.setId].
 *
 * @param parent The [Recomposer] or parent composition reference.
 * @param content Composable that will be the content of the view.
 */
internal fun ViewGroup.setContent(
    parent: CompositionContext,
    content: @Composable () -> Unit
): Composition {
    GlobalSnapshotManager.ensureStarted()
    val composeView =
        if (childCount > 0) {
            getChildAt(0) as? AndroidComposeView
        } else {
            removeAllViews(); null
        } ?: AndroidComposeView(context).also { addView(it.view, DefaultLayoutParams) }
    return doSetContent(composeView, parent, content)
}
```



다시 돌아와서, ComposeView의 `setContent()` 이라는 녀석을 보자.

```kotlin
/**
 * A [android.view.View] that can host Jetpack Compose UI content.
 * Use [setContent] to supply the content composable function for the view.
 *
 * This [android.view.View] requires that the window it is attached to contains a
 * [ViewTreeLifecycleOwner]. This [androidx.lifecycle.LifecycleOwner] is used to
 * [dispose][androidx.compose.runtime.Composition.dispose] of the underlying composition
 * when the host [Lifecycle] is destroyed, permitting the view to be attached and
 * detached repeatedly while preserving the composition. Call [disposeComposition]
 * to dispose of the underlying composition earlier, or if the view is never initially
 * attached to a window. (The requirement to dispose of the composition explicitly
 * in the event that the view is never (re)attached is temporary.)
 */
class ComposeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private val content = mutableStateOf<(@Composable () -> Unit)?>(null)

    @Suppress("RedundantVisibilityModifier")
    protected override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    @Composable
    override fun Content() {
        content.value?.invoke()
    }

    /**
     * Set the Jetpack Compose UI content for this view.
     * Initial composition will occur when the view becomes attached to a window or when
     * [createComposition] is called, whichever comes first.
     */
    fun setContent(content: @Composable () -> Unit) {
        shouldCreateCompositionOnAttachedToWindow = true
        this.content.value = content
        if (isAttachedToWindow) {
            createComposition()
        }
    }
}
```

결론적으로 `AbstractComposeView` 라는 녀석은 ViewGroup을 상속받은 녀석이며, 모든 composable의 상태가 변화 되었을 때 이를 감지하는 중요한 녀석이다.

`setContent()`라는 함수는 위에서 설명했으니 넘어가고, 이번에는 `Content`라는 녀석을 보자. 이녀석은 추상 메소드로, `createComposition()` 이라는 함수가 호출 되었을 때, 가장 먼저 불리는 함수이다. 아까 언급되었던 `ensureCompsositionCreated()` 함수에서 tree계층의 ComposeView가 다 붙었다면, 이후에 즉시 Content함수가 호출이된다.

```kotlin
@Suppress("DEPRECATION") // Still using ViewGroup.setContent for now
    private fun ensureCompositionCreated() {
        if (composition == null) {
            try {
                creatingComposition = true
                composition = setContent(
                    parentContext ?: findViewTreeCompositionContext() ?: windowRecomposer
                ) {
                    Content() // 이곳에서 뷰가 다 window에 붙게되면 콜백을 호출한다.
                }
            } finally {
                creatingComposition = false
            }
        }
    }
```

그러면 아래 `ComposeView`의 오버라이딩 된 Content가 호출되면서, 기존에 생성된 View에 UI속성과 같은 Content가 붙게된다.

```kotlin
/**
* The Jetpack Compose UI content for this view.
* Subclasses must implement this method to provide content. Initial composition will
* occur when the view becomes attached to a window or when [createComposition] is called,
* whichever comes first.
*/
@Composable
abstract fun Content()
```

Content는 설명에서 보는것과 같이 `createComposition()` 함수 호출 후 View가 Window에 붙은 이후 즉시 호출된다.

최종적으로 `ComponentActivity.setContent(CompositionContext?, @Composable () -> Unit)` 함수에서 구현된 ComposeView 인스턴스를 ContentLayout을 widht/height를 wrapContent크기로 정하여 ContentView를 Set해주게 된다.

```kotlin
/**
 * Composes the given composable into the given activity. The [content] will become the root view
 * of the given activity.
 *
 * This is roughly equivalent to calling [ComponentActivity.setContentView] with a [ComposeView]
 * i.e.:
 *
 * ```
 * setContentView(
 *   ComposeView(this).apply {
 *     setContent {
 *       MyComposableContent()
 *     }
 *   }
 * )
 * ```
 *
 * @param parent The parent composition reference to coordinate scheduling of composition updates
 * @param content A `@Composable` function declaring the UI contents
 */
public fun ComponentActivity.setContent(
    parent: CompositionContext? = null,
    content: @Composable () -> Unit
) {
  	...
		else ComposeView(this).apply {
        // Set content and parent **before** setContentView
        // to have ComposeView create the composition on attach
        setParentCompositionContext(parent)
        setContent(content)
        // Set the view tree owners before setting the content view so that the inflation process
        // and attach listeners will see them already present
        setOwners()
        setContentView(this, DefaultActivityContentLayoutParams)
    }
}
```

### 13. ComposeView

android.view.View 는 Jetpack Compose UI 콘텐츠를 사용할 수 있도록 해줍니다. setContent 를 사용하면 composable function content 를 뷰에 제공할 수 있다.

Compose 의 계층 구조는 아래와 같으며. ComposeView 를 통해 androidx.compose.materia 에 정의된 다양한 컴포넌트를 조합하여 Composable function 콘텐츠를 구성할 수 있다.

```
kotlin.Any
 ↳ android.view.View
   ↳ android.view.ViewGroup
     ↳ androidx.compose.ui.platform.AbstractComposeView
       ↳ androidx.compose.ui.platform.ComposeView
```

### 14. Compose Compiler / Compose Runtime

Compose Compiler 는 `@Composable` 이 설정된 경우 Composable function 으로 코드 변환과 코틀린 컴파일러 플러그인과 함께 최적화를 활성화한다.

Compose Runtime은 Compose의 프로그래밍 모델과 상태 관리, 그리고 Compose 컴파일러를 지정하기 위한 코어 런타임에 대한 기본 설정을 수행한다.


```koltin
@Composable
fun Greeting(name: String) {
    var greet by remember { mutableStateOf("Hello $name") }
    Text(text = greet, color = Color.Red)
}
```

위의 코드는 Compose Compiler에 의해 `@Composeable`은 아래와 같이 변경된다.

```kotlin
fun Greeting(
  $composer: Composer,
  $static: Int,
  name: String
) {
  $composer.start(123)
  var greet by remember { mutableStateOf("Hello $name") }
  Text(text = greet, color = Color.Red)
  $composer.end()
}
```

Compose 는 `composer.start` 에서 고유의 키를 가지고 있고, 이는 Compose 의 state 가 변경될 때 해당 키를 가진 Compose 만 변경되도록 동작한다.

static 은 상태(state)의 변경여부를 알 수 있는데 상태의 변화가 없는 경우, `composer.start` 와 `composer.end` 사이의 UI 의 변경을 하지 않는다.

이때 데이터의 상태가 변경되어 UI 를 다시 구성하는 경우는 Recomposition 이라고 한다.

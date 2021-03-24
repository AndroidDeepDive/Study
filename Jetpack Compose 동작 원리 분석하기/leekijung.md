# (Android Deep Dive) Jetpack Compose Part 1



### Table of Contents

1. Jetpack Compose을 사용해야하는 이유
2. 구조 (Sturucture)
3. Sample App 분석
4. Hello, World 후 빌드된 코드 분석



## Jetpack Compose란 무엇인가?

Compose는 Native UI를 코드레벨로 구현할 수 있는 최신 툴킷이다. Compose를 이용하면 아래와 같은 장점이 있다.

![](https://drive.google.com/uc?export=view&id=1vXOGJD7inmGfDYiPu6wMVrpu5xFnov38)

> **출처** [Android Developers#Jetpack Compse](https://developer.android.com/jetpack/compose)

- ### 코드 감소 `Less Code`

  - 적은 수의 코드로 더 많은 작업을 하고 전체 버그 클래스를 방지할 수 있으므로 코드가 간단하며 유지 관리하기 쉽습니다.

- ### 직관적 `Intuitive`

  - UI만 설명하면 나머지는 Compose에서 처리합니다. 앱 상태가 변경되면 UI가 자동으로 업데이트됩니다.

- ### 빠른 개발 과정  `Accelerate Development`

  - 기존의 모든 코드와 호환되므로 언제 어디서든 원하는 대로 사용할 수 있습니다. 실시간 미리보기 및 완전한 Android 스튜디오 지원으로 빠르게 반복할 수 있습니다.

- ### 강력한 성능 `Powerful`

  - Android 플랫폼 API에 직접 액세스하고 머티리얼 디자인, 어두운 테마, 애니메이션 등을 기본적으로 지원하는 멋진 앱을 만들 수 있습니다.



## Jetpack Compose을 사용해야하는 개인적인 이유

지금까지 나온 안드로이드의 네이티브 뷰 컴포넌트 요소는 컴포넌트를 한번 Window에 추가한 이후 인스턴스에서 제공하는 속성값을 제어하는 형태로 구성되어 왔다. 그러다보니, 기존에 있던 뷰 상태를 체크하여 기존 뷰를 변경하는 UI 로직이 필연적이었고, 이런 부분에서 개발자가 집중해야할 본질적인 부분인 비즈니스로직에 대해 신경을 분산시키는 원인이 되기도했다.

이런문제를 깔끔하게 해결해줄 수 있는 Toolkit인 Compose가 리액티브 프로그래밍 모델을 기반으로 선언형 UI Builder 패턴과 함께 우리앞에 선보이게 되었는데, 코드만 보면 화면의 상태`Model` 에 따라 UI의 상태를 결정짓기 떄문에, 더 이상 UI 상태를 보고 UI로직을 구성할 필요가 없다라는 장점이 있다.

또한, Gof의 State 패턴과 같이 화면의 상태를 단일로 표현할 수 있는 수단이 있다면, 화면을 제어할 때 더할나위없이 쉬운방법으로 개발을 경험할 수 있다는 것 또한 장점이다.

일단 코드랩의 코드를 보고 한번 어떤 느낌인지 보도록하자. [Compose CodeLab](https://developer.android.com/codelabs/jetpack-compose-basics?authuser=1#1)

## OverView

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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BasicsCodelabTheme {
        Greeting("Android")
    }
}
```

일반적으로 우리가 아는 Activity의 라이프사이클 콜백 `onCreate()`에서   `setContentView(Int)` 함수를 호출하던것이 `setContent()` 함수로 바뀐것이 가장 큰 특징으로 보여진다.

그리고 하단에 `@Composable` 이라는 어노테이션이 보이고, `@Preview` 어노테이션이 보이는데, 이 두가지를 통해 우리는 유추해볼 수 있는 단어의 의미가 있다.

- Composable : 무언가 조합이 가능한 녀석이다. 컴포넌트를 생성하여 조합이 가능할 것으로 보인다.
- Preview : 미리보기를 할 수 있는 녀석이다. @Composable이 아래에 붙은것을 보니 컴포넌트에 대한 미리보기가 가능한 형태로 제공할 것으로 보인다.

자, 그러면 하나하나씩 코드를 보자.

### Composable Function

Composable Function은 어노테이션을 이용한 기술이다. 함수위에 `@Composable` 어노테이션을 붙이게 되면 함수 안 다른 함수를 호출할 수 있게된다. 아래 코드를 보자.

```kotlin
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
```

단순하게 내부에는 Text라는 함수가 존재하는데, 이를 통해 UI계층 별 요구하는 컴포넌트를 생성해준다. 기본적으로 보이는 text 파라미터는 내부 속성에서 받는 일부 중 하나이다.

### Activity에서 작성

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
**
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

위에 뭐라뭐라 써져 있는데, 결론적으로 `AbstractComposeView` 라는 녀석은 ViewGroup을 상속받은 녀석이며, 모든 composable의 상태가 변화 되었을 때 이를 감지하는 중요한 녀석이다.

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




















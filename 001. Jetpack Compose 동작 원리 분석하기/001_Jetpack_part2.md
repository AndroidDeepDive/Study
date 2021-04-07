# Jetpack Compose Part 2 - Preview / Layout / Decompile 

**Author in [AndroidDeepDive Study](https://github.com/AndroidDeepDive/Study)**
- 김남훈 @Naver
- 배희성 @RocketPunch
- 송시영 @SmartStudy
- 이기정 @BankSalad

## Android Studio의 Jetpack Compose

Android Studio Preview를 사용해 Compose를 사용하다보면 Preview라 코드레벨에서 활성화되는 것을 알 수 있다.

좀 더 자세히 살펴보자.

### Preview의 기능들

Preview 와 Compose 를 사용해서 아래와 같이 구성할 수 있다.

![](https://cdn-images-1.medium.com/max/5556/1*ybDWG4W2bPYNq79vr1gE8w.png)

Split(design/code) 을 선택에 따라 코드 및 디자인의 패널을 변경할 수도 있다.

![](https://cdn-images-1.medium.com/max/2000/1*XVvr-CB2pn88Te7Gg52GPA.png)

미리보기에서 인터렉티브 모드를 설정할 수 있다. 

인터렉티브 모드를 설정하면 실제 디바이스처럼  클릭이나 드래그 등의 상호 작용을 확인해 볼 수 있다.

다만, 네트워크나 파일에 접근 또는 일부 Context API 는 인터렉티브 모드를 지원하지 않고 있다.

> 간헐적으로 interactive 모드가 작동하지 않는 경우가 있다.

![](https://cdn-images-1.medium.com/max/3096/1*9IA2k2s5hAS9pSyzpmQ6Pw.png)

미리보기에서 직접 디바이스 혹은 에뮬레이터로 배포하여 결과를 확인할 수도 있다.

![](https://cdn-images-1.medium.com/max/3234/1*5RZOM7sWV0sXctJtTPdshQ.png)


### `@Preview` Annotation 분석

위에서 언급했다시피 `@Preview`를 사용하면 디바이스나 에뮬레이터를 실행하지 않고 실시간으로 Compose UI 를 볼 수 있다.

`@Preview`를는 아래와 같이 구성되어 있으며 설정에 따라서 미리보기를 다양하게 구성할 수 있다.

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
- **apiLevel** : api level 설정에 따라 Composable 을 렌더링해준다.
- **widthDp** : Preview 의 너비를 설정한다. (기본 단위는 dp)
- **heightDp** : Preview 의 높이를 설정한다. (기본 단위는 dp)
- **locale** : 사용자 locales 에 따라 보여지는 UI 를 테스트 하기 위해 사용한다.
- **fontScale** : 기본 density 애 배율을 적용해서 폰트 사이즈를 변경할 수 있다.
- **showSystemUi** : true 로 설정하면 status bar 와 action bar 를 노출한다.
- **showBackground** : true 로 설정하면 기본 배경색상을 적용해준다.
- **backgroundColor** : 미리보기의 배경색을 설정할 수 있으며, showBackground 설정에 따라 노출 유무를 결정한다.
- **uiMode** : uiMode 를 설정한다.
- **device** : 기존 정의된 디바이스를 프리뷰에 적용한다. Devices object 에 정의된 값을 선택해서 사용할 수 있습니다. (Devices.NEXUS_9)

### Compose의 레이아웃 구성

- **Column** : 아이템을 세로로 배치한다.

```kotlin
@Composable
fun ComposeColumn() {
    Column {
        Text(text = "My First Compose")
        Text(text = "My First Compose")
    }
}
```

![](https://cdn-images-1.medium.com/max/2000/1*bSyX8yT7H2HevBNw9fbyYw.png)

- **Row** : 아이템을 가로로 배치한다.

```kotlin
@Composable
fun ComposeRow() {
    Row {
        Text(text = "My First Compose")
        Text(text = "My First Compose")
    }
}
```

![](https://cdn-images-1.medium.com/max/2000/1*T9Pww7JyPMPhCJ_6yU8fyQ.png)

- **Box** : 구성 요소를 다른 구성 요소 위에 배치한다.

```kotlin
@Composable
fun ComposeBox() {
    Box {
        Text(text = "My First Compose 1")
        Text(text = "My First Compose 2")
    }
}
```

![](https://cdn-images-1.medium.com/max/2000/1*wFM4jF71V5aJog3lALaRKQ.png)

- **Modifier** : 구성 요소의 크기, 마진등을 변경하거나 클릭이나 스크롤 등의 이벤트를 제어할 수 있도록 한다.

```kotlin
@Composable
fun ComposeModifier() {
    Box(modifier = Modifier
        .padding(5.dp)
    ) {
        Text(text = "Compose Modifier")
    }
}
```

![](https://cdn-images-1.medium.com/max/2000/1*tPAf-Akf2IpDRrrZo1f1dg.png)

- **LazyColumn / LazyRow** : Recyclerview 유사하게 화면에 보여지는 구성 요소만을 렌더링한다. 큰 데이터셋을 다루기에 용이하다.

```kotlin
@Composable
fun ComposeLazyColumn() {
    val itemsList = (0..100).toList()

    LazyColumn {
        items(items = itemsList, itemContent = { item ->
            Text(text = "Item : $item", style = TextStyle(fontSize = 80.sp))
        })
    }
}
```

![](https://cdn-images-1.medium.com/max/2000/1*m7mSalYCza-SoJjmlYeWdA.png)

- **ConstraintLayout** : 기존 ContraintLayout 과 같이 여러 제약 참조를 설정해서 사용할 수 있다.
 `createRefs` / `createRefFor` 를 통해 참조를 생성하며, constrainAs 를 통해 제약 조건을 설정한다.

```kotlin
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
```

Jetpack Compose 의 ConstraintLayout 을 사용하기 위해서는 아래 의존성을 추가해야 한다.

```groovy
implementation "androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha05"
```

![](https://cdn-images-1.medium.com/max/2000/1*UoLVl1dbUjKh9kd4uY0ROA.png)

### Jetpack Compose의 동작 원리 파악을 위한 빌드 과정 추적

##### 1. 프로젝트 생성

Compose가 내부적으로 어떻게 동작하는 지 알아보기 위해 먼저 프로젝트를 빌드해보자.

빌드 후 Kotlin > Byte Code > Decompiled Java 순서로 변환하여 살펴볼 것이다.

Android Studio Preview에서 Empty Compose Activity로 프로젝트를 생성하면 아래와 같은 샘플 코드를 얻을 수 있다.

프로젝트 생성 후 임의로 Hello World로 파라미터값을 변경하였다.


```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloWorldTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("World")
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
    HelloWorldTheme {
        Greeting("World")
    }
}
```

부가적으로 `src` 폴더 내부에 `ui.theme` 패키지가 생성되고 `Color.kt`, `Shape.kt`, `Theme.kt`, `Type.kt` 파일도 생성된다.

이 파일들은 필요한 경우 들여다 보도록 하자.

생성 후 Preview에 아래와 같이 렌더링 된다.

![](https://drive.google.com/uc?export=view&id=1_KB2Zz3OMmPZQaY8Ndyiw5divy7KWrD_)

##### 2. MainActivity 디컴파일

```java
@Metadata(
   mv = {1, 4, 2},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0007\u001a\u0010\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007¨\u0006\u0005"},
   d2 = {"DefaultPreview", "", "Greeting", "name", "", "app_debug"}
)
public final class MainActivityKt {
   @Composable
   public static final void Greeting(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      TextKt.Text-Vh6c2nE$default("Hello " + name + '!', (Modifier)null, 0L, 0L, (FontStyle)null, (FontWeight)null, (FontFamily)null, 0L, (TextDecoration)null, (TextAlign)null, 0L, (TextOverflow)null, false, 0, (Function1)null, (TextStyle)null, 65534, (Object)null);
   }

   @Composable
   public static final void DefaultPreview() {
      ThemeKt.HelloWorldTheme$default(false, (Function0)null.INSTANCE, 1, (Object)null);
   }
}

// MainActivity.java
@Metadata(
   mv = {1, 4, 2},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0014¨\u0006\u0007"},
   d2 = {"Lcom/example/helloworld/MainActivity;", "Landroidx/activity/ComponentActivity;", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"}
)
public final class MainActivity extends ComponentActivity {
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ComponentActivityKt.setContent$default(this, (CompositionContext)null, (Function0)null.INSTANCE, 1, (Object)null);
   }
}
```

`ComponentActivityKt.setContent()`의 구현체는 아래와 같다.

```kotlin
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
        setContentView(this, DefaultActivityContentLayoutParams)
    }
}
```

`CompositionContext` 파라미터는 null을 그대로 넘겨주었고, `(Function0)null.INSTANCE, 1, (Object)null`의 값으로 무언가를 넘겨주는데,

이 값이 `@Composable` Annotation의 구현체이다.

위의 코드 흔적을 술어로 표현해보면 **`@Composable` 구현체를 넘겨주면 이를 기반으로 `ComposeView` 객체를 생성하여 `Activity`의 `setContentView()`에 적용한다.** 가 되겠다.


##### 3. `@Composable` 구현체 확인

`Composable` Annotaion 클래스의 구현체는 아래와 같다.

```kotlin
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    // function declarations
    // @Composable fun Foo() { ... }
    // lambda expressions
    // val foo = @Composable { ... }
    AnnotationTarget.FUNCTION,

    // type declarations
    // var foo: @Composable () -> Unit = { ... }
    // parameter types
    // foo: @Composable () -> Unit
    AnnotationTarget.TYPE,

    // composable types inside of type signatures
    // foo: (@Composable () -> Unit) -> Unit
    AnnotationTarget.TYPE_PARAMETER,

    // composable property getters and setters
    // val foo: Int @Composable get() { ... }
    // var bar: Int
    //   @Composable get() { ... }
    AnnotationTarget.PROPERTY_GETTER
)
annotation class Composable
```

AnnotationTarget을 통해 메서드나 Lambda 객체를 넘겨서 뷰를 조립하는 방식인데, `View`와 `ViewGroup`처럼 내부적으로 트리 구조로 실행지점에 대한 정보를 저장하고 있다.

파면 팔수록 Flutter의 Widget, React Native의 Component와 유사한 느낌을 준다.

## Jetpack Compose 는 어떻게 동작할까?

* *Jetpack Compose 는 안드로이드스튜디오 카나리 버전에서 사용이 가능합니다. (Android Studio Canary 9 기준으로 작성되었습니다)*



## Activity 에서 UI 를 구성하는 방법

### setContentView

* 일반적으로 Activity 에서 UI 를 구성할 때 사용합니다.
>  Set the activity content from a layout resource. The resource will be inflated, adding all top-level views to the activity.
>  Set the Activity’s content view to the given layout and return the associated binding. The given layout resource must not be a merge layout.

레이아웃 리소스로 부터 Activity 콘텐츠 설정할 수 있습니다. 리소스는 확장되며, 모든 최상위 레벨 뷰는 Activity 에 추가됩니다.

레이아웃과 레이아웃에 바인딩된 결과를 통해서 Activity 콘텐츠를 설정할 수 있습니다. 레이아웃 리소스는 merge 레이아웃이 아니여야 합니다.



### setContent

* Jetpack Compose 를 통해서 UI 를 구성할 때 사용합니다.
>  A android.view.View that can host Jetpack Compose UI content. Use setContent to supply the content composable function for the view.
>  Set the Jetpack Compose UI content for this view. Initial composition will occur when the view becomes attached to a window or when createComposition is called, whichever comes first.

android.view.View 는 Jetpack Compose UI 를 사용할 수 있게 하며, setContent 를 사용하여 composable function 를 제공할 수 있습니다. Jetpack Compose 는 뷰가 윈도우에 attached 되는 시점 혹은 createComposition 이 호출 시점 중 빠른 시점에 구성 초기화가 됩니다.

초기화 되는 코드를 살펴보면 아래와 같이 ComponentActivity.setContent 를 통해 Jetpack Compose 를 구성하고 초기화할 수 있습니다.

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

    fun setContent(content: @Composable () -> Unit) {
        shouldCreateCompositionOnAttachedToWindow = true
        this.content.value = content
        if (*isAttachedToWindow*) {
            createComposition()
        }
    }



## ComposeView?
>  A android.view.View that can host Jetpack Compose UI content. Use setContent to supply the content composable function for the view.

android.view.View 는 Jetpack Compose UI 콘텐츠를 사용할 수 있도록 해줍니다. setContent 를 사용하면 composable function content 를 뷰에 제공할 수 있습니다.

Compose 의 계층 구조는 아래와 같으며. ComposeView 를 통해 androidx.compose.materia 에 정의된 다양한 컴포넌트를 조합하여 Composable function 콘텐츠를 구성할 수 있습니다.

kotlin.Any
 ↳ android.view.View
 ↳ android.view.ViewGroup
 ↳ androidx.compose.ui.platform.AbstractComposeView
 ↳ androidx.compose.ui.platform.ComposeView



## Compose Compiler?
>  Transform @Composable functions and enable optimizations with a Kotlin compiler plugin.

[@Composable](http://twitter.com/Composable) annotation 을 설정한 function 의 코드 변환과 코틀린 컴파일러 플러그인과 함께 최적화를 활성화합니다.



## Compose Runtime?
>  Fundamental building blocks of Compose’s programming model and state management, and core runtime for the Compose Compiler Plugin to target.

Compose의 프로그래밍 모델과 상태 관리, 그리고 Compose 컴파일러를 지정하기 위한 코어 런타임에 대한 기본 설정을 합니다. 





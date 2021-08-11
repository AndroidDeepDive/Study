### IME (Input Method Editor)

Android 11 (API 레벨 30) 에서는 기존에 비해 많이 개선된 WindowInsets API 에 통해서 앱이 화면 키보드를 열거나 닫을 때 끊김 없는 전환을 만들 수 있으며, 구글 검색 앱과 메시지 앱에 잘 적용되어 있다.

IME 가 부드럽게 열거나 닫는 경험을 앱에 추가하는 방법은 세 단계가 있다.

 1. edge-to-edge 로 가야한다.

 2. 앱이 삽입된 애니메이션에 반응하기 시작한다.

 3. 앱이 적용된 이벤트가 탐지가 되면 삽입된 애니메이션을 제어하고 실행한다.



* edge-to-edge

Android 10(API 29) 에서 새로운 제스처 탐색을 최대한 활용할 수 있는 방법으로 edge-to-edge 컨셉을 도입했다. 간단히 요약하면, edge-to-edge 로 이동하면 앱이 시스템 바 뒤에 그려진다. 이는 앱 콘텐츠가 빛을 발하여 사용자에게 보다 몰입도 높은 경험 제공할 수 있게 해준다.



* edge-to-edge 와 키보드의 관계

edge-to-edge 로 가는것은 실제로 상태표시바과 탐색바 뒤에 그리는 것 이상이다. 앱은 시스템 UI 와 앱이 겹칠 수 있는 부분에 대한 처리를 한다. 이러한 처리가 필요한 시스템 UI 의 하나로 화면 키보드 또는 IME 가 언급된다.



* edge-to-edge 가는법

edge-to-edge 는 3가지 작업으로 구성된다

 1. 시스템바 색상 변경

    // 상태표시바 색상 변경
    window.statusBarColor = Color.parseColor("#FF2244")

    // 탐색바 색상 변경
    window.navigationBarColor = getColor(R.color.purple_500)

2. 풀스크린으로 설정

앱에서 풀스크린을 사용하기 위해서는 여러 플래그와 함께 systemUiVisibility 사용이 필요했었다. 하지만 Android 11(API 30) 으로 컴파일 하는 경우, 기존에 API 를 사용할 수 없다. 따라서 새롭게 변경된 setDecorFitsSystemWindows 을 호출해야 한다. 많은 플래그 대신에 앱이 시스템 윈도우에 맞게 처리하는 경우(풀스크린) boolean: false 를 인자로 전달합니다.

3. 시각적 충돌 처리

시스템 UI 와 충돌을 피하기 위해서는 window insets 을 사용하여 콘텐츠가 이동할 위치를 아는것으로 요약할 수 있다. 안드로이드에서는 insets 는 WindowInsets 클래스로 표시되고 AndroidX에서는 WindowInsetsCompat 에 의해 표시된다. 
Android 11(API 30) 업데이트 이전에 WindowInsets 살펴보면 대부분 일반적인 사용되는 inset 타입은 시스템 윈도우 inset 이다. 여기에는 상태표시바와 탐색바 그리고 키보드가 열려 있는 상태를 포함한다.
windowInsets 를 사용하기 위해서 일반적으로 OnApplyWindowInsetsListener 를 뷰에 추가하고 뷰에 전달되는 모든 inset 를 처리한다.
일반적으로 시스템 윈도우 inset 을 가져오고, 뷰의 padding 맞게 업데이트 한다. Android 10(API 29) 에서는 최근에 추가된 제스처 inset 을 포함하여 사용 가능한 다른 inset 타입이 있다.
systemUiVisibility API 와 유사하게 많은 WindowInsets API 가 더이상 사용할 수 없으며, 다양한 타입에 대한 inset 에 쿼리할 수 있는 새로운 function 이 생겼다.

1) getInsets(type: Int)
주어진 유형에 대해 보이는 inset을 반환
2) getInsetsIgnoringVisibility(type: Int)
주어진 유형의 노출여부와 상관없이 inset 을 반환
3) isVisible(type: Int)
주어진 유형이 노출되는 경우에만 inset 반환

다영한 inset 에 대해서 WindowInsets.Type 클래스에 정의되어 있으며, 각각은 integer 플래그로 반환한다. OR 비트 연산을 통해 여러 타입을 결합하여 사용할 수 있다. 
이러한 모든 API 는 WindowInsetsCompat 으로 backported 되어 Android 4.0 (API 14) 에서도 안전하게 사용할 수 있다.



* IME 타입

키보드의 노출 상태를 확인하기 위해서는 루트 윈도우 inset 를 가져오고 isVisible() 에 IME 타입을 전달하여 호출할 수 있다. 마찬가지로 키보드의 높이를 알고 싶다면 getInsets() 에 타입을 전달하여 호출할 수 있다.

    val insets = *window*.*decorView*.*rootWindowInsets*
    val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
    val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

키보드 변경 사항을 수신해야 하는 경우 일반 OnApplyWindowInsetsListener를 사용하고 동일한 기능을 사용할 수 있다.

    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
      val imeVisible = insets.isVisible(Type.ime())    
      val imeHeight = insets.getInsets(Type.ime()).bottom
    }

* 키보드 표시/숨김

Android 11(API 30) 에서는 WindowInsetsController 를 호출하여 키보드를 숨기거나 나타나게 할 수 있다. 앱은 모든 뷰에서 컨트롤러에 접근 가능하며, show() 또는 hide() 호출을 통해 키보드를 표시하거나 숨길 수 있다.

    val controller = *window*.*insetsController*
    
    // Show the keyboard (IME)
    controller.show(WindowInsetsCompat.Type.ime())
    
    // Hide the keyboard
    controller.hide(WindowInsetsCompat.Type.ime())

그러나 키보드를 숨기고 표시하는 것이 컨트롤러가 할 수 있는 전부는 아니다.



* WindowInsetsController

Android 11(API 30) 에서 View.SYSTEM_UI_* 더이상 사용할 수 없으며 새로운 API 로 대체되었다. 다음을 포함하여 시스템 UI 모양 또는 가시성을 변경하는 것과 관련된 여러 다른 View.SYSTEM_UI 플래그는 사용할 수 있다.

    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    View.SYSTEM_UI_FLAG_LOW_PROFILE
    View.SYSTEM_UI_FLAG_FULLSCREEN
    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    View.SYSTEM_UI_FLAG_IMMERSIVE
    View.SYSTEM_UI_FLAG_VISIBLE
    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

다른것과 마찬가지로 이것들도 Android 11(API 30) 에서 더이상 사용할 수 없으며 WindowInsetsController의 API로 대체되었다. 이러한 모든 프래그에 대해 마이그레이션을 진행하는 대신 몇 가지 일반적인 시나리오를 다루고 업데이트 하는 방법을 살펴본다.



* Immersive modes

Immersive modes 는 사용자가 화면과 많이 상호작용하는 앱을 위해 만들어졌다. 게임이나 갤러리의 이미지 뷰어, 책 또는 프레젠테이션 슬라이드와 같은 경우가 해당된다. 시스템 표시줄을 다시 가져오려면 숨겨진 시스템바의 가장 자리에서 스와이프하면 된다. 의도된 제스처가 필요하기 때문에 우연한 터치나 스와이프에 의해 시스템바가 노출되지 않도록 한다.
Immersive mode 는 WindowInsetsController 로 구현할 수 있으며, 시스템바 타입과 함게 hide() 와 show() 를 사용한다.

    val controller = *window*.*insetsController*
    
    // When we want to hide the system controller.hide(WindowInsetsCompat.Type.systemBars())
    
    // When we want to show the system controller.show(WindowInsetsCompat.Type.systemBars())

Immersive mode 에서는 스와이프하여 다시 시스템바를 숨길수도 있다. WindowInsetsController 로 구현할 수 있으며, 노출과 숨김에 대한 이벤트 액션을 BEHAVIOR_SHOW_BARS_BY_SWIPE 변경하면 된다.

    val controller = *window*.*insetsController*
    
    // Immersive is now...
    controller?.setSystemBarsBehavior(
      WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
    )
    
    // When we want to hide the system barscontroller.hide(Type.systemBars())

마찬가지로 고정 Immersive mode 를 사용하는 경우, BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE 를 사용할 수 있다.

    val controller = *window*.*insetsController*
    
    // Sticky Immersive is now ...
    controller?.setSystemBarsBehavior(
      BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    )
    
    // When we want to hide the system barscontroller.hide(Type.systemBars())



* 상태표시바 색상

상태표시바의 어두운 배경에 시간 및 아이콘을 밝은 콘텐츠로 설정하거나 밝은 배경에 어두운 콘텐츠 설정을 할 수 있다. WindowInsetsController 에 setSystemBarsAppearance() 을 사용할 수 있다.

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    val controller = *window*.*insetsController*
    
      // Enable light status bar content
      controller?.setSystemBarsAppearance(
        APPEARANCE_LIGHT_STATUS_BARS, // value
        APPEARANCE_LIGHT_STATUS_BARS // mask
      )
    }

상태표시바에 어두운 배경을 설정하려면 APPEARANCE_LIGHT_STATUS_BARS 대신에 0 값을 전달하면 된다.





* 키보드 애니메이션

Android 10 이하에서 텍스트 입력을 클릭한 경우, 키보드 애니메이션은 제자리에 고정되지만 앱은 상태 사이를 변경합니다. 이것은 잠시 동안 본 동작이며 20% 속도로 더 쉽게 볼 수 있다. Android 11 에서는 앱은 키보드와 함께 움직이며 매끄러운 경험을 제공할 수 있게 만들어 준다.
Android 11 에서는 WindowInsetsAnimation 을 지원하며 앱은 WindowInsetsAnimation.Callback 을 통해 애니메이션 이벤트에 대해 수신할 수 있다.

    val view = findViewById<EditText>(R.id.*chatbox*)
    val animationCallback = object : WindowInsetsAnimation.Callback(*DISPATCH_MODE_STOP*) {
        override fun onProgress(
            insets: WindowInsets,
            runningAnimations: MutableList<WindowInsetsAnimation>
        ): WindowInsets {
            *TODO*("Not yet implemented")
        }
    }
    
    view.setWindowInsetsAnimationCallback(animationCallback)



키보드가 현재 닫혀있고, 사용자가 EditText 를 클릭했다고 상상해본다. 이제 시스템에서는 키보드를 보여주기 시작하고 WindowInsetsAnimation.Callback 가 설정되어 다음 호출을 순서대로 수신한다.

    val animationCallback = object : WindowInsetsAnimation.Callback(*DISPATCH_MODE_STOP*) {
      // 첫번째, 현재 레이아웃에서 모든 뷰의 상태를 기록하기 위해 onPrePare 호출 된다.
      override fun onPrepare(animation: WindowInsetsAnimation) {
      }
    
    // 두번째, onPrepare 후, 일반 WindowInsets 는 종료 상태를 포함하는 뷰 계층 구조로 전달하며, OnApplyWindowInsetsListener 가 호출되어 레이아웃 패스가 종료 상태를 반영하게 된다.
      override fun onStart(
        animation: WindowInsetsAnimation,
        bounds: WindowInsetsAnimation.Bounds
      ): WindowInsetsAnimation.Bounds {
        // 세번째, onStart 가 호출되며 애니매니션이 시작할 때 호출된다. 앱은 타겟의 뷰의 상태나 종료 상태를 기록한다.
        return bounds
      }
    
    override fun onProgress(
        insets: WindowInsets,
        runningAnimations: List<WindowInsetsAnimation>
      ): WindowInsets {
        // 네번째, 매우 중요한 호출인 onProgress 이다. 애니메이션에서 inset 이 변경될 때무다 호출된다. 키보드의 경우, 스크린위에서 미끄러지듯 움직인다.
        return insets
      }
    
      override fun onEnd (animation: WindowInsetsAnimation) {
      // 마지막으로 모든 애니메이션이 끝나면 onEnd 가 호출된다. 오래된 상태를 지우기 위해 사용한다.
      }





https://medium.com/androiddevelopers/animating-your-keyboard-fb776a8fb66d

https://medium.com/androiddevelopers/animating-your-keyboard-reacting-to-inset-animations-839be3d4c31b
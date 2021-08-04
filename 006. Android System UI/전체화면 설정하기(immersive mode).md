# 전체화면 설정하기(immersive mode)

System bar(Status bar 및 Navigation bar)를 사용자에게 노출 하지 않는 것이 목적

구글의 [Enable fullscreen mode](https://developer.android.com/training/system-ui/immersive) 문서 참조하면 다음과 같은 유즈케이스에 대해서 설명하고 있음.

- Leanback : 사용자가 화면과 상호작용을 자주 하지 않음. ex) 비디오 시
  `SYSTEM_UI_FLAG_FULLSCREEN and SYSTEM_UI_FLAG_HIDE_NAVIGATION`.
- Immersive : 사용자가 화면과 상호작용을 자주 함. 시스템 표시바를 다시 나타내야 하는 경우 가장자리를 스와이프 할 수 있다.
  ex) 게임, 갤러리, 책 뷰어, 프레젠테이션
  `SYSTEM_UI_FLAG_FULLSCREEN and SYSTEM_UI_FLAG_HIDE_NAVIGATION and SYSTEM_UI_FLAG_IMMERSIVE `
- Sticky Immersive : 게임과 같이 자주 엣지를 스와이프 하게 되는 경우 이 모드를 사용함. 이 모드를 사용하면 시스템 UI 변경사항 콜백을 수신 할 수 없다. 
  `SYSTEM_UI_FLAG_FULLSCREEN and SYSTEM_UI_FLAG_HIDE_NAVIGATION and SYSTEM_UI_FLAG_IMMERSIVE_STICKY `

전체 화면 모드 활성/비활성 예제 코드

```kotlin
override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) hideSystemUI()
}

private fun hideSystemUI() {
    // Enables regular immersive mode.
    // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
    // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Hide the nav bar and status bar
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

// Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
private fun showSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}
```

그러나 위 문서는 오래된 것으로 신속히 갱신 될 필요가 있다. **API Level 30** 이후로 setSystemUiVisibility 메서드는 Deprecated 되었으며, [WindowInsetsController](https://developer.android.com/reference/android/view/WindowInsetsController)를 사용하라고 가이드 하고 있다. 개발자가 싫어하는 분기 WindowsInsetsCompat 사용으로 해결!

구글에서 제공하는 클래스 중 접미어가 Compat인 경우 하위 버전이 상위 버전 구현과 최대한 비슷하게 동작할 수 있도록 초점을 맞춘Wrapper클래스를 말한다. 하위 호환성 및 보일러플레이트 코드 제거를 위해서 사용할 수 있으며, 원본 클래스보다 Compat 클래스의 사용을 권장한다.

전체화면을 구현하는 방법에 대한 공식문서는 낡았지만, 구글의 github에서 WindowInsetsController를 사용하는 최신 예제를 발견했다.

구글 공식 예제 : [user-interface-samples/ImmersiveMode](https://github.com/android/user-interface-samples/tree/main/ImmersiveMode)

## WindowInsetsController의 Behavior 플래그 종류

navigation bar를 숨겼을 때 사용자의 터치 또는 스와이프에 따른 기대하는 동작

```kotlin
// 화면을 터치하면 navigation bar를 노출
WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH

// System bar 영역을 스와이프 하면 System bar를 노출
WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE

// System bar 영역을 스와이프 하면 일시적으로 투명한 System bar를 노출하고 일정시간 뒤 사라진다.
WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
```

WindowInsets.Type 종류

```kotlin
WindowInsetsCompat.Type.systemBars() // status bar 및 navigation bar (+caption bar)
WindowInsetsCompat.Type.statusBars() // status bar
WindowInsetsCompat.Type.navigationBars() // navigation bar
```



WindowInsetsController 인스턴스 얻기

```kotlin
// API Level 30이상 
val insetsController = window.decorView.windowInsetsController

// API Level 30 이하 (권장)
val insetsController = WindowInsetsControllerCompat(window, rootView)
```

System bar 컨트롤 하기. System bar를 숨겼을 때의 동작과 어떤 종류의 System bar를 숨길 것인지 명시한다.

```kotlin
// WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
// WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
// WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
val behavior = ...

// WindowInsetsCompat.Type.systemBars()
// WindowInsetsCompat.Type.statusBars()
// WindowInsetsCompat.Type.navigationBars()
val type = ...

insetsController.systemBarsBehavior = behavior
// System Bar 나타내기
insetsController.show(type)
// System Bar 숨기기
insetsController.hide(type)
```

## 디스플레이 컷아웃

컷아웃 영역 처리하기 위해 총 4가지 모드를 제공한다.

```kotlin
// 화면 방향에 따라서 컷아웃 영역의 사용이 다를 수 있음.
WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT

// 컷아웃 영역에 렌더링을 허용하지 않음
WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER

// short edge에 한해 cutout영역에 렌더링 허용
WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

// 컷아웃 영역에 렌더링 허용
WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
```

컷아웃 모드를 다음 예제와 같이 변경할 수 있다. 

```kotlin
window.attributes = window.attributes.apply {
    layoutInDisplayCutoutMode = cutoutMode.mode
}
```

컷아웃 모드는 API Level 28이상부터 지원한다. DisplayCutout이라는 클래스로 디스플레이의 컷아웃 영역을 표현하는데, 하위 호환을 위해 DisplayCutoutCompat도 지원한다.

하위 버전에서는 다음과 같이 DisplayCutoutCompat 인스턴스를 얻을 수 있음.

```kotlin
ViewCompat.setOnApplyWindowInsetsListener(
    view
) { v, insets ->
    val displayCutout:DisplayCutoutCompat? = insets.displayCutout
    insets
}

// 또는 (API Level 23이상 요구)
val displayCutout = WindowInsetsCompat.toWindowInsetsCompat(window.decorView.rootWindowInsets).displayCutout
```

API Level 28 이전에 나온 기기가 cutout 디스플레이를 가지고 있을 때 사용할 수 있을 것  같다. [참조](https://stackoverflow.com/a/56609043)

## 기타 팁

#### FitsSystemWindows

전체화면 설정 후에 System bar 영역에서 콘텐츠를 표시해야 하는 경우 다음의 내용을 참고하자.

```kotlin
fitsSystemWindows = true 
//시스템 UI 영역과 겹치지 않게 함

fitsSystemWindows = false
//시스템 UI 영역과 겹침

```

### Action Bar

전체화면 설정시 Action Bar를 노출 하고 싶지 않다면

사용하고 있는 theme에 다음과 같은 속성을 추가 한다.

```xml
<item name="windowActionBar">false</item>
<item name="windowNoTitle">true</item>
```

또는 Theme.AppCompat.NoActionBar 등과 같이 정의된 스타일의 접미어가 NoActionBar인 것을 사용

Action Bar가 가지고 있는 hide() 메서드를 호출하는 것도 방법
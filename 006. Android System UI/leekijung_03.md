# Introduce Android System UI - 3

이번시간에는 우리가 흔히 말하는 노치영역이란 무엇인지, 이것을 안드로이드 디바이스에서는 어떻게 대응을 해줘야 하는지 소개한다.

## Instroduce Display Cutout

![Display Cutout Sample](https://imgur.com/lyMYR34.jpg)



디스플레이 컷아웃(Display Cutout)은 기기 전면에 중요한 센서들을 위한 공간을 제공하는 동시, 에지 투 에지(Edge to Edge) 경험을 가능하게 한다. Android 9(API 28) 이상을 실행하는 기기라면 공식적으로 디스플레이 컷아웃을 지원한다.



### 디스플레이 컷아웃이 있는 경우 기기에서 예상되는 사항

Android 9이상을 실행하는 기기에서는 일관성, 앱 호환성을 보장하기 위해 다음과 같은 컷아웃 동작을 보장해야한다.

- 단일 가장자리에 컷아웃을 최대 1개 포함할 수 있다.
- 기기에 컷아웃이 3개 이상 있을 수 없다.
- 기기 양쪽의 긴 가장자리(세로 모드 시 좌/우)에는 컷아웃이 있을 수 없다.
- 특수 플래그를 설정하지 않은 세로 방향에서는 상태 표시줄이 적어도 컷아웃 높이까지 확장되어야 한다.
- 기본적으로 전체 화면 또는 가로 방향에서는 전체 컷아웃 영역이 레터박스 처리되어야 한다.

따라서, 다음과 같은 컷아웃 유형을 지원한다.

- 상단 중앙: 상단 가장자리 중앙의 컷아웃
- 상단 비중앙: 컷아웃이 모서리에 위치하거나 중앙에서 약간 벗어날 수 있음
- 하단: 하단의 컷아웃
- 이중: 상단의 컷아웃 1개, 하단의 컷아웃 1개

## 앱의 컷아웃 영역 처리 방법 소개

콘텐츠가 컷아웃 영역과 겹치지 않게 하려면 콘텐츠가 상태 표시줄(Status Bar) 및 탐색메뉴(Navigation Bar)와 겹치지 않게 하려면 컷아웃 영역에서 Inset을 부여하여 처리하면 해결이 가능하다.

컷아웃 영역으로 렌더링 하는 경우 `[WindowInsets.getDisplayCutout()` 함수를 사용하여 각 컷아웃의 Safe Inset Area와 Safe Area가 포함된 `DisplayCutout` 객체를 탐색할 수 있다.

이러한 API를 사용한다면 콘텐츠가 컷아웃과 겹치는지 여부를 찬단하여 위치를 조정할 수 있다.

> **참고:** 여러 API 레벨에서 컷아웃 구현을 관리하려면 [AndroidX 라이브러리](https://developer.android.com/topic/libraries/support-library/androidx-overview?hl=ko)에서 [DisplayCutoutCompat](https://developer.android.com/reference/androidx/core/view/DisplayCutoutCompat?hl=ko)을 사용해도 된다(SDK 관리자를 통해 사용 가능).



안드로이드에서는 컷아웃 영역내에 콘텐츠를 표시할지 여부를 제어할 수 있는데, Window Layout Param인 `layoutInDisplayCutoutMode` 를 사용할 수 있다.

- [LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT](https://developer.android.com/reference/android/view/WindowManager.LayoutParams?hl=ko#LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT): 기본동작이며, 세로 모드에서는 콘텐츠가 컷아웃 영역으로 렌더링되고 가로 모드에서는 콘텐츠가 레터박스 처리된다.
- [LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES](https://developer.android.com/reference/android/view/WindowManager.LayoutParams?hl=ko#LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES): 세로 모드와 가로 모드 모두에서 콘텐츠가 컷아웃 영역으로 렌더링된다.
- [LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER](https://developer.android.com/reference/android/view/WindowManager.LayoutParams?hl=ko#LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER): 콘텐츠가 컷아웃 영역으로 렌더링되지 않는다.



컷아웃 모드는 프로그래밍 방식으로 설정하거나, XML을 통해 Activity 타겟으로 스타일을 지정하여 설정이 가능하다. 다음은 예시이다.

```xml
<style name="ActivityTheme">
  <item name="android:windowLayoutInDisplayCutoutMode">
    shortEdges <!-- default, shortEdges, never -->
  </item>
</style>
```



### 기본 동작(Default)

기본적으로 특수 플래그가 설정되지 않은 세로 모드에서는 컷아웃이 있는 기기의 상태 표시줄 크기가 최소 컷아웃 높이까지 조절되고, 그 하단에 콘텐츠가 표시된다.

가로모드의 경우 전체화면 모드임에도 컷아웃 영역에 콘텐츠가 표시되지 않고, 레터박스(검은색 박스) 처리된다.

아래는 세로, 가로방향시 대응된 모습이다.

![Defaut Cutout Mode Portrait](https://imgur.com/VfLNBy4.jpg)



![Defaut Cutout Mode Landscape](https://imgur.com/dvBgdih.jpg)



### 컷아웃 영역에 콘텐츠 렌더링을 하지 않음(Never)

이 모드는 현재 디스플레이에 컷아웃 영역을 어떤 Screen 모드이던 레터박스로 가리도록 처리한다. 그덕에, Status Bar에서 보이는 컨텐츠 영역은 Full Screen Mode를 적용했음에도 컷아웃 영역이 레터박스이기 때문에 침범되지 않는다. 따라서 어떤 상황이든 레터박스 영역에 컨텐츠가 표시된다.

이 모드는 플래그 설정, 해제 시 다른 레이아웃이 실행되지 않도록 `View.SYSTEM_UI_FLAG_FULLSCREEN` 또는 `View.SYSTEM_UI_FLAG_HIDE_NAVIGATION`를 설정하는 창과 사용해야 한다.

아래는 예제이다.

|           | Non Full Screen                                              | Full Screen                                                  |
| --------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Portrait  | ![Shorcut Never Portrait Non Full Screen](https://imgur.com/4LUYGFa.jpg) | ![Shorcut Never Portrait Full Screen](https://imgur.com/omPC2HR.jpg) |
| LandScape | ![Shorcut Never Landscape Non Full Screen](https://imgur.com/IeZKNHq.jpg) | ![Shorcut Never Landscape Full Screen](https://imgur.com/W0SDqyl.jpg) |



### 짧은 가장자리 컷아웃 영역에서 콘텐츠 렌더링하기(Short Edges)

동영상, 사진, 지도, 게임과 같이 콘텐츠에 몰입도를 더 주기 위해 에지 투 에지 경험을 효과적으로 제공할 수 있는 케이스에는 Short Edge 모드를 사용한다.

해당 모드를 사용하면 시스템 표시줄(System Bars)이 숨겨지거나 표시되는지 상관없이 세로모드, 가로모드에서 디스플레이의 짧은 가장자리에 있는 컷아웃 영역으로 확장된다.

다만, 컷아웃 영역이 가장자리 기준 길게 되어 있다면, 윈도우 영역은 확장될 수 없음을 참고하자. (너무 길다면 컨텐츠가 가려지는 면적이 크기 떄문으로 추측된다)

이 모드를 사용할 때는 중요한 콘텐츠가 컷아웃 영역과 겹치지 않도록 해야한다.

안드로이드에서는 ContentView가 System Bar와 겹치는 것을 허용하지 않을 수 있는데, 동작을 재정의하고 콘텐츠를 컷아웃 영역으로 확장하려면 `View.setSystemUiVisibility(int)` 함수를 통해 플래그를 적용하면 된다. 적용이 가능한 플래그는 아래와 같다.

- SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
- SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
- SYSTEM_UI_FLAG_LAYOUT_STABLE



아래는 Short Edge 모드를 사용 시 화면에 보여지는 것에 대한 예시이다.

필자가 추가적으로 시각적인 비교를 위해 넣어준 옵션은 `themes.xml`에 아래와 같이 추가적인 옵션을 심어주었다.

``` xml
...
<item name="android:windowLayoutInDisplayCutoutMode" tools:ignore="NewApi">
  shortEdges <!-- default, shortEdges, never -->
</item>
<item name="android:windowBackground">
  @color/teal_200
</item>
...
```



![Cutout Short Edge Mode Portrait Screen](https://imgur.com/M1Serl0.jpg)

Portrait 모드였다면 다음과 같이 기본적으로는 Window와 내부 컨텐츠 Safe Area를 넘지는 않는다. 하지만, Full Screen Mode로 적용을 한 경우에는 아래와 같이 Window는 시스템 영역을 침범하며, Content 요소는 Cutout 요소를 침범하지 않는다.

![Cutout Short Edge Mode + Immersive Mode Portrait Screen](https://imgur.com/FwX0qEW.jpg)



Landscape 모드에서도 마찬가지이다. 

![Cutout Short Edge Mode + Landscape Screen](https://imgur.com/uJPPQn2.jpg)



Immersive 모드를 적용했을때도 콘텐츠는 여전히 Cutout 영역을 침범하지 않는다.

중요한 컨텐츠를 보여준다고 한다면, 다음과 같이 보여지는 것이 맞을지도 모른다.

![Cutout Short Edge Mode + Immersive Mode + Landscape Screen](https://imgur.com/dgE8Vzs.jpg)



만약 여기에서 컨텐츠들이 컷아웃 영역까지를 계산한 상태로 유저에게 보여져야 한다면, 어떻게 처리를 해야할까? 유저가 보기에는 에지 투 에지 화면은 문제 없이 보여지겠지만, 실제로 컨텐츠는 중심이 아니기 때문이다.



## Let's handle Display Cutout programatically

결국에는 Display Cutout은 Root Window에 포함된 Inset들이기 때문에, Window Inset 요소에서 꺼낼 수 있는 녀석들이다. 따라서, Content View에 요소를 제어하기 위해서는, 기본적으로는 `fitSystemWindows ` 옵션을 제어해주는 것이 좋다.

### How to detect Cutout bounds exist?

안드로이드에서는 기본적으로 Android API 28(Android 9) 버전부터 Cutout API에 대해 지원한다. 디바이스에서 Cutuout을 갖고있는지 확인하기 위해서는 Decor View가   Window에 붙었는지를 판단하는 것이 필요하다.

Display Cutout에 대한 정보는 `Window#getDecorView().getRootWindowInsets().getDisplayCutout()`

에서 확인이 가능하다. 따라서, 우리는 `Window#onAttachToWindow()` 함수를 오버라이딩하여, Decor View가 붙었을 때 체크해주면 된다.

```kotlin
override fun onAttachedToWindow() {
  super.onAttachedToWindow()
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    val cutout = window.decorView.rootWindowInsets.displayCutout
    if (cutout != null && cutout.boundingRects.isNotEmpty()) {
      binding.fullScreenSwitch.isVisible = true
      binding.fullScreenSwitch.setOnCheckedChangeListener { _, isChecked ->
                                                           window.handleCutoutInsetInSafeArea(isChecked)
                                                          }
    } else {
      binding.fullScreenSwitch.isVisible = false
      binding.fullScreenSwitch.setOnCheckedChangeListener(null)
    }
  }
}
```

Android 9 이상인 경우, 컷아웃 유무에 따라 화면 확장을 할 수 있는 스위치 보임 유무와 변경 시 동작에 대해 정의했다.



Cutout 사용 시 시스템 영역까지 넘어설 것인지, Safe Area안에서 동작하게 할 것인지는 isFullMode라는 파라미터를 받았는데, 여기서 처리하는 것에 따라 `android:windowLayoutInDisplayCutoutMode` 를 XML에서 정의하였다.

우리는 선택적으로 inset을 조정해야 하므로 `shortEdgges` 모드로 정의한다.

```kotlin
@RequiresApi(Build.VERSION_CODES.P)
private fun Window.handleCutoutInsetInSafeArea(isFullMode: Boolean) {
  WindowCompat.setDecorFitsSystemWindows(this, isFullMode.not())
  ...
}
```

이렇게 된다면 우리가 할 것은 다 처리했다. fitSystemWindows 옵션에 따라 꽉채운다면 false, Safe Area에 맞춘다면 true를 부여한다.



만약 여기서 Cutout 바운더리에 대해 계산하여, 추가적인 적용을 하고 싶다면, 다음과 같이 처리가 가능하다.

```kotlin
// 만약 필요하다면 Cutout에서 당신이 원하는 Inset을 추출하여 적용할 수 있음
ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
  val displayCutout = insets.displayCutout
  if (displayCutout != null && displayCutout.boundingRects.size > 0) {
    // displayCutout.getSafeInsetXXX() top, bottom, left, right
  }
}
```



`DisplayCutoutCompat` 에서는 아래와 같이 방향에 따른 Inset값을 제공한다.

```java
/** Returns the inset from the top which avoids the display cutout in pixels. */
public int getSafeInsetTop() {
  if (SDK_INT >= 28) {
    return ((DisplayCutout) mDisplayCutout).getSafeInsetTop();
  } else {
    return 0;
  }
}

/** Returns the inset from the bottom which avoids the display cutout in pixels. */
public int getSafeInsetBottom() {
  if (SDK_INT >= 28) {
    return ((DisplayCutout) mDisplayCutout).getSafeInsetBottom();
  } else {
    return 0;
  }
}

/** Returns the inset from the left which avoids the display cutout in pixels. */
public int getSafeInsetLeft() {
  if (SDK_INT >= 28) {
    return ((DisplayCutout) mDisplayCutout).getSafeInsetLeft();
  } else {
    return 0;
  }
}

/** Returns the inset from the right which avoids the display cutout in pixels. */
public int getSafeInsetRight() {
  if (SDK_INT >= 28) {
    return ((DisplayCutout) mDisplayCutout).getSafeInsetRight();
  } else {
    return 0;
  }
}
```

이렇게 처리하여 나온 결과는 아래와 같다.

| Portrait                                                     | Landscape                                                    |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![Cutout handle on Portrait Mode](https://imgur.com/pr72BmF.gif) | ![Cutout handle on Landscape Mode](https://imgur.com/8YqoVm3.gif) |

이번 시간에는 Cutout의 개념, 어떻게 Cutout을 대응해야 하는지를 알아보았다.
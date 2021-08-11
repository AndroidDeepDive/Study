# Introduce Android System UI - 1

## Preparing Full-Screen Case

안드로이드에서는 Full-Screen Mode로 총 세가지의 옵션을 제공한다.

- lean back
- Immersive
- sticky immersive

이 세 가지 방식에서는 시스템 표시줄(Sttatus Bar, Navigation Bar)가 숨겨지고, Activity에서 모든 터치 이벤트를 수신하도록 구성이 되어 있다. 각 세가지의 차이점은 사용자가 시스템 상 컴포넌트를 어떻게 보여줄지에 대한 것이다.



### Lean back Mode

**Lean back** 모드는 사용자가 동영상을 시청할 때와 같이 화면과 **거의 상호작용하지 않을 때** 사용할 수 있는 전체 화면 설정이다. 대표적으로 비디오 앱에서 볼 수 있다.

시스템 표시줄을 다시 표시하려면 사용자는 화면의 아무곳이나 탭하면된다.

설정 시 [setSystemUiVisibility()](https://developer.android.com/reference/android/view/View#setSystemUiVisibility(int)) 함수를 호출하며, `SYSTEM_UI_FLAG_FULLSCREEN`과 `SYSTEM_UI_FLAG_HIDE_NAVIGATION` 플래그를 전달하면 된다.

코드로는 다음과 같은 예제로 볼 수 있다.

```kotlin
private fun setLeanBackMode() {
  window.decorView.systemUiVisibility =
  (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 풀 스크린 모드
   or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // 하단 네비게이션 바 숨기기 플래그
}
```

![Lean back Example](https://imgur.com/nyQGkQU.gif)



### Immersive Mode

**Immersive** 모드는 사용자가 **화면과 많이 상호작용하는** 앱용으로 만들어졌다. 대표적으로 게임을 한다던지, 갤러리에서 이미지를 보거나, 책, 프레젠테이션 슬라이드 처럼 페이지가 나뉜 콘텐츠를 읽을 경우가 이에 해당한다.

풀스크린을 벗어나기 위해서는 시스템 표시줄이 숨겨진 가장 자리에서 스와이프를 해야한다.

설정 시 [setSystemUiVisibility()](https://developer.android.com/reference/android/view/View#setSystemUiVisibility(int)) 함수를 호출하며,  `SYSTEM_UI_FLAG_IMMERSIVE`, `SYSTEM_UI_FLAG_FULLSCREEN` ,`SYSTEM_UI_FLAG_HIDE_NAVIGATION` 플래그를 전달하면 된다.

코드로는 다음과 같은 에제로 볼 수 있다.

```kotlin
private fun setImmersiveMode() {
  window.decorView.systemUiVisibility =
  (View.SYSTEM_UI_FLAG_IMMERSIVE // 가장 자리 스와이프 시 발동, 다만 앱에서는 인지 못함
   or View.SYSTEM_UI_FLAG_FULLSCREEN // 풀 스크린 모드
   or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // 하단 네비게이션 바 숨기기 플래그
}
```

![Immersive Example](https://imgur.com/SSOLA2j.gif)

### Sticky Immersive Mode

**Immersive** 모드에서는 사용자가 가장자리에서 스와이프하면 그때마다 시스템 표시줄을 표시하는 역할을 시스템이 담당한다. 따라서 앱은 그런 동작이 발생했는지 인식을 하지 못한다.

사용자가 스와이프를 많이 해야하는 게임, 그림 앱 등을 사용하는 경우 화면 가장자리에서 스와이프하는 경우에는 **Sticky Immersive** 모드를 사용 설정해야한다.

쉽게 말해 Sticky와 아닌것의 가장 큰 차이는 제스처를 인식하는지이다.

**Sticky Immersive** 모드에서는 스와이프 시 시스템 표시줄이 표시되지만 반투명 상태이며, 터치 동작이 앱에 전달된다.

설정 시 [setSystemUiVisibility()](https://developer.android.com/reference/android/view/View#setSystemUiVisibility(int)) 함수를 호출하며,   `SYSTEM_UI_FLAG_IMMERSIVE_STICKY`, `SYSTEM_UI_FLAG_FULLSCREEN`, `SYSTEM_UI_FLAG_HIDE_NAVIGATION` 플래그를 전달한다.

코드로는 다음과 같은 예제로 볼 수 있다.

```kotlin
private fun setStickyImmersiveMode() {
  window.decorView.systemUiVisibility =
  (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // 가장 자리 스와이프 시 발동, 앱에서 제스처 인지
   or View.SYSTEM_UI_FLAG_FULLSCREEN // 풀 스크린 모드
   or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // 하단 네비게이션 바 숨기기 플래그
}
```



지금까지 코드로 보면, Immersive 모드, Sticky Immersive 모드에서는 추가적인 플래그를 부여했음을 알 수 있는데, 내부적인 동작에도 과연 코드상 차이가 있는지 알아보도록 하겠다.

## Analyze View#setSystemUiVisibility

```java
/**
  * Request that the visibility of the status bar or other screen/window
  * decorations be changed.
  * ...
  * @param visibility  Bitwise-or of flags {@link #SYSTEM_UI_FLAG_LOW_PROFILE},
  * {@link #SYSTEM_UI_FLAG_HIDE_NAVIGATION}, {@link #SYSTEM_UI_FLAG_FULLSCREEN},
  * {@link #SYSTEM_UI_FLAG_LAYOUT_STABLE}, {@link #SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION},
  * {@link #SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN}, {@link #SYSTEM_UI_FLAG_IMMERSIVE},
  * and {@link #SYSTEM_UI_FLAG_IMMERSIVE_STICKY}.
  * @deprecated SystemUiVisibility flags are deprecated. Use {@link WindowInsetsController}
  */

public void setSystemUiVisibility(int visibility) {
  if (visibility != mSystemUiVisibility) {
    mSystemUiVisibility = visibility;
    if (mParent != null && mAttachInfo != null && !mAttachInfo.mRecomputeGlobalAttributes) {
      mParent.recomputeViewAttributes(this);
    }
  }
}
```

![Sticky Immersive Example](https://imgur.com/fI05Rih.gif)

### Let's apply full screen mode

⚠️ 아래에서 설명하는 API는 16 ~ 29까지 사용되었던 API입니다. 30에서는 Deprecated되었습니다.

사용하려는 전체 화면 모드 종류에 상관없이 `setSystemUiVisibility()`를 호출하고 이를 `SYSTEM_UI_FLAG_HIDE_NAVIGATION` 또는 `SYSTEM_UI_FLAG_FULLSCREEN` 중 하나에 또는 두 개 모두에 전달해야 한다.

시스템 표시줄이 숨겨졌다 표시될 때 레이아웃의 크기가 조절되는 것을 방지하기 위해서는 추가로 시스템 UI 플래그를 포함 시켜주는 것이 좋다.

- `SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION` : 네비게이션 숨김 처리 시 `SYSTEM_UI_FLAG_HIDE_NAVIGATION` 플래그와 같이 사용된다. 네비게이션 숨김 처리 시 모든 뷰는 위 플래그에 따라 요청되어 펼쳐지고, Window가 펼쳐짐과 동시에 뷰도 재 조정이 된다.
-  `SYSTEM_UI_FLAG_LAYOUT_STABLE` : setDecorFitsSystemWindows(Rect) 함수를 통해   안정된 뷰에 insets를 부여한다. 상태 표시줄과 탐색 메뉴를 숨기거나 표시할 때 레이아웃 크기가 변동되지 않도록 하는 플래그이다. 기존에 우리가 Window에서 Safe Area(상태표시줄, 네비게이션 바)영역을 넘어서도록 해주는 옵션이기도하다.



## Full Screen Mode Android 11 대응

![FULL SCREEN Flag Deprecated](https://imgur.com/RQC9kPC.jpg)

Android 11(SDK Version 30)의 경우에는 기존에 제공하던 `View#systemUiVisibility(int)`가 Deprecated 되었다.

안드로이드 11에서 왜 `View#systemUiVisibility(int)`가 Deprecated되었는지는 기존에 Android 10부터 도입 되었던 Edge-to-Edge 개념에 대해서 알아 볼 필요가 있다.

### Edge to Edge

![Edge to Edge](https://imgur.com/wa6ViMq.gif)

쉽게 정리하면, 에지 투 에지(Edge to Edge)개념은 앱이 **시스템 표시 줄 뒤에 그려질 수 있음**을 의미한다. 다음과 같이 바뀐 이유는 사용자에게 몰입감 있는 경험을 제공하기 위한 재설계를 했기 때문이다.

Deprecate된 `setSystemUiVisibility` 함수를 참고하면, `WindowInsetsController`라는 인터페이스를 참고하라는 것을 볼 수 있다. `WindowInsetsController` 는 어떻게 구성이 되어있는지 확인하자.

```java 
/**
 * Inset 생성 시 Window를 제어하기 위한 인터페이스
 */
public interface WindowInsetsController {

  /**
   * 어두운 백그라운드, 밝은 포그라운드 색상을 가진 불투명한 상태표시줄을 만듦
   * @hide
   */
  int APPEARANCE_OPAQUE_STATUS_BARS = 1;

  /**
   * 어두운 백그라운드, 밝은 포그라운드 색상을 가진 불투명한 네비게이션 바를 만듦
   * @hide
   */
  int APPEARANCE_OPAQUE_NAVIGATION_BARS = 1 << 1;

  /**
   * 상태바 레이아웃이 변경됨 없이 덜 두드러지게 상태표시줄의 아이템을 적용함
   * @hide
   */
  int APPEARANCE_LOW_PROFILE_BARS = 1 << 2;

  /**
   * 밝은 상태표시줄로 변경하여 상태바 내 아이템들의 시연성을 뚜렷하게 함
   */
  int APPEARANCE_LIGHT_STATUS_BARS = 1 << 3;

  /**
   * 밝은 네비게이션 바로 변경하여 상태바 내 아이템들의 시연성을 뚜렷하게 함
   */
  int APPEARANCE_LIGHT_NAVIGATION_BARS = 1 << 4;

  ...

}
```

기존에 존재하던 플래그와는 다른 별도의 플래그를 위와 같이 제공하는 것을 알 수 있다. 필요시에 다음 플래그를 적용하면 되고, 이와 별도로 Window의 Inset을 적용하는 경우 `Window#setDecorFitsSystemWindows` 함수를 사용한다.

여러 SDK 버전을 커버하는 WindowCompat 클래스에는 다음과 같이 분기처리가 되어 있는 것을 볼 수 있다.

```java
public final class WindowCompat {

    ...
    public static void setDecorFitsSystemWindows(@NonNull Window window, final boolean decorFitsSystemWindows) {
      if (Build.VERSION.SDK_INT >= 30) {
        Impl30.setDecorFitsSystemWindows(window, decorFitsSystemWindows);
      } else if (Build.VERSION.SDK_INT >= 16) {
        Impl16.setDecorFitsSystemWindows(window, decorFitsSystemWindows);
      }
    }  
    ...

}
```

Android SDK 버전 30 이상인 경우 Impl30의 함수를 이용하도록 분기처리가 되어있다. 각 버전에 대한 로직 차이는 다음과 같이 나뉘어진다.

**API 16 ~ 29 대응**

```java 
@RequiresApi(16)
private static class Impl16 {
  static void setDecorFitsSystemWindows(@NonNull Window window,
                                        final boolean decorFitsSystemWindows) {
    final int decorFitsFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    final View decorView = window.getDecorView();
    final int sysUiVis = decorView.getSystemUiVisibility();
    decorView.setSystemUiVisibility(decorFitsSystemWindows
                                    ? sysUiVis & ~decorFitsFlags
                                    : sysUiVis | decorFitsFlags);
  }
}
```

Window 객체에서 DecorView에 접근하여 ` setSystemUiVisibility(int)`  함수를 호출하는 것을 알 수 있다.



**API 30 이상 대응**

```java
@RequiresApi(30)
private static class Impl30 {
  static void setDecorFitsSystemWindows(@NonNull Window window,
                                        final boolean decorFitsSystemWindows) {
    window.setDecorFitsSystemWindows(decorFitsSystemWindows);
  }

  static WindowInsetsControllerCompat getInsetsController(@NonNull Window window) {
    WindowInsetsController insetsController = window.getInsetsController();
    if (insetsController != null) {
      return WindowInsetsControllerCompat.toWindowInsetsControllerCompat(
        insetsController);
    }
    return null;
  }
}
```

Window 객체의 `setDecorFitsSystemWindows(boolean)` 함수를 호출한다.

xml에서는 `android:fitsSystemWindows="boolean"` 옵션으로 제공한다.

파라미터인 `decorFitsSystemWindows`  값이 true라면, 내부적으로 SYSTEM UI LAYOUT FLAG를 체크하고, 하위 엘리먼트의 옵션을 체크하여 화면을 구성하게 된다.

반대로 false로 설정이 되어 있다면 기존에 정해져 있던 FLAG들을 무시하고, Window Inset을 통해 화면을 직접 제어한다.



따라서, 실제 코드에서 사용시에는 다음과 같이 케이스에 따라 나눌 수 있을 것이다.

```kotlin
// Tell the window that we (the app) want to handle/fit any system
// windows (and not the decor)
window.setDecorFitsSystemWindows(false)

// OR you can use WindowCompat from AndroidX v1.5.0-alpha02
WindowCompat.setDecorFitsSystemWindows(window, false)
```


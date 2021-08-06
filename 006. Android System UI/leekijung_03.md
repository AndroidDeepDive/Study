# Introduce Android System UI - 3

이번시간에는 우리가 흔히 말하는 노치영역이란 무엇인지, 이것을 안드로이드 디바이스에서는 어떻게 대응을 해줘야 하는지 소개한다.

## Instroduce Display Cutout

![Display Cutout Sample](https://imgur.com/lyMYR34.jpg)



디스플레이 컷아웃(Display Cutout)은 기기 전면에 중요한 센서들을 위한 공산을 제공하는 동시, 에지 투 에지(Edge to Edge) 경험을 가능하게 한다. Android 9(API 28) 이상을 실행하는 기기라면 공식적으로 디스플레이 컷아웃을 지원한다.



### 디스플레이 컷아웃이 있는 경우 기기에서 예상되는 사항

Android 9이상을 실행하는 기기에서는 일관성, 앱 호환성을 보장하기 위해 다음과 같은 컷아웃 동작을 보장해야한다.

- 단일 가장자리에 컷아웃을 최대 1개 포함할 수 있습니다.
- 기기에 컷아웃이 3개 이상 있을 수 없습니다.
- 기기 양쪽의 긴 가장자리에는 컷아웃이 있을 수 없습니다.
- 특수 플래그를 설정하지 않은 세로 방향에서는 상태 표시줄이 적어도 컷아웃 높이까지 확장되어야 합니다.
- 기본적으로 전체 화면 또는 가로 방향에서는 전체 컷아웃 영역이 레터박스 처리되어야 합니다.



### 앱의 컷아웃 영역 처리 방법 소개

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




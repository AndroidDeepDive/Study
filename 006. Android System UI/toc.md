## Android System UI TOC


![](./img/auSbY.png)

#### Code Analysis
https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/packages/SystemUI

### Part 1 - Status Bar
System bar positioned along the screen and that serves as a navigation bar. The Status Bar also provides functionality to support
- Connectivity icons. Including Bluetooth, Wi-Fi, and Hotspot/Mobile connection.
- Pulldown notification panel. For example, swiping down from the top of the screen.
- Heads up notifications (HUN).

#### Features
- Color
- Height
- Transparent


#### References
- https://developer.android.com/training/system-ui/dim
- https://developer.android.com/training/system-ui/status

### Part 2 - Navigation Bar
System bar that can can be positioned on the left, bottom, or right of the screen and that can include facet buttons for navigation to different apps, toggle the notification panel, and provide vehicle controls (such as HVAC). This differs from the Android System UI implementation, which provides the Back, Home, and app-stack buttons.

#### Features
- Color
- Transparent
- Icon Color

#### References
- https://developer.android.com/training/system-ui/navigation


### Part 3 - Modal Case (like BottomSheetDialog)

### Part 4 - Full Screen Case
- 전체화면 적용시 System ui 적용의 어려움
- 전체화면 종료 후 돌아오는 것
- cutout : https://developer.android.com/guide/topics/display-cutout
- Navigation 영역이 hardware냐 soft냐에 따른 동작

### Part 5 - Theme : Day and Night
- Night mode 하나로도 굉장히 deep한 주제
  - Theme의 상속에 대한 분석 및 관리

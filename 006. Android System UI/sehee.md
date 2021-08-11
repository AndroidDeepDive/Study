# Window
`Window` 란 화면의 가장 상위요소이다. 화면 구성에 존재하는 각각의 Frame안에 존재하는 영역, 또는 그 전체로 볼 수 있다. 하나의 화면에 여러개의 Window가 존재할 수 있으며, Window들은 WindowManager가 관리하게 된다.

```
Application --->
  Activity --->
    Window Manager --->
      Window --->
        Surface ---> 
          Canvas --->
            View Root ---> 
              View Group --->
                View ---> 
                  Bitmap/Open GL panel ---> 
                    Current Surface Buffer ---> 
                      Surface Flinger --->
                        Screen
                        
```

계층구조는 위와같이 되어있고, Activity와 Dialog는 내부적으로 window를 가지고 있다. Window는 하나의 Surface를 가지고 있고, 단일 계층구조이며 Surface는 ViewGroup을 포함하고 있다.
<br>

# Window Fitting

|                        Navigation Bar                        |                          Status Bar                          |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://images.velog.io/images/jshme/post/ce128b19-5dd2-44f5-9295-08d217bd94f3/image.png) | ![](https://images.velog.io/images/jshme/post/5e9fd7f1-8d51-4722-9c1b-329e9745afe7/image.png) |

전체화면으로 구성할 때 위 사진처럼 System Bar와 View가 겹치는 이슈가 존재할 수 있다. 이 때 우리는 System Bar의 높이를 알아내서, 뷰가 겹쳐지지 않도록 구성해야 한다. 창틀에 유리를 딱 맞게 끼우는 것처럼, 우리가 원하는 레이아웃을 구성하기 위해 window와 구성한 뷰를 적절히 설정하는 것이다.
<br>

## 1. Jelly Bean 이전 API
window는 System bar 사이에 위치한다. 그래서, 위 사진처럼 System bar 뒷 쪽에 뷰를 그릴 수가 없는데, 이 때 `setSystemUiVisibility()` 라는 함수를 이용해 System Ui Layout 과 Visiblity 를 핸들링 할 수 있다.

``` kotlin
// 이 api를 통해 앱의 view hierarchy 상관없이 호출하면 
// window 까지 전파될 수 있어, Navigation Bar를 제외한 
// fullscreen 을 구성할 수 있다.
// `Window Transform Flags`
window.decorView.systemUiVisibility =
View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 
```
<br>

## 2. KitKat
반 투명 시스템바가 기본적으로 들어가있다. style을 아래와같이 적용해주면 내부적으로 `Window Transform Flags` 가 사용되고 있어, 코드를 추가할 필요없이 전체화면을 구성할 수 있다. 대신 이 옵션을 추가해주었을 때 위아래 가장자리 영역에 검정색의 Gradient가 생긴다는 것을 알아두자.

``` xml
<item name="android:windowTranslucentStatus">true</item>
<item name="android:windowTranslucentNavigation">true</item>
```
![](https://images.velog.io/images/jshme/post/1d376c15-1578-49b8-8ac5-c1df641d80b7/image.png)
<br>

## 3. Lollipop 
`android:windowDrawsSystemBarBackgrounds` 라는 옵션이 추가되었는데, System Bar의 Background가 Window 안에 위치하는 것을 설정하는 Attribute이다. Lollipop부터 이 옵션이 true가 default이기 때문에 Layout Inspector로 확인해보면 DecorView 아래에 위치`(navigationBarBackground, statusBarBackground)` 하고 있다는 것을 볼 수 있을 것이다. 

![](https://images.velog.io/images/jshme/post/98bc19d8-8b27-4c93-95d8-a35ec9771adb/image.png)

Window안에 위치하고 있으므로, window.statusBarColor, window.navigationBarColor를 통해 SystemBar의 색상을 변경할 수 있다.

Lollipop도 반투명의 시스템바를 지원하기 때문에 Kitkat과 동일하게 xml을 구성하면된다. 하지만 `windowTranslucentStatus`, `windowTranslucentNavigation` 을 true로 설정했다면, 해당 옵션들의 우선순위가 높기 때문에 SystemBar 색상을 변경하는 코드를 추가해도 무시되어 반투명 시스템바가 나올 수 있다.

``` xml
<item name="android:windowTranslucentStatus">true</item>
<item name="android:windowTranslucentNavigation">true</item>
```

화면에 보여지는 모습은 KitKat과는 달리 SystemBar가 전체적으로 회색 빛을 띄고있는 것을 볼 수 있다.

![](https://images.velog.io/images/jshme/post/d1762f42-bdf6-4246-b5c2-b6be49e7d683/image.png)
<br>


# fitSystemWindows
전체화면을 만들기 위해 `android:fitSystemWindows` 를 설정하는 방법도 있다. 


``` kotlin
// 2번 방식
// systemUiVisibility 없이 
// fitSystemWindows 만 설정한 경우
android:fitSystemWindows="true"
```

fitSystemWindows를 true로 주면 Window 영역을 확장시킬 수 있다. SystemBar 뒤쪽 영역을 그리기 때문에 뷰가 잘릴 수 있을 수 있는데, Navigation Bar & Status Bar Hight 만큼 `Padding` 을 부여해 Safe Area 안에 뷰를 그릴 수 있게 할 수 있다.

``` kotlin
// 1번 방식
android:fitSystemWindows="true"

window.decorView.systemUiVisibility =
View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or 
View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
```

하지만 DrawerLayout, CoordinatorLayout, AppBarLayout, CollapsingToolBarLayout같은 경우에는 fitSystemWindows을 true로 적용해주었을 때 내부적으로 `systemUiVisibility` 도 자동으로 설정되기 때문에 SystemBar의 뒷편으로도 UI를 그릴 수 있게 된다.

아래의 이미지는 전부 **전체화면**이 적용되었지만, `systemUiVisibility`에 따른 Safe Area의 차이를 보여준다.

|            1번 방식 <br>(Safe Area가 적용된 모습)            |        2번 방식 <br>(Safe Area가 적용되지 않은 모습)         |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://images.velog.io/images/jshme/post/8e685d1a-f628-49a9-a424-c2d60a3a3d0c/Screenshot_1628010857.png) | ![](https://images.velog.io/images/jshme/post/66bb2c9a-15e7-4139-9c1e-e10684ab773c/Screenshot_1628010970.png) |
<br>


# WindowInsets 
안드로이드 시스템에서 System UI가 어디에 위치해있는지, 혹은 나중에 보여지게 되는지 안내해주는 역할을 한다. 이 `WindowInsets` 을 통해 상하좌우의 Inset을 얻을 수 있어 해당 디바이스의 Status Bar와 Navigation Bar의 Height를 쉽게 얻을 수 있다.

https://www.youtube.com/watch?v=q6ZC4E4lAM8

'# WindowInsets 
Safe Area영역을 지키면서 화면이 왼쪽처럼 잘리지 않게 하려면 `WindowInsets` 옵션을 사용하면 된다. WindowInsets은 안드로이드 시스템에서 System UI가 어디에 위치해있는지, 혹은 나중에 보여지게 되는지 안내해주는 역할을 한다. 해당 옵션을 통해 상하좌우의 Inset을 얻을 수 있어 해당 디바이스의 Status Bar와 Navigation Bar의 Height를 쉽게 얻을 수 있게 된다.

- windowInset.stableInsetLeft
- windowInset.stableInsetRight
- windowInset.stableInsetTop : 해당 옵션을 통해 Status Bar의 Size를 얻을 수 있음.
- windowInset.stableInsetBottom : 해당 옵션을 통해 Navigation Bar의 Size를 얻을 수 있음.

<br>


# WindowInsetsControllerCompat
하지만 System Bar를 핸들링 할 수 있는 `systemUiVisibility` 옵션도 곧 Deprecated 될 예정이다. 그럼 무엇을 사용해야 할까? 

![](https://images.velog.io/images/jshme/post/e77c0a60-f419-4019-b3a5-31ad90a92cfb/image.png)


👉 `WindowInsetsControllerCompat` 혹은 `WindowInsetsController` 를 사용하면 된다. 
안드로이드 문서를 살펴보면, SDK 29 이하는 WindowInsetsControllerCompat, 30 이상부터는 WindowInsetsController를 권장하고 있다.

WindowInsetsControllerCompat은 `androidx.core` library에 속해있는 클래스이며, 기존 systemUiVisibility 보다 간소화된 옵션을 제공하고 있다.

- `BEHAVIOR_SHOW_BARS_BY_TOUCH`: 사용자가 화면을 터치할 때 System Bar가 등장함.
- `BEHAVIOR_SHOW_BARS_BY_SWIPE`: 사용자가 화면 내 System Bar 근처에서 스와이프 할 때 등장함.
- `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`: 사용자가 화면 내 System Bar 근처에서 스와이프할 때 등장함. (BEHAVIOR_SHOW_BARS_BY_SWIPE 와 차이점은 System Bar를 투명으로 제공하냐의 유무)
<br>

WindowInsetsControllerCompat을 사용하면 모든 SDK 버전에 대응할 수 있게 되는데, 이는 해당 클래스 내부에 모든 SDK 버전에 대응할 수 있도록 분기처리가 이루어지고 있기 때문이다.

``` java
    public WindowInsetsControllerCompat(@NonNull Window window, @NonNull View view) {
        if (SDK_INT >= 30) {
            mImpl = new Impl30(window, this);
        } else if (SDK_INT >= 26) {
            mImpl = new Impl26(window, view);
        } else if (SDK_INT >= 23) {
            mImpl = new Impl23(window, view);
        } else if (SDK_INT >= 20) {
            mImpl = new Impl20(window, view);
        } else {
            mImpl = new Impl();
        }
    }
```

WindowInsetsController 는 WindowInsetsControllerCompat과 달리 interface로 구현이 되어있는데, WindowInsetsControllerCompat의 속성을 대부분 담고 있으며, SDK 30 부터 사용을 권장하고 있다. 

WindowInsetsController의 속성에는 APPEARANCE_OPAQUE_STATUS_BARS, APPEARANCE_LOW_PROFILE_BARS 등 WindowInsetㄴControllerCompat의 기본 속성외에 SystemBar의 세부 속성을 핸들링 할 수 있는 요소가 들어있다. 

systemUiVisibility 가 deprecated 되면서 SDK 30 부터는 WindowInsetsController 사용을 권장하고 있기 때문에, 대부분 아래처럼 Build Version에 따른 분기처리로 SystemBar를 핸들링하고 있다.

``` kotlin
if (Build.version.sdk_int >= build.version_codes.R) { /** use WindowInsetsControllr */ }
else { /** use SystemUisivility */ }
```
하지만 단순히 SystemBar Visiblity만을 핸들링하고 싶은 경우라면 분기처리 없이 모든 SDK를 핸들링 할 수 있는 `WindowInsetsControllerCompat` 옵션을 사용해도 괜찮지 않을까 싶다. 

<br>

#### Reference
https://www.youtube.com/watch?v=q6ZC4E4lAM8'
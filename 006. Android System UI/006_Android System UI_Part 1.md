# Android System UI Part 1 - What is System UI?

Android에서 쓰이는 System UI의 종류와 호출 방식, 그리고 실무에서 어떤 걸 고려해야하는 지 알아보도록 하자.

## System UI란 무엇인가?

### Status Bar

**Status Bar** 는 알림이 표시되거나, 기기의 상태 전달 및 기기 탐색을 위한 영역이다.

일반 적으로 애플리케이션이 실행되더라도 유지되지만, 동영상과 같은 몰입형 컨텐츠의 경우 의도적으로 숨기기도 한다.

### Navigation Bar

## System UI 제어하기

### Android 4.0 and Below

Android 4.0 이하의 버전에서는 **WindowManager** 를 통해 Flag 값들을 주입함으로서 제어할 수 있다.

이때 주입 방법은 Manifest에서 해당 Activity의 theme를 지정해주거나, 코드 레벨에서 처리하는 방법이 있는데,

늘 같은 System UI 상태를 노출하는 것이 목적이라면 theme로 지정해두는 것이 권장된다.

theme로 지정하는 방법은 아래와 같다.

```xml
<application
    ...
    android:theme="@android:style/Theme.Holo.NoActionBar.Fullscreen" >
    ...
</application>
```

이처럼 theme를 활용하면 오류 가능성을 줄이고, 유지 보수성을 증대시킬 수 있으며 Activity가 실행될 때부터 적용되어 있으므로, 좀 더 부드러운 화면 전환이 가능하다.

반면 코드로 제어를 한다면 아래와 같이 flag를 주입한다.

```kotlin
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
      
        setContentView(R.layout.activity_main)
    }
    ...
}
```

위의 코드에서 `setContentView`를 호출하기전에 즉, layout을 inflating하기 전에 flag를 설정해주어야 하는 것을 확인할 수 있다.

> **참고** [Android Developers WindowManager](https://developer.android.com/reference/android/view/WindowManager)

### Over Android 4.1

Androdi 4.1 이상부터는 Status Bar를 숨기기 위해 `setSystemUiVisibility()`를 사용한다.

해당 인터페이스로 접근하는 설정의 경우 `Window` 객체와 연계되어 있느넫, 이를 통해 `WindowManager`보다 좀 더 세밀한 제어가 가능하다.

코드로 살펴보자.

```kotlin
// Hide the status bar.
window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
actionBar?.hide()
```

위와 같이 Activity에 설정하여 status bar를 숨기더라도, 해당 Activity를 벗어나면 관련 설정들은 모두 초기화된다.

만약 위의 코드를 `onCreate()`에 적용한다면, 최초 접근시엔 status bar가 숨겨지겠지만, 홈 버튼을 눌러 홈 화면으로 돌아가서 설정을 초기화한 후

다시 애플리케이션으로 돌아오면 `onCreate()`가 호출되지않아 개발자의 의도와 다른 화면이 보일 수 있다.

따라서 `onResume()`이나, `onWindowFocusChanged()`에서 System UI를 제어하는 것이 권장된다.

애플리케이션에서 System UI의 상태가 변하는 걸 캐치하기 위해선 아래의 Listener를 등록해서 사용한다.

```kotlin
window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
    // Note that system bars will only be "visible" if none of the
    // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
    if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
        // TODO: The system bars are visible. Make any desired
        // adjustments to your UI, such as showing the action bar or
        // other navigational controls.
    } else {
        // TODO: The system bars are NOT visible. Make any desired
        // adjustments to your UI, such as hiding the action bar or
        // other navigational controls.
    }
}
```

이를 통해 System UI의 변경이 발생하는 경우에 대응하는 코드를 작성하여 싱크를 맞춘다.

또한 Android 4.1부터는 컨텐츠를 Status Bar 영역까지 표시할 수 있는데, 이는 `SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN` 옵션을 부여하는 방식으로 구현할 수 있다.

> **참고** [Android Developers View#setSystemUiVisibility](https://developer.android.com/reference/android/view/View#setSystemUiVisibility(int))

> **참고** Android Api 30부터는 WindowInsets 이라는 방식을 사용한다. Part 3를 참고바란다.

- Status Bar / Navigation Bar 소개
- PDK 딥 다이브 - System UI 리스트 
- System UI 실행 프로세스 (Android 11 기준)
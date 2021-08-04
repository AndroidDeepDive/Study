# Android System UI - 1 

## Status Bar 

Status Bar 는 알림 표시, 기기 상태 전달, 기기 탐색을 위한 전용 화면 영역이다. 일반적으로는 앱이 실행되면서 동시에 표시되지만, 동영상 앱 같은 몰입형 컨텐츠 감상을 위해서는 종종 Status Bar를 흐리게 보여주거나, 일시적으로 숨길 수 있다.



## Control System UI

Status bar 와 Navigation Bar 는 특정 상황에서 hide 하거나 dim 처리를 할 수 있다. 어떻게 할 수 있는지 자세히 알아보자. 

### Android 4.0 이하

Android 4.0 보다 낮은 버젼에서는 WindowManager(링크 첨부하기) Flags를 설정함으로서 가능하다. 이를 코드로 직접 할 수도 있고, Manifest 파일에서 해당 액티비티의 theme을 지정해줌으로서 가능하기도 하다. 늘 같은 System UI 상태를 보여주고자 한다면 theme으로 설정하는게 나을것이다. 

```xml
<application
    ...
    android:theme="@android:style/Theme.Holo.NoActionBar.Fullscreen" >
    ...
</application>
```

이렇게 theme으로 설정하면 코드로 설정하는것보다 더 적은 error-prone 및 유지하기가 쉽고, 액티비티가 처음에 luanch 되면서부터 theme이 적용이 되므로 코드로 설정하는것 보다 더 부드러운 UI 전환이 가능하다. 

반면 코드로 제어를 한다면 WindowManager 플래그로 설정할 수 있다. 

```kotlin
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
      
        setContentView(R.layout.activity_main)
    }
    ...
}
```

코드를 보면 setContentView - 즉 레이아웃을 인플레이팅 하기 전에 WindowManager에 있는 flag들을 설정해 주는 걸 알 수 있다.

### Android 4.1 이상

Androdi 4.1 이상부터는 Status Bar를 숨기기 위해 setSystemUiVisibility(링크)를 사용한다. 이 설정은 Window와 연계되어 있는데, setSystemUiVisibility()를 사용하면 WindowManager를 사용하는 것 보다 더 세부적으로 제어가 가능한다.

```kotlin
// Hide the status bar.
window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
actionBar?.hide()
```

액티비티에서 이렇게 설정해준다 하더라도, 액티비티를 벗어나면 SystemUI 관련 설정들은 Reset이 된다. 앱에서 SystemUI Change가 되는 상황을 listener 등록할 수 있는데, 아래 코드로 가능하다. 

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

이렇게 리스너를 등록해놓으면 SystemUI 가 변경될 때 마다 적절한 처리를 할 수 있어서 sync를 맞출 수 있다.

다시 코드로서 SystemUI를 제어하는 상황으로 돌아가보면, Activity의 onCreate()에서 Status Bar를 하이딩 하더라도, 홈 버튼을 눌러 홈 화면으로 돌아온다면 Status Bar는 다시 나타나게 된다. 이 상태에서 앱을 다시 실행해도 onCreate()가 불리지 않기 때문에, Status Bar는 그대로 나타나게 되므로 이런 경우엔 onResume()이나 onWindowFocusChanged() 에서 SystemUI를 제어해주는게 좋다. 

또한 Android 4.1 이상부터는 앱의 컨텐츠가 아예 Status Bar 영역까지 꽉 차도록 그릴 수 있는데(이러면 SystemUI 가 변경되더라도 컨텐츠의 크기는 변하지 않는다), [SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN](https://developer.android.com/reference/android/view/View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) 옵션을 줌으로서 가능하다. 

### Window 

Android 4.1 이상 버젼에서 Status Bar를 제어하는 코드를 보면, Window, DecorView 라는 객체로 제어하는게 보인다. 이들이 무엇인지 간략하게 알아보자.

Window 클래스는 Android.View 패키지에 들어있는, 화면에 그림을 그릴 수 있는 일종의 사각 도화지이다. (물론 그림 자체는 View를 통해서 그린다)

[공식문서][https://developer.android.com/reference/android/view/Window]를 읽어보면 Window는 Top-Level 윈도우가 어떻게 보여지고 행동하는지에 대한 Abstract base class라고 나와있다. Window 를 구현하는 클래스는 Top-Level 뷰로서 WindowManager에 추가되어서 사용될 것이다. Window 클래스는 background, title area, default key processing 같은 표준 UI 정책을 규정한다. 

### DecorView

<img src="/Users/ukhyuns/Library/Application Support/typora-user-images/image-20210728200136231.png" alt="image-20210728200136231" style="zoom:50%;" />

[DecorView][https://developer.android.com/reference/android/view/Window#getDecorView()]는 Top-Level 윈도우의 Decor View를 뜻하며, 이는 표준 윈도우의 프레임/데코레이션 및 클라이언트의 컨텐츠를포함하고 있다. 

Activity 는 사용자와 상호작용 하기 위한 단위 중 하나이다. 이를 위해 Window가 필요하고, Window를 통해 UI Element들을 배치하여 사용자에게 보여지게 된다. 이 때 PhoneWindow는 Activity에서 제공하는 Window로서 View 계층 단계 이전의 루트로 볼 수 있다.

먼저 Activity가 Launch 되는 흐름을 간단하게 살펴보자. Launch 이벤트가 ApplicationThread에 스케쥴링 되고, Activity Thread의 Handler에서 LAUNCH_ACITIVTY라는 메세지를 처리하게 된다. 이후, Intrumentation 가 Activity를 인스턴스 화 한 뒤에 Activity 구성을 위한 기타 정보들을 attach() 메소드를 통해 Activity에 전달하게 되는데, 이 시점에서 PhoneWindow가 생성된다. PhoneWindow는 Content View의 Root View가 될 DecorView를 생성한다. FrameLayout을 상속받아 구현 된 DecorView는 Window로부터 터치 이벤트를 View 관점에서 최초로 받게 되고, 하위 View 들에게 전달하는 등의 Root View 역할을 수행한다. DecorView는 installDecor()를 통해 생성된다.

결론은 Activity가 생성되면서 PhoneWindow가 생성이 되고, PhoneWindow는 setContentView의 installDeco()함수를 통해 RootView인 DecorView를 만든다. 

```java
@Override
public void setContentView(int layoutResID) {
    // Note: FEATURE_CONTENT_TRANSITIONS may be set in the process of installing the window
    // decor, when theme attributes and the like are crystalized. Do not check the feature
    // before this happens.
    if (mContentParent == null) {
        installDecor(); // 여기서 DecorView를 생성
    } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        mContentParent.removeAllViews();
    }

    if (hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        final Scene newScene = Scene.getSceneForLayout(mContentParent, layoutResID,
                getContext());
        transitionTo(newScene);
    } else {
        mLayoutInflater.inflate(layoutResID, mContentParent);
    }
    mContentParent.requestApplyInsets();
    final Callback cb = getCallback();
    if (cb != null && !isDestroyed()) {
        cb.onContentChanged();
    }
    mContentParentExplicitlySet = true;
}
```

2부에서는 Theme과 Style이란 무엇이고 Style.xml에 정의된 Android Theme를 어떻게 가져오는지를 살펴보기 위해 installDeco() 함수를 살펴보도록 하자.

## Reference

- https://developer.android.com/training/system-ui/status

- DecorView 소스 https://android.googlesource.com/platform/frameworks/base.git/+/master/core/java/com/android/internal/policy/DecorView.java
- Window Backgrounds & UI Speed https://aroundck.tistory.com/m/676
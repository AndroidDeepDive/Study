## Android System UI

## Android System UI 란?

System UI 는 3rd-party 앱과 독립적으로 커스텀된 디스플레이를 가능하게 하는 안드로이드 어플리케이션이다. 간단하게 말해 앱이 아닌 안드로이드에서 사용자가 볼 수 있는 모든것이 System UI 이다. 
시스템 바는 알림을 표시하고 기기의 상태를 알리거나 앱 사이를 이동하는 역할을 한다. 시스템 바는 보통 앱과 언제나 함께 표시된다. 앱이 영화나 그림처럼 몰입형 컨텐츠를 제공하는 경우는 방해받지 않도록 시스템 바를 숨겨서 전체 화면으로 컨텐츠를 즐기게 할 수도 있다.
 시스템 바에는 상태표시바(Status bar) 와 네비게이션바(Navigation Bar) 가 있다.

## 1. 상태표시바 (Status Bar)

사용자 확인이 필요한 알림을 왼쪽에, 시간이나 배터리 양, 네트워크 신호 강도 등을 오른쪽에 표시한다. 상태표시바를 아래로 스와이프하면 세부 알림 사항이 나온다. 상태표시바를 숨기면 콘텐츠가 더 많은 표시 공간을 사용할 수 있으므로 보다 몰입도 높은 사용자 경험을 제공할 수 있다. 주의해야할 것은 상태표시바를 숨기면 액션바 도 같이 숨김처리된다.


상태표시바는 매니페스트에서 설정하는 방법과 프로그래밍 설정을 통해 숨김 처리할 수 있으며, OS 버전에 따라 방법이 조금씩 차이가 있다.

### 매니페스트 설정 방법

    <application
      ...
      android:theme="@android:style/Theme.Holo.NoActionBar.Fullscreen" >
      ...
    </application>



### 프로그래밍 설정 방법

* **Android 4.0 (API 레벨 14) 이하에서 설정**

WindowManager 플래그를 설정하여 상태표시바를 숨길수 있으며, 프로그래밍 방식으로 코드를 직접 작성하는 방법과 앱의 매니페스트 파일에서 액티비티 테마를 설정하는 방법이 있다. 앱에서 항상 상태표시바를 숨길 경우, 매니페스트 파일에서 액티비티의 테마를 설정하는것이 좀 더 선호되는 설정 방법이다.엄밀하게 말하면, 원하는 경우 프로그래밍 방법으로 테마를 재정의 할 수 있다.

* **Android 4.1 (API 레벨 15) 이상에서 설정**

systemUiVisibility 를 사용하여 상태표시바를 숨길 수 있다. systemUiVisibility 는 개별 뷰 레벨에서 UI 플래그를 설정한다. 이런 설정은 윈도우 레벨로 합쳐진다. UI플래그를 systemUiVisibility 를 사용해서 설정하면 WindowManager 플래그를 이용하는 것보다 상태표시바를 좀 더 세부적으로 설정할 수 있다. SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 를 사용하여 상태표시줄 뒤에 콘텐츠를 표시하여 상태표시줄이 숨겨지거나 나타날 때 콘텐츠가 리사이즈 되지 않도록 할 수 있다. 앱이 안정적인 레이아웃을 유지하는데 도움이 되도록 SYSTEM_UI_FLAG_LAYOUT_STABLE 사용이 필요할 수도 있다.

* **Android 11 (API 레벨 30) 이상에서 설정**

WindowInsetsControllerCompat 를 사용해서 상태표시바를 숨길수 있다. systemBarsBehavior 설정을 통해 시스템 UI 가 노출 액션을 설정하거나 hide 또는 show 할 시스템 UI 에 대한 타겟을 지정할 수 있다.

    class MainActivity : Activity() {
      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
          Build.VERSION.SDK_INT < 16 -> {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
          }
          Build.VERSION.SDK_INT in 16..29 -> {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    //        actionBar?.hide()
            // ActionBar 지원하지 않을수도 있으니 supportActionBar를 사용한다.
            supportActionBar?.hide()
          }
          Build.VERSION.SDK_INT >= 30 -> {
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            // 시스템 UI 동작을 설정한다
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
            // 숨길 시스템 UI 타겟을 설정한다
            insetsController.hide(WindowInsetsCompat.Type.statusBars())
    //        insetsController.show(WindowInsetsCompat.Type.statusBars())
    //        actionBar?.hide()
            // ActionBar 지원하지 않을수도 있으니 supportActionBar를 사용한다.
            supportActionBar?.hide()
          }
        }
        setContentView(R.layout.activity_main)
      }
      ...
    }

Android 11 (API 레벨 30) 에서 사용 가능한 WindowInsetsControllerCompat 는 SDK 버전에 따라 시스템 UI 에 대한 처리가 구현되어 있다. Impl 클래스에 가장 기본적인것이 UI 처리에 대한 구현되어 있으며, API 버전에 따라 그 이전에 구현된 클래스를 상속 받아 새로 추가된 UI 처리에 대해 구현되어 있다.

    @RequiresApi(30)
    private WindowInsetsControllerCompat(@NonNull WindowInsetsController insetsController) {
        if (SDK_INT >= 30) {
            mImpl = new Impl30(insetsController, this);
        } else {
            mImpl = new Impl();
        }
    }
    
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


​    

    private static class Impl {
        Impl() {
            //privatex
        }
    
        void show(int types) {
        }
    
        void hide(int types) {
        }
    
        void controlWindowInsetsAnimation(int types, long durationMillis,
                Interpolator interpolator, CancellationSignal cancellationSignal,
                WindowInsetsAnimationControlListenerCompat listener) {
        }
    
        void setSystemBarsBehavior(int behavior) {
        }
    
        int getSystemBarsBehavior() {
            return 0;
        }
    
        public boolean isAppearanceLightStatusBars() {
            return false;
        }
    
        public void setAppearanceLightStatusBars(boolean isLight) {
        }
    
        public boolean isAppearanceLightNavigationBars() {
            return false;
        }
    
        public void setAppearanceLightNavigationBars(boolean isLight) {
        }
    
        void addOnControllableInsetsChangedListener(
                WindowInsetsControllerCompat.OnControllableInsetsChangedListener listener) {
        }
    
        void removeOnControllableInsetsChangedListener(
                @NonNull WindowInsetsControllerCompat.OnControllableInsetsChangedListener
                        listener) {
        }
    }


    @RequiresApi(20)
    private static class Impl20 extends Impl {
        ...
    }


    @RequiresApi(23)
    private static class Impl23 extends Impl20 {
        ...
    }


    @RequiresApi(26)
    private static class Impl26 extends Impl23 {
        ...
    }


    @RequiresApi(30)
    private static class Impl30 extends Impl {
        ...
    }



상태표시바는 Android 5.0 (API 21) 이상에서 임의의 색상을 지정할 수 있으며, xml 을 통해 style 을 설정하는 방법과 프로그래밍 설정을 통해 설정하는 방법이 있다.



### xml 에서 style 설정 방법

    <resources xmlns:tools="http://schemas.android.com/tools">
      <!-- Base application theme. -->
      <style name="Theme.Snippet" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <item name="android:windowTranslucentStatus">true</item>
        <item name="android:statusBarColor">@color/teal_700</item>
        // Target api 를 추가해 설정할 수도 있다
        <item name="android:statusBarColor" tools:targetApi="l">@color/teal_700</item>
      </style>
    </resources>



### 프로그래밍 설정 방법

    class MainActivity : Activity() {
      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          window.statusBarColor = Color.parseColor("#FF2244")
        }
        setContentView(R.layout.activity_main)
      }
      ...
    }















## 2. Navigation Bar

안드로이드 4.0 폰에 처음 추가된 내비게이션 바는 하드웨어 키가 없는 기기에만 표시된다. 내비게이션 바에는 이전, 홈, 최근 앱 컨트롤이 있으며, 안드로이드 2.3 이하 버전 앱을 사용할 경우 해당 앱의 메뉴를 띄우는 컨트롤이 함께 표시된다. 

### 매니페스트 설정 방법

    <application
      ...
      android:theme="@android:style/Theme.Holo.NoActionBar.Fullscreen" >
      ...
    </application>



### 프로그래밍 설정 방법

* **Android 4.0 (API 레벨 14) 이하에서 설정**

WindowManager 플래그를 설정하여 상태표시바를 숨길수 있으며, 프로그래밍 방식으로 코드를 직접 작성하는 방법과 앱의 매니페스트 파일에서 액티비티 테마를 설정하는 방법이 있다. 앱에서 항상 상태표시바를 숨길 경우, 매니페스트 파일에서 액티비티의 테마를 설정하는것이 좀 더 선호되는 설정 방법이다.엄밀하게 말하면, 원하는 경우 프로그래밍 방법으로 테마를 재정의 할 수 있다.

* **Android 4.1 (API 레벨 15) 이상에서 설정**

setSystemUiVisibility() 를 사용하여 상태표시바를 숨길 수 있다. setSystemUiVisibility() 는 개별 뷰 레벨에서 UI 플래그를 설정한다. 이런 설정은 윈도우 레벨로 합쳐진다. UI플래그를 setSystemUiVisibility() 를 사용해서 설정하면 WindowManager 플래그를 이용하는 것보다 상태표시바를 좀 더 세부적으로 설정할 수 있다. SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 를 사용하여 상태표시줄 뒤에 콘텐츠를 표시하여 상태표시줄이 숨겨지거나 나타날 때 콘텐츠가 리사이즈 되지 않도록 할 수 있다. 앱이 안정적인 레이아웃을 유지하는데 도움이 되도록 SYSTEM_UI_FLAG_LAYOUT_STABLE 사용이 필요할 수도 있다.

* **Android 11 (API 레벨 30) 이상에서 설정**

WindowInsetsControllerCompat 를 사용해서 상태표시바를 숨길수 있다. 이 클래스는 WindowInsetsController 를 둘러싼 래퍼로서 하위 SDK 의 경우, 원래 구현에 최대한 가깝게 동작하는 것을 목표로 한다.

    class MainActivity : Activity() {
      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
          Build.VERSION.SDK_INT < 16 -> {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
          }
          Build.VERSION.SDK_INT in 16..29 -> {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    //        actionBar?.hide()
            // ActionBar 지원하지 않을수도 있으니 supportActionBar를 사용한다.
            supportActionBar?.hide()
          }
          Build.VERSION.SDK_INT >= 30 -> {
            val insetsController = WindowInsetsControllerCompat(window, window.decorView)
            // 시스템 UI 동작을 설정한다
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
            // 숨길 시스템 UI 타겟을 설정한다
            insetsController.hide(WindowInsetsCompat.Type.navigationBars())
    //        insetsController.show(WindowInsetsCompat.Type.navigationBars())
    //        actionBar?.hide()
            // ActionBar 지원하지 않을수도 있으니 supportActionBar를 사용한다.
            supportActionBar?.hide()
          }
        }
        setContentView(R.layout.activity_main)
      }
      ...
    }











[https://github.com/android/user-interface-samples/blob/master/ImmersiveMode/app/src/main/java/com/example/android/immersivemode/MainActivity.kt](https://github.com/android/user-interface-samples/blob/master/ImmersiveMode/app/src/main/java/com/example/android/immersivemode/MainActivity.kt)

[https://klutzy.github.io/android-design-ko/get-started/ui-overview.html](https://klutzy.github.io/android-design-ko/get-started/ui-overview.html)

[https://tillerdigital.com/glossary/system-ui/](https://tillerdigital.com/glossary/system-ui/)
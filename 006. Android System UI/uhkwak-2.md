# Android System UI - 2

## Android Theme

### Android Theme and Styles

Android 의 스타일 및 테마를 사용한다면 웹 프론트엔드 처럼 UI 구조 및 동작에서 앱 디자인의 세부 사항을 분리할 수 있다. 

여기서 Style이란 단일 뷰의 appearnce, 모습에 관한 특성의 모음이다. 스타일은 글꼴, 글꼴 색상, 배경 색상 등 다양한 속성을 지정할 수 있다.

테마란 개별적인 View 뿐 아니라, 전체적인 앱, 액티비티, 뷰 계층 등에 적용이 되는 속성들의 모음이다. 테마를 적용한다면 전체 앱 또는 액티비티의 모든 뷰가 지원하는 각 테마의 속성을 적용하게 된다. 테마는 또한 Status Bar, Window Background 와 같이 뷰의 요소가 아닌 부분에도 스타일을 적용할 수 있다. 스타일과 테마는 안드로이드 리소스 폴더는 res/values/ 밑에 styles.xml 에 정의된다.

테마와 스타일은 모두 키-밸류 쌍으로 속성과 리소스를 매핑하고 있다는 공통점 외에는 사용 목적이 다르다. 

스타일은 보통 특정 타입의 뷰를 위한 특성들을 정의한다. 예를 들면 어떤 버튼에 대한 속성들을 정의하는 식이다. 자주 사용되는 위젯들에 대한 속성값들을 style로 정의해둔다면, 레이아웃 파일에서 중복되는 코드를 줄일 수 있다.

테마는 스타일, 레이아웃, 위젯 등에서 참조할 수 있는 명명된 리소스 모음을 정의한다. 테마는 보통 colorPrimary 같은 의미론적 이름을 Android Resource 에 할당한다. 



## SystemUI 은 어떤 과정을 통해 보여지는가?

### installDeco()

보통 액티비티의 onCreate() 안에서 xml에 만든 레이아웃을 인플레이팅 하기 위해 setContentView() 를 부르곤 한다. setContentView() 함수 내부를 들여다 보면 

```java
@Override
public void setContentView(int layoutResID) {
    // Note: FEATURE_CONTENT_TRANSITIONS may be set in the process of installing the window
    // decor, when theme attributes and the like are crystalized. Do not check the feature
    // before this happens.
    if (mContentParent == null) {
        installDecor();
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

이런 구조로 되어 있다. 위 코드는 PhoneWindow의 setContentView 함수 내부인데, Window Abstract 클래스에 setContentView가 abstract method로 있고, Activity 가 내부적으로 생성되어 attach() 될 때 PhoneWindow를 생성하고 있다.

여기서 눈여겨 볼 부분은 mContentParent가 null이면 installDeco()를 통해 mContentParent를 만들어주게 되는데, 밑에서 installDeco() 함수를 자세히 보겠지만 인자로 받은 layoutResID를 inflating 될 상위 ViewGroup으로 볼 수 있다. 

```java
final void attach(Context context, ActivityThread aThread,
        Instrumentation instr, IBinder token, int ident,
        Application application, Intent intent, ActivityInfo info,
        CharSequence title, Activity parent, String id,
        NonConfigurationInstances lastNonConfigurationInstances,
        Configuration config, String referrer, IVoiceInteractor voiceInteractor,
        Window window, ActivityConfigCallback activityConfigCallback) {
    attachBaseContext(context);

    mFragments.attachHost(null /*parent*/);

    mWindow = new PhoneWindow(this, window, activityConfigCallback);
    mWindow.setWindowControllerCallback(this);
    mWindow.setCallback(this);
    mWindow.setOnWindowDismissedCallback(this);
    mWindow.getLayoutInflater().setPrivateFactory(this);
    if (info.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
        mWindow.setSoftInputMode(info.softInputMode);
    }
    if (info.uiOptions != 0) {
        mWindow.setUiOptions(info.uiOptions);
    }
    mUiThread = Thread.currentThread();
    ...
}
```

installDeco() 함수 내부를 살펴보자.

```java
private void installDecor() {
    mForceDecorInstall = false;
    if (mDecor == null) {
        mDecor = generateDecor(-1);
        mDecor.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        mDecor.setIsRootNamespace(true);
        if (!mInvalidatePanelMenuPosted && mInvalidatePanelMenuFeatures != 0) {
            mDecor.postOnAnimation(mInvalidatePanelMenuRunnable);
        }
    } else {
        mDecor.setWindow(this);
    }
    if (mContentParent == null) {
        mContentParent = generateLayout(mDecor);
          ....
        }
    }
}
```

Decor가 할당된게 없다면 generateDecor(); 를 통해서 DecorView를 만들고 있다.

```java
protected DecorView generateDecor(int featureId) {
    // System process doesn't have application context and in that case we need to directly use
    // the context we have. Otherwise we want the application context, so we don't cling to the
    // activity.
    Context context;
    if (mUseDecorContext) {
        Context applicationContext = getContext().getApplicationContext();
        if (applicationContext == null) {
            context = getContext();
        } else {
            context = new DecorContext(applicationContext, getContext());
            if (mTheme != -1) {
                context.setTheme(mTheme);
            }
        }
    } else {
        context = getContext();
    }
    return new DecorView(context, featureId, this, getAttributes());
}
```

genereateDecor() 함수에서는 현재 가지고 있는 mTheme 값을 컨텍스트에 할당하고 있는것을 볼 수 있다. setTheme 은 해당 컨텍스트를 위한 BaseTheme을 할당하고 있는 것이고 이 작업은 레이아웃이 인플레이팅 되기 전에 발생한다. 

installDeco()를 이어서 보면, mContentParent가 null이면 위에서 generateDecor()로 생성한 DecorView를 인자로 넣은 generateLayout(mDecor) 으로 mContentParent를 만들어주고 있다. 

그리고 generateLayout() 은 파라미터로 받은 DecorView로 **systemUI**를 제어하고 있다. 

```java
protected ViewGroup generateLayout(DecorView decor) {
		...
      if (a.getBoolean(R.styleable.Window_windowLightStatusBar, false)) {
          decor.setSystemUiVisibility(
                  decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      }
      if (a.getBoolean(R.styleable.Window_windowLightNavigationBar, false)) {
          decor.setSystemUiVisibility(
                  decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
      }
     ...
}     
```

## Reference

- https://developer.android.com/guide/topics/ui/look-and-feel/themes
- https://pluu.github.io/blog/android/2020/08/02/global-style/


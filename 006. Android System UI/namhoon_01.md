iOS에 비해 상대적으로 막대한 자유도가 보장된 Android에도 개발자가 쉽게 건들 수 없는 영역이 있다.

디자인에 맞추어 색상을 바꾸거나, 숨기거나 흐리게 하는 정도만 허용된 개발자가 건들 수 없는 영역.

우리는 이 영역을 System UI라고 부른다.

자유도가 없는 만큼 상대적으로 소홀히 여겨도 되는 영역이지만, 다크모드의 대중화로 디자인적으로 신경써야하는 영역이 되어버린 이 System UI를 살펴보자.

![](https://raw.githubusercontent.com/AndroidDeepDive/Study/master/006.%20Android%20System%20UI/img/auSbY.png)

보통 화면에서 System UI는 크게 두 곳으로 나뉜다.

위의 그림에서 보듯 상단에 위치한 **Status Bar**와 하단에 위치한 **Navigation Bar** 이다.

### Status Bar

Fullscreen 등으로 가리지않는 이상 상단에 항상 노출된 상태를 유지하는 영역이다.

Bluetooth나 Wi-Fi, LTE 등의 네트워크의 연결 및 사용자에게 편의와 접근성을 제공하고 서비스에서는 리텐션을 높이는 주요 수단이 Notification이 노출되는 영역이기도 하다.

Android에서는 위에서 아래로 쓸어내리는 동작으로 Notification Panel 영역을 최대로 넓힐 수 있다.

Floating된 듯한 알림 (흔히 Head-up이라고 하는)도 노출해주는 역할을 한다.

- Color
- Height
- Transparent

#### 관련 공식 문서들
- https://developer.android.com/training/system-ui/dim
- https://developer.android.com/training/system-ui/status

### Navigation Bar

주로 화면의 아래에 위치하고 있다.

다만 화면의 회전에 따라 좌우에도 위치할 수 있다. 

물리적인 hardware Key가 존재하는 경우와 터치스크린에 위치한 Soft Key 방식이 존재한다.

버튼은 주로 3개이며, 각각 **Back**, **Home**, **Overview** 버튼으로 불리운다.

Back 버튼은 뒤로가기 동작을 수행하며, Home 버튼은 기기의 Home 화면을 최상위로 끌어올린다.

Overview 버튼은 앱들의 Stack을 보여주어 앱간의 전환을 용이하게 해주는 역할을 한다.

- Color
- Transparent
- Icon Color

#### References
- https://developer.android.com/training/system-ui/navigation


### Deep Dive

System UI에 대한 간략한 소개를 했으니, 진짜 어떻게 구현되어있나 파들어가보도록 하자.

Android의 공개 프로젝트에 SystemUI라는 프로젝트가 있다.

`com.android.systemui` 패키지 아래 `SystemUI.java` 파일의 코드를 찾아보면 아래와 같다.

```java
public abstract class SystemUI implements Dumpable {
    protected final Context mContext;

    public SystemUI(Context context) {
        mContext = context;
    }

    public abstract void start();

    protected void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public void dump(@NonNull FileDescriptor fd, @NonNull PrintWriter pw, @NonNull String[] args) {
    }

    protected void onBootCompleted() {
    }

    public static void overrideNotificationAppName(Context context, Notification.Builder n,
            boolean system) {
        final Bundle extras = new Bundle();
        String appName = system
                ? context.getString(com.android.internal.R.string.notification_app_name_system)
                : context.getString(com.android.internal.R.string.notification_app_name_settings);
        extras.putString(Notification.EXTRA_SUBSTITUTE_APP_NAME, appName);

        n.addExtras(extras);
    }
}
```

이 클래스가 바로 System UI의 최상위에 위치한 코드이다.

그리고 이 `SystemUI.java`를 `StatusBar.java`가 상속받고 있다.

```java
public class StatusBar extends SystemUI implements 
        DemoMode,
        ActivityStarter, 
        KeyguardStateController.Callback,
        OnHeadsUpChangedListener, 
        CommandQueue.Callbacks,
        ColorExtractor.OnColorsChangedListener, 
        ConfigurationListener,
        StatusBarStateController.StateListener, 
        ActivityLaunchAnimator.Callback,
        LifecycleOwner, 
        BatteryController.BatteryStateChangeCallback {
    // ...
}
```

전체 코드는 약 4,400줄에 달하므로 `StatusBar` 클래스의 선언부만 가져와보았다.

`DemoMode`는 `com.android.systemui.demo`의 액션값을 통해 실행할 수 있는 `enter`, `exit`, `battery` 등의 명령어를 정의한 인터페이스이다.

`ActivityStarter`가 `startActivity()` 관련 인터페이스를 구현하도록 강제하며, 

`KeyguardStateController.Callback`을 통해 기기의 잠금 상태를 얻어온다.

`OnHeadsUpChangedListener`는 Notification의 Head-up 관련 상태를 처리한다.

이하 굉장히 많은 인터페이스들을 구현하고 있음을 확인할 수 있는데

이들 중 눈에 익은 **Configuration**, **StatusBarState**, **LifecyclerOwner**, **Battery** 등의 키워드를 통해 우리가 Status Bar를 통해 얻을 수 있는 정보나 장이 많은 인터페이스들을 구현하고 있음을 확인할 수 있다.

이 `StatusBar`가 어떤 프로세스로 로딩이 되는 지 Deep Dive해보자.

Android는 부팅 이후 **Zygote**를 실행한 뒤 **SystemServer**를 실행하도록 되어 있다.

```bash
service zygote /system/bin/app_process -Xzygote /system/bin --zygote --start-system-server
```

이때 실행되는 `SystemServer.java`에 System UI를 호출하는 코드가 존재한다.

```java
// SystemServer#startSystemUi()
private static void startSystemUi(Context context, WindowManagerService windowManager) {
    Intent intent = new Intent();
    intent.setComponent(
        new ComponentName(
            "com.android.systemui",
            "com.android.systemui.SystemUIService"
        )
    );
    intent.addFlags(Intent.FLAG_DEBUG_TRIAGED_MISSING);
    //Slog.d(TAG, "Starting service: " + intent);
    context.startServiceAsUser(intent, UserHandle.SYSTEM);
    windowManager.onSystemUiStarted();
}
```

`"com.android.systemui.SystemUIService`라는 힌트를 얻었다.

바로 해당 클래스로 진입해보자.

```java
public class SystemUIService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication) getApplication()).startServicesIfNeeded();

        // For debugging RescueParty
        if (Build.IS_DEBUGGABLE && SystemProperties.getBoolean("debug.crash_sysui", false)) {
            throw new RuntimeException();
        }

        if (Build.IS_DEBUGGABLE) {
            // b/71353150 - looking for leaked binder proxies
            BinderInternal.nSetBinderProxyCountEnabled(true);
            BinderInternal.nSetBinderProxyCountWatermarks(1000,900);
            BinderInternal.setBinderProxyCountCallback(
                    new BinderInternal.BinderProxyLimitListener() {
                        @Override
                        public void onLimitReached(int uid) {
                            Slog.w(SystemUIApplication.TAG,
                                    "uid " + uid + " sent too many Binder proxies to uid "
                                    + Process.myUid());
                        }
                    }, Dependency.get(Dependency.MAIN_HANDLER));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        SystemUI[] services = ((SystemUIApplication) getApplication()).getServices();
        if (args == null || args.length == 0) {
            for (SystemUI ui: services) {
                pw.println("dumping service: " + ui.getClass().getName());
                ui.dump(fd, pw, args);
            }
            if (Build.IS_DEBUGGABLE) {
                pw.println("dumping plugins:");
                ((PluginManagerImpl) Dependency.get(PluginManager.class)).dump(fd, pw, args);
            }
        } else {
            String svc = args[0].toLowerCase();
            for (SystemUI ui: services) {
                String name = ui.getClass().getName().toLowerCase();
                if (name.endsWith(svc)) {
                    ui.dump(fd, pw, args);
                }
            }
        }
    }
}
```

여기서 주의깊게 봐야할 부분은 `SystemUIApplication`이다.

`SystemUIApplication`의 구현체를 찾아보면 `startServicesIfNeeded()`라는 메서드를 얻을 수 있다.

```java
// SystemUIApplication#startServicesIfNeeded()
public void startServicesIfNeeded() {
    String[] names = getResources().getStringArray(R.array.config_systemUIServiceComponents);
    startServicesIfNeeded(names);
}
```

뭔가 System UI Service들이 배열 형태로 선언되어있고, 이를 서비스로 등록하는 것을 알 수 있다.

PDK 레벨에서 `R.array.config_systemUIServiceComponents`의 내용을 확인해보자.

```xml
<!-- SystemUI#config.xml-->
<!-- SystemUI Services: The classes of the stuff to start. -->
<string-array name="config_systemUIServiceComponents" translatable="false">
    <item>com.android.systemui.util.NotificationChannels</item>
    <item>com.android.systemui.keyguard.KeyguardViewMediator</item>
    <item>com.android.systemui.recents.Recents</item>
    <item>com.android.systemui.volume.VolumeUI</item>
    <item>com.android.systemui.stackdivider.Divider</item>
    <item>com.android.systemui.statusbar.phone.StatusBar</item>
    <item>com.android.systemui.usb.StorageNotification</item>
    <item>com.android.systemui.power.PowerUI</item>
    <item>com.android.systemui.media.RingtonePlayer</item>
    <item>com.android.systemui.keyboard.KeyboardUI</item>
    <item>com.android.systemui.pip.PipUI</item>
    <item>com.android.systemui.shortcut.ShortcutKeyDispatcher</item>
    <item>@string/config_systemUIVendorServiceComponent</item>
    <item>com.android.systemui.util.leak.GarbageMonitor$Service</item>
    <item>com.android.systemui.LatencyTester</item>
    <item>com.android.systemui.globalactions.GlobalActionsComponent</item>
    <item>com.android.systemui.ScreenDecorations</item>
    <item>com.android.systemui.biometrics.AuthController</item>
    <item>com.android.systemui.SliceBroadcastRelayHandler</item>
    <item>com.android.systemui.SizeCompatModeActivityController</item>
    <item>com.android.systemui.statusbar.notification.InstantAppNotifier</item>
    <item>com.android.systemui.theme.ThemeOverlayController</item>
    <item>com.android.systemui.accessibility.WindowMagnification</item>
    <item>com.android.systemui.accessibility.SystemActions</item>
    <item>com.android.systemui.toast.ToastUI</item>
</string-array>
```

리스트 중에서 반가운 이름 `com.android.systemui.statusbar.phone.StatusBar`가 보인다.

이제 다시 코드로 돌아오자.

리스트를 파라미터로 하여 `startServicesIfNeeded(String[] services)` 메서드를 실행하게 된다.

코드가 너무 기므로 핵심 코드만 살펴보자면

```java
final int N = services.length;
for (int i = 0; i < N; i++) {
    String clsName = services[i];
    Class cls = Class.forName(clsName);
    Object o = cls.newInstance();
    if (o instanceof SystemUI.Injector) {
        o = ((SystemUI.Injector) o).apply(this);
    }
    mServices[i] = (SystemUI) o;
    mServices[i].mContext = this;
    mServices[i].mComponents = mComponents;
    if (mBootCompleted) {
        mServices[i].onBootCompleted();
    }
}
```

위와 같다.

이후 `StatusBar`객체의 `start()` 메서드가 실행되게 된다.

DemoMode,
        ActivityStarter, 
        KeyguardStateController.Callback,
        OnHeadsUpChangedListener, 
        CommandQueue.Callbacks,
        ColorExtractor.OnColorsChangedListener, 
        ConfigurationListener,
        StatusBarStateController.StateListener, 
        ActivityLaunchAnimator.Callback,
        LifecycleOwner, 
        BatteryController.BatteryStateChangeCallback


```java
@Override
public void start() {
    mScreenLifecycle.addObserver(mScreenObserver);
    mWakefulnessLifecycle.addObserver(mWakefulnessObserver);
    mUiModeManager = mContext.getSystemService(UiModeManager.class);
    mBypassHeadsUpNotifier.setUp();
    mBubbleController.setExpandListener(mBubbleExpandListener);
    mActivityIntentHelper = new ActivityIntentHelper(mContext);

    mColorExtractor.addOnColorsChangedListener(this);
    mStatusBarStateController.addCallback(this,
            SysuiStatusBarStateController.RANK_STATUS_BAR);

    mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    mDreamManager = IDreamManager.Stub.asInterface(
            ServiceManager.checkService(DreamService.DREAM_SERVICE));

    mDisplay = mWindowManager.getDefaultDisplay();
    mDisplayId = mDisplay.getDisplayId();
    updateDisplaySize();

    mVibrateOnOpening = mContext.getResources().getBoolean(
            R.bool.config_vibrateOnIconAnimation);

    // start old BaseStatusBar.start().
    mWindowManagerService = WindowManagerGlobal.getWindowManagerService();
    mDevicePolicyManager = (DevicePolicyManager) mContext.getSystemService(
            Context.DEVICE_POLICY_SERVICE);

    mAccessibilityManager = (AccessibilityManager)
            mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);

    mKeyguardUpdateMonitor.setKeyguardBypassController(mKeyguardBypassController);
    mBarService = IStatusBarService.Stub.asInterface(
            ServiceManager.getService(Context.STATUS_BAR_SERVICE));

    mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
    mWallpaperSupported =
            mContext.getSystemService(WallpaperManager.class).isWallpaperSupported();

    // Connect in to the status bar manager service
    mCommandQueue.addCallback(this);

    RegisterStatusBarResult result = null;
    try {
        result = mBarService.registerStatusBar(mCommandQueue);
    } catch (RemoteException ex) {
        ex.rethrowFromSystemServer();
    }

    createAndAddWindows(result);

    if (mWallpaperSupported) {
        // Make sure we always have the most current wallpaper info.
        IntentFilter wallpaperChangedFilter = new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED);
        mBroadcastDispatcher.registerReceiver(mWallpaperChangedReceiver, wallpaperChangedFilter,
                null /* handler */, UserHandle.ALL);
        mWallpaperChangedReceiver.onReceive(mContext, null);
    } else if (DEBUG) {
        Log.v(TAG, "start(): no wallpaper service ");
    }

    // Set up the initial notification state. This needs to happen before CommandQueue.disable()
    setUpPresenter();

    if (containsType(result.mTransientBarTypes, ITYPE_STATUS_BAR)) {
        showTransientUnchecked();
    }
    onSystemBarAppearanceChanged(mDisplayId, result.mAppearance, result.mAppearanceRegions,
            result.mNavbarColorManagedByIme);
    mAppFullscreen = result.mAppFullscreen;
    mAppImmersive = result.mAppImmersive;

    // StatusBarManagerService has a back up of IME token and it's restored here.
    setImeWindowStatus(mDisplayId, result.mImeToken, result.mImeWindowVis,
            result.mImeBackDisposition, result.mShowImeSwitcher);

    // Set up the initial icon state
    int numIcons = result.mIcons.size();
    for (int i = 0; i < numIcons; i++) {
        mCommandQueue.setIcon(result.mIcons.keyAt(i), result.mIcons.valueAt(i));
    }


    if (DEBUG) {
        Log.d(TAG, String.format(
                "init: icons=%d disabled=0x%08x lights=0x%08x imeButton=0x%08x",
                numIcons,
                result.mDisabledFlags1,
                result.mAppearance,
                result.mImeWindowVis));
    }

    IntentFilter internalFilter = new IntentFilter();
    internalFilter.addAction(BANNER_ACTION_CANCEL);
    internalFilter.addAction(BANNER_ACTION_SETUP);
    mContext.registerReceiver(mBannerActionBroadcastReceiver, internalFilter, PERMISSION_SELF,
            null);

    if (mWallpaperSupported) {
        IWallpaperManager wallpaperManager = IWallpaperManager.Stub.asInterface(
                ServiceManager.getService(Context.WALLPAPER_SERVICE));
        try {
            wallpaperManager.setInAmbientMode(false /* ambientMode */, 0L /* duration */);
        } catch (RemoteException e) {
            // Just pass, nothing critical.
        }
    }

    // end old BaseStatusBar.start().

    // Lastly, call to the icon policy to install/update all the icons.
    mIconPolicy.init();
    mSignalPolicy = new StatusBarSignalPolicy(mContext, mIconController);

    mKeyguardStateController.addCallback(this);
    startKeyguard();

    mKeyguardUpdateMonitor.registerCallback(mUpdateCallback);
    mDozeServiceHost.initialize(this, mNotificationIconAreaController,
            mStatusBarKeyguardViewManager, mNotificationShadeWindowViewController,
            mNotificationPanelViewController, mAmbientIndicationContainer);

    mConfigurationController.addCallback(this);

    mBatteryController.observe(mLifecycle, this);
    mLifecycle.setCurrentState(RESUMED);

    // set the initial view visibility
    int disabledFlags1 = result.mDisabledFlags1;
    int disabledFlags2 = result.mDisabledFlags2;
    mInitController.addPostInitTask(
            () -> setUpDisableFlags(disabledFlags1, disabledFlags2));

    mPluginManager.addPluginListener(
            new PluginListener<OverlayPlugin>() {
                private ArraySet<OverlayPlugin> mOverlays = new ArraySet<>();

                @Override
                public void onPluginConnected(OverlayPlugin plugin, Context pluginContext) {
                    mMainThreadHandler.post(
                            () -> plugin.setup(getNotificationShadeWindowView(),
                                    getNavigationBarView(),
                                    new Callback(plugin), mDozeParameters));
                }

                @Override
                public void onPluginDisconnected(OverlayPlugin plugin) {
                    mMainThreadHandler.post(() -> {
                        mOverlays.remove(plugin);
                        mNotificationShadeWindowController
                                .setForcePluginOpen(mOverlays.size() != 0);
                    });
                }

                class Callback implements OverlayPlugin.Callback {
                    private final OverlayPlugin mPlugin;

                    Callback(OverlayPlugin plugin) {
                        mPlugin = plugin;
                    }

                    @Override
                    public void onHoldStatusBarOpenChange() {
                        if (mPlugin.holdStatusBarOpen()) {
                            mOverlays.add(mPlugin);
                        } else {
                            mOverlays.remove(mPlugin);
                        }
                        mMainThreadHandler.post(() -> {
                            mNotificationShadeWindowController
                                    .setStateListener(b -> mOverlays.forEach(
                                            o -> o.setCollapseDesired(b)));
                            mNotificationShadeWindowController
                                    .setForcePluginOpen(mOverlays.size() != 0);
                        });
                    }
                }
            }, OverlayPlugin.class, true /* Allow multiple plugins */);
}
```

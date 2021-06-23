## 이미지 라이브러리의 동작 원리 분석

Android 에서 가장 많이 쓰이는 `Glide`를 기준으로 이미지 라이브러리의 동작 방식을 살펴보자.

먼저 Part 1에서 다루었던 기본 예제를 수정하도록 하자.

### Glide 의존성 설정

```gradle
dependencies {
    ...
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
    ...
}
```

### 인터넷 사용 권한 부여

```xml
<manifest 
    xmlns:android="http://schemas.android.com/apk/res/android"
    package="android.deepdive.raw">

    <uses-permission android:name="android.permission.INTERNET"/>

    <!-- ... -->

</manifest>
```

### 레이아웃 파일에서 이미지 소스 제거

```js
<ImageView
    android:id="@+id/imageView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent"/>
```

### 코드에 Glide 예제 적용

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var imageView: ImageView = findViewById(R.id.imageView)

        Glide
            .with(this)
            .load("https://goo.gl/gEgYUd")
            .into(imageView)
    }
}
```

위와 같이 작업을 하고 나면 아래와 같은 화면을 렌더링할 수 있다.

<img src="https://drive.google.com/uc?export=view&id=1APd5vaE1UjJqY3riyxVl92riT1_Uzggp" width="350">

## 기능 톺아보기
### 이미지 렌더링 방식

```
Glide
    .with(this)
    .load("https://goo.gl/gEgYUd")
    .into(imageView)
```

개발자 입장에서는 매우 간단하게 이미지를 렌더링할 수 있게 해준다.

Glide 문서에서 최소로 요구되고 있는 `with()`와 `load()`, `into()`의 내부 구조를 한 번 살펴보자.

#### 1. with 메서드

```java
@NonNull
public static RequestManager with(@NonNull FragmentActivity activity) {
    return getRetriever(activity).get(activity);
}
```

```java
@NonNull
private static RequestManagerRetriever getRetriever(@Nullable Context context) {
    // Context could be null for other reasons (ie the user passes in null), but in practice it will
    // only occur due to errors with the Fragment lifecycle.
    Preconditions.checkNotNull(
        context,
        "You cannot start a load on a not yet attached View or a Fragment where getActivity() "
            + "returns null (which usually occurs when getActivity() is called before the Fragment "
            + "is attached or after the Fragment is destroyed).");
    return Glide.get(context).getRequestManagerRetriever();
}
```

`with()`메서드를 호출하면 `getRetriever()` 메서드를 통해 `RequestManagerRetriever` 객체를 획득한다.

이후 `get()` 메서드를 이용해 `RequestManager` 객체를 생성하는 코드도 연달아 호출하게 된다.

```java
@NonNull
public RequestManager get(@NonNull Context context) {
    if (context == null) {
        throw new IllegalArgumentException("You cannot start a load on a null Context");
    } else if (Util.isOnMainThread() && !(context instanceof Application)) {
        if (context instanceof FragmentActivity) {
        return get((FragmentActivity) context);
        } else if (context instanceof Activity) {
        return get((Activity) context);
        } else if (context instanceof ContextWrapper
            // Only unwrap a ContextWrapper if the baseContext has a non-null application context.
            // Context#createPackageContext may return a Context without an Application instance,
            // in which case a ContextWrapper may be used to attach one.
            && ((ContextWrapper) context).getBaseContext().getApplicationContext() != null) {
        return get(((ContextWrapper) context).getBaseContext());
        }
    }
    return getApplicationManager(context);
}
```

Context의 instance type에 대한 처리후 제일 마지막에 `getApplicationManager()`를 호출한다.

```java
@NonNull
private RequestManager getApplicationManager(@NonNull Context context) {
    // Either an application context or we're on a background thread.
    if (applicationManager == null) {  
        synchronized (this) {
            if (applicationManager == null) {
            // Normally pause/resume is taken care of by the fragment we add to the fragment or
            // activity. However, in this case since the manager attached to the application will not
            // receive lifecycle events, we must force the manager to start resumed using
            // ApplicationLifecycle.

          // T  ODO(b/27524013): Factor out this Glide.get() call.
            Glide glide = Glide.get(context.getApplicationContext());
            applicationManager =
                factory.build(
                    glide,
                    new ApplicationLifecycle(),
                    new EmptyRequestManagerTreeNode(),
                    context.getApplicationContext());
            }
        }
    }
    return applicationManager;
}
```

`with()` 메서드는 Global Scope에서 애플리케이션의 생명주기와 연동하여 Glide의 싱글턴 객체를 획득하는 것이 목적이라고 볼 수 있다.

#### 2. load 메서드

두 번째로 `load()` 메서드를 살펴보자.

```java
@NonNull
@CheckResult
@Override
public RequestBuilder<Drawable> load(@Nullable String string) {
    return asDrawable().load(string);
}
```

단순힌 `asDrawable()`을 호출해 `RequestBuilder` 객체를 만들고, `load()`를 호출한다고만 명세되어있다.

`asDrawable()` 구현체를 살펴보자.

```java
@NonNull
@CheckResult
public RequestBuilder<Drawable> asDrawable() {
    return as(Drawable.class);
}
```

`android.graphics.drawable.Drawable` 타입을 `as()`에 파라미터로 넘겨주고 있다.

`as()` 구현체를 살펴보자.


```java
@NonNull
@CheckResult
public <ResourceType> RequestBuilder<ResourceType> as(@NonNull Class<ResourceType> resourceClass) {
    return new RequestBuilder<>(glide, this, resourceClass, context);
}
```

파라미터로 주어진 객체 타입(여기서는 `android.graphics.drawable.Drawable`)으로 디코딩하여 반환한다.

여기서 쓰이는 디코더는 `com.bumptech.glide.load.ResourceDecoders`이다.

`Drawable.class`로 고정되어있지 않은 이유는 리소스 클래스의 서브클래스들도 호환하기 위해서이다.

`Glide.load()`에서 호출하는 `asDrawable()`이 디코딩할 리소스 클래스를 생성하였다면 바로 뒤에 붙은 `RequestBuilder.load()` 메서드에서 데이터를 불러올 것임을 추측할 수 있다.

`RequestBuilder.load()` 메서드의 호출 구조를 살펴보자.

```java
@NonNull
@Override
@CheckResult
public RequestBuilder<TranscodeType> load(@Nullable String string) {
    return loadGeneric(string);
}
```

`load()`의 파라미터로 주어진 `string`값을 key로 사용해 데이터를 캐시하는 영역이다.

동일 이미지의 반복적인 렌더링 작업에 대해 처리한 부분임을 알 수 있다.

이후 `loadGeneric()`을 호출한다.

```java
@NonNull
private RequestBuilder<TranscodeType> loadGeneric(@Nullable Object model) {
    if (isAutoCloneEnabled()) {
        return clone().loadGeneric(model);
    }
    this.model = model;
    isModelSet = true;
    return selfOrThrowIfLocked();
}
```

파라미터의 타입이 `String`에서 `Object`로 변경되었다.

이 `model`은 `com.bumptech.glide.load.model.UriLoader` 객체에서 핸들링하게 된다.

```java
@NonNull
@SuppressWarnings("unchecked")
protected final T selfOrThrowIfLocked() {
    if (isLocked) {
        throw new IllegalStateException("You cannot modify locked T, consider clone()");
    }
    return self();
}
```

```java
@SuppressWarnings("unchecked")
private T self() {
    return (T) this;
}
```

이후 `RequestBuilder.load()`는 최종적으로 `RequestBuilder` 객체를 반환해주게 된다.

#### 3. into 메서드

현재까지 `with()`에서 `RequestManager`객체를 생성한 뒤, 이 객체의 `load()` 메서드를 호출하여 `RequestBuilder` 객체를 획득하는 과정까지 진행되었다.

이제 `RequestBuilder`에서 `into()` 메서드를 호출해 `ImageView`에 리소스를 할당하는 마지막 작업이다.

리소스를 할당할 ImageView를 파라미터로 요구한다.

```java
@NonNull
public ViewTarget<ImageView, TranscodeType> into(@NonNull ImageView view) {
    Util.assertMainThread();
    Preconditions.checkNotNull(view);

    BaseRequestOptions<?> requestOptions = this;
    if (!requestOptions.isTransformationSet()
        && requestOptions.isTransformationAllowed()
        && view.getScaleType() != null) {
        // Clone in this method so that if we use this RequestBuilder to load into a View and then
        // into a different target, we don't retain the transformation applied based on the previous
        // View's scale type.
        switch (view.getScaleType()) {
        case CENTER_CROP:
            requestOptions = requestOptions.clone().optionalCenterCrop();
            break;
        case CENTER_INSIDE:
            requestOptions = requestOptions.clone().optionalCenterInside();
            break;
        case FIT_CENTER:
        case FIT_START:
        case FIT_END:
            requestOptions = requestOptions.clone().optionalFitCenter();
            break;
        case FIT_XY:
            requestOptions = requestOptions.clone().optionalCenterInside();
            break;
        case CENTER:
        case MATRIX:
        default:
            // Do nothing.
            }
        }
    return into(
        glideContext.buildImageViewTarget(view, transcodeClass),
        /*targetListener=*/ null,
        requestOptions,
        Executors.mainThreadExecutor());
}
```
`Util.assertMainThread()`에서 메인 쓰레드 여부를 검증한 뒤, `Preconditions.checkNotNull(view)`에서 파라미터로 주어진 ImageView에 대한 null check를 수행한다.

이후 ImageView의 scaleType에 대한 처리 후, `into(target, targetListener, options, callbackExecutor)`를 호출한다.

이때 `target` 자리에 `GlideContext.buildImageViewTarget()`이라는 메서드를 주입하는데, 파라미터로 주어진 ImageView를 이용해 `BitmapImageViewTarget`이나 `DrawableImageViewTarget`으로 변환하는 작업을 수행한다.

`GlideContext.buildImageViewTarget()`의 두 번째 파라미터인 `transcodeClass`의 값은 `Glide.load()`를 호출하였을 때 수행하는 `as()` 메소드의 파라미터인 `Drawable.class`로 이미 주입되어있다.

```java
private <Y extends Target<TranscodeType>> Y into(
    @NonNull Y target,
    @Nullable RequestListener<TranscodeType> targetListener,
    BaseRequestOptions<?> options,
    Executor callbackExecutor) {

    Preconditions.checkNotNull(target);
    if (!isModelSet) {
        throw new IllegalArgumentException("You must call #load() before calling #into()");
    }

    Request request = buildRequest(target, targetListener, options, callbackExecutor);
    Request previous = target.getRequest();
    if (request.isEquivalentTo(previous) && !isSkipMemoryCacheWithCompletePreviousRequest(options, previous)) {
        // If the request is completed, beginning again will ensure the result is re-delivered,
        // triggering RequestListeners and Targets. If the request is failed, beginning again will
        // restart the request, giving it another chance to complete. If the request is already
        // running, we can let it continue running without interruption.
        if (!Preconditions.checkNotNull(previous).isRunning()) {
            // Use the previous request rather than the new one to allow for optimizations like skipping
            // setting placeholders, tracking and un-tracking Targets, and obtaining View dimensions
            // that are done in the individual Request.
            previous.begin();
        }
        return target;
    }

    requestManager.clear(target);
    target.setRequest(request);
    requestManager.track(target, request);

    return target;
}
```

`target`으로 주어진 ImageView에 리소스를 세팅하는 곳인데, 특기할만한 부분은 `requestManager.clear(target)`이다.

해당 메서드를 호출하면 현재 target으로 설정된 뷰에 대한 모든 로딩을 취소하고 모든 리소스를 해제한다.

## 마무리

Glide를 사용하기 위해 호출하는 3개의 메서드만으로도 이미지 라이브러리의 고도화를 상당히 수준급이라는 것을 알 수 있었다.

튜닝 및 다양한 기능들을 지원하는 만큼 좀 더 살펴본다면 많은 공부가 될 것으로 보인다.
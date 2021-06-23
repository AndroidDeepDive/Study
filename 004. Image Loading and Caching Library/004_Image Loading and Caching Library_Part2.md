# Image Loading and Caching Library Part 2 - Principle / Memory & Footprint / Compose

## Image Library의 동작 방식

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

## Memory & Footprint

Bitmap를 다루다보면 필연적으로 OOM 관련하여 메모리 이슈를 피할 수 없다.

Image Library를 사용하지 않은 상태에서 많은 이미지를 사용하거나 고해상도 이미지를 이미지뷰에 로드해야하는 경우 메모리 부족으로 OOM이 발생하게 된다.

요즘 나오는 스마트폰은 굉장히 많은 메모리를 가지고 태어나는데 **왜 이정도도 못 버티지(?)** 라는 생각을 한다면, 안드로이드는 앱 내에서 사용할 수 있는 힙 메모리가 정해져있기 때문이다.

![](https://images.velog.io/images/jshme/post/49b4df11-5ceb-4492-9d4f-f40655655291/IMG_06A0242D2ABE-1.jpeg)

Android의 메모리 모델은 운영체제 버전에 따라 두가지로 나뉘게 된다. 

* Dalvik heap 영역 : java 객체를 저장하는 메모리
* External 영역 : native heap의 일종으로 네이티브의 비트맵 객체를 저장하는 메모리
Dalvik heap 영역와 External 영역의 `Dalvik heap footprint + External Limit`을 합쳐 프로세스당 메모리 한계를 초과하면 OOM이 발생하게 된다.

### Footprint
> java의 footprint는 한 번 증가하면 크기가 다시 감소되지 않기 때문에, footprint가 과도하게 커지지 않게끔 잘 관리해야한다. 

![](https://images.velog.io/images/jshme/post/840b5317-446b-413e-933b-20e50307c18f/image.png)

Dalvik VM은 처음에 동작에 필요한 만큼만 프로세스에 Heap을 할당하게 되고, 프로세스에 할당된 메모리보다 많은 메모리를 필요하게될 때마다 Dalvit Footfrint도 증가하게된다. 하지만 증가된 Footfrint는 결코 감소하지 않기 때문에 java 객체가 사용가능한 메모리 공간의 여유가 있어도, External heap의 크기가 증가되면 OOM이 발생할 수 있다. 하지만 이런 문제도 Honeycomb 이후부터는 Dalvik heap 과 External 영역이 합쳐졌기 때문에, 고려할 필요가 없어졌다. 

External 영역을 사용하는 Honeycomb 미만 버전에서는 이미지를 많이 사용하고있는 화면에서 화면을 전환하는 행동이 발생했을 때도 OOM이 발생하면서 앱이 중지될 것이다. 화면을 전환하면 이전 액티비티 인스턴스에 있던 이미지뷰나 할당되었던 비트맵이 함께 소멸되어 메모리가 회수되고 새로운 액티비티 인스턴스를 생성할텐데, 이 과정에서 이전 액티비티 인스턴스의 비트맵 객체가 회수되지 않아 메모리 누수가 발생했기 때문이다. 

비트맵 객체에 대한 참조가 없는데도 왜 회수가 될 수 없을까? Honeycomb 미만 버전에서는 Java 비트맵 객체는 실제 비트맵 데이터를 가지고 있는 곳을 가리키는 포인터일 뿐이고 실제 데이터는 External 영역인 Native Heap 영역에 저장되기 때문이다. Java 비트맵 객체는 참조가 없을 때 GC에 의해 회수되지만 Native Heap 영역은 GC 수행영역 밖이기 때문에 메모리 소멸 시점이 다르다. 

이러한 문제점 때문에, Honeycomb 이후 버전에서는 External 영역이 없어지면서 Dalvik heap 영역에 비트맵 메모리를 올릴 수 있게 되었고 GC도 접근할 수 있게 되었다.

다시 돌아와, 만약 고해상도 이미지를 로드할 때 OOM이 발생하는 경우 BitmapFactory 객체를 이용해 다운샘플링, 디코딩 방식을 선택해 적절하게 뷰에 로드하면 된다. 하지만 많은 이미지를 사용하게 되면서 OOM이 발생한다면, `이미지 캐싱`을 이용해보는 것이 어떨까?

### Bitmap Caching

이미지가 화면에서 사라지고 다시 구성할 때 이미지를 매번 로드하는 것은 성능상으로나 사용자 경험에 좋지 않다. 이럴 때 메모리와 디스크 캐시를 이용하여 어디에선가 저장되어있던 비트맵을 다시 가져온다면 다시 로드하는 시간도 단축시킬 수 있으며 성능 개선도 가능할 것이다. 이 때 캐싱을 위해 `Memory Cache`와 `Disk Cache` 사용을 추천하는데 두가지가 어떤 차이점이 있는지 알아보자.

#### 1. Memory Cache
Memory Cache는 어플리케이션 내에 존재하는 메모리에 비트맵을 캐싱하고, 필요할 때 빠르게 접근가능하다. 하지만 Memory Cache도 곧 어플리케이션 용량을 키우는 주범이 될 수 있기 때문에 많은 캐싱을 요구하는 비트맵의 경우에는 Disk Cache에 넣는 것이 더 좋을 수 있다.


#### 2. Disk Cache
Memory Cache에 넣기엔 많은 캐시를 요구하는 경우, 혹은 앱이 백그라운드로 전환되어도 적재한 캐시가 삭제되지 않기를 바란다면 Disk Cache를 이용하는 것이 좋다. 하지만 Disk로부터 캐싱된 비트맵을 가져올 때는 Memory에서 로드하는 것보다 오랜시간이 걸린다.


### BitmapPool
Memory Cache의 예시를 위해 소개할 것은 `BitmapPool`이다. `BitmapPool`의 원리는 사용하지 않는 Bitmap을 리스트에 넣어놓고, 추후에 동일한 이미지를 로드할 때 다시 메모리에 적재하지 않고 pool에 있는 이미지를 가져와 재사용하는 것이다. 

보통 BitmapPool을 이용해 재사용 Pool을 만들게 될 때, LRU 캐싱 알고리즘으로 구현된 LinkedList `(lruBItmapList)`와 Byte Size 순으로 정렬된 LinkedList`(bitmapList)`를 사용하여 구현하게 된다. 이 둘은 들어있는 비트맵의 순서만 다를 뿐, 같은 비트맵이 담기게된다.

``` kotlin
private val lruBitmapList = LinkedList<Bitmap>()
private val bitmapList = LinkedList<Bitmap>()
```

LRU 알고리즘을 이용해 오랫동안 참조되지않은 비트맵 객체는 맨 뒤로 밀리게되고, `맨 뒤에있는 객체를 회수`하면서 BitmapPool을 유지시키는 것이다. LRU 알고리즘을 이용하지 않는다면 처음 BitmapPool이 가득 찰 때까지는 문제없이 동작하지만, 비트맵을 재사용하는 시점부터는 특정 비트맵만 재사용될 수 있으며, 앱이 끝날 때까지 메모리가 줄어들지 않게된다. 자세한 내용은 [이 블로그](https://jamssoft.tistory.com/195)를 참고하길 바란다. :) 



대표적인 이미지 로더 라이브러리인 `Glide`에서 구현한 LruBitmapPool Class 내부를 보며, LruBitmapList와 bitmapList가 어떻게 쓰이고있는지 살펴보자. 

``` java
public class LruBitmapPool implements BitmapPool {
    private static final String TAG = "LruBitmapPool";
    private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;

    private final LruPoolStrategy strategy;
    private final Set<Bitmap.Config> allowedConfigs;
    private final BitmapTracker tracker;

    // Pool에 들어올수 있는 최대 크기
    private long maxSize;
    // 현재 pool에 들어간 bitmap byte 크기
    private long currentSize;

  // ...
```

Glide내 LruBitmapPool Class 에서는 strategy(LruPoolStrategy)가 곧 LRU 기반으로 구현된 리스트이며, tracker(BitmapTracker)가 Bitmap size 순으로 정렬된 리스트이다.

``` java
@Override
public synchronized void put(Bitmap bitmap) {
    if (bitmap == null) {
        throw new NullPointerException("Bitmap must not be null");
    }
    // isRecycled: 재활용된 bitmap인지 여부
    if (bitmap.isRecycled()) {
        throw new IllegalStateException("Cannot pool recycled bitmap");
    }
    // isMutable: canvas를 얻을 수 있는 bitmap 인지 여부.
    if (!bitmap.isMutable()
        || strategy.getSize(bitmap) > maxSize
        || !allowedConfigs.contains(bitmap.getConfig())) {
        
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(
                TAG,
                "Reject bitmap from pool"
                + ", bitmap: "
                + strategy.logBitmap(bitmap)
                + ", is mutable: "
                + bitmap.isMutable()
                + ", is allowed config: "
                + allowedConfigs.contains(bitmap.getConfig()));
        }
        bitmap.recycle();
        return;
    }
    // pool에 넣으려는 bitmap의 사이즈를 얻어온다.
    final int size = strategy.getSize(bitmap);
    strategy.put(bitmap);
    tracker.add(bitmap);

    puts++;
    currentSize += size;

    if (Log.isLoggable(TAG, Log.VERBOSE)) {
        Log.v(TAG, "Put bitmap in pool=" + strategy.logBitmap(bitmap));
    }
    dump();

    evict();
}
```

Bitmap을 Pool에 넣을 수 있는 조건을 충족시킨다면, strategy & tracker에 bitmap이 들어가게된다. 

> **참고** Lru 기반인 strategy 는 최근에 들어온 bitmap일수록 리스트의 맨 앞으로 배치시켜야하는데, 그 로직이 `LruPoolStrategy` 구현체 내부에 존재하게 된다.

```java
// Pool의 최대 사용량을 넘기면 크기를 줄여준다.
private synchronized void trimToSize(long size) {
    while (currentSize > size) {
        //LRU List에서 삭제한다.
        final Bitmap removed = strategy.removeLast();
        if (removed == null) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Size mismatch, resetting");
                dumpUnchecked();
            }
            currentSize = 0;
            return;
        }
        // BitmapList에서 삭제한다. 
        tracker.remove(removed);
        currentSize -= strategy.getSize(removed);
        evictions++;
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Evicting bitmap=" + strategy.logBitmap(removed));
        }
        dump();
        removed.recycle();
    }
}
```

trimToSize()를 이용하면 pool이 최대사용량을 넘어선 경우 참조를 가장 적게하고 있는 lastIndex 부터 객체를 지워가며 크기를 줄여줄 수 있다. 

```java
@Override
@Nullable
public Bitmap get(int width, int height, Bitmap.Config config) {
    final int size = Util.getBitmapByteSize(width, height, config);
    Key key = keyPool.get(size);

    Integer possibleSize = sortedSizes.ceilingKey(size);
    if (possibleSize != null && possibleSize != size && possibleSize <= size * MAX_SIZE_MULTIPLE) {
        keyPool.offer(key);
        key = keyPool.get(possibleSize);
    }

    final Bitmap result = groupedMap.get(key);
    // 비트맵을 찾은 경우, pool에서 제거한다.
    if (result != null) {
        result.reconfigure(width, height, config);
        decrementBitmapOfSize(possibleSize);
    }
    return result;
}
```

이미지를 로드하기 위해 pool에서 필요한 비트맵을 가져온다.

## Jetpack Compose에서 이미지 라이브러리 사용하기

Google 에서는 Jetpack Compose를 보다 편하게 사용하기 위한 라이브러리를 묶어서 제공한다.

**Accompanist** 라고 하는 GroupId를 가진 라이브러리 모음이다.

> **참고** [Google#Accompanist Repository](https://github.com/google/accompanist)

Accompanist에서는 Glide와 Coil을 지원하고 있다.

의존성 설정은 아래와 같다.

```gradle
dependencies {
    ...
    implementation "com.google.accompanist:accompanist-glide:0.11.1"
    implementation "com.google.accompanist:accompanist-coil:0.11.1"
    ...
}
```

Glide는 아래와 같이 적용한다.

```kotlin
Image(
    painter = rememberGlidePainter(
        request = "https://picsum.photos/300/300",
        // placeHolder
    ),
    contentDescription = stringResource(R.string.image_content_desc)
)
Image(
    painter = rememberGlidePainter(
        request = "https://cataas.com/cat/gif",
        // placeHolder
    ),
    contentDescription = null
)
```

Coil은 아래와 같이 적용한다.

```kotlin
Image(
    painter = rememberCoilPainter(
        request = "https://picsum.photos/300/300",
        // placeHolder
    ),
    contentDescription = null
)
```

Coil에서 gif를 렌더링하기 위해선 `coil-gif` 의존성을 추가한다.

```gradle
dependencies {
    ...
    implementation "com.google.accompanist:accompanist-glide:0.11.1"
    implementation "com.google.accompanist:accompanist-coil:0.11.1"
    // Coil 에서 gif 를 사용하기 위해서 추가
    implementation "io.coil-kt:coil-gif:1.2.2"
    ...
}
```

아래와 같이 별도의 ImageLoader를 설정한다.

```kotlin
fun gifImageLoader(context: Context) = ImageLoader.Builder(context)
    .componentRegistry {
    if (SDK_INT >= 28) {
        add(ImageDecoderDecoder(context))
    } else {
        add(GifDecoder())
        }
    }
    .build()
```

Image의 적용은 아래와 같이 적용한다.

```kotlin
Image(
    painter = rememberCoilPainter(
        request = "https://cataas.com/cat/gif",
        imageLoader = gifImageLoader(LocalContext*.current),
    ),
    contentDescription = null
)
```

---

해당 포스트는 아래 팀원들과 함께 작성되었습니다.

- 곽욱현 @Knowre
- 김남훈 @Naver
- 배희성 @Rocketpunch
- 송시영 @Smartstudy
- 옥수환 @Naver
- 이기정 @Banksalad
- 정세희 @Banksalad

함께 공부하고 싶으신 분들은 [여기](https://github.com/AndroidDeepDive/Contact-Us/issues)에 이슈를 등록해주세요!
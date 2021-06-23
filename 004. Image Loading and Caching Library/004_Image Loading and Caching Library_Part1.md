# Image Loading and Caching Library Part 1 - Why use?

Android에서는 Image를 나타내기 위해 ImageView라는 위젯을 사용한다.

ImageView가 무엇인지 간단하게 알아보자.

### android.widget.ImageView

```kotlin
kotlin.Any
  ㄴ android.view.View
    ㄴ android.widget.ImageView
```

ImageView는 View를 상속받아 구현된 Image를 보여주기 위한 위젯이다.

`android.graphics.Bitmap`나 `android.graphics.drawable.Drawable` 리소스를 표현해줄 수 있으며, tint 처리나 스케일링에 대한 처리에 일반적으로 사용된다.

아래 xml 코드가 ImageView의 일반적인 쓰임새를 나타낸다.

```js
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/my_image"
        android:contentDescription="@string/my_image_description"/>
</LinearLayout>
```

> **참고** [Android Developers#Drawble Resources](https://developer.android.com/guide/topics/resources/drawable-resource.html)

> **참고** [Android Developers#Handling bitmaps](https://developer.android.com/topic/performance/graphics/index.html)

### Bitmap

```kotlin
kotlin.Any
  ㄴ android.graphics.Bitmap
```

흔히 `.bmp` 확장자로 알려진 이미지 파일 포맷이다.

사이즈가 정해진 이미지를 픽셀들의 조합으로 표현하는 방식이다.

이 사이즈만큼의 픽셀 정보를 다 저장해야하기 때문에 상대적으로 용량이 크고 처리 속도가 느리므로, Android 개발시 메모리 관리에 신경써주어야 하는 부분이 많다.

흔히 볼 수 있는 확장자인 `.jpg`, `.jpeg`, `.png`, `.gif` 파일들이 비트맵 방식으로 이루어진 이미지 파일이다.

## Android에서 이미지를 렌더링하려면?

Android Platform에서 이미지를 렌더링하려면 어떤 걸 고려해야할까?

먼저 예제를 작성해보자.

Android Studio에서 **Empty Activity** 으로 프로젝트를 생성한 후, Layout을 아래와 같이 수정하였다.

```js
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_launcher_foreground"
        android:background="@drawable/ic_launcher_background"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

빌드 후 에뮬레이터로 실행하면 아래와 같이 렌더링 된다.

<img src="https://drive.google.com/uc?export=view&id=1Q_7IFYTtCi774NbQpodP12KE_oeJ1miE" width="350">

코드레벨에서 분석하기 위해 xml을 수정하고 ImageView의 소스를 코드로 주입해보자.

코드는 간단하게 아래와 같다.

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var imageView: ImageView = findViewById(R.id.imageView)
        imageView.background = getDrawable(R.drawable.ic_launcher_background)
        imageView.setImageResource(R.drawable.ic_launcher_foreground)
    }
}
```

여기서 `imageView` 변수의 `background` 속성은 `View`에 해당하는 것이므로 제외하고, `ImageView` 위젯의 메서드인 `setImageResource`에 대해서 탐구해보자.


```java
// ImageView#setImageResource
@android.view.RemotableViewMethod(asyncImpl="setImageResourceAsync")
public void setImageResource(@DrawableRes int resId) {
  // The resource configuration may have changed, so we should always
  // try to load the resource even if the resId hasn't changed.
  final int oldWidth = mDrawableWidth;
  final int oldHeight = mDrawableHeight;

  updateDrawable(null);
  mResource = resId;
  mUri = null;

  resolveUri();

  if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
    requestLayout();
  }
  invalidate();
}
```

`ImageView`에는 위 메서드와 유사한 메서드들이 더 존재하고 있다.

```java
// ImageView#setImageBitmap
@android.view.RemotableViewMethod
public void setImageBitmap(Bitmap bm) {
  // Hacky fix to force setImageDrawable to do a full setImageDrawable
  // instead of doing an object reference comparison
  mDrawable = null;
  if (mRecycleableBitmapDrawable == null) {
      mRecycleableBitmapDrawable = new BitmapDrawable(mContext.getResources(), bm);
  } else {
      mRecycleableBitmapDrawable.setBitmap(bm);
  }
  setImageDrawable(mRecycleableBitmapDrawable);
}
```

```java
// ImageView#setImageDrawable
public void setImageDrawable(@Nullable Drawable drawable) {
  if (mDrawable != drawable) {
    mResource = 0;
    mUri = null;

    final int oldWidth = mDrawableWidth;
    final int oldHeight = mDrawableHeight;

    updateDrawable(drawable);

    if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
      requestLayout();
    }
    invalidate();
  }
}
```

```java
// ImageView#setImageURI
@android.view.RemotableViewMethod(asyncImpl="setImageURIAsync")
public void setImageURI(@Nullable Uri uri) {
  if (mResource != 0 || (mUri != uri && (uri == null || mUri == null || !uri.equals(mUri)))) {
    updateDrawable(null);
    mResource = 0;
    mUri = uri;

    final int oldWidth = mDrawableWidth;
    final int oldHeight = mDrawableHeight;

    resolveUri();

    if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
      requestLayout();
    }
    invalidate();
  }
}
```

어떤 방식을 선택하더라도 `ImageView`에 비트맵을 밀어넣으려하면 비슷비슷한 로직들을 수행하고 있다.

1. 이미지의 가로 및 세로 길이를 획득한다.
2. `requestLayout()` 메서드를 호출한다.
3. `invalidate()` 메서드를 호출한다.

`requestLayout()`와 `invalidate()` 모두 `View`에 속해있는 메서드이다.

결국 주어진 이미지 리소스를 뷰의 사이즈를 측정한 뒤 onDraw하는 역할을 공통적으로 수행하는 것이다.

이렇게만 보면 간단하지만, Android에서 `Bitmap` 또는 `Drawable`과 같은 리소스를 사용할 때엔 아래와 같은 사항을 고려해야 한다.

- `Bitmap` 은 쉽게 앱의 메모리한도를 고갈시킬 수 있다. 예를 들어 픽셀 폰은 카메라 사진이 4048x3036 픽셀(12 메가픽셀)까지 찍을수 있다. `Bitmap` 구성이 ARGB_8888 인 경우, 기본적으로 안드로이드 2.3 (API level 9) 이상에서는 하나의 사진을 메모리에 로딩하기 위해 48MB 의 메모리를 차지하게 된다. 이렇게 큰 메모리를 요구하면 앱에서 사용할 수 있는 모든 메모리를 즉시 사용하게 될 수 있다.

- UI 스레드에서 비트맵을 로딩하는 것은 앱의 성능을 저하되어 늦은 응답성 또는 ANR 메시지와 같은 원인이 된다. 따라서 `Bitmap` 을 작업할 때에는 스레드를 적절하게 관리하는 것이 중요하다.

- 앱에서 여러 `Bitmap` 을 메모리에 로딩할 때에는, 메모리 관리와 디스크 캐싱이 필요하다. 그렇지 않으면, 앱의 응답성과 유동성이 나빠질 수 있다.

- 앱에서 `Bitmap` 을 가져와서 디코딩하고 표시하기 위해서는 이미지 라이브러를 사용하는 것이 좋다. 이미지 라이브러리는 `Bitmap` 과 관련된 다양하고 복잡한 과정을 대신 관리해주며, 손쉽게 사용할 수 있도록 되어있다.


고려사항을 주제별로 쪼개어서 조금 더 명세해보자.

### ImageView를 사용할때 고려해야할 점

#### Out of memory

Android에서 가장 중요한 것 중 하나는 메모리를 관리하는 것이다.

카메라 하드웨어의 발달로 사진 한 장의 사이즈가 어마어마하게 커졌는데, 이걸 사용자에게 보여준다고 생각한다면 OOM 이슈는 개발자로서 꼭 피해야할 요소일 것이다.

아무리 큰 사이즈의 이미지여도 사용자에게 보여지는 사이즈엔 한계가 있고, 특히 썸네일의 경우 더더욱 고화질일 필요가 없으므로 다운샘플링을 통해 적당한 사이즈의 이미지로 가공하는 테크닉이 필요하다.

#### Slow Loading

Bitmap 이미지가 지나치게 크면 이미지를 보여주는 데 많은 시간이 걸리게 되고, 이는 사용자로 하여금 로딩 속도가 느려진 듯한 불쾌한 경험을 선사하게 된다.

비단 사이즈 뿐만 아니라, 이미지를 다운로드 받는 시간과 불필요한 디코딩 등 여러가지 원인이 존재할 수 있다.

#### Bitmap Caching (Memory / Disk cache)

반복적으로 노출되는 이미지에 쓰이는 작업을 줄이는 방법은 해당 이미지의 비트맵을 캐싱해두는 것이다.

예를 들어 외부 url을 통해 다운로드 받은 이미지를 캐싱해둔다면, 동일한 url에서 이미지를 다시 그려줄 때에 해당 캐시에서 바로 가져와 사용할 수 있을 것이다.

Android에서 캐싱을 처리하기 위해선 먼저 Memory를 먼저 확인한 뒤, 그 다음에 Disk를 검증하도록 처리할 수 있다.

#### LRU Cache

Android에서는 `LruCache`라는 객체를 제공해주고 있다.

이 객체를 이용해 비트맵 캐시를 생성해서 작업할 수 있다.

가장 최근에 불러온 이미지를 가장 앞으로 불러오는 식으로 캐시 적중률을 올리는 알고리즘인 **LRU Algorithm**으로 동작하며, 4Mib(메비바이트) 단위로 캐시 사이즈가 제한되어 있다.

> **참고** [Android Developers#LruCache](https://developer.android.com/reference/android/util/LruCache)

> **참고** [androidx.collection.LruCache](https://developer.android.com/reference/androidx/collection/LruCache)

> **참고** [Cache replacement policies#recently used](https://en.wikipedia.org/wiki/Cache_replacement_policies#Least_recently_used_(LRU))

#### Unresponsive UI / Gabarge collector

Bitmap의 사이즈가 지나치게 크다면 해당 Bitmap을 처리하기 위해 많은 작업이 필요하게 된다.

ui thread에서의 과도한 작업은 결국 끊어지는 듯한 ui를 사용자에게 보여주게 될 것이며, 만약 16ms 이상 걸리는 작업이라면 Android OS가 해당 작업의 프레임을 건너뛰는 이슈를 겪게 될 것이다.

### Image Library 소개

Android에서 ImageView를 통해 Bitmap을 단순히 보여주기 위해서도 고려해야할 점, 처리해야할 문제들이 매우 많다.

이 많은 문제들을 한 번에 다 처리할 수 있는 방법은 바로 **검증된 이미지 라이브러리를 사용**하는 것이다.

간단하게 Android에서 자주 쓰이는 이미지 라이브러리들의 리스트를 살펴보자.

#### 1. Picasso

![](https://square.github.io/picasso/static/sample.png)

- 개발 주체 : [Square Open Source](https://square.github.io)
- 웹 사이트 : https://square.github.io/picasso/
- 주요 기능
  - 이미지 재활용
  - 다운로드 취소 처리
  - 최소한으로 사용하는 메모리
  - 이미지 변환
  - 메모리 및 디스크 캐싱 자동화


#### 2. Glide

![](https://github.com/bumptech/glide/blob/master/static/glide_logo.png?raw=true)

- 개발 주체 : Bump Technologies
- 웹 사이트 : https://bumptech.github.io/glide/
- 주요 기능
  - 미디어 디코딩
  - `LruResourceCache` 및 `MemorySizeCalculator`를 사용한 메모리 캐싱
  - `Lru` 기반의 디스크 캐싱 (기본값 250mb)
  - 리소스 풀링을 위한 간단한 인터페이스 제공
  - `RGB_565`의 기본 포맷 사용
  - gif, jpeg, war, png, webp 포맷 지원

### Coil (Coroutine Image Loader)

![](https://github.com/coil-kt/coil/raw/main/logo.svg)

- 개발 주체 : [Instacart](https://www.instacart.com)
- 웹 사이트 : https://github.com/coil-kt/coil
- 주요 기능
  - `MemoryCache.Key`를 사용한 메모리 캐싱
  - `OkHttpClient` 기반의 디스크 캐싱 (`CoilUtils.createDefaultCache`에서 옵션 설정)
  - 다운 샘플링
  - 이미지 재사용
  - 일시정지 및 취소의 자동화를 비롯한 최적화 작업
  - Corotines, Okio 등의 최신 라이브러리 사용
  - bmp, jpeg, png, webp 포맷 지원
  - Android 8.0 이상에서 heif 포맷 지원
  - `coil-gif` 의존성 추가시
    - Android 9.0 이상에서 animated webp 포맷 지원 
    - Android 11.0 이상에서 animated heif 지원 ()
  - `coil-svg` 의존성 추가시 svg 포맷 지원
  - `coil-video` 의존성 추가시 Android가 지원하는 모든 비디오 코덱으로부터 정적 frame 획득 가능



### Fresco

<img alt="Fresco Logo" src="https://github.com/facebook/fresco/raw/master/docs/static/sample-images/fresco_logo.svg" width="15%" />

- 개발 주체 : [Facebook](https://developers.facebook.com)
- 웹 사이트 : https://frescolib.org
- 주요 기능
  - 압축된 이미지를 통한 메모리 사용 최소화
  - 이미지 파이프라인을 통한 최적화
  - Drawees를 사용한 이미지 렌더링
  - URI 지정을 통한 점진적 jpeg 이미지 개선
  - Android 4.0 이하에서 OOM을 최소화
  - 다운로드중일 경우 PlaceHolder 제공

### AMUL (Android-Universal-Image-Loader)

![](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/sample/src/main/res/drawable-mdpi/ic_launcher.png)

- 개발 주체 : [Sergey Tarasevich(User)](https://github.com/nostra13/Android-Universal-Image-Loader)
- 웹 사이트 : https://github.com/nostra13/Android-Universal-Image-Loader/wiki
- 주요 기능
  - 동기/비동기 기반의 멀티스레드 이미지 로딩
  - Thread / Downloader / Decoder에 대한 광범위한 커스터마이징
  - 메모리 및 디스크 캐싱 자동화
  - 다운로드 진행률 제공


## 대표적인 라이브러리들의 간단한 성능 비교

### Picasso

Picasso 는 번거로움 없이 종종 단 한줄의 코드만으로도 이미지를 로딩할 수 있게 해준다. 

어댑터에서 이미지뷰를 재사용하고 다운로드와 취소할 수 있다. 적은 메모리 사용으로 복잡한 이미지 변환을 할 수 있다. 자동 메모리와 디스크 캐싱을 지원한다.

또한 Picasso는 최소한의 메모리로 이미지의 다양한 Transformation을 지원하며, 자동으로 메모리 & 디스크 캐싱, 어댑터에서 ImageView를 재활용 및 다운로드 취소가 가능하다는 점을 강조하고 있다.

| MinSdkVersion | CompileSdkVersion | AAR Size | 비고 |
| :--: | :--: | :--: | :--: |
| 14 | 29 | 105kb | GIF를 지원하지 않음 |

``` kotlin
Picasso.get().load(url)
    .transform(RoundedCornersTransformation(128, 3))
    .into(imageView5, object : Callback {
        override fun onSuccess() {
            toast("Complete")
        }

        override fun onError(e: Exception?) {
            TODO("Not yet implemented")
        }
    })
```

| 최초 로딩 속도 : 6.4s | 기본 캐싱 적용 : 1.6s |
| :--: | :--: |
| ![](https://images.velog.io/images/jshme/post/3dd57603-26d9-4738-8b87-75c60aa6c6a6/picasso%20test.gif) | ![](https://images.velog.io/images/jshme/post/243acd77-60b0-4dc4-b3f6-b47eefed1496/picasso%20caching.gif) |

Picasso는 원본 이미지 크기를 그대로 비트맵에 그린 후에 이미지뷰에 적용한다. 아래와같이  `1000 * 800` 의 이미지가 존재할 때, Bitmap 에 `1000 * 800 * 4bytes` = 3MB 가 ImageView 위에 올라갈 것이다. 그렇기 때문에 고화질의 이미지를 로드한다면 OOM을 발생시킬 수 있다. 

![](https://images.velog.io/images/jshme/post/cc8746a5-0a89-4267-a6ff-a9cda8a99a14/IMG_9F5E046409C4-1.jpeg)

이 문제를 방지하기 위해 `fit()` 함수를 이용한다면 고화질 이미지를 로드하기 전 이미지뷰의 크기를 먼저 측정하기 때문에 메모리 사용량을 최소화할 수 있을 것이다.

```kotlin
Picasso.get()
    .fit()
    .transform({...})
    .load(url)
```

#### Heap Dump
![](https://images.velog.io/images/jshme/post/abb805e7-0a7f-4fcf-a85e-270163e7adea/image.png)

10,136,858 byte (= 10MB)

### Glide

Google에서 만든 이미지 로더 라이브러리인 Glide는 빠른 이미지 로딩, 버벅 거림과 끊김 현상이 발생하지 않는다는 점을 강조하고 있다. 

미디어 디코딩, 메모리 및 디스크 캐싱 그리고 리소스 풀링을 간단하고 사용하기 쉽게 인터페이스로 래핑하였으며 페치, 디코딩, 그리고 비디오스틸, 이미지, 움직이는 GIF 를 표시할 수 있다. 

커스텀된 HttpUrlConnection 을 기본 스택으로 사용하지만 거의 모든 네트워크 스택에 연결할 수 있는 유연한 API 를 포함하고 있어, Volly 또는 OkHttp 라이브러리를 대신 사용할 수 있다. 

| MinSdkVersion | CompileSdkVersion | AAR Size | 비고 |
| :--: | :--: | :--: | :--: |
| 14 | 26 | 625kb | |

Glide는 아래처럼 싱글톤으로 만들어 간단하게 사용할 수 있다. 

```kotlin
Glide.with(this)
    .load(url)
    .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(128, 3)))
    .listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            TODO("Not yet implemented")
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            toast("Complete")
            return false
        }
    })
    .into(imageView)
```

| 최초 로딩 속도 : 6.2s | 기본 캐싱 적용 : 0.72s |
| :--: | :--: |
| ![](https://images.velog.io/images/jshme/post/bc24b36a-8b2e-49c1-89ef-192062e2f417/glide%20test.gif) | ![](https://images.velog.io/images/jshme/post/2641ff8f-323f-4193-a501-a4b241048cc6/glide%20caching.gif) |

Picasso와는 달리 Glide는 이미지뷰의 크기를 측정한 다음 원본이미지를 가져와 이미지 뷰 크기에 맞게 리사이징 후 비트맵에 그려주는 것을 기본 옵션으로 하기 때문에 메모리 효율성이 Picasso보다 좋다.

![](https://images.velog.io/images/jshme/post/ef838ebb-0dd2-4607-af11-62ba316cd145/IMG_8FF7402ED110-1.jpeg)

Glide가 언급한 이미지 라이브러리 중에서 완벽하게 Gif를 지원하는 것이 특징이다.

Picasso는 미지원, Coil은 `1.2.2` 기준 확장 라이브러리로 Gif를 지원하지만 실제 사용해보니 라이브러리 자체가 불안정했으며, Fresco는 Gif에 Transformation와 같은 다양한 옵션을 적용할 수는 없었다.)

![](https://images.velog.io/images/jshme/post/d3750800-5347-4f63-b956-369abbab76c6/glide%20gif%20.gif)

#### Heap Dump

![](https://images.velog.io/images/jshme/post/179bed8b-6c74-4b2f-b27c-3a4e232b91da/image.png)

11,004,024 byte (= 11MB)


### Coil

Instacart에서 만든 Coil 은 Coroutine Image Loading의 줄임말로 위에 설명했던 이미지로더 라이브러리와 달리 코틀린 & 코루틴으로 구성되어있다. 제일 장점으로는 라이브러리가 거의 100% 코틀린으로 이루어졌다는 점과 AndroidX, OkHttp 등 현업에서 많이 쓰이고있는 라이브러리들을 지원하고 있다는 점이다. Coil 라이브러리 내부를 살펴보면, Glide와 굉장히 비슷하다는 것을 알 수 있는데 Glide를 많이 `벤치마킹`했다고 한다 👀. 또한 ImageView의 확장함수로 지원하고, 코틀린의 매력인 함수형 언어 덕으로 다른 라이브러리보다 `더욱 간결한` 코드를 구성할 수 있다.

| MinSdkVersion | CompileSdkVersion | AAR Size | 비고 |
| :--: | :--: | :--: | :--: |
| 14 | 30 | 16kb | |


``` kotlin
imageView.load(url) {
    listener(
        onError = { _,_ -> /** Show toast. */ },
        onSuccess = { _,_ -> toast("Complete") }
    )
    transformations(RoundedCornersTransformation(128f))
}
```

| 최초 로딩 속도 : 5.24s | 기본 캐싱 적용 : 1.3s |
| :--: | :--: |
| ![](https://images.velog.io/images/jshme/post/8af3e7e7-5c91-4633-b221-b3bde98bcb61/coil%20test.gif) | ![](https://images.velog.io/images/jshme/post/eda9f834-16c4-43f0-b2ec-5b37f1a6ef2c/coil%20caching.gif) |

#### Heap Dump

![](https://images.velog.io/images/jshme/post/28120e8c-aabc-4d6a-8af6-10f8d777e05a/image.png)

6,995,072 byte (= 7MB)


좀 더 자세한 퍼포먼스 테스트는 Part 3에서 다루도록 하자.

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
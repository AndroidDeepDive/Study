## 이미지 라이브러리를 왜 사용해야하는가?

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

이렇게만 보면 간단하지만, Android에서 이미지를 관리하기 위해선 아래와 같은 문제점들을 고려해야 한다.

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
  - 메모리 및 디스크 캐싱 자동화
  - 리소스 풀링을 위한 간단한 인터페이스 제공

### Coil (Coroutine Image Loader)

![](https://github.com/coil-kt/coil/raw/main/logo.svg)

- 개발 주체 : [Instacart](https://www.instacart.com)
- 웹 사이트 : https://github.com/coil-kt/coil
- 주요 기능
  - 메모리 및 디스크 캐싱 자동화
  - 다운 샘플링
  - 이미지 재사용
  - 일시정지 및 취소의 자동화를 비롯한 최적화 작업
  - Corotines, Okio 등의 최신 라이브러리 사용


### Fresco

<img alt="Fresco Logo" src="https://github.com/facebook/fresco/raw/master/docs/static/sample-images/fresco_logo.svg" width="15%" />

- 개발 주체 : [Facebook](https://developers.facebook.com)
- 웹 사이트 : https://frescolib.org
- 주요 기능
  - 압축된 이미지를 통한 메모리 사용 최소화
  - 이미지 파이프라인을 통한 최적화
  - Drawees를 사용한 이미지 렌더링
  - URI 지정을 통한 점진적 jpeg 이미지 개선

### AMUL (Android-Universal-Image-Loader)

![](https://github.com/nostra13/Android-Universal-Image-Loader/raw/master/sample/src/main/res/drawable-mdpi/ic_launcher.png)

- 개발 주체 : [Sergey Tarasevich(User)](https://github.com/nostra13/Android-Universal-Image-Loader)
- 웹 사이트 : https://github.com/nostra13/Android-Universal-Image-Loader/wiki
- 주요 기능
  - 동기/비동기 기반의 멀티스레드 이미지 로딩
  - Thread / Downloader / Decoder에 대한 광범위한 커스터마이징
  - 메모리 및 디스크 캐싱 자동화
  - 다운로드 진행률 제공
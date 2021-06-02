# Image Loader Library 톺아보기

안드로이드에서 사용할 수 있는 많은 이미지 로더 라이브러리가 존재한다. 개발자가 어떠한 라이브러리 없이 이미지를 로드하려고 한다면 고려해야하는 요소가 많이 존재하게되는데, 이를 third party에게 위임함으로서 걱정을 좀 덜게될 수 있다. 
대표적인 라이브러리의 종류로는 예전에 많이 쓰이던 AUIL부터 (but he is..👋), 현재에도 쓰이고 있는 Piccaso, Glide, 떠오르는 신흥강자 Coil 등등 많이 존재하는데 각각의 라이브러리들은 어떻게 사용하는지 간단하게 적어볼 예정이다.

<br>

# 1️⃣ Piccaso
>MinSdkVersion = 14
>CompileSdkVersion = 29

Square에서 만든 Piccaso는 최소한의 메모리로 이미지의 다양한 Transformation을 지원하며, 자동으로 메모리 & 디스크 캐싱, 어댑터에서 ImageView를 재활용 및 다운로드 취소가 가능하다는 점을 강조하고 있다.

``` kotlin
Picasso.get()
  .load(url)
  .centerCrop()
  .into(imageView)
```

<br>

# 2️⃣ Glide
>MinSdkVersion = 14
>CompileSdkVersion = 26

Google에서 만든 이미지 로더 라이브러리인 Glide는 빠른 이미지 로딩, 버벅 거림과 끊김 현상이 발생하지 않는다는 점을 강조하고 있다. 커스텀된 `HttpUrlConnection`을 사용하지만 Volley 또는 OkHttp 라이브러리에 연결할 수 있는 유틸리티 라이브러리도 포함되어있어, 우리가 자주 사용하고 있는 라이브러리와 호환성이 좋다.
Glide는 아래처럼 싱글톤으로 만들어 간단하게 사용할 수 있다. 

``` kotlin
  Glide
    .with(this)
    .load(url)
    .centerCrop()
    .into(imageView)
```

<br>

# 3️⃣ Coil
>MinSdkVersion = 14
>CompileSdkVersion = 30

Instacart에서 만든 Coil 은 Coroutine Image Loading의 줄임말로 위에 설명했던 이미지로더 라이브러리와 달리 코틀린 & 코루틴으로 구성되어있다. 제일 장점으로는 라이브러리가 거의 100% 코틀린으로 이루어졌다는 점과 AndroidX, OkHttp 등 현업에서 많이 쓰이고있는 라이브러리들을 지원하고 있다는 점이다. Coil 라이브러리 내부를 살펴보면, Glide와 굉장히 비슷하다는 것을 알 수 있는데 Glide를 많이 `벤치마킹`했다고 한다 👀. 또한 ImageView의 확장함수로 지원하고있어, 다른 라이브러리보다 더욱 간결하게 이미지 로딩을 위한 코드를 구성할 수 있다.

``` kotlin
imageView.load(R.drawable.image)
```
<br>
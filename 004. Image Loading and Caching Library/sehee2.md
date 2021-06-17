# Image Loader Library 톺아보기

안드로이드에서 사용할 수 있는 많은 이미지 로더 라이브러리가 존재한다. 개발자가 어떠한 라이브러리 없이 이미지를 로드하려고 한다면 고려해야하는 요소가 많이 존재하게되는데, 이를 third party에게 위임함으로써 걱정을 좀 덜게될 수 있다. 
대표적인 라이브러리의 종류로는 예전에 많이 쓰이던 AUIL부터 (but he is..👋), 현재에도 쓰이고 있는 Piccaso, Glide, Coil 등이 존재하는데 각각의 라이브러리들은 어떻게 사용하는지, 성능은 어떠한지 간단하게 적어볼 예정이다.

<br>

# 1️⃣ Picasso
>MinSdkVersion = 14
>CompileSdkVersion = 29
>Library Size : 121Kb 
>GIF 지원 ❌

Square에서 만든 Picasso는 최소한의 메모리로 이미지의 다양한 Transformation을 지원하며, 자동으로 메모리 & 디스크 캐싱, 어댑터에서 ImageView를 재활용 및 다운로드 취소가 가능하다는 점을 강조하고 있다.

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
|                    최초 로딩 속도 : 6.4s                     |                    기본 캐싱 적용 : 1.6s                     |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://images.velog.io/images/jshme/post/3dd57603-26d9-4738-8b87-75c60aa6c6a6/picasso%20test.gif) | ![](https://images.velog.io/images/jshme/post/243acd77-60b0-4dc4-b3f6-b47eefed1496/picasso%20caching.gif) |

<br>

Picasso는 원본 이미지 크기를 그대로 비트맵에 그린 후에 이미지뷰에 적용한다. 아래와같이  `1000 * 800` 의 이미지가 존재할 때, Bitmap 에 `1000 * 800 * 4bytes` = 3MB 가 ImageView 위에 올라갈 것이다. 그렇기 때문에 고화질의 이미지를 로드한다면 OOM을 발생시킬 수 있다. 

![](https://images.velog.io/images/jshme/post/cc8746a5-0a89-4267-a6ff-a9cda8a99a14/IMG_9F5E046409C4-1.jpeg)

이 문제를 방지하기 위해 `fit()` 함수를 이용한다면 고화질 이미지를 로드하기 전 이미지뷰의 크기를 먼저 측정하기 때문에 메모리 사용량을 최소화할 수 있을 것이다.

```kotlin
        Picasso.get()
        .fit()
        .transform({...})
        .load(url)
```
<br>

### 💁 Heap Dump
![](https://images.velog.io/images/jshme/post/abb805e7-0a7f-4fcf-a85e-270163e7adea/image.png)

10,136,858 byte (= 10MB)


<br>


# 2️⃣ Glide
>MinSdkVersion = 14
>CompileSdkVersion = 26
>Library Size : 440 Kb
>GIF 지원 ⭕️

Google에서 만든 이미지 로더 라이브러리인 Glide는 빠른 이미지 로딩, 버벅 거림과 끊김 현상이 발생하지 않는다는 점을 강조하고 있다. 커스텀된 `HttpUrlConnection`을 사용하지만 Volley 또는 OkHttp 라이브러리에 연결할 수 있는 유틸리티 라이브러리도 포함되어있어, 우리가 자주 사용하고 있는 라이브러리와 호환성이 좋다.
Glide는 아래처럼 싱글톤으로 만들어 간단하게 사용할 수 있다. 

``` kotlin
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
|                    최초 로딩 속도 : 6.2s                     |                    기본 캐싱 적용 : 0.72s                    |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://images.velog.io/images/jshme/post/bc24b36a-8b2e-49c1-89ef-192062e2f417/glide%20test.gif) | ![](https://images.velog.io/images/jshme/post/2641ff8f-323f-4193-a501-a4b241048cc6/glide%20caching.gif) |

<br>

Picasso와는 달리 Glide는 이미지뷰의 크기를 측정한 다음 원본이미지를 가져와 이미지 뷰 크기에 맞게 리사이징 후 비트맵에 그려주는 것을 기본 옵션으로 하기 때문에 메모리 효율성이 Picasso보다 좋다.

![](https://images.velog.io/images/jshme/post/ef838ebb-0dd2-4607-af11-62ba316cd145/IMG_8FF7402ED110-1.jpeg)


>또한 라이브러리들을 사용해보면서 느낀 점은 Glide가 언급한 이미지 라이브러리 중에서 완벽하게 Gif를 지원하는 것 같다😁 
>(Picasso는 미지원, Coil은 `1.2.2` 기준 확장 라이브러리로 Gif를 지원하지만 실제 사용해보니 라이브러리 자체가 불안정했으며, Fresco는 Gif에 Transformation와 같은 다양한 옵션을 적용할 수는 없었다.) 
>![](https://images.velog.io/images/jshme/post/d3750800-5347-4f63-b956-369abbab76c6/glide%20gif%20.gif)


<br>

### 💁 Heap Dump

![](https://images.velog.io/images/jshme/post/179bed8b-6c74-4b2f-b27c-3a4e232b91da/image.png)

11,004,024 byte (= 11MB)


<br>

# 3️⃣ Coil
>MinSdkVersion = 14
>CompileSdkVersion = 30
>GIF 지원 ⭕️

Instacart에서 만든 Coil 은 Coroutine Image Loading의 줄임말로 위에 설명했던 이미지로더 라이브러리와 달리 코틀린 & 코루틴으로 구성되어있다. 제일 장점으로는 라이브러리가 거의 100% 코틀린으로 이루어졌다는 점과 AndroidX, OkHttp 등 현업에서 많이 쓰이고있는 라이브러리들을 지원하고 있다는 점이다. Coil 라이브러리 내부를 살펴보면, Glide와 굉장히 비슷하다는 것을 알 수 있는데 Glide를 많이 `벤치마킹`했다고 한다 👀. 또한 ImageView의 확장함수로 지원하고, 코틀린의 매력인 함수형 언어 덕으로 다른 라이브러리보다 `더욱 간결한` 코드를 구성할 수 있다.

``` kotlin
        imageView.load(url) {
            listener(
                    onError = { _,_ -> /** Show toast. */ },
                    onSuccess = { _,_ -> toast("Complete") }
                    }
            )
            transformations(RoundedCornersTransformation(128f))
        }
```

|                    최초 로딩 속도 : 5.24s                    |                    기본 캐싱 적용 : 1.3s                     |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![](https://images.velog.io/images/jshme/post/8af3e7e7-5c91-4633-b221-b3bde98bcb61/coil%20test.gif) | ![](https://images.velog.io/images/jshme/post/eda9f834-16c4-43f0-b2ec-5b37f1a6ef2c/coil%20caching.gif) |
<br>

### 💁 Heap Dump

![](https://images.velog.io/images/jshme/post/28120e8c-aabc-4d6a-8af6-10f8d777e05a/image.png)

6,995,072 byte (= 7MB)


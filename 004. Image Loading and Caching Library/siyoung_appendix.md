
## Jetpack Compose 이미지 라이브러리 사용하기

## **Accompanist**

개발자가 일반적으로 필요로 하지만 아직 사용할 수 없는 Jetpack Compose 의 기능을 지원하는 라이브러리 그룹이다. Coil, Glide 라이브러를 사용하게 도와 줄뿐 아니라 여러 레이아웃이나 레이아웃을 설정을 할 수 있게 도와준다. 

accompanist 에서 Glide 와 Coil 을 사용하기 위해서는 아래와 같이 dependency 를 추가한다.

    repositories {
        mavenCentral()
    }
    
    dependencies {
        implementation "com.google.accompanist:accompanist-glide:0.11.1"
        implementation "com.google.accompanist:accompanist-coil:0.11.1"
        // Coil 에서 gif 를 사용하기 위해서 추가
        implementation "io.coil-kt:coil-gif:1.2.2"
    }


아래와 같이 Glide 와 Coil 를 사용하기 위한 코드를 작성할 수 있다.

    *// Glide*
    *Image*(
      painter = *rememberGlidePainter*(
        request = "https://picsum.photos/300/300",
      ),
      contentDescription = null
    *)
    Image*(
      painter = *rememberGlidePainter*(
        request = "https://cataas.com/cat/gif",
      ),
      contentDescription = null
    *)*

    *// Coil
    Image*(
      painter = *rememberCoilPainter*(
        request = "https://picsum.photos/300/300",
      ),
      contentDescription = null
    *)
    Image*(
      painter = *rememberCoilPainter*(
        request = "https://cataas.com/cat/gif",
        imageLoader = *gifImageLoader*(*LocalContext*.current),
      ),
      contentDescription = null
    )

rememberGlidePainter / rememberCoilPainter 는 각각 Glide.kt / Coil.kt 에 Glide 와 Coil 라이브러리를 Composable function 으로 래핑되어 기존의 라이브러리를 Compose 에서도 손쉽게 사용할 수 있도록 되어 있다. 

Coil 에서 gif 를 사용하기 위해서는 아래와 같이 별로의 이미지 로더 설정이 필요하다.

    fun gifImageLoader(context: Context) = ImageLoader.Builder(context)
      .componentRegistry {**
        **if (*SDK_INT *>= 28) {
          add(ImageDecoderDecoder(context))
        } else {
          add(GifDecoder())
          }
      }**
      **.build()



원격으로 이미지를 호출하는 경우, manifest 에 퍼미션을 추가한다. 

    <uses-permission android:name="android.permission.INTERNET" />

간혹 퍼미션을 추가했음에도 데이터를 로드하지 못하는 경우가 발생하면, 앱을 삭제했다가 다시 설치하면 정상적으로 로드할 수 있다.



Accompanist 에서 더 자세한 Glide / Coil 사용법과 기타 다른 라이브러리는 아래 사이트에서 확인할 수 있다.

[https://google.github.io/accompanist/](https://google.github.io/accompanist/)





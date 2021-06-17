
## 어떤 이미지 라이브러리 사용할까?

## Imageview 와 bitmap

안드로이드에서는 bitmap 또는 drawable 와 같은 리소스를 가지고 이미지를 화면에 표시할 수 있다. bitmap 을 로딩해 이미지를 표시하는 할 때 주의해서 사용을 해야한다.

* bitmap 은 쉽게 앱의 메모리한도를 고갈시킬 수 있다. 예를 들어 픽셀 폰은 카메라 사진이 4048x3036 픽셀(12 메가픽셀)까지 찍을수 있다. bitmap 구성이 ARGB_8888 인 경우, 기본적으로 안드로이드 2.3 (API level 9) 이상에서는 하나의 사진을 메모리에 로딩하기 위해 48MB 의 메모리를 차지하게 된다. 이렇게 큰 메모리를 요구하면 앱에서 사용할 수 있는 모든 메모리를 즉시 사용하게 될 수 있다.

* UI 스레드에서 비트맵을 로딩하는 것은 앱의 성능을 저하되어 늦은 응답성 또는 ANR 메시지와 같은 원인이 된다. 따라서 bitmap 을 작업할 때에는 스레드를 적절하게 관리하는 것이 중요하다.

* 앱에서 여러 bitmap 을 메모리에 로딩할 때에는, 메모리 관리와 디스크 캐싱이 필요하다. 그렇지 않으면, 앱의 응답성과 유동성이 나빠질 수 있다.

앱에서 bitmap 을 가져와서 디코딩하고 표시하기 위해서는 이미지 라이브러를 사용하는 것이 좋다. 이미지 라이브러리는 bitmap 과 관련된 다양하고 복잡한 과정을 대신 관리해주며, 손쉽게 사용할 수 있도록 되어있다.



## 안드로이드 이미지 라이브러리

### Picasso

이미지는 컨텍스트와 시각적 감각을 안드로이드 어플리케이션에 추가한다. Picasso 는 번거로움 없이 종종 단 한줄의 코드만으로도 이미지를 로딩할 수 있게 해준다. 어댑터에서 이미지뷰를 재사용하고 다운로드와 취소할 수 있다. 적은 메모리 사용으로 복잡한 이미지 변환을 할 수 있다. 자동 메모리와 디스크 캐싱을 지원한다.

### Glide

글라이드는 빠르고 효율적으로 미디어 관리 오픈소스이다. 미디어 디코딩, 메모리 및 디스크 캐싱 그리고 리소스 풀링을 간단하고 사용하기 쉽게 인터페이스로 래핑된 안드로이드 이미지 로딩 프레임워크이다. 페치, 디코딩, 그리고 비디오스틸, 이미지, 움직이는 GIF 를 표시할 수 있다. 개발자가 거의 모든 네트워크 스택에 연결할 수 있는 유연한 API 를 포함한다. 커스텀된 HttpUrlConnection 을 기본 스택으로 사용하지만, 구글 볼리 프로젝트 또는 스쿼어의 OkHttp 라이브러리를 대신 사용할 수 있다. 기본적으로 어떠한 이미지 목록도 부드럽고 빠르게 스크롤링 하는것이 초점이 맞춰져 있지만, 또한 거의 모든 경우 어디서라도 페치, 리사이즈, 그리고 원격 이미지를 표시가 필요할 때에도 효과적이다.

* 메모리 캐시는 기본적으로 LruResourceCache 방식을 사용하여 메모리 캐시가 구현되어 있으며 디바이스의 메모리 사이즈나 스크린 해상도에 관계 없이 Glide 의 MemorySizeCalculator 에 의해 캐시 사이즈가 계산된다.

* 디스크 캐시 또한 Lru 기반으로 동작하며, 기본적으로 250MB 의 디스크캐시를 가지며, 어플리케이션 폴더내에 특정 디렉토리에 있다.

* Lru 기반의 Bitmap Pool 을 사용하여 스크린 사이즈와 밀도를 기반으로 MemorySizeCalculator 에 의해 Bitmap Pool 사이즈가 정해진다.

* Glide 는 RGB_565 Bitmap 포맷을 기본으로 동작하며, 화질은 다소 떨어지나 메모리 효율은 좀 더 낫다.

* gif, jpeg, war, png, webp 포맷을 지원한다.

### Fresco

프레스코는 안드로이드 어플리케이션에서 이미지를 표시하기 위한 강력한 시스템이다. 이미지 로딩과 표시를 관리한다. 네트워크, 로컬 스토리지 또는 로컬 리소스로부터 이미지를 로드하고 이미지가 로드되기전까지 플레이스홀더를 표시한다. 메모리와 내부 스토리지 두 단계의 캐시를 가지고 있다. 안드로이드 4.0 이하에서, 이미지를 안드로이드 메모리의 특별한 영역에 두어 어플리케이션이 빠르고 OutOfMemoryError 가 적게 발생하도록 한다. 

### Coil

코일은 Coroutine Image Loader 의 약어로서 코틀린 코루틴 기반 만들어진 안드로이드 이미지 라이브러리이다. 메모리, 디스크 캐싱, 메모리에서 이미지의 다운샘플링, 비트맵 재사용, 자동 일시중지 및 취소 등의 많은 최적화 수행으로 빠르다. (앱에 이미 OkHttp 와 코루틴이 포함된 경우) 2000 개의 메소드만을 추가한다. 이는 피카소와 비슷한 수의 메소드를 가지거나 글라이드, 프레스코 현저히 적은 메소드를 가지며 가볍다는 것을 의미한다. API 는 코틀린 언어 기능을 활용하여 단순하고 적은 보일러 플레이트로 사용하기 쉽다. 코틀린 우선으로 코루틴, OkHttp, Okio, AndroidX Lifecycles 등의 모던한 라이브러리를 포함하고 있다.

* Dynamic image sampling 을 지원한다. 500x500 이미지를 100x100 이미지뷰에 로드할 때, Coil 은 100x100 이미지로 미리 로딩하여 우선 placeholder 로 사용한다. 이후에 500x500 이미지가 로딩되어 사용이 가능해지면 500x500 으로 바꾸어 이미지 로딩을 한다.

* 메모리 캐시는 MemoryCache.Key 를 사용하여 로드된 이미지를 메모리 캐시에 저장할 수 있다. 디바이스가 가진 메모리 크기에 따라서 0.15~0.20% 메모리를 디스크 캐시로 사용한다.

* 디스크 캐시는 OkHttpClient 을 기반으로 디스크 캐시를 처리하며 디바이스 남아있는 공간에 따라 최대 10–250MB 까지 정해진다. 커스텀 OkHttpClient 을 설정해 사용하는 경우, CoilUtils.createDefaultCache 을 옵션으로 사용할 수 있으며 다른 크기와 위치를 가지는 캐시 인스턴스를 만들수 있다.

* bmp, jpeg, png, webp, heif (Android 8.0+) 포맷을 지원한다.

* coil-gif 를 추가하면 GIF, animated WebP (Android 9.0+), animated HEIF (Android 11.0+) 을 추가로 사용할 수 있다.

* coil-svg 를 디펜던시로 추가하면 svg 를 사용할 수 있다.

* coil-video 를 디펜던시로 추가하면 안드로이드가 지원하는 모든 비디오 코덱으로 부터 정적 비디오 프레임을 사용할 수 있다.









[https://developer.android.com/topic/performance/graphics](https://developer.android.com/topic/performance/graphics)

[https://square.github.io/picasso/](https://square.github.io/picasso/)
[**bumptech/glide**
*View Glide's documentation | 简体中文文档 | Report an issue with Glide Glide is a fast and efficient open source media…*github.com](https://github.com/bumptech/glide)
[**Glide - Caching Basics**
*After we've looked at loading, displaying and manipulating images, we'll move on to optimize the process. One of the…*futurestud.io](https://futurestud.io/tutorials/glide-caching-basics)
[**How The Android Image Loading Library Glide and Fresco Works?(번역)**
*원본 - How The Android Image Loading Library Glide and Fresco Works? 저는 오늘 어렵게 슥듭한 지식을 공유해보려합니다. 지식은 그것을 갈망하는 자에게 온다. 따라서…*wooooooak.github.io](https://wooooooak.github.io/%EB%B2%88%EC%97%AD%ED%95%98%EB%A9%B0%20%EA%B3%B5%EB%B6%80%ED%95%98%EA%B8%B0/2021/02/21/How-The-Android-Image-Loading-Library-Glide-and-Fresco-Works/)

[https://www.charlezz.com/wordpress/wp-content/uploads/2020/10/www.charlezz.com-glide-v4-glide-v4--by-charlezz.pdf](https://www.charlezz.com/wordpress/wp-content/uploads/2020/10/www.charlezz.com-glide-v4-glide-v4--by-charlezz.pdf)

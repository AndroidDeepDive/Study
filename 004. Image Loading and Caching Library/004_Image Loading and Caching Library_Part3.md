# Image Loading and Caching Library Part 3 - Performance Test

이미지 라이브러리들 중 Glide, Fresco, Coil에 대해서 성능 테스트를 해보자.

성능 테스트에 사용한 기기는 `Pixel 4a`, 값의 측정은 `Android Studio Profiler`를 사용하였다.

각 세트는 동일한 조건에서 테스트하였으며, 별도의 추가 옵션없이 각 라이브러리의 기본 세팅으로 테스트를 수행하였다.

측정값은 이미지 로딩 전후로 CPU, Memory, Disk를 10번씩 측정한 후의 평균값이다.

테스트 애플리케이션의 스토리지가 증가한 뒤, 스토리지를 비우고 다시 측정하였다.

> **참고** 기본 세팅이니만큼 Picasso의 경우 거대한 이미지를 대부분 불러오지 못했다.

### Case 1) 하나의 거대한 이미지를 출력하는 경우

테스트에 쓰인 이미지는 `7,680 x 4,320`의 사이즈와 `4.29mb`의 용량을 가진 24-bit의 color depth를 가진 이미지이다.

![one big image profiling](https://i.imgur.com/m7itaqy.png)

| Library | CPU | Memory | Disk |
|:--:|:--:|:--:|:--:|
|Coil|38.3%|4.26MB|X|
|Glide|40.8%|3.41MB|0.21MB|
|Fresco|40.2%|8.14MB|X|

1개의 거대한 이미지의 경우 특기할만한 성능 차이는 존재하지 않았다.

### Case 2) 동일한 이미지를 여러 번 출력하는 경우 

Case 1의 이미지를 ScrollView에 50개 추가하여 테스트하였다.

![50 big image profiling](https://i.imgur.com/BntHJ2Y.png)

| Library | CPU | Memory | Disk |
|:--:|:--:|:--:|:--:|
|Coil|93.6%|350.8MB|X|
|Glide|45.4%|4.6MB|0.21MB|
|Fresco|72.4%|8.4MB|X|

Coil의 Cpu, Memory 사용량이 매우 크게 증가하였다.

Glide는 앱을 끈 후 다시 로딩했을 때 딜레이가 없었어 재로딩 했을 때 매우 효율적으로 동작하였다.

아래 세부 프로파일링을 보면 Coil이 Coroutine을 사용할 때 CPU, Memory가 정말 많이 사용된다는 것을 알 수 있다.

아래는 Coil로 테스트하였을 때의 프로파일링 결과이다.

![50 big image profiling coil](https://i.imgur.com/rb1bMMt.png)

아래는 Glide로 테스트하였을 때의 프로파일링 결과이다.

![50 big image profiling glide](https://i.imgur.com/0oDUU69.png)

Glide가 하나의 이미지를 여러개 불러올 때 제일 효율이 좋은 것으로 나타났다.

### Case 3) 여러 이미지를 각각 출력해보는 경우 (feat_ ScrollView)

2mb ~ 10mb 사이의 각기 다른 용량을 가진 이미지 10개를 ScrollView에 추가하여 테스트하였다.

![10 different image profiling](https://i.imgur.com/TIrybR8.png)

| Library | CPU | Memory | Disk |
|:--:|:--:|:--:|:--:|
|Coil|88.6%|800MB|X|
|Glide|89%|303.3MB|4.6MB|
|Fresco|92.6%|276MB|X|

캐싱을 기대할 수 없는 만큼 대체적으로 모두 CPU, Memory사용량이 올라갔다.

그 중 Coil이 제일 많은 Memory를 사용하였다.

### Case 4) 여러 이미지를 각각 출력해보는 경우 (feat_ RecyclerView)

Case 3에사 사용한 이미지를 RecyclerView에 추가하여 테스트하였다.

![10 different image recyclerView profiling](https://i.imgur.com/QafKUB1.png)

| Library | CPU | Memory | Disk |
|:--:|:--:|:--:|:--:|
|Coil|94%|800MB|X|
|Glide|94%|270MB|4.52MB|
|Fresco|94.6%|281MB|X|

Coil의 경우 ScrollView에 로딩할 때보다 CPU 사용량이 증가한 것을 확인할 수 있었다.

### 마무리 

Coil이 Coroutine으로 만들어져 기대를 많이 하고 테스트를 해보았는데 생각보다 CPU, Memory의 사용량이 많았다.

Gilde는 정말로 모든 부분에서 최적화가 잘 되어있는 것으로 보이는 것에 비해, Picasso는 잘 사용하고 싶다면 정말 커스텀을 많이, 잘 해야될 필요가 있어보인다.

Fresco는 최적화가 정말 잘 되어있지만 러닝커브가 높다. 퍼포먼스 측면에서는 Glide와 제일 비슷하였다.

## References

### Members of Study

### Official

### ETC


[https://developer.android.com/topic/performance/graphics](https://developer.android.com/topic/performance/graphics)

[https://square.github.io/picasso/](https://square.github.io/picasso/)
[**bumptech/glide**
*View Glide's documentation | 简体中文文档 | Report an issue with Glide Glide is a fast and efficient open source media…*github.com](https://github.com/bumptech/glide)
[**Glide - Caching Basics**
*After we've looked at loading, displaying and manipulating images, we'll move on to optimize the process. One of the…*futurestud.io](https://futurestud.io/tutorials/glide-caching-basics)
[**How The Android Image Loading Library Glide and Fresco Works?(번역)**
*원본 - How The Android Image Loading Library Glide and Fresco Works? 저는 오늘 어렵게 슥듭한 지식을 공유해보려합니다. 지식은 그것을 갈망하는 자에게 온다. 따라서…*wooooooak.github.io](https://wooooooak.github.io/%EB%B2%88%EC%97%AD%ED%95%98%EB%A9%B0%20%EA%B3%B5%EB%B6%80%ED%95%98%EA%B8%B0/2021/02/21/How-The-Android-Image-Loading-Library-Glide-and-Fresco-Works/)

[https://www.charlezz.com/wordpress/wp-content/uploads/2020/10/www.charlezz.com-glide-v4-glide-v4--by-charlezz.pdf](https://www.charlezz.com/wordpress/wp-content/uploads/2020/10/www.charlezz.com-glide-v4-glide-v4--by-charlezz.pdf)



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





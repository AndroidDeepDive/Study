[TOC]



# Introduce Android UI Rendering Principle and View Optimization - 3

저번 포스트에서는 UI Rendering 시 화면에 보여질 때 어떤 과정을 거치는지 알 수 있었고, 현재 보편적으로 사용되는 하드웨어 모델 기반 렌더링 방식에서 발생하는 문제는 무엇이고, 해당 문제를 해결하기 위해 어떤 방법이 있는지, 실제로 어느정도의 성과를 낼 수 있는지 정리해보았다.



## 버벅거림(Jank)

앱이 느린 렌더링으로 어려움을 겪는 경우, 시스템은 프레임을 건너 뛰게되고 사용자는 앱에서 끊김을 감지하게 된다.

![Frame Drop](https://images.velog.io/images/jshme/post/68faaf0a-1faf-46f3-be10-51c5fffa7d62/image.png)



버벅거림에는 여러문제가 있지만, 대표적으로 UI렌더링 시에는 **OverDraw, Slow Rendering**과 같은 문제가 존재한다.

## Slow Rendering 방지하기

해당 내용은 Google Android Source Document의 [느린 렌더링](https://developer.android.com/topic/performance/vitals/render#custom-monitoring)글 을 참고하였다.

렌더링 시 버벅거림을 감지할 수 있는 방법은 세가지이다.

- [Visual inspection](https://developer.android.com/topic/performance/vitals/render#visual-inspection)
- [Systrace](https://developer.android.com/topic/performance/vitals/render#systrace)
- [Custom performance monitoring](https://developer.android.com/topic/performance/vitals/render#custom-monitoring)



### 시각적 검사(Visual Inspection)

우리는 안드로이드에서 제공하는 몇가지 옵션을 통해 시각화 하여 문제를 해결할 수 있다. 해결책은 아래와 같다.

#### `Profile GPU Rendering` 을 활성화

60fps(GPU 연산으로 초 당 60프레임으로 화면을 뿌리는 것)일 때 렌더링에 얼마나 성능을 잡아먹는지 확인하기 위해 개발자 옵션을 통해 다음 도구를 사용설정 할 수 있다.

프로파일링은 Android 4.1(API 16 ) 이상에서 사용 가능하며 미리 해당 기능을 활성화하는 작업이 필요하다. 다음과 같은 순서로 진행하면 활성화 할 수 있다.

##### 여는방법

1. 기기에서 **설정**으로 이동하여 **개발자 옵션**을 탭한다.
2. **모니터링** 섹션에서 **프로필 GPU 렌더링**을 선택한다.
3. ‘프로필 GPU 렌더링’ 대화상자에서 **화면에 막대로 표시**를 선택하여 기기의 화면에 그래프를 오버레이한다.
4. 프로파일링하려는 앱을 연다.

설정을 활성화 하고 확인해보면, 아래와 같이 히스토그램으로 표시되는 것을 볼 수 있다. 

![Profile GPU Rendering](https://camo.githubusercontent.com/cb1f4dc0cbfecc2d7e34812f80ecb1c162bfcc5d5b1b37339927fc7f4f6339bf/68747470733a2f2f63646e2d696d616765732d312e6d656469756d2e636f6d2f6d61782f323030302f312a704d4f67776c5275524752756244367935504b726c512e706e67)



- 표시되는 각 애플리케이션에 관해 그래프가 표시된다.
- 가로축의 각 세로 막대는 프레임을 나타내며 각 세로 막대의 높이는 프레임을 렌더링하는 데 걸린 시간(밀리초)을 나타낸다.
- 녹색 가로선은 16밀리초를 나타냅니다. 60fps를 달성하려면 각 프레임의 세로 막대가 이 선 아래에 머물러야 합니다. 막대가 이 선을 넘어서면 애니메이션이 일시중지될 수 있다.
- 16밀리초 임계값을 초과하는 프레임은 막대를 더 넓고 덜 투명하게 만들어 강조표시한다.
- 각 막대에는 렌더링 파이프라인의 단계로 매핑되는 색상으로 표시된 구성요소가 있습니다. 구성요소의 수는 기기의 API 수준에 따라 다르다.
- 각 막대에는 렌더링 파이프 라인의 단계에 매핑되는 색상 구성 요소가 있으므로 어느 부분이 가장 오래 걸리는지 확인할 수 있다.
- 예를 들어 프레임이 입력을 처리하는 데 많은 시간을 소비하는 경우 사용자 입력을 처리하는 앱 코드를 살펴 봐야한다.

##### 막대 색상 별 의미

![Profile Graph Color](https://camo.githubusercontent.com/e7eedd5657a242bee661ba52a18350688770bb363dbfe62974c6e3d9750f5ecf/68747470733a2f2f63646e2d696d616765732d312e6d656469756d2e636f6d2f6d61782f323030302f302a724a4d6e52713644416a6932586e756c2e706e67)

세로 막대 차트는 위와 같이 8개의 단계로 표시된다.

- **프로세스/스왑버퍼(Swap)**: 안드로이드는 디스플레이 목록을 GPU 에 제출하는것을 끝내면, 시스템은 그래픽 드라이버에 현재 프레임이 완료되었음을 알리며 드라이버를 업데이트된 이미지를 화면에 표시한다. CPU 와 GPU 는 병렬로 처리되는데 CPU 가 GPU 처리보다 빠른 경우, 프로세스간 통신 큐가 가득찰 수 있다. CPU 는 큐에 공간이 생길때까지 기다린다. 큐가 가득한 상태는 스왑버퍼 상태에서 자주 발생하는데 이 단계에서 프레임 전체의 명령어가 제출되기 때문이다. 명령어 실행 단계와 비슷하게 GPU 에서 일어나는 일의 복잡성을 줄여 문제를 해결할 수 있다.
- **명령어 실행(Issue)**: 디스플레이 목록을 화면에 그리기 위한 모든 명령어를 실행하기 위해 걸리는 시간을 측정한다. 주어진 프레임에서 렌더링하는 디스플레이 목록과 수량을 직접적으로 측정한다. 내재적으로 많은 그리기 동작이 있는 경우, 오랜 시간이 걸릴수 있다.
- **동기화/업로드(Upload)**: 현재 프레임에서 비트맵 객체가 CPU 메모리에서 GPU 메모리로 전송되는데 걸리는 시간을 측정한다. 오랜 시간이 걸리는 경우는 작은 리소스가 많이 로드되어 있거나 적지만 큰 리소스가 로드되어 있는 경우이다. 비트맵 해상도가 실제 디스플레이 해상도보다 큰 사이즈로 로드되어 있지 않도록 하거나 동기화 전에 비동기로 미리 업로드해서 실행 시간을 줄일 수 있다.
- **그리기(Draw)**: 백그라운드 혹은 텍스트 그리기와 같은 뷰를 그리기 명령어로 번역하는 단계로 시스템은 명령어를 디스플레이 목록에 캡처한다. 무효화된 뷰에서 onDraw 호출을 실행해 걸리는 모든 시간을 측정하며, 오랜 시간이 걸린다는 것은 무효화된 뷰가 많다는 것을 의미한다. 뷰가 무효화되는 경우 뷰의 디스프레이 목록을 재생성한다. 혹은 커스텀한 뷰가 onDraw 할때 복잡한 로직을 가질 때도 실행시간이 오랜 걸린다.
- **측정 및 배치(Measure)**: 안드로이드에서 뷰를 스크린에 그리기 위해 측정하고 배치하는데 걸리는 시간을 측정한다. 일반적으로 오랜 시간이 걸리는 경우는 배치할 뷰가 너무 많거나 혹은 계층구조의 잘못된 장소에서 중복 계산이 이뤄지며, Traceview와 Systrace 를 사용해서 호출 스택을 확인하여 문제를 파악할 수 있다.
- **애니메이션(Anim)**: 현재 프레임에서 실행되는 애니메이션에 대해 걸리는 시간을 측정한다. 일반적으로 오랜 시간이 걸리는 경우는 애니메이션의 속성 변경으로 인해 발생한다.
- **입력 처리(Input)**: 앱이 입력 이벤트를 처리하는 시간으로 입력 이벤트 콜백의 결과로 호출된 코드를 실행하는데 걸리는 시간을 나타낸다. 일반적으로 오랜 시간이 걸리는 경우는 메인쓰레드에서 발생하며, 작업을 최적화하거나 다른 쓰레드를 이용해서 작업을 실행하도록 한다.
- **기타(Misc)**: 렌더링 시스템이 작업을 수행하는 시간외에 렌더링과 관련 없는 추가적인 작업이 있다. 일반적으로 렌더링의 연속된 두 프레임 사이에서 UI 스레드에서 발생할 수 있는 일을 나타낸다. 오랜 시간이 걸리는 경우, 다른스레드에서 실행해야할 콜백, 인텐트 또는 기타 다른 작업이 있을 수 있다. Method tracing 또는 Systrace 를 사용하면 메인스레드에서 실행중인 작업을 확인해 문제를 해결할 수 있다.

아래에는 이미지 뷰에 클릭 이벤트를 설정하고 이미지 리소스를 변경하는 로직이다. 좌측에는 단순하게 이미지 리소스를 변경하는 로직만 추가하였고, 우측에는 리소스를 변경하는 과정에서 스레드를 일시 중단하는 코드를 추가하였다. 그 결과 입력처리 시간에 차이가 발생한다는 것을 확인할 수 있다.

![Image Comparison By GPU Rendering Profiling - 1](https://camo.githubusercontent.com/8ab82eec28d64ec117dbb8894a603a49e91f971fdedf7824addf3a1f756ab229/68747470733a2f2f63646e2d696d616765732d312e6d656469756d2e636f6d2f6d61782f323931322f312a467137564b7a55675f6337574363507934494c4a44772e706e67)

아래에는 이미지 뷰에 각각 다른 크기의 이미지 리소스를 배치하였다. 좌측에는 8.1MB 크기의 이미지 리소스를 배치하고 우측에는 16KB 크기의 이미지 리소스를 배치하였다. 

그 결과 이미지를 업로드 시간에 차이가 발생한 다는 것을 확인할 수 있다.

![Image Comparison By GPU Rendering Profiling-2](https://camo.githubusercontent.com/6e3902ae229bbd302c24ba4c0ef8b754add45a1fb0e6c2f5cede6e55f67547b6/68747470733a2f2f63646e2d696d616765732d312e6d656469756d2e636f6d2f6d61782f323933322f312a5f4364343244594c686a5942636d506a4f5a726f47772e706e67)



#### Etc...

예외적인 케이스에 대한 해법으로는 아래 세가지가 있다.

1. Release 버전의 앱을 실행하자.
   - ART 런타임은 디버깅 기능을 지원하기 위해 몇 가지 중요한 최적화를 비활성화하기 때문이다.
2. 때때로 버벅 거림은 앱이 [cold start](https://developer.android.com/topic/performance/vitals/launch-time#cold) 에서 시작될 때만 재현 될 수 있다.
3. 버벅거림을 극대화 시키기 위해 더 느린 장치에서 앱을 실행해 봐라.



### Systrace

전체 기기가 수행하는 작업을 보여주는 도구이지만 앱에서 버벅 거림을 식별하는 데 유용 할 수 있다.

Systrace는 최소한의 시스템 오버 헤드를 가지므로 계측 중에 현실적인 버벅 거림을 경험할 수 있다.



### 버벅거림을 유발할 수 있는 요소들

버벅거림을 유발하는 요소는 대표적으로 **Scrollable lists, Layout performance, Rendering performance**가 있다.

#### Scrollable lists

ListView 및 특히 RecyclerView는 버벅 거림에 가장 취약한 복잡한 스크롤 목록에 일반적으로 사용된다.

둘 다 Systrace 마커가 포함되어 있으므로 Systrace를 사용하여 앱에서 버벅 거림을 유발하는지 여부를 파악할 수 있다.

RecyclerView의 systrace 섹션을 표시하려면 명령에 `-a <your-package-name>`을 전달해야한다. 사용 가능한 경우 systrace 출력에 생성 된 경고의 지침을 따르자.

Systrace 내에서 RecyclerView systrace 섹션을 클릭하여 RecyclerView가 수행하는 작업에 대한 설명을 볼 수 있다.

1. 작은 업데이트를 위해 `notifyDataSetChanged()`, `setAdapter(Adapter)`, or `swapAdapter(Adapter, boolean)`를 호출하지 마라.
   - 전체 목록 아이템이 변경되었다고 알리기 때문이다.
   - 대신 `SortedList` 또는 `DiffUtil`을 사용하여 콘텐츠가 변경되거나 추가 될 때 최소한의 업데이트를 하도록하자.

2. #### **Nested recyclerview**

   - 내부 RecyclerView의 LinearLayoutManager에 `setInitialPrefetchItemCount(int)` 를 설정할 수도 있다.
   - 예를 들어 항상 3.5 개의 항목이 한 행에 표시되는 경우, `innerLinearLayoutManager.setInitialItemPrefetchCount(4);` 를 호출한다.

3. `RecyclerView` 의 `view type` 이 적을수록 새 item type 이 화면에 표시 될 때 수행해야하는 inflation 이 줄어 든다.
   - 만약 아이콘, 색상, 작은 문구 변경 차이라면, 별도의 view type 으로 나누지 않고 bind 할 때 변경하는 것이 낫다. (동시에 앱의 메모리 사용량 감소)
4. Bind (즉, onBindViewHolder (VH, int) 는 매우 간단해야하며 가장 복잡한 항목을 제외한 모든 항목에 대해 1 밀리 초 미만이 소요되야 한다.
   - 단순히 adapter 의 내부 item 에서 POJO item 을 가져와서, ViewHolder의 view 에서 setter 를 호출해야 한다.

#### Layout performance

`Systrace`에서 `Choreographer # doFrame`의 레이아웃 세그먼트가 너무 많은 작업을 수행하거나 너무 자주 작업을 수행하는 것으로 표시되면 레이아웃 성능 문제가 있음을 의미한다.

> 세그먼트가 몇 밀리 초보다 길면 `RelativeLayouts` 또는 `weighted-LinearLayouts`에 대해 최악의 경우 중첩 성능에 도달 할 수 있다. 
>
> 이러한 각 레이아웃은 자식의 여러 측정 / 레이아웃 패스를 트리거 할 수 있으므로 중첩하면 중첩 깊이에서 O (n ^ 2) 동작이 발생할 수 있다. 
>
> **계층 구조의 최하위 리프 노드를 제외한 모든 노드에서 RelativeLayout 또는 LinearLayout의 가중치 기능을 피해라.**

#### Rendering performance

**Android UI는 UI 스레드의 Record `View # draw`와 RenderThread의 `DrawFrame`의 두 단계로 작동한다**.

첫 번째는 모든 `invalidated View` 에서 `draw (Canvas)`를 실행한다.

두 번째는 기본 `RenderThread`에서 실행되지만 `View#draw()` 단계에서 생성 된 작업을 기반으로 작동한다.

> `View#draw()` 가 너무 오래 걸린다면, 이 경우는 보통 UI 스레드에서 bitmap 이 그려지는 경우다. `Bitmap` 에 그리기는 CPU 렌더링을 사용하므로 일반적으로 가능하면 이를 피해야 한다.



### Custom Performance Monitoring(맞춤 성능 모니터링 사용)

로컬 기기에서 버벅거림을 재현할 수 없는 경우 앱에 맞춤 성능 모니터링을 빌드하여 현장에서 기기의 버벅거림 원인을 식별할 수 있다.

이를 위해 [`FrameMetricsAggregator`](https://developer.android.com/reference/androidx/core/app/FrameMetricsAggregator)를 사용하여 앱의 특정 부분에서 프레임 렌더링 시간을 수집하고 [Firebase Performance Monitoring](https://firebase.google.com/docs/perf-mon/)을 사용하여 데이터를 기록하고 분석할 수 있다.

자세한 내용은 [Android vitals에 Firebase Performance Monitoring 사용](https://firebase.google.com/docs/perf-mon/get-started-android#pdc)을 참조하자.

## Overdraw

> An app may draw the same pixel more than once within a single frame, an event called overdraw. Overdraw is usually unnecessary, and best eliminated. It manifests itself as a performance problem by wasting GPU time to render pixels that don't contribute to what the user sees on the screen.

오버 드로우는 시스템이 단일 렌더링 프레임에서 화면에 여러 번 픽셀을 그리는 것을 말한다.

> ⚠️ 참고 : Overdraw는 더 이상 Google I / O 성능 세션 및 성능 패턴 동영상에서 논의되었을 때 만큼 심각한 문제가 아니다.
>
> 이는 저가형 장치가 GPU 성능에서 지속적으로 성장하는 반면 디스플레이는 상대적으로 낮은 해상도에서 정체 되었기 때문. 알려진 저 성능 GPU 장치를 최적화하지 않는 한, 원활한 앱 성능을 보장하는 것 대신 UI 스레드 작업을 최적화하는 데 집중하는 것이 좋다.

다른 개발자 옵션으로 UI 에 컬러를 지정함으로써 오버드로우를 식별할 수 있다. 같은 프레임내에서 같은 픽셀을 한번 이상 그릴 때 오버드로우가 발생한다.

앱에서 필요 이상으로 많은 렌더링이 발생하는 곳을 시각화로 보여주며, 사용자에게 보여지지 않는 픽셀을 렌더링하기 위해 추가 GPU 작업으로 성능 문제가 발생한 수 있음을 알 수 있다. 다음과 같이 설정하면 오버드로우 시각화를 확인 할 수 있다.



### Overdraw 디버깅

##### 여는방법

1. 기기에서 **설정**으로 이동하여 **개발자 옵션**을 탭한다.
2. **하드웨어 가속 렌더링** 섹션으로 스크롤하여 **GPU 오버드로 디버그**를 선택한다.
3. **GPU 오버드로 디버그** 대화상자에서 **오버드로 영역 표시**를 선택한다.
4. **분석**

- **True color:** No overdraw
- **Blue:** Overdrawn 1 time [![https://developer.android.com/topic/performance/images/gpu/overdraw-blue.png](https://camo.githubusercontent.com/cf2ed154ae5732d4a988686d1839154ce2528d0dfd0b69b050b136c91a44e67b/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f746f7069632f706572666f726d616e63652f696d616765732f6770752f6f766572647261772d626c75652e706e67)](https://camo.githubusercontent.com/cf2ed154ae5732d4a988686d1839154ce2528d0dfd0b69b050b136c91a44e67b/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f746f7069632f706572666f726d616e63652f696d616765732f6770752f6f766572647261772d626c75652e706e67)
- **Green:** Overdrawn 2 times [![https://developer.android.com/topic/performance/images/gpu/overdraw-green.png](https://camo.githubusercontent.com/b43e7eccdf672e28367b2afd726fa7d2adb6e0572d663f5460b8ec7e9d31b131/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f746f7069632f706572666f726d616e63652f696d616765732f6770752f6f766572647261772d677265656e2e706e67)](https://camo.githubusercontent.com/b43e7eccdf672e28367b2afd726fa7d2adb6e0572d663f5460b8ec7e9d31b131/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f746f7069632f706572666f726d616e63652f696d616765732f6770752f6f766572647261772d677265656e2e706e67)
- **Pink:** Overdrawn 3 times [![https://developer.android.com/topic/performance/images/gpu/overdraw-pink.png](https://camo.githubusercontent.com/8245afe8fd2f919e9ca5b2563cdd981062d9f1bb2cbb896662146c0ec51c602e/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f746f7069632f706572666f726d616e63652f696d616765732f6770752f6f766572647261772d70696e6b2e706e67)](https://camo.githubusercontent.com/8245afe8fd2f919e9ca5b2563cdd981062d9f1bb2cbb896662146c0ec51c602e/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f746f7069632f706572666f726d616e63652f696d616765732f6770752f6f766572647261772d70696e6b2e706e67)
- **Red:** Overdrawn 4 or more times [![https://developer.android.com/topic/performance/images/gpu/overdraw-red.png](https://camo.githubusercontent.com/c2cbd35e732b9041b2c540d42f73b18331b3defd34517e2ef6b76647a9bcb93f/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f746f7069632f706572666f726d616e63652f696d616765732f6770752f6f766572647261772d7265642e706e67)](https://camo.githubusercontent.com/c2cbd35e732b9041b2c540d42f73b18331b3defd34517e2ef6b76647a9bcb93f/68747470733a2f2f646576656c6f7065722e616e64726f69642e636f6d2f746f7069632f706572666f726d616e63652f696d616765732f6770752f6f766572647261772d7265642e706e67)

디버깅 결과는 아래와 같이 비교 가능하다.

뷰의 배치에 따라 각각 오버드로우가 발생한 픽셀을 확인할 수 있다.

![오버드로우 시각화](https://imgur.com/zw3H5pZ.jpg)



### Overdraw 줄이기

Overdraw를 줄일 수 있는 방법은 대표적으로 세가지가 있다.

1. layouts 에서 불필요한 backgrounds 는 제거하자

2. View 계층을 평면화하자

3. 투명도를 줄이자 

   - 알파 렌더링으로 알려진 화면에서 투명 픽셀을 렌더링하는 것은 오버 드로의 주요 원인이다.

   - 시스템이 그 위에 불투명 한 픽셀을 그려서 기존의 그려진 픽셀을 완전히 숨기는 표준 오버 드로와 달리, 투명한 객체는 올바른 `blending equation` 이 발생하도록 기존 픽셀을 먼저 그려야한다.

     >  (**blending equation** : 2개의 픽셀 컬러 값을 결합시키는 것. ([블로그 참고](https://xysterxx.tistory.com/50))



## UI 렌더링 성능 개선

지금까지는 원인 및 분석 방법에 대해서 살펴보았다. 이번에는 본격적으로 코드레벨에서 분석 및 개선방안을 통해 어떻게 개선할 수 있는지 보자.

### 레이아웃 재사용

- nclude, merge를 통해 뷰를 재사용한다.
- include로만 뷰를 재사용하면 다음과 같이 뷰가 중첩될 수 있다.

![before-merge](https://camo.githubusercontent.com/c9a59ec933d1ad6b0020b1e392989d465f021d0dae23a37775ac308112fcee46/68747470733a2f2f6d69726f2e6d656469756d2e636f6d2f6d61782f313030302f312a4772786a36347737676d56724a7844736b34714463412e706e67)

- merge를 같이 사용하여 중첩을 줄여준다 

  ![before-merge](https://camo.githubusercontent.com/2625ce87015149b8020570c12aeabeddf5d5c1fdbf01d9b7819cb40d55cdc43d/68747470733a2f2f6d69726f2e6d656469756d2e636f6d2f6d61782f313030302f312a46797a436a4d5933653865554d487a626f6252547a672e706e67)

  

### 오버드로잉 방지(뷰 백그라운드)

일반적으로 우리는 앱의 배경 색상을 적용하기 위해 `android:background="@color/white"` 해당 뷰의 배경색상을 적용한다.

```xml
<!-- /res/layout/activity_main.xml -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:background="@color/white">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

이러한 방법은 View에 그려짐에 있어 중복으로 그릴 수 있는 여지가 있다.

![example1](https://camo.githubusercontent.com/512d4438befe359f2bbe7b80240ef895a1ff15f6778c5c382b4b564ddf04aca1/68747470733a2f2f692e696d6775722e636f6d2f53666a7834424a2e706e67)

왜 Overdraw가 생기는지 확인을 위해 앱 테마 값을 확인해보았다.

```xml
<!-- /res/values/themes.xml -->
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.UITest" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
    </style>
</resources>
```

특별히 지정해준 사항이 없어 해당 테마의 Parent를 따라가 보았다.

```
Theme.MaterialComponents.DayNight.DarkActionBar > Theme.MaterialComponents.Light.DarkActionBar > Base.Theme.MaterialComponents.Light.DarkActionBar >Base.Theme.MaterialComponents.Light > Base.V14.Theme.MaterialComponents.Light
```

해당 소스에서 아래와 같이 배경 색상을 지정하는 걸 확인하였고

```xml
<item name="android:colorBackground">@color/design_default_color_background</item>
```

해당 값을 확인해보니 아래와 같이 흰색으로 설정하고 있었다.

```xml
<color name="design_default_color_background">#FFFFFF</color>
```

한마디로 배경 색상을 흰색으로 칠해주고 그 위에 다시 흰색으로 칠해주고 있었다. 해당 Overdraw를 없애고 내가 원하는 색상으로 설정하기 위해 테마 파일을 아래와 같이 `<item name="android:windowBackground">@color/beige</item>` 를 통해 오버드로잉 문제를 해결할 수 있다.

```xml
<!-- /res/values/themes.xml -->
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.UITest" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
        <item name="android:windowBackground">@color/beige</item>
    </style>
</resources>
```

 `/res/layout/activity_main.xml` 파일에 `android:background="@color/beige"` 코드를 입력 후 Overdraw를 확인한 결과는 다음과 같다.

![example2](https://camo.githubusercontent.com/272e5a83a01635a63bec30f93e9860e581b956a7048e2c0011381a6bec9ef51c/68747470733a2f2f692e696d6775722e636f6d2f73354c417642582e706e67)

결론적으로 배경 색상 지정을 테마를 통해 지정하면 Overdraw를 줄일 수 있다.



### Lazy Load by ViewStub

해당 내용은 [찰스의 안드로이드 - ViewStub 활용으로 성능 높이기](https://www.charlezz.com/?p=19977)글 을 참고하였다.

ViewStub은 사이즈가 없는 보이지 않는 뷰로 런타임에서 늦은 전개(lazy-inflate)를 원할 때 사용할 수 있다.

ViewStub을 보이게 만들거나 inflate() 메서드를 호출하면 레이아웃이 전개되면서 ViewStub을 대체하기 때문에 ViewStub은 사라진다.



전개된 뷰는 ViewStub의 부모 뷰에 추가 된다. 레이아웃에서 ViewStub을 사용하는 예제를 확인하자.

```xml
<ViewStub android:id="@+id/stub"
          android:inflatedId="@+id/subTree"
          android:layout="@layout/mySubTree"
          android:layout_width="120dp"
          android:layout_height="40dp" />
```



findViewById() 호출을 통해 ViewStub에 접근할 수 있다.

```kotlin
val viewStub = findViewById(R.id.stub)
```

생성되는 바인딩 클래스에서 ViewStub은 ViewStubProxy로 표현되며, ViewStub에 대해 접근할 수 있게 해준다.

```kotlin
val binding: ActivityMainBinding = ...
val viewStubProxy = binding.stub
val viewStub = viewStubProxy.viewStub()
```

ViewStub에 지정된 레이아웃을 전개 시키기 위해서 서는 setVisibility() 또는 inflate()를 호출 할 수 있다.

```kotlin
val viewStub: ViewStub = ...
viewStub.inflate()
//또는 viewStub.visibility = View.VISIBLE
```

ViewStub은 복잡하게 구성된 레이아웃을 빠르게 전개시켜야하는 상황에서, 레이아웃의 전개 시기를 선택적으로 늦출 수 있다.

예를 들어 리스트 형태의 UI를 구성하고 하나의 뷰홀더가 전개 되는데 상당한 비용이 발생한다고 가정하자.

이 때 사용자가 빠르게 화면을 스크롤 할 경우 프레임 드랍이 발생 할 수 있다. 이럴 때 선택적으로 불필요한 레이아웃의 전개를 제어하고 전개 시기를 늦춤으로써 성능을 개선시킬 수 있다.


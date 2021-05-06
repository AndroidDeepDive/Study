## Overdraw 줄이기  
### Overdraw 란?

> An app may draw the same pixel more than once within a single frame, an event called overdraw. Overdraw is usually unnecessary, and best eliminated. It manifests itself as a performance problem by wasting GPU time to render pixels that don't contribute to what the user sees on the screen.

오버 드로우는 시스템이 단일 렌더링 프레임에서 화면에 여러 번 픽셀을 그리는 것을 말한다.  

> ℹ️ 참고 : Overdraw는 더 이상 Google I / O 성능 세션 및 성능 패턴 동영상에서 논의되었을 때 만큼 심각한 문제가 아니다. 이는 저가형 장치가 GPU 성능에서 지속적으로 성장하는 반면 디스플레이는 상대적으로 낮은 해상도에서 정체 되었기 때문. 알려진 저 성능 GPU 장치를 최적화하지 않는 한, 원활한 앱 성능을 보장하는 것 대신 UI 스레드 작업을 최적화하는 데 집중하는 것이 좋다. 

### Overdraw 디버깅
* 방법 : 설정 → 개발자 옵션 → 하드웨어 가속 렌더링 섹션 → `GPU 오버드로 디버그` 선택 
* 분석 : 
    * **True color:** No overdraw
    * **Blue:** Overdrawn 1 time
    ![https://developer.android.com/topic/performance/images/gpu/overdraw-blue.png](https://developer.android.com/topic/performance/images/gpu/overdraw-blue.png)
    * **Green:** Overdrawn 2 times
    ![https://developer.android.com/topic/performance/images/gpu/overdraw-green.png](https://developer.android.com/topic/performance/images/gpu/overdraw-green.png)
    * **Pink:** Overdrawn 3 times
    ![https://developer.android.com/topic/performance/images/gpu/overdraw-pink.png](https://developer.android.com/topic/performance/images/gpu/overdraw-pink.png)
    * **Red:** Overdrawn 4 or more times
    ![https://developer.android.com/topic/performance/images/gpu/overdraw-red.png](https://developer.android.com/topic/performance/images/gpu/overdraw-red.png) 

![image](https://user-images.githubusercontent.com/24906264/117275997-93edda00-ae99-11eb-8001-d0b235b2e05b.png)

### Overdraw 줄이기
1. layouts 에서 불필요한 backgrounds 는 제거하자
2. View 계층을 평면화하자
3. 투명도를 줄이자
    * 알파 렌더링으로 알려진 화면에서 투명 픽셀을 렌더링하는 것은 오버 드로의 주요 원인. 시스템이 그 위에 불투명 한 픽셀을 그려서 기존의 그려진 픽셀을 완전히 숨기는 표준 오버 드로와 달리, 투명한 객체는 올바른 `blending equation` 이 발생하도록 기존 픽셀을 먼저 그려야한다. (**blending equation** 이란 : 2개의 픽셀 컬러 값을 결합시키는 것. ([블로그 참고](https://xysterxx.tistory.com/50)) )


<br/> 

## Slow Rendering 방지하기 
버벅거림 (Jank)이란? 
앱이 느린 렌더링으로 어려움을 겪는 경우, 시스템은 프레임을 건너 뛰게되고 사용자는 앱에서 끊김을 감지하게 된다.

### 버벅거림 감지 방법
- [Visual inspection](https://developer.android.com/topic/performance/vitals/render#visual-inspection)
- [Systrace](https://developer.android.com/topic/performance/vitals/render#systrace)
- [Custom performance monitoring](https://developer.android.com/topic/performance/vitals/render#custom-monitoring)

#### 1.Visual Inspection

- Release 버전의 앱을 실행하자. ART 런타임은 디버깅 기능을 지원하기 위해 몇 가지 중요한 최적화를 비활성화하기 때문에.
- `Profile GPU Rendering` 을 활성화하자. `Profile GPU Rendering`은 프레임 당 16ms 벤치 마크를 기준으로 UI 창의 프레임을 렌더링하는 데 걸리는 시간을 빠르게 시각적으로 보여주는 막대를 화면에 표시한다. 각 막대에는 렌더링 파이프 라인의 단계에 매핑되는 색상 구성 요소가 있으므로 어느 부분이 가장 오래 걸리는지 확인할 수 있다. 예를 들어 프레임이 입력을 처리하는 데 많은 시간을 소비하는 경우 사용자 입력을 처리하는 앱 코드를 살펴 봐야합니다.
- 때때로 버벅 거림은 앱이 [cold start](https://developer.android.com/topic/performance/vitals/launch-time#cold) 에서 시작될 때만 재현 될 수 있다.
- 버벅거림을 극대화 시키기 위해 더 느린 장치에서 앱을 실행해 봐라.

#### 2. Systrace

Systrace는 전체 기기가 수행하는 작업을 보여주는 도구이지만 앱에서 버벅 거림을 식별하는 데 유용 할 수 있다. Systrace는 최소한의 시스템 오버 헤드를 가지므로 계측 중에 현실적인 버벅 거림을 경험할 수 있다.  


### 버벅거림을 유발할 수 있는 요소들

#### Scrollable lists

ListView 및 특히 RecyclerView는 버벅 거림에 가장 취약한 복잡한 스크롤 목록에 일반적으로 사용된다. 둘 다 Systrace 마커가 포함되어 있으므로 Systrace를 사용하여 앱에서 버벅 거림을 유발하는지 여부를 파악할 수 있다. RecyclerView의 systrace 섹션을 표시하려면 명령에 `-a <your-package-name>`을 전달해야한다. 사용 가능한 경우 systrace 출력에 생성 된 경고의 지침을 따르자. Systrace 내에서 RecyclerView systrace 섹션을 클릭하여 RecyclerView가 수행하는 작업에 대한 설명을 볼 수 있다.

- 작은 업데이트를 위해 `notifyDataSetChanged()`, `setAdapter(Adapter)`, or `swapAdapter(Adapter, boolean)`를 호출하지 마라. 전체 목록 아이템이 변경되었다고 알리기 때문. 대신 `SortedList` 또는 `DiffUtil`을 사용하여 콘텐츠가 변경되거나 추가 될 때 최소한의 업데이트를 생자.
- `Nested recyclerview` :
    - 내부 RecyclerView의 LinearLayoutManager에 setInitialPrefetchItemCount(int) 를 설정할 수도 있다. 예를 들어 항상 3.5 개의 항목이 한 행에 표시되는 경우, `innerLinearLayoutManager.setInitialItemPrefetchCount(4);` 를 호출.
- The fewer the view types in a RecyclerView's content, the less inflation will need to be done when new item types come on screen.
`RecyclerView` 의 `view type` 이 적을수록 새 item type 이 화면에 표시 될 때 수행해야하는 inflation 이 줄어 든다.
    - ⇒ 만약 아이콘, 색상, 작은 문구 변경 차이라면, 별도의 view type 으로 나누지 않고 bind 할 때 변경하는 것이 낫다. (동시에 앱의 메모리 사용량 감소)
- Bind (즉, onBindViewHolder (VH, int) 는 매우 간단해야하며 가장 복잡한 항목을 제외한 모든 항목에 대해 1 밀리 초 미만이 소요되야 한다. 단순히 adapter 의 내부 item 에서 POJO item 을 가져와서, ViewHolder의 view 에서 setter 를 호출해야 한다.


#### Layout performance

`Systrace`에서 `Choreographer # doFrame`의 레이아웃 세그먼트가 너무 많은 작업을 수행하거나 너무 자주 작업을 수행하는 것으로 표시되면 레이아웃 성능 문제가 있음을 의미한다.
- 세그먼트가 몇 밀리 초보다 길면 `RelativeLayouts` 또는 `weighted-LinearLayouts`에 대해 최악의 경우 중첩 성능에 도달 할 수 있다. 이러한 각 레이아웃은 자식의 여러 측정 / 레이아웃 패스를 트리거 할 수 있으므로 중첩하면 중첩 깊이에서 O (n ^ 2) 동작이 발생할 수 있다. **계층 구조의 최하위 리프 노드를 제외한 모든 노드에서 RelativeLayout 또는 LinearLayout의 가중치 기능을 피해라.**

#### Rendering performance

**Android UI는 UI 스레드의 Record `View # draw`와 RenderThread의 `DrawFrame`의 두 단계로 작동한다**. 첫 번째는 모든 `invalidated View` 에서 `draw (Canvas)`를 실행한다. 두 번째는 기본 `RenderThread`에서 실행되지만 `View#draw()` 단계에서 생성 된 작업을 기반으로 작동한다. 

- `View#draw()` 가 너무 오래 걸린다면, 이 경우는 보통 UI 스레드에서 bitmap 이 그려지는 경우다. `Bitmap` 에 그리기는 CPU 렌더링을 사용하므로 일반적으로 가능하면 이를 피해야 한다. 
  
<br/> 

## UI 성능 분석 방법 
- [Analyze with Profile GPU Rendering](https://developer.android.com/topic/performance/rendering/profile-gpu)
- [Overview of system tracing](https://developer.android.com/topic/performance/tracing)
- [Navigate a Systrace report](https://developer.android.com/topic/performance/tracing/navigate-report)


---
### Reference 
- [Slow rendering](https://developer.android.com/topic/performance/vitals/render)
- [Frozen frames](https://developer.android.com/topic/performance/vitals/frozen)
- [Performance and View Hierarchies](https://developer.android.com/topic/performance/rendering/optimizing-view-hierarchies)
- [Analyze with Profile GPU Rendering](https://developer.android.com/topic/performance/rendering/profile-gpu)
- [Overview of system tracing](https://developer.android.com/topic/performance/tracing)
- [Navigate a Systrace report](https://developer.android.com/topic/performance/tracing/navigate-report)
- [Overdraw official documents](https://developer.android.com/topic/performance/rendering/overdraw)
- [Visualize GPU overdraw](https://developer.android.com/topic/performance/rendering/inspect-gpu-rendering#debug_overdraw)
- [Painter's algorithm](https://en.wikipedia.org/wiki/Painter%27s_algorithm)
- [Hidden cost of transparency](https://www.youtube.com/watch?v=wIy8g8yNhNk&list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE&index=47)

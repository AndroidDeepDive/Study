
## 안드로이드 View 어떻게 그려질까?
>  When an Activity receives focus, it will be requested to draw its layout. The Android framework will handle the procedure for drawing, but the Activity must provide the root node of its layout hierarchy.
>  Drawing begins with the root node of the layout. It is requested to measure and draw the layout tree. Drawing is handled by walking the tree and rendering each View that intersects the invalid region. In turn, each ViewGroup is responsible for requesting each of its children to be drawn (with the draw() method) and each View is responsible for drawing itself. Because the tree is traversed pre-order, this means that parents will be drawn before (i.e., behind) their children, with siblings drawn in the order they appear in the tree.
>  Drawing the layout is a two pass process: a measure pass and a layout pass.

액티비티가 포커스를 받을때, 액티비티는 레이아웃을 그리도록 요청을 한다. 안드로이드 프레임워크는 그리기 위한 절차를 다룬다. 액티비티는 레이아웃 계층의 루트 노드를 제공해야 한다.

그리기는 레이아웃의 루트 노드에서 시작한다. 레이아웃 트리를 측정하고 그려야 한다. 그리기는 트리를 따라서 유효하지 않는 영역과 교차하는 각각의 뷰를 렌더링한다. 차례로 각 뷰그룹은 차일드뷰에게 draw() 함수와 함께 그려질 것을 요청하며 각 뷰는 스스로 그리기를 담당한다. 트리는 이미 정해진 순서대로 순회하며, 이것은 부모뷰가 먼저 자식뷰보다 먼저 그려지며, 같은 계층의 뷰는 트리의 먼저 나타나는 것이 먼저 그려지는 것을 의미한다.

측정 단계와 레이아웃 두 단계 절차로 레이아웃을 그린다.

### Measure Pass

측정 패스는 measure(int, int) 로 구현되며 뷰 트리의 탑-다운으로 순회한다. 각 뷰는 트리를 재귀하는 동안 측정한 수치를 트리 아래로 보낸다. 측정이 모두 끝나면 모든 뷰는 측정한 값을 가지게 된다. 두번째 단계 또한 탑-다운으로 layout(int, int, int) 에서 일어난다. 각각의 부모는 측정단계에서 계산된 사이즈를 사용해서 자신의 모든 자식을 배치한다.

### Layout Pass

레이아웃을 시작하려면, requestLayout() 를 호출한다. 일반적으로 현재 범위내에 더이상 맞지 않을때 자체적으로 뷰에 의해 호출된다.



## 렌더링

앱에서 사용자의 품질 인식에 영향을 미칠수 있는 앱의 주요한 측면은 이미지와 텍스트가 스크린에 부드럽게 렌더링하는 것이다. 가장 중요한 것은 버벅거림과 늦은 응답을 피하는 것이다.

### Reduce overdraw

앱이 하나의 프레임안에서 같은 픽셀을 다시 그리는 횟수를 최소화한다.
- 레이아웃에서 불필요한 배경 삭제
- 뷰 계층 구조의 평면화
- 투명도 줄이기

### Performance and view hierarchies

레이아웃 및 측정 실행을 효율적으로 하며, 중복적인 계산을 피한다.
- 불필요한 중첩 레이아웃 제거
- include / merge 적용

### Analyze with Profile GPU Rendering

프로파일 툴을 사용해서 렌더링이 느려질수 있는 병목 구간이 발생하는 부분을 확인한다.
- 입력 처리 : 앱이 입력 이벤트를 처리하는 시간으로 입력 이벤트 콜백의 결과로 호출된 코드를 실행하는데 걸리는 시간을 나타낸다. 일반적으로 오랜 시간이 걸리는 경우는 메인쓰레드에서 발생하며, 작업을 최적화하거나 다른 쓰레드를 이용해서 작업을 실행하도록 한다
- 애니메이션 : 현재 프레임에서 실행되는 애니메이션에 대해 걸리는 시간을 측정한다. 일반적으로 오랜 시간이 걸리는 경우는 애니메이션의 속성 변경으로 인해  발생한다.
- 측정 및 배치 : 안드로이드에서 뷰를 스크린에 그리기 위해 측정하고 배치하는데 걸리는 시간을 측정한다. 일반적으로 오랜 시간이 걸리는 경우는 배치할 뷰가 너무 많거나 혹은 계층구조의 잘못된 장소에서 중복 계산이 이뤄지며, Traceview와 Systrace 를 사용해서 호출 스택을 확인하여 문제를 파악할 수 있다.
- 그리기 : 백그라운드 혹은 텍스트 그리기와 같은 뷰를 그리기 명령어로 번역하는 단계로 시스템은 명령어를 디스플레이 목록에 캡처한다. 무효화된 뷰에서 onDraw 호출을 실행해 걸리는 모든 시간을 측정하며, 오랜 시간이 걸린다는 것은 무효화된 뷰가 많다는 것을 의미한다. 뷰가 무효화되는 경우 뷰의 디스프레이 목록을 재생성한다. 혹은 커스텀한 뷰가 onDraw 할때 복잡한 로직을 가질 때도 실행시간이 오랜 걸린다.
- 동기화/업로드 : 현재 프레임에서 비트맵 객체가 CPU 메모리에서 GPU 메모리로 전송되는데 걸리는 시간을 측정한다. 오랜 시간이 걸리는 경우는 작은 리소스가 많이 로드되어 있거나 적지만 큰 리소스가 로드되어 있는 경우이다. 비트맵 해상도가 실제 디스플레이 해상도보다 큰 사이즈로 로드되어 있지 않도록 하거나 동기화 전에 비동기로 미리 업로드해서 실행 시간을 줄일 수 있다.
- 명령어 실행 : 디스플레이 목록을 화면에 그리기 위한 모든 명령어를 실행하기 위해 걸리는 시간을 측정한다. 주어진 프레임에서 렌더링하는 디스플레이 목록과 수량을 직접적으로 측정한다. 내재적으로 많은 그리기 동작이 있는 경우, 오랜 시간이 걸릴수 있다.
- 프로세스/스왑버퍼 : 안드로이드는 디스플레이 목록을 GPU 에 제출하는것을 끝내면, 시스템은 그래픽 드라이버에 현재 프레임이 완료되었음을 알리며 드라이버를 업데이트된 이미지를 화면에 표시한다. CPU 와 GPU 는 병렬로 처리되는데 CPU 가 GPU 처리보다 빠른 경우, 프로세스간 통신 큐가 가득찰 수 있다. CPU 는 큐에 공간이 생길때까지 기다린다. 큐가 가득한 상태는 스왑버퍼 상태에서 자주 발생하는데 이 단계에서 프레임 전체의 명령어가 제출되기 때문이다. 명령어 실행 단계와 비슷하게 GPU 에서 일어나는 일의 복잡성을 줄여 문제를 해결할 수 있다.
- 기타 : 렌더링 시스템이 작업을 수행하는 시간외에 렌더링과 관련 없는 추가적인 작업이 있다. 일반적으로 렌더링의 연속된 두 프레임 사이에서 UI 스레드에서 발생할 수 있는 일을 나타낸다. 오랜 시간이 걸리는 경우, 다른스레드에서 실행해야할 콜백, 인텐트 또는 기타 다른 작업이 있을 수 있다. Method tracing 또는 Systrace 를 사용하면 메인스레드에서 실행중인 작업을 확인해 문제를 해결할 수 있다.








[https://developer.android.com/guide/topics/ui/how-android-draws](https://developer.android.com/guide/topics/ui/how-android-draws)

[https://developer.android.com/topic/performance/rendering](https://developer.android.com/topic/performance/rendering)

[https://developer.android.com/topic/performance/rendering/inspect-gpu-rendering](https://developer.android.com/topic/performance/rendering/inspect-gpu-rendering)

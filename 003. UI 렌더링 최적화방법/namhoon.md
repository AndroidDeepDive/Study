## Optimize UI rendering - View Rendering

Android의 근간인 GUI.

이 GUI를 구성하는 화면 구성 요소의 기본이 바로 View이다.

View는 크게 두 가지로 구분할 수 있다.

먼저 눈에 보이는 Widget. 그리고 눈에 보이지않는 Layout이 그것이다.

이 GUI를 구성하는 View가 어떻게 렌더링 되는 지 알아보자.

>>>>
Ref
- https://developer.android.com/reference/android/view/View
- https://developer.android.com/training/custom-views/create-view
>>>

### LifeCycle

당연하겠지만 View도 Lifecycle을 가지고 있다.

>>>
LifeCycle 이미지 추가
>>>

>>>
Ref
- https://codentrick.com/android-view-lifecycle/
- https://medium.com/@sahoosunilkumar/understanding-view-lifecycle-in-android-e42890aab16
>>>

### View를 그리는 방법

>>>>
Ref
- https://developer.android.com/training/custom-views/create-view

1. 코드로 작성하는 법
2. xml로 작성하는 법
성능 차이가 있을까?
컴파일된 입장에서 이미 거의 없을텐데?
- https://stackoverflow.com/questions/54032921/performance-android-views-generated-programmatically-vs-xml-views
>>>

### Rendering

Android에서의 Screen Layer를 명세해야할 필요가 있다.

추상화 Level 1
Kernel -> Framework -> Application -> User 순서

추상화 Level 2
SurfaceFlinger
- BufferQueue / SurfaceConstrol or ASurfaceControl

WindowManager
- get Layer from SurfaceFlinger
- Layer = SurfaceControl(BufferQueue + Surface + Meta data(+ Diplay Frame...))

ASurfaceControl
- ASurfaceControl
- ASurfaceTransaction
- ASurfaceTransactionStats

WindowManager
- Window Control
- Window Object = View's Container

>>>
Ref 
- https://source.android.com/devices/graphics/surfaceflinger-windowmanager?hl=ko
- Surface + SurfaceHolder = https://source.android.com/devices/graphics/arch-sh 
  - Canbas Rendering : Skia가 여기서 등판
  - OpenGL ES or Vulkan
>>>

## Optimize UI rendering - How to Optimize?

View를 그리는 것의 최적화는 두 가지 방향성이 있다고 볼 수 있다.

1. 그려지는 View 자체를 최적화하는것
2. View를 렌더링하는 프로세스를 최적화하는 것

각각 분리해서 알아보자.

### 그려지는 View 자체를 최적화하는것

1. Optimizing Layout Hierarchies (Hierarchy Viewer / Lint)
2. weight 속성 최소화
3. `<inclue>`와 `<merge>` 활용
4. 무의미한 background 제거
5. Linear in Linear -> Relative로 전환

>>>
Ref
- https://developer.android.com/training/improving-layouts/optimizing-layout
- https://developer.android.com/topic/performance/rendering/optimizing-view-hierarchies?hl=ko
- https://developer.android.com/training/improving-layouts/loading-ondemand
>>>

### View를 렌더링하는 프로세스를 최적화하는 것

1. Lazy Load by ViewStub
2. Inspect GPU rendering speed and overdraw
3. 커스텀 뷰의 `onDraw()` 미사용시 clip과 reject 분리

>>>
Ref
- https://developer.android.com/topic/performance/rendering/inspect-gpu-rendering
- https://developer.android.com/reference/android/graphics/Canvas.html
- https://stuff.mit.edu/afs/sipb/project/android/docs/training/custom-views/optimizing-view.html
>>>

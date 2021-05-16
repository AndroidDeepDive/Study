# Introduce Android UI Rendering Principle and View Optimization - 2

저번 포스트에서는 View와 ViewGroup 개념, UI가 그려지는 과정, UI가 그려질 때 View의 생명주기에 대해 보았다. 이번시간에는 UI Rendering 시 화면에 보여질 때 어떤 과정을 거치는지 알아보자.

## UI Rendering

아래 내용은 Google Android Source Document의 [그래픽 아키텍쳐](https://source.android.com/devices/graphics/architecture) 내용을 참고하여 작성되었다.

위와 같은 최적화가 내부적으로 일어나기 위해서는 화면을 그리는 필수요소로, **Window, Surface, Canvas, View** 레이어로 나눠서 처리하게 된다.

### Window

무언가를 **그릴 수 있는 창**(Window)이다. 이 영역에서는 그릴 수 있는 Surface를 들고 있고, 애플리케이션은 WindowManager와 상호작용을 통해 Window를 만들고, Window위에 올려진 Surface를 통해 UI를 그린다.

하나의 화면에서는 여러개의 Window를 가지며, WindowManager를 통해 관리받게 된다. 

대표적으로 PhoneWindowManager, DialogWindowManager와 같은 요소가 있다.

## Surface and SurfacHolder

기본적으로 안드로이드에서 모든 화면에 그려지는 요소들은, **Surface**를 통해 처리된다. 안드로이드에서는 Surface와 SurfaceHolder를 통해 그려지는 영역을 제어한다.

### Surface

Surface는 화면에 표시하는 이미지를 앱이 렌더링 할 수 있도록 해준다. 해당 클래스는 Android 1.0 초창기부터 API에 내장되어 있었으며, 안드로이드 온라인 도움말 사이트에는 **Surface** 클래스에 대해서 **Screen Compositor**가 관리하는 하나의 Raw 버퍼를 가리키는 핸들이라고 설명한다.

잘 이해가 안갈테니, 이미지를 함께 보면서 이해 해보도록 하자.

![Surgace & Surface Flinger](https://imgur.com/ptHtNno.jpg)

하나의 애플리케이션은 기본적으로 하나의 **Surface**를 가진다.  **BufferQueue**는 그래픽 데이터의 버퍼를 생성하는 요소(생산자)를 표시 데이터 또는 추가 처리를 허용하는 요소(소비자)에 연결한다.

**Suface**는 자주 사용되는 **BufferQueue**를 생성하고(Producer), 버퍼에 데이터를 담아 **SurfaceFlinger**(Consumer)로 전달하여 출력한다. 이를 통해 화면에 drawing을 하여 Application과 상호작용하게 된다.

각 Surface는 **이중 버퍼 렌더링**을 위한 1개 이상 (보통 2개)의 버퍼를 가진다.

> #### 이중 버퍼 렌더링(Double Buffer Rendering)
>
> 스크린에 출력될 화면 데이터는 **프레임 버퍼**에 저장되는데, 하나의 버퍼만 가지는 경우 이미지가 반복해서 그려지게 되거나, 이미지가 다 그려지지 않아도 화면 주사율 때문에 렌더링을 해야할 때, 다 그려지지 않은 프레임 버퍼가 렌더링이 되어서 이미지가 깜빡이는 Flicker 현상이 나타날 수 있다.
>
> 이를 해결하기 위해 프레임 버퍼에 바로 렌더링 하지 않고, 다른 버퍼를 만들어서 그 버퍼에 렌더링을 완료 하고, 다음 프레임 버퍼에 옮기는 방식을 사용하여 Flicker 현상을 해결한다.

### SurfaceFlinger

SurfaceFlinger는 버퍼를 받아들이고 버퍼를 구성하며 버퍼를 디스플레이로 보내는 역할을 한다. SurfaceFlinger는 BufferQueue 및 SurfaceControl을 통해 버퍼를 처리할 수 있다.

 안드로이드 10부터는 **ASurfaceControl**라는 것을 통해 버퍼를 받아들인다.

자세한 내용은 Google Android Source Document의 [SurfaceFlinger 및 WindowManager](https://source.android.com/devices/graphics/surfaceflinger-windowmanager)를 참고하자.

### SurfaceHolder

SurfaceHolder 인터페이스는 앱이 Surface을 수정하고 제어할 수 있게 해준다. 일부 서비스에서는 제어가 필요한 케이스가 있는데, 대표적으로 **SurfaceView, GLSurfaceView**를 사용한 기능이다. 영상처리나 카메라 미리보기 화면을 렌더링할 때 특히 많이 사용한다. 일반 VIew와 달리 onDraw를 호출하는 것이 아닌, Thread를 이용하여 화면에 강제로 그려 처리한다.

### Canvas Rendering

안드로이드에서 모든 그려지는 UI 요소는 대부분은 [OpenGL ES](https://source.android.com/devices/graphics/arch-egl-opengl) 또는 [Vulkan](https://source.android.com/devices/graphics/arch-vulkan)을 사용하여 View를 렌더링한다.

하지만, 예외 요소도 있기 마련이다. 예를들면 2D게임을 구현한다던지, 스트리밍 컨텐츠를 보여주는 플레이어라던지, 카메라 등에서는 캔버스 API를 통해 뷰를 그리는 작업이 이뤄진다. 이를 **Canvas Rendering**이라 한다.

Canvas가 가지는 특징을 정리하면 아래와 같다.

- **onDraw() 재정의**
- Canvas(그리는 내용) => Paint(그리기 방법)
- 직접 구현도 가능하고, Drawable를 상속한  BitmapDrawable를 이용하여 그릴 수 있다.
- 프로젝트에 저장된 이미지 리소스(비트맵 파일)를 확장한다.
- Drawable 속성을 정의하는  **XML** 리소스(우리가 아는 벡터 파일 변환도 여기 해당!)
- 도형 Drawable(라운딩 처리 된 사각형, 동그라미 등등등..)
- NinePatch 드로어블
- 최종적으로는 SKIA를 통해 캔버스 c++ native API를 호출하여 그린다.



예전에는 모든 렌더링은 소프트웨어 레벨에서 처리되었다. 지금은 하드웨어 레벨에서 처리되어 최적화되어 구현이 가능하지만, 이러한 방식으로 수동적으로 처리가 가능하다.

안드로이드의 경우 일반적으로 그리기를 프레임워크에 맡겨서 Rendering Process를 거치지만(우리가 알고 있는 `onDraw()` 함수를 통해 그리는 것을 말함), 위에서 언급한 Canvas Rendering의 경우 쓰레드를 이용해 화면에 강제로 그려 원하는 시점에 화면에 그릴 수 있다.

캔버스 구현은 [Skia 그래픽 라이브러리](https://skia.org/)에서 제공한다. 직사각형을 그리고 싶은 경우 적절히 버퍼에 바이트를 설정하는 캔버스 API를 호출한다.

Canvas 인스턴스를 생성할 때는 이에 바탕이 되는 Bitmap 인스턴스를 생성하여 처리한다.

Surface까지 도달하기 위해 어떤 흐름을 타는지 알아보자.

![android canvas drawing](https://i.imgur.com/JNfYVx7.png)



예를 들어, 우리가 View에서 직접 Rect를 생성하여 사각형을 그리고 싶다면, Skia 라이브러리를 호출하여 처리할 수 있다. Skia는 바이트들을 적절하게 버퍼의 형태로 구성한다. 그리고 버퍼가 두 클라이언트에 의해 한번에 업데이트 되는 것을 방지하기 위해, 버퍼에 액세스 하기 위해서는 락을 사용해야 한다. **lockCanvas()**는 버퍼에 락을 걸고 드로잉을 처리하기 위한 Canvas를 리턴한다. 그리고 **unlockCanvasAnsPost()**는 버퍼에 락을 해제하고, 그것을 compositor로 전달한다.

그 과정에서 일어나는 매커니즘은 매우 복잡하지만, 결론은 Surface로 전달 된다.

과거에는 View를 캔버스로 처리했지만, 현재는 범용 3D 엔진을 탑재한 기기들이 늘어나면서, 안드로이드에서 모든 그려지는 UI 요소는 대부분은 [OpenGL ES](https://source.android.com/devices/graphics/arch-egl-opengl) 또는 [Vulkan](https://source.android.com/devices/graphics/arch-vulkan)을 사용하여 View를 렌더링하도록 구성된다. 하지만 이전에 사용하던 API와의 호환성이 중요하기 때문에, 하드웨어 가속 Canvas API가 만들어지게 되었다.

---

지금까지는 그래픽 데이터를 처리하는 과정 및 요소에 대한 내용을 보았다면, 이번에는 해당 데이터를 화면에 직접 보여지는 과정에서 어떤 요소들이 필요하고 어떻게 동작하는지 보도록 하겠다.

## Surface Flinger and Harware Composer

해당글은 [Android Drawing Process 1(App surface, SF Layer)](https://lastyouth.tistory.com/24를 참고하여 작성하였다.

앞에서 언급했던 **SyrfaceFlinger**에서는 **HWComposer(Hardware Composer)**를 통해 그래픽 데이터를 디바이스 디스플레이에 전송한다.

이를 도식화 하면, 아래와 같이 표현 가능하다.

![Surface Flinger and Harware Composer](https://imgur.com/cK00ybR.jpg)



### Layer Management

여기서 Layer Management는 우리가 앞서 언급한 각 어플리케이션이 갖는 Surface에서 SufraceFlinger에 의해 Layer 형태로 관리된다. 도식화 하면 아래와 같다.

![Layer Management](https://i.imgur.com/7990Bf0.png)



### Screen Refreshing

디바이스 디스플레이는 특정 주기로 리프레시되며, 일반적으로는 폰이나 테블릿에서는 초당 60프레임의 속도로 리프레시된다. 요즘 나오는 디바이스의 경우 90hz, 120hz 고주사율 디스플레이를 탑재하면서, 게임이 아닌 일반적인 애플리케이션은 높은 fps를 지원하게 되었다.

일반적인 디스플레이 주사율을 기준으로 했을 때, 60 FPS(1초에 60프레임)을 지원하며, 이를 바꿔부르면, 16ms안에 화면을 갱신하면 사용자에게 있어 자연스러운 화면전환을 제공할 수 있기 때문에, 16ms 안에 draw가 완료되어야 한다.

안드로이드 4.0 - ICS(아이스크림 샌드위치) 버전까지는 Screen Refershing과정에서 Framebuffer 출력이 SurfaceFligner의 Thread 동작 주기로 임으로 출력이 출력이 되어 프레임이 일정하지 않았다.

만약 16ms 이상 걸리게 된다면 어떻게 될까? View 렌더링 과정이 지체되어 사용자 경험에 좋지 않을 것이고, 애니메이션이 매끄럽게 보이지 않으며, 앱이 정상적으로 작동되지 않는 것처럼 보이게 될 것이다. 흔히 "렉걸린다" 라는 표현을 주로 하는데, 이를 `Frame Drop` 이라고 부른다. `Frame Drop` 이 발생하는 원인은 오랜 시간동안 뷰의 계층구조를 새로 그리거나, 유저가 볼 수 없는 화면에 공을 들여 색칠하는 경우 CPU & GPU 에 과부하를 일으키게 된다.

만약 디스플레이 컨텐츠가 UI 리프레시 도중에 업데이트가 된다면, **티어링(tearing)** / **프레임 드롭(Frame Drop)** 현상이 나타날 것이다. 

![Frame Drop](https://images.velog.io/images/jshme/post/68faaf0a-1faf-46f3-be10-51c5fffa7d62/image.png)



### Project Butter 🚀

![Proejct Butter](https://imgur.com/n6fsbPA.jpg)

이러한 문제를 해결하기 위해 Android 4.1JB(젤리빈) 버전부터는 **Project Butter**를 발표하여 60hz디스플레이에 온전하게 60 FPS를 애플리케이션에서 지원하도록 하였다.

그래서 각 리프레시 주기 동안에 컨텐츠 업데이트를 마무리 하는 것이 중요하다.

Proejct Butter에서는 매끄러운 화면 출력을 위해 **VSync** 기술과 **Choreographer**를 도입하였다.

### Vsync Processing

**VSync**는 Vertical Synchronization(수직 동기화)의 약자이다. 

![Dwawing with VSync](https://imgur.com/ha7Eriy.jpg)

**VSync**는 화면의 출력되는 정보가 변경이 되었을 때 이를 동기화하고, 지속적으로 갱신해주는 기능이다. 쉽게 설명하면, GPU의 Rendering Rate, 즉 fps와 Device Presenting Rate, 즉 hz사이의 간극이 있을 때 이를 조정한다.

예를들면, GPU는 100fps(초 당 100개의 프레임을 그림), Device Screen은 60hz(초 당 60개의 화면을 그림)라면, GPU에서 그리는 것을 그대로 Device Screen에 뿌리게 되면 계속 Delay가 생길 수 있다.

이 때, VSync가 GPU 프레임과 Device 화면간의 시간차(Time Gap)를 계산하여 필요한 프레임만 만 그려주어 최적화를 해준다. VSync에 대한 Detail한 원리에 대해서 잘 소개된 영상은 [Android Performance Patterns: Understanding VSYNC](https://www.youtube.com/watch?v=1iaHxmfZGGc) 영상을 참고하라.



### Choreographer

SurfaceFlinger에서 송신되는 Vsync Event를 요청/수신하여 아래 작업을 처리한다.

- **Input Event Handling**
- **Self-Invalidation**
- **Animation**



### ViewRootImpl

**ViewRootImpl**은 DecorView와 **Choreographer**를 연결해주는 일종의 핸들러 역할을 하는 객체로써, DecorView와 Choreographer 사이에서 둘의 요청을 처리하고 중계한다.



### Rendering Flow

최종적으로 **Vsync Process**에서는 아래 과정으로 처리된다.

![Rendering Flow](https://i.imgur.com/1ZQEOg0.jpg)



실제 그리기는 현재 foreground에 표시되고 있는 어플리케이션에서 그릴 것이 생김으로써 시작한다.

1. 특정 변화가 생겨 `Invalidate`를 호출하게 되면 ViewRootImpl에  `scheduleTraversal()` 함수를 호출한다. 

   `scheduleTraversal()` 메서드 내부에서는 **Choreographer** 객체에게 다음 Vsync를 예약해달라는 요청을 보내게 된다. 이 작업이 위 그림에서 Invalidate와 Vsync scheduling에 해당한다.

2. Choreographer에 VSync 스케쥴링을 요청하여 Frame Render를 Screen Rate에 맞춘다. Vsync 예약 요청을 받은 Choreographer는 SurfaceFlinger로 하여금 다음 Vsync 도착 시 알려달라고 요청한다.

3. 다음 Vsync signal이 도착하여 Choreographer에서 받게된다.

4. Choreographer는 ViewRootImpl의 `performTraversal()` 메서드를 호출한다. performTraversal 메서드 내부에서는 다시 그려야 될 부분을 `measure()`하고, 레이아웃을 재구성한다.

5. 마지막으로 `performDraw()` 메서드를 호출하여 그리기를 수행한다.



## Software Rendering Model V.S. Hardare Rendering Model

안드로이드의 렌더링 모델은 안드로이드3.0((API 레벨 11, 허니컴))버전을 기준으로 나뉜다. 이전까지는 소프트웨어 모델을 기반으로 렌더링이 되었다.

### Software Rendering Model

Software Rendering Model은 Android 3.0 전 버전까지 사용되던 방식이다.

CPU 연산기반으로 동작하였으며, 아래 두단계로 그려진다.

1. 계층 구조 무효화
2. 계층 구조 그리기



### Hardare Rendering Model (By using GPU)

Android 3.0부터 Android 2D 렌더링 파이프라인 하드웨어에서는 가속화를 지원하므로, `View`의 캔버스에서 실행되는 모든 그리기 작업에서 GPU를 사용한다.

하드웨어 가속을 사용하려면 필요한 리소스가 늘어나므로 앱에서 더 많은 RAM을 사용한다.

Android 시스템에서는 여전히 `invalidate()` 및 `draw()`를 사용하여 화면을 업데이트하고 보기를 렌더링하지만, 실제 그리기는 다르게 처리한다.

Android 시스템에서는 즉시 그리기 명령을 실행하지 않고, 보기 계층 구조의 그리기 코드 출력을 포함하는 표시 목록에 기록한다.

또한 Android 시스템에서 `invalidate()` 호출을 통해 더티로 표시된 보기의 표시 목록을 기록하고 업데이트만 하면 되도록 최적화한다.

무효화되지 않은 보기는 단순히 이전에 기록된 표시 목록을 다시 실행하여 다시 그릴 수 있습니다. 새 그리기 모델에는 다음 세 단계가 포함된다.

1. 계층 구조 무효화
2. 표시 목록 기록 및 업데이트
3. 표시 목록 그리기

### Internal UI Rendering Optimization

우리가 사용하는 `상위 수준의 객체`들(Button, TextView, etc.)은 아래의 과정을 거쳐 화면의 픽셀로 나타낼 수 있다.

![View Rendering Principle](https://media.vlpt.us/images/jshme/post/b2efded5-296a-403c-8ca4-8f0bc62d055b/image.png)



- CPU Level에서 상위 수준의 객체들을 가져오고, 이 객체를 그리기 위한 집합인 `displaylist` 로 바꾸어준다.
- GPU는 높은 수준의 객체들을 가져와 텍스처 또는 화면에서 픽셀로 바꾸어주는 `Rasterization`기능을 수행한다. CPU에서 만들어진 명령어들은 OpenGL-ES api를 이용해 GPU에 업로드 되며, 픽셀화에 이용된다. 한번 업로드되면 displaylist 객체는 캐싱되는데, 이는 우리가 다시 displaylist를 통해 그리고 싶다면 다시 새로 생성할 필요없이 기존에 존재하는 것을 다시 그리기 때문에 비용이 훨씬 더 저렴하다.

UI 렌더링이 진행될 때, CPU에서 displaylist 를 생성하고 GPU에 업로드 하는 과정은 매우 고비용(High Cost)적이기 때문에, 효율적인 개선안을 찾아야한다.

> ## Extra Info
>
> ### Displaylist란 ?
>
> ![Displaylist](https://t1.daumcdn.net/cfile/blog/24147F3558C5B7AB06?original)
>
> 안드로이드 3.0 이전에는 View의 변경사항이 일어나면, ViewRoot까지 전파하도록 설계되었다. View에서 ViewGroup, ViewRoot를 거쳐 다시 View 까지 내려와야하는 과정이 많은 코드를 접근하기 때문에 비효율적이었다.
>
> 이러한 대안에 Displaylist가 나오게 되었는데, 이는 UI 요소를 그리기 위한 정보를 리스트로 모아둔 것이다. UI요소의 변경이 일어났을 때, 다시 View Tree 를 탈 필요가 없이 리스트를 확인하고 사용하면 된다는 이점이 있다.

---

이번시간에는  Window, Surface, Canvas에 대해 소개하고, 소프트웨어 레벨에서 어떻게 하드웨어 레벨까지 동기화 되는지 정리해보았다. 또한 View가 렌더링 될 때 두가지 방식으로 어떻게 동작하는지 개념 설명 및 원리 설명을 했다.

 다음 포스트에서는 하드웨어 가속을 사용하는 GPU 렌더링 체계의 경우 어떤 문제가 있고, 어떻게 프로파일링 하고, 어떻게 모니터링하는지 알아보도록 하겠다. 그리고 어떻게 최적화 할 수 있을지 방법을 소개하도록 하겠다.


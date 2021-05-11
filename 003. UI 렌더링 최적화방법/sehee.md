## 👏 UI는 어떻게 생성될까?
우리가 사용하는 `상위 수준의 객체` 들(Button, TextView, etc.)은 아래의 과정을 거쳐 화면의 픽셀로 나타낼 수 있다. ![](https://images.velog.io/images/jshme/post/b2efded5-296a-403c-8ca4-8f0bc62d055b/image.png)
* CPU Level에서 상위 수준의 객체들을 가져오고, 이 객체를 그리기 위한 집합인 `displaylist` 로 바꾸어준다.
* GPU는 높은 수준의 객체들을 가져와 텍스처 또는 화면에서 픽셀로 바꾸어주는 `Rasterization` 기능을 수행한다. CPU에서 만들어진 명령어들은 OpenGL-ES api를 이용해 GPU에 업로드 되며, 픽셀화에 이용된다. 한번 업로드되면 displaylist 객체는 캐싱되는데, 이는 우리가 다시 displaylist를 통해 그리고 싶다면 다시 새로 생성할 필요없이 기존에 존재하는 것을 다시 그리기 때문에 비용이 훨씬 더 저렴하다.

UI 렌더링이 진행될 때, CPU에서 displaylist 를 생성하고 GPU에 업로드 하는 과정은 매우 고비용적이기 때문에, 효율적인 개선안을 찾아야한다. 


### Displaylist 란 ?
![](https://t1.daumcdn.net/cfile/blog/24147F3558C5B7AB06?original)
안드로이드 3.0 이전에는 View의 변경사항이 일어나면, ViewRoot까지 전파하도록 설계되었다. View에서 ViewGroup, ViewRoot를 거쳐 다시 View 까지 내려와야하는 과정이 많은 코드를 접근하기 때문에 비효율적이었다.

이러한 대안에 Displaylist가 나오게 되었는데, 이는 UI 요소를 그리기 위한 정보를 리스트로 모아둔 것이다. UI요소의 변경이 일어났을 때, 다시 View tree 를 탈 필요가 없이 리스트를 확인하고 사용하면 된다는 이점이 있다.



## 👏 UI 렌더링 과정에 대해 이해해보자
View가 렌더링 될 때, 상위수준의 `ViewGroup` 에서부터 하위 자식인 `View` 로 내려가면서 `Measure -> Layout -> Draw` 를 거치게 된다.

![](https://images.velog.io/images/jshme/post/384fceb5-01d8-4b9a-8241-d509eedda458/image.png)

1. Measure - ViewGroup와 View들의 크기를 측정하기 위해 View Tree를 top-down 형식으로 탐색을 완료한다. ViewGroup이 측정되면, 하위속성들도 측정한다.

	* View 의 크기는 2가지로 정의될 수 있다.
   
    		1. measured width & measured height : 뷰가 부모뷰 크기의 범위 내에서 가지고 싶어하는 너비와 높이이다.
    		2. drawing width & drawing height :  뷰의 실제 너비와 높이로 뷰를 그리기 위해서 사용한다.
   
     
   
     ✅ View의 Padding, Margin 등을 고려하면 원하는 크기에서 Padding 및 Margin 값을 빼야 하기 때문에 Measured Width, Measured Height 는 Drawing Width, Drawing Height 와 다를 수 있다.
   
2. Layout - 이 과정에서도 top-down 형식의 탐색이 일어나게 되는데, 이 때는 각각의 ViewGroup이 Measure 단계에서 측정된 크기를 이용해서 하위속성들의 위치를 결정한다.

3. Draw - GPU에게 명령을 내려주는 단계이다. 그려야 할 객체들의 리스트를 GPU에게 보내기 위해 View Tree에서 각 객체의 대한 Canvas 객체가 생성된다. 이 때, 이전 1,2단계를 거쳐 결정된 객체들의 크기와 위치에 대한 정보가 들어가게 된다.

만약 View가 변할 때, 시스템에게 알려주기 위한 2가지 방법이 존재하는데, `Invaliadate()`가 호출 될 때에는 draw부터 다시 작업이 시행되고, `requestLayout()`이 호출될 때에는 measure -> layout -> draw 단계를 다시 거치게 된다. 

> **번외) `layout_weight` 의 배신**
>
> Linear Layout 의 layout_weight 속성을 사용하는 경우 자식 뷰는 두번의 Measure pass가 필요하기 때문에 많은 비용이 소모된다. layout_weight는 단순히 비율을 나누어 공간을 차지하는 것이 아닌, 부모의 View가 그려지고 나서 남은 공간이 얼마만큼인지, 다른 View들이 그려지고 나서 다시한번 남은공간도 계산하고 나서 자기 자신을 그리기 때문에 지속적인 계산이 일어나게 된다.  




## 👏 Slow Rendering
<img src="https://images.velog.io/images/jshme/post/b17e81d9-7efb-410a-8c9b-3332e19518e7/image.png" width="100%">

 16ms안에 화면을 갱신하면 사용자에게 있어 자연스러운 화면전환을 제공할 수 있기 때문에, 초당 60fps 안에 draw가 완료되어야 한다.

16ms 라는 수치는 정해진 수치이며, View 렌더링에 사용되는 `Choreographer` class에서 frame rate를 제어할 수 있도록 구성되어있다.  

``` java
public final class Choreographer {
    private static float getRefreshRate() {
        DisplayInfo di = DisplayManagerGlobal.getInstance().getDisplayInfo(
                Display.DEFAULT_DISPLAY);
        return di.refreshRate;
    }
}

/**
 * Describes the characteristics of a particular logical display.
 * @hide
 */
public final class DisplayInfo implements Parcelable {
    /**
     * The refresh rate of this display in frames per second.
     * <p>
     * The value of this field is indeterminate if the logical display is presented on
     * more than one physical display.
     * </p>
     */
    public float refreshRate;
}

final class VirtualDisplayAdapter extends DisplayAdapter {
    private final class VirtualDisplayDevice extends DisplayDevice implements DeathRecipient {
        @Override
        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (mInfo == null) {
                mInfo = new DisplayDeviceInfo();
                mInfo.name = mName;
                mInfo.uniqueId = getUniqueId();
                mInfo.width = mWidth;
                mInfo.height = mHeight;
                mInfo.refreshRate = 60;
                /*** Part of the code is omitted ***/
            }
            return mInfo;
        }
    }
}
```

<img src="https://images.velog.io/images/jshme/post/68faaf0a-1faf-46f3-be10-51c5fffa7d62/image.png" width="100%">

만약 16ms 이상 걸리게 된다면 어떻게 될까? View 렌더링 과정이 지체되어 사용자 경험에 좋지 않을 것이고, 애니메이션이 매끄럽게 보이지 않으며, 앱이 정상적으로 작동되지 않는 것처럼 보이게 될 것이다. 흔히 "렉걸린다" 라는 표현을 주로 하는데, 이를 `Frame Drop` 이라고 부른다. `Frame Drop` 이 발생하는 원인은 오랜 시간동안 뷰의 계층구조를 새로 그리거나, 유저가 볼 수 없는 화면에 공을 들여 색칠하는 경우 CPU & GPU 에 과부하를 일으키게 된다.  



> ![](https://images.velog.io/images/jshme/post/69d9cb45-6fdf-4715-acfa-3605901e381d/image.png)
>
> 
>
> 개발자 옵션에서 볼 수 있는 `GPU 렌더링 프로파일` 에서 녹색 라인이 16ms 를 나타내는데, 이를 넘지 말라는게 위의 이유 때문이다. 
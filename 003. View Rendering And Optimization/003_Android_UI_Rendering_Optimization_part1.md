[TOC]

# Introduce Android UI Rendering Principle and View Optimization - 1

우리는 안드로이드 개발자로 있으면서 기본적인 것들을 놓치곤한다. 안드로이드에서 가장 중요한것은 사용자에게 보여지는 UI와 UX이다. 이를 위해서 안드로이드 개발자가 해야할 일은 UI를 이쁘게 요구사항에 맞게 그리고, 지연없는 UI로딩을 통해 UI를 그리는 것이다.

글을 작성하다 보니 문득 든 생각이 있었다. 과연 우리가 안드로이드에서 UI를 그릴 때 정확하게 방법을 알고 View를 그리는 코드를 작성하고 있었을까? 나 스스로도 그렇지 않다라는 생각이 들었고, 궁금증이 생겨 ADD 멤버분들과 함께 글을 정리해보았다.

## Android View

안드로이드 개발자라면 한번쯤은 들어봤을 법한, 컴포넌트들이 있다. 바로 아래의 녀석들이다.

![Android Views](https://imgur.com/7AGranC.jpg)

안드로이드의 다양한 컴포넌트들은 다음과 같이 **View**라는 녀석을 상속받아서 만들어졌다. 



### View & ViewGroup

간단히 개념만 잡고 넘어가보도록 해보자. 안드로이드에서 화면을 나누는 두가지 부류는 View와 ViewGroup 두가지로 나눌 수 있다.

![View & ViewGroup](https://imgur.com/EzHZMUm.jpg)

**View**는 Activity, 즉 한 화면에서 화면을 구성하는 최소 단위의 개념이다. 또다른 말로는 위젯이라고 하는데, 이 View를 상속받아 구현되는 수많은 요소들이 존재한다. 대표적으로 ImageView, TextView, Button과 같은 녀석이 있다. 위젯이나 노티피케이션 위젯과 같이 사용성이 특수한 경우 RemoteView를 이용하여 구현을 해야하는 경우도 있는데, 여기서는 이에 대한 설명은 생략하겠다.

**ViewGroup**은 View를 상속받아 구현된 클래스이다. 다른말로는 레이아웃이라고 하며, View를 N개 담을 수 있는 녀석이다. 대표적으로 LinearLayout, FrameLayout, RelativeLayout, ConsraintLayout이 있다.

이러한 방식으로 View, ViewGroup은 Java, 또는 Kotlin코드로 구성될 수 있지만, 더 쉬운 방법으로 Mark-Up 랭귀지를 이용하여 구성도 가능하다. 안드로이드 프로그래밍에서는 XML이라는 마크업 랭귀지를 이용하는데, 결국 이도 XML Parser를 통해 파싱되어 Java코드로 변환되어진다.

안드로이드 개발자들은 어플리케이션의 좋은 성능을 낼 수 있는 방법과, 올바른 방법으로 개발을 하기 위해서라도 View가 그려지는 과정과 생명주기에 대해 잘 알고 있어야 한다. 이를 위해서 먼저 View가 어떤 생명주기를 갖는지 보도록하자.



## View Life-Cycle

상용화 하는 어플리케이션을 만들수록, 우리가 만들어나가는 View는 고도화 될 필요가 있다. 특히 View, ViewGroup을 기반으로 한 컴포넌트를 구성하게 될 때, 우리는 `커스텀 뷰`라는 것을만들게 된다. 커스텀 뷰를 만들게 될 때, 만들면 만들수록 점점 뷰를 그려지는 방식은 복잡해지고, 성능적인 부분을 충족하기 위해 최적화할 수 있는 방법을 고민해야한다. 이를 위해서는 View의 생명주기에 대해서도 잘 알고 있어야 한다.

안드로이드 컴포넌트, Fragment와 마찬가지로 View도 생명주기를 갖고있다.

View의 생명주기는 아래와 같이 도식화 하여 표현 가능하다,

![View Life Cycle](https://imgur.com/qu5Mr6x.jpg)



생명주기에 대해 몇 가지 특징적으로 나눌 수 있는 부분이 있다.



### Constructors

Programmatically하게 View를 생성하는 방법이다. View를 생성하는 방법은 여러가지가 존재하지만, 가장 직관적인 방법 중 하나이다.

생성자의 종류는 여러가지가 존재한다.

- `View(Context context) `
- `View(Context context, @Nullable AttributeSet attrs) `
- `View(Context context, @Nullable AttributeSet attrs, int defStyleAttr) `
- `View(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)`



### Attachment / Detachment

- **Attachment**는 Window에서 붙었음을 의미한다.
- **Detachment**는 Window에서 떼어졌음을 의미한다.

위 동작이 수행되었음을 받을 수 있는 Callback Function으로 두가지 함수를 각각 제공한다.



### onAttachedToWindow()

View가 Window에 붙었을 때 호출된다. 이때부터는 View가 Active하다고 판단되며, 이때부터는 리소스 할당, 파라미터에 대한 계산, 리스너를 부착할 수 있는 준비가 된다.



### onDetachedFromWindow() 

View가 Window에 떼어졌을 때 호출된다. Window상에서 더 이상 존재하지 않으므로, Inactive하며, 기존에 View에 할당된 리소스를 해제해주어야한다. 해당 콜백함수가 불리는 케이스로는 두가지가 있다.

- ViewGroup에서 View가 제거될 때
- Activity가 `finish()` 함수를 호출하여 Activity가 Destroyed될 때



### onMeasure()

View의 크기를 계산한다. ViewGroup은 하위 노드인 View에 대한 크기를 측정하고, 관계성을 파악 후 자신의 크기를 계산한다. 따라서, 하위 노드가 ViewGroup에 올바르게 붙었는지 확인해야한다.

```java
/**
 * 
 * 기본적으로 View의 크기를 측정할 때는 Background의 Dimension Size를 보고 결정한다.
 * 
 * 현재 계산된 크기를 비교하여 Background크기보다 작은경우, Background크기를 반환하여 크기를 반환한다.
 * @param widthMeasureSpec 부모뷰에 의해 적용된 수평 공간
 * @param heightMeasureSpec 부모뷰에 의해 적용된 수직 공간
 *
 */
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                       getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
}
```



### MeasureSpec

ViewGroup의 하위 컴포넌트인 View에서는 크기에 대한 제약을 정하기 위해 스펙을 정하게 되는데, 이를 MeasureSpec을 통해 해결한다. MeasureSpec은 Width, Height에 대한 스펙이며, 크기와 모드로 나뉜다.

그리고 세가지의 값으로 나뉜다.

- **MeasureSpec.EXACTLY** : 부모뷰가 자식뷰의 정확한 크기를 결정한다. 자식뷰의 사이즈와 관계없이 주어진 경계내에서 사이즈가 결정된다.

- **MeasureSpec.AT_MOST** : 자식뷰는 지정된 크기까지 원하는 만큼 커질 수 있다.

- **MeasureSpec.UNSPECIFIED** : 부모뷰가 자식뷰에 제한을 두지 않기 때문에, 자식뷰는 원하는 크기가 될 수 있다.

아래와 같은 코드로 체크할 수 있다.

```java
@Override
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  int mode = MeasureSpec.getMode(heightMeasureSpec)
    if (mode != MeasureSpec.EXACTLY) {
      // Logic
    }
}
```



### onLayout()

계산이 된 이후에는 onLayout 함수를 통해 계산된 View를 Window에 배치한다. 

View와 같은 경우 아래 코드를 기반으로 상속받아 처리한다.

```java
protected void onLayout(boolean changed, int left, int top, int right, int bottom)
```



### onDraw()

onLayout에서 View에 대한 크기 계산 및 배치가 완료되었으므로, 이를 기반으로 onDraw에서는 Canvas라는 인스턴스를 넘겨주며, 해당 Canvas 인스턴스는 DisplayList에 그릴 데이터를 담아서 넘겨준다. onDraw는 한프레임을 그리는 함수기 때문에 반복적으로 호출이된다. 특이하게 여러번 호출되고 CPU연산이 되는 곳이기 떄문에, 객체를 만들어서 사용하는 것은 금기시한다.

View가 draw를 하는 기준은 두가지로 나눌 수 있다.

- View의 모양이 변형이 생겼을 때 - invalidate()
- View의 경계에 변화가 생겼을 때 - requestLayout()

### Invalidate()

invalidate 함수는 직접적으로도 호출이 가능하지만, 상위에서 View에서 여러 View내에 상태가 변형이 되었을 때 호출을한다.

직접 코드를 까보는 것이 아무래도 좋으니, 본인이 확인한 invalidate를 호출하는 시점을 리스트업 해본다.

- **drawableStateChanged** - Background, 포커스 되었을 때 나타다는 하이라이트, Foreground Drawable에 대해 변경점을 체크하며, 상태가 존재하면 변경된 것으로 판단
- **setDefaultFocusHighlight** - 포커스 되었을 때 하이라이트를 기본적으로 세팅해줌. 무조건 새로 그림
- **setForeground**
- **setScrollIndicators** - indicator의 색상과 마스크를 설정한다. 기존 마스크와 머지하고, 플래그를 체크하여 업데이트 된 경우 새로 그린다.
- **setAutoFilled**
- **requestAccessibilityFocus** - 포커스를 쓸 수 있는 권한을 요청할 때  View에대한 포커스에 대해서 달라진 점이 생기면 플래그로 비교하여 처리
- **setForceDarkAllowed** - 어둡게 처리시 필요한 함수
- **onDrawScrollBars** - 스크롤바에 움직임이 생길 시
- **setLayerPaint** - 색상에 대한 변형이 있을 때 처리한다. (alpha, Xfermode, colorFilter)

### requestLayout()

뷰의 경계가 변경되면 무조건 호출돼야하는 함수이다. 이를 통해 재 계산을 거쳐 onMeasure => onLayout을 거친다.

## View State Save/Restore

View는 Activity의 상태에 따라 상태를 저장/복원해야 할 필요가있다. 이를 위한 두가지의 함수를 제공한다.

### onSaveInstanceState() -> Parcelable

상태를 저장하기 위한 함수에서는 상태를 저장할 때 각 View에 대한 고유 ID를 필요로한다. 저장을 할때는 Bundle 인스턴스를 생성하여 저장하면 된다.

Bundle은 Parcelable을 구현한 클래스로, 적절하게 저장하여 처리할 수 있다.

아래 코드를 참고하자.

```java
@Override
public Parcelable onSaveInstanceState() {
  Bundle bundle = new Bundle();
  // The vars you want to save - in this instance a string and a boolean
  String someString = "something";
  boolean someBoolean = true;
  State state = new State(super.onSaveInstanceState(), someString, someBoolean);
  bundle.putParcelable(State.STATE, state);
  return bundle;
}
```

State라는 녀석이 보이는데, 해당 State는 직접 구현해주어야 하는 클래스가 될 수 있다. View에대한 상태를 저장할 수 있는 어떤것이든 상관없다.

### onRestoreInstanceState(Parcelable state)

해당함수를 오버라이딩하면 다시 Paracleable로 된 인스턴스를 받아서 처리가 가능하다. Parcelable을 그대로 쓸수는 없으니, 구현체에 해당하는 인스턴스로 캐스팅하여 처리하면 된다.

아래는 예시이다.

```java
@Override
public void onRestoreInstanceState(Parcelable state) {
  if (state instanceof Bundle) {
    Bundle bundle = (Bundle) state;
    State customViewState = (State) bundle.getParcelable(State.STATE);
    // The vars you saved - do whatever you want with them
    String someString = customViewState.getText();
    boolean someBoolean = customViewState.isSomethingShowing());
    super.onRestoreInstanceState(customViewState.getSuperState());
    return;
  }
  // Stops a bug with the wrong state being passed to the super
  super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE); 
}
```



## View가 그려지는 순서

View가 렌더링 될 때, 상위수준의 `ViewGroup` 에서부터 하위 자식인 `View` 로 내려가면서 `Measure -> Layout -> Draw` 를 거치게 된다.

### Measure Pass

측정 패스는 measure(int, int) 로 구현되며 뷰 트리의 탑-다운으로 순회한다. 각 뷰는 트리를 재귀하는 동안 측정한 수치를 트리 아래로 보낸다.

측정이 모두 끝나면 모든 뷰는 측정한 값을 가지게 된다. 두번째 단계 또한 탑-다운으로 layout(int, int, int) 에서 일어난다. 각각의 부모는 측정단계에서 계산된 사이즈를 사용해서 자신의 모든 자식을 배치한다.

View 의 크기는 2가지로 정의될 수 있다.

- **measured width & measured height** : 뷰가 부모뷰 크기의 범위 내에서 가지고 싶어하는 너비와 높이이다.
- **drawing width & drawing height** : 뷰의 실제 너비와 높이로 뷰를 그리기 위해서 사용한다.

✅ View의 Padding, Margin 등을 고려하면 원하는 크기에서 Padding 및 Margin 값을 빼야 하기 때문에 Measured Width, Measured Height 는 Drawing Width, Drawing Height 와 다를 수 있다.



### Layout Pass

레이아웃을 시작하려면, requestLayout() 를 호출한다. 일반적으로 현재 범위내에 더이상 맞지 않을때 자체적으로 뷰에 의해 호출된다.  이 과정에서도 top-down 형식의 탐색이 일어나게 되는데, 이 때는 각각의 ViewGroup이 Measure 단계에서 측정된 크기를 이용해서 하위속성들의 위치를 결정한다.



### Draw Pass

GPU에게 명령을 내려주는 단계이다. 그려야 할 객체들의 리스트를 GPU에게 보내기 위해 View Tree에서 각 객체의 대한 Canvas 객체가 생성된다. 이 때, 이전 1,2단계를 거쳐 결정된 객체들의 크기와 위치에 대한 정보가 들어가게 된다.

만약 View가 변할 때, 시스템에게 알려주기 위한 2가지 방법이 존재하는데, `Invaliadate()`가 호출 될 때에는 draw부터 다시 작업이 시행되고, `requestLayout()`이 호출될 때에는 measure -> layout -> draw 단계를 다시 거치게 된다.



코드로 보면서 어떤식으로 View가 그려지는지 한번 보도록하자.



### 실험을 통한 View Rendering Process 관찰

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    android:id="@+id/root1"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/constraintContainer2"
        android:layout_width="400dp"
        android:layout_height="400dp"
        android:background="@color/teal_200"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent">

        <TextView
            android:id="@+id/constraintTextView3"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="First TextView"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent" />

        <LinearLayout
            android:id="@+id/linearContainer4"
            android:layout_width="200dp"
            android:layout_height="200dp"
            android:orientation="vertical"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintEnd_toEndOf="parent">

            <ImageView
                android:id="@+id/linearImageView5"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@drawable/ic_launcher_foreground"/>

            <TextView
                android:id="@+id/linearTextView5"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Second TextView"/>

            <RelativeLayout
                android:id="@+id/relativeContainer5"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@color/purple_500">

                <TextView
                    android:id="@+id/relativeTextView6"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Thired TextView"/>

            </RelativeLayout>

        </LinearLayout>

    </androidx.constraintlayout.widget.ConstraintLayout>

    <FrameLayout
        android:id="@+id/frameContainer2"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:background="@color/teal_200_30"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

root Container 를 부모 레이아웃으로 삼아 다양한 depth 의 View 가 쌓여져 있고, 이는 아래처럼 그래프로 나타낼 수 있다.

![View Tree Example](https://media.vlpt.us/images/jshme/post/31b049e8-aa03-4584-be0d-025e7aba3260/Untitled%20Diagram.png)



각각의 View, ViewGroup에 onLayout 시점을 포착할 수 있는 코드를 구현해, 어느 순서로 View 가 그려지는지 확인해보자.

> doOnLayout, doOnDraw, doOnXXX 등을 통해 원하는 라이프사이클 시점을 포착할 수 있다.

### OnLayout 호출로 순서 보기

```kotlin
linearContainer4.doOnLayout {
            println("linearContainer4 doOnLayout")
        }

        constraintContainer2.doOnLayout {
            println("ConstraintContainer2 doOnLayout")
        }

        constraintTextView3.doOnLayout {
            println("constraintTextView3 doOnLayout")
        }

        root1.doOnLayout {
            println("root1 doOnLayout")
        }

        relativeContainer5.doOnLayout {
            println("relativeContainer5 doOnLayout")
        }

        relativeTextView6.doOnLayout {
            println("relativeTextView6 doOnLayout")
        }

        linearImageView5.doOnLayout {
            println("linearImageView5 doOnLayout")
        }

        linearTextView5.doOnLayout {
            println("linearTextView5 doOnLayout")
        }

        frameContainer2.doOnLayout {
            println("frameContainer2 doOnLayout")
        }
```

![onLayout Log](https://images.velog.io/images/jshme/post/80fdce92-5afd-4375-b41e-6fabdf03323a/image.png)

onLayout 시점에는 트리구조로 이루어진 View & ViewGroup 이 **후위순회**를 하며 **측정이 된다**는 것을 알 수 있다.



### 실험을 통해 알게 된 결론

Layout 내에 존재하는 Chile View가 lifecycle 를 거치면서 크기가 측정되는 순서와, 그려지는 순서는 다르다. 

1. Child View 들의 크기가 정해지고 Parent View 가 그 측정값을 알아야지 자신의 크기를 정할 수 있는 것이고, `(Layout 단계)`
2. 도화지가 준비되어야 그림을 그릴 수 있듯이, Parent View 가 먼저 그려져야 Child View 가 그려질 수 있기 때문이다. `(Draw 단계)`

>## Extra Info
>
>### **`layout_weight` 의 배신**
>
>Linear Layout 의 layout_weight 속성을 사용하는 경우 자식 뷰는 두번의 Measure pass가 필요하기 때문에 많은 비용이 소모된다. 
>
>layout_weight는 단순히 비율을 나누어 공간을 차지하는 것이 아닌, 부모의 View가 그려지고 나서 남은 공간이 얼마만큼인지, 다른 View들이 그려지고 나서 다시한번 남은공간도 계산하고 나서 자기 자신을 그리기 때문에 지속적인 계산이 일어나게 된다. 
>
>복잡한 View의 계산을 피하기 위해 Releative Layout & Constraint Layout을 권하는 것이다.
>
>
>
>### **Overdraw 를 피하는 방법**
>
>OverDraw를 피하는 방법 중, **사용자에게 보여지지 않는 Layout의 Background 색을 제거**하면 성능 향상에 도움이 된다는 글이 많이 존재한다. 
>
>하지만 어째서 배경 색을 지우는 것만으로도 성능이 크게 향상될 수 있다는 것일까?
>
>Layout에 Background를 제거한 후 디버깅을 해보면 View **Lifecycle의 onDraw를 거치지 않음**을 알 수 있다. UI 렌더링 시 가장 비용이 많이 드는 부분이 onDraw(GPU에 업로드 하는 과정) 인데, 이 부분이 skip 되니 성능이 향상되는건 당연한 부분일 것이다. 🙂



---

여기까지 View의 생명주기, View가 그려지는 과정에 대해 알아보았다. 

다음 글에서는 View 상위에서 그려지는 요소인 Window, Surface, Canvas에 대해 소개하고, 소프트웨어 레벨에서 어떻게 하드웨어 레벨까지 동기화 되는지 분석하고, 현재 사용하는 하드웨어 모델 기반 렌더링에 대해 소개하도록 하겠다.



---

해당 포스트는 아래 팀원들과 함께 작성되었습니다.

- 김남훈 @Naver
- 배희성 @Rocketpunch
- 송시영 @Smartstudy
- 이기정 @Banksalad
- 정세희 @Banksalad
- 최소영 @Banksalad
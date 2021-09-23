# MVVM vs MVI Part 3 - What is MVI Architecture Pattern?

## MVI의 등장

**MVVM** 아키텍쳐 패턴에 대해 살펴보았으니, 이번엔 **MVI** 아키텍쳐 패턴에 대해 살펴보자.

**MVI** 는 3개의 개념을 골자로 한다.

1. 단방향 데이터 흐름 (unidirectional cycle of data)
2. 차단되지않는 의도 (Processing of intents is non-blocking)
3. 상태의 불변성 (The state is immutable)

**MVI** 의 구성요소는 `Model`, `View`, `Intent`로, 아래의 흐름대로 동작한다.

![MVI Architecture](https://imgur.com/LlKp9DH.jpg)

**MVVM** 과 비교해보면 흐름도상 양방향 의존성을 가지지않는데, 이러한 특징을 **UDA** 라고 한다.

UDA에 대해서 먼저 살펴보자.

### UDA : UniDirectional Architecture

**UDA** 는 말 그대로 단방향 구조를 뜻한다.

이러한 구조는 javascript 생태계에서 파생된 것인데, 일반적으로 사용자가 예측하는 흐름과 실제 뷰가 지니는 상태가 달라지는 문제를 해결하기 위해 나온 방안이다.

UDA 구조를 사용하면 View에 영향을 주는 State는 한 방향으로만 수정할 수 있고, 앞의 동작이 끝난 후 다음 동작을 수행하는 동기적인 방식으로 실행된다.

이러한 구조를 Android에도 도입하는 것이 **MVI** 아키텍쳐 패턴인 것이다.

기존 **MVC** 아키텍쳐 패턴에서 `Controller`가 `View`에 대한 제어를 하던 것과 다르게 `Intent`라는 요소를 사용해서 처리하게 된다.

**MVC** 아키텍쳐 패턴을 통해 좀 더 자세히 **UDA** 에 대해 이해해보자.

### MVC : Model-View-Controller

과거 사용되었던 **Smalltalk-80** 이라는 언어가 있다.

과거의 **MVC** 는 현재의 **MVC** 와 다르게 **Smalltalk-80** 을 위해 고안된 방식이다.

좀 더 자세한 내용은 아래 레퍼런스를 참고하기 바란다.

> **참고** [Looking at Model-View-Controller in Cocoa](https://www.cocoawithlove.com/blog/mvc-and-cocoa.html)
> **참고** [Interactive Application Architecture Patterns](http://aspiringcraftsman.com/2007/08/25/interactive-application-architecture/)

간단하게 그림으로 Smalltalk-80의 MVC를 표현하면 아래와 같다.

![Smalltalk-80 MVC](https://imgur.com/2iDilf3.jpg)

특징적인 부분은 Model이 애플리케이션의 State와 비즈니스 로직을 관리한다는 것이다.

View는 Model을 화면에 렌더링해주고, Model의 현재 상태를 Update한다.

View와 User 사이에서 어떠한 상호작용이 발생하면 이를 `Controller`가 받아서 수행한다.

모든 흐름이 단방향으로 흐르고 있는데, 이러한 구조를 UDA라고 이해하면 된다.

### MVVM과 MVP의 한계점

기존의 **MVP** 와 **MVVM** 도 충분히 좋은 아키텍쳐 패턴이고 널리 쓰이고 있는데, 개발자가 **UDA** 까지 알아야할까?

**MVVM** 이 **MVP** 의 단점을 극복하기 위해 고안되었듯이, **MVI** 도 **MVVM** 의 단점을 극복하기 위해 고안된 것이다.

**MVP** 와 **MVVM** 이 공통적으로 가지고 있는 단점은 아래와 같다.

1. Presenter, ViewModel은 비즈니스 로직이 언제든지 꼬일 수 있고, 여러가지 State가 공존하기 때문에 View의 State와 Presenter의 State가 서로 다를 수 있다.
2. User와의 상호작용, 백그라운드의 업데이트 등 다중 스레드 상황에서 Model에 대한 업데이트가 동시에 일어나는 경우 State 관리가 쉽지않다.

좀 더 빠른 이해를 위해 두 가지 케이스에 대해 살펴보자.

#### Case 1. Multiple states in business logic

우리가 기존에 사용하던 **MVP**, **MVVM** 에서는 여러 State를 Presenter, ViewModel에서 관리하였다. 

이때 State 1, 2, 3이 주어지고 이를 연속적으로 반영하게 되거나, 반영 중간에 오류가 발생하는 경우 아래 그림처럼 흐름이 그려진다.

![Multiple State Expection](https://imgur.com/oe2gRbX.jpg)

이때 사이드 이펙트가 발생하는 경우, 위의 그림의 기대와는 달리 아래 그림처럼 흘러갈 수도 있다.

![Multiple States Unexpection](https://imgur.com/v0gN8D4.jpg)

만약 1번에서 사이드 이펙트가 발생했다면, 실제 시대와는 달리 X만 보여지게 될 것이다.

#### Case 2. Asyncronous updates in business logic

동일한 상황을 이번엔 비동기 업데이트로 한다고 가정해보자.

기대 결과는 아래 그림과 같다.

![Asynctronous Updates Expection](https://imgur.com/iPvDyhN.jpg)

이때 비동기로 인해 호출된 순서와는 다르게 동작하여 아래 그림과 같이 기대 결과에 맞지않는 결과가 나올 수 있다.

![Asynctronous Updates Unexpection](https://imgur.com/ybsUcyn.jpg)

2번을 호출하는 과정에서 사이드 이펙트가 발생하여 1초만큼 딜레이가 되는 상황이 발생하게 되었을 때,

우리는 기대한 1, 2, 3에 대한 상태를 화면에 반영하는 것을 볼 수 없을 것이다.

위의 예시들과 같은 이유들로 인해, 상태의 변경 혹은 반영에 대해 하나의 상태로 관리하는 UDA 구조가 필요함을 알 수 있다.

UDA에서는 Single State를 통해 상태변경 시 사이드 이펙트가 발생하더라도, 우리가 기대한 화면을 볼 수 있도록 설계할 수 있다.

### Immutable State and UDA

![Immutable State Flow](https://imgur.com/u5Xx82K.jpg)

UDA에서는 상태 변화에 대해 단방향으로 처리하여 상태에 대해 사이드 이펙트가 발생하더라도 우리가 기대한 화면을 보여줄 수 있도록 구현할 수 있다.

UDA는 View와 Model을 직접 연결하는 방식으로 

UDA는 뷰와 모델을 직접 연결하여 위 문제를 해결했다. UDA에서는 모든 상태정보가 모델에서 관리된다. 또한, UDA의 차별화된 특징은 View와 State가 완전히 분리되어 있다.

UDA를 어떤 방식으로 만들더라도 반드시 지켜야 할 것은 단방향 흐름으로 구조화 해야한다는 것이다.

### MVI의 구성 요소

상술했듯, MVI 아키텍쳐 패턴은 `Model`, `View`, `Intent`로 구성되어 있다.

#### 1. Model

View의 상태를 표현하고, View를 올바르게 렌더링하는 데 필요한 모든 정보가 포함되어 있다.

Model은 애플리케이션이 현재 가지고 있는 상태정보를 가지고 있으며,

전달된 intent(=유저의 의도)를 분석해 현재 상태에 맞추어 새로운 불변 객체인 Model을 생성한다.

여기서 상태란 데이터 로딩, 에러, 현재 화면의 포지션, 유저의 의도에 따른 UI 변경 등을 포괄한다.

상술한 설명과 같이, MVI에서의 Model은 다른 아키텍쳐 패턴과는 개념이 다르며, 상태를 포괄하고 있다는 점에 주목해야한다.

즉, MVI의 `Model-View-Intent`는 `Model(State)-View-Intent`로 표현될 수 있다.

아래의 코드가 그 예시이다.

```kotlin
data class State(
    val isLoading: Boolean,
    val error: Throwable,
    val persons: List<Person>
)
```

#### 2. View

View는 사용자의 행동과 시스템 이벤트를 관찰한다. 

View를 통해 트리거된 이벤트에 대한 의도를 설정하며, Model의 상태 변화에 대응한다.

이때 Model로부터 상태를 전달받아 화면에 UI를 렌더링한다.

#### 3. Intent

애플리케이션의 상태 즉, Model의 상태를 변경하는 작업을 표현한다.

유저의 의도(Action or Event)가 무엇인지 정의하고 있다.

결과만 놓고 보자면 Intent를 주입받아 Model을 생성하여 전달하고 이를 View를 통해 표현하게 된다.




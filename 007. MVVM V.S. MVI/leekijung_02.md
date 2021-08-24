[TOC]

# MVVM V.S. MVI - MVI

이번시간에는 MVVM에서 더 나아간 아키텍쳐인 MVI에 대해 소개한다. 

## MVI

MVI는 Model + VIew + Intent(실제로는 비즈니스 로직을 포함)을 사용하는 아키텍쳐이다. 아래의 구조로 표현이 가능하다는 것을 알 수 있다.

![MVI Architecture](https://imgur.com/LlKp9DH.jpg)



그런데, 다른 아키텍쳐들과 비교하면 어느 하나도 양방향 의존성을 갖지 않는 신기한 모습을 보여준다. 이러한 구조가 바로 UDA(Uni-Directional Architecture)방식으로 이루어짐을 의미한다. 그러면, UDA는 어떤것일까?



### UDA(Uni-Directional Architecture)

UDA는 말 그대로 단방향 구조이다. 자바스크립트 생태계에서 파생된 아키텍쳐이다.

특징적인 것은 기존에 MVC 패턴에서 Controller에서 뷰에 대한 제어를 하던것과는 다르게, Intent라는 Reactive요소를 이용한 아키텍쳐라는 것이다.

과연 기존에 이러한 아키텍쳐가 존재했을까?



### 과거의 MVC

우리가 UDA를 이해하려면, 과거에 사용되었던 Smalltalk-80이라는 언어에 대해 이해해야한다. 과거 MVC는 Smalltalk-80를 위해 구현된 방식이었다.

여기서 설명하는 MVC는 현재 우리가 안드로이드에서 사용되고 있는 MVC아키텍쳐와는 다르다.

자세한 내용은 다루지 않을 것이기 때문에 레퍼런스로 언급할  [cocoawithlove](https://www.cocoawithlove.com/blog/mvc-and-cocoa.html)와 [aspiring craftsman](http://aspiringcraftsman.com/2007/08/25/interactive-application-architecture/)이 작성한 MVC에 대한 기원을 참고하시기 바란다.



아래 구성방식은 Smalltalk-80에서 사용된 MVC구조이다. 우리 기존에 이해하던 MVC와는 다른 방식을 보이는 것을 알 수 있다.

![Smalltalk-80 MVC](https://imgur.com/2iDilf3.jpg)



여기서 특징적인 부분은 모델이 중심에 놓여있고, 모델이 어플리케이션의 상태 및 비즈니스 로직을 관리를 한다는 것이 우리가 알고 있던 MVC와 다른 것을 알 수 있다.

뷰는 모델을 화면에 나타내고, 모델의 현재상태를 반영(Update)한다. 뷰와 사용자 사이에서 상호작용이 일어났을 때 컨트롤러에서는 이와 관련된 동작을 수행하고, 한방향의 흐름으로 반복된다.

우리는 이것을 UDA라고 부를 수 있다.



## 그런데 우리가 이것에 대해 왜 관심을 가져야 할까

왜 UDA방식에 대해 관심을 갖고, 도전을 해야할까? 기존 방식보다 과연 유용할까?

기존에 우리가 잘만 사용하던 MVP, MVVM으로도 충분할텐데, 왜 우리가 힘들게 MVI, UDA와 같은 구조에 대해 이해하고 있어야할까?



MVP, MVVM과 같은 구조는 공통적으로 이러한 문제가 있다.

1. 프레젠터, 뷰모델과 같은 존재는 비즈니스 로직이 언제든 뒤엉킬 수 있으며, 여러 상태가 공존하기 때문에 뷰의 상태와 프레젠터(MVP, MVVM 마찬가지)의 상태가 서로 다를 수 있다.
2. 상태 정보, 뷰와 모델이 두 곳에 존재하기 때문에 다중 스레드 및 (사용자 상호작용, 백그라운드 업데이트 등)에 의해 모델에 대한 업데이트가 동시에 일어나는 경우에 상태관리가 쉽지 않다.

좀 더 쉬운 이해를 돕고자 두가지 케이스(여러 상태를 가짐, 비동기적인 업데이트)에 대해 시각적으로 자료를 준비했다.

### Case1 - Multiple states in business logic

우리가 기존에 사용하던 MVP, MVVM에서는 여러 상태를 프레젠터, 뷰-모델에서 관리하였다. 그렇다면 다음과 같은 그림이 될 수 있을것이다.

아래 이미지는 1, 2, 3에 대한 상태를 연속적으로 반영하거나, 중간에 에러가 발생하는 경우 이에 대한 상태를 뷰에 반영하는 로직을 시각적으로 정리한 것이다.

![Multiple State Expection](https://imgur.com/oe2gRbX.jpg)

하지만, 상태가 점점 많아질수록, 우리가 예상치 못한 결과를 마주하게 될 수도 있다. 개발자의 실수로 인해 사이드이펙트가 발생한다면, 실제로 우리가 기대했던 것과는 다른 모습을 보이게된다.

![Multiple States Unexpection](https://imgur.com/v0gN8D4.jpg)

사이드 이펙트가 발생했다면 우리가 기대하는 화면은 1, 2, 3을 반영한 상태의 화면이거나, 정상적인 에러 화면을 띄워야 하지만, 화면을 띄우는 로직에 대해 별도의 상태관리가 되므로, 사이드 이펙트가 1번 상태에 발생하는 경우 실제로 표시되는 상태는 X만 보여지게 될 것이다. 

### Case2 - Asyncronous updates in business logic

두번째 케이스를 보자, 우리가 기대하는 상황은 다음과 같다. 비동기 적인 상태 업데이트를 진행하더라도, 결과는 오른쪽과 같아야 한다.

![Asynctronous Updates Expection](https://imgur.com/iPvDyhN.jpg)

하지만, 여러 상태를 관리하는 케이스의 경우 사이드 이펙트가 발생하는 경우, 호출한 순서와는 다르게 반영 순서가 기대와 다를 수 있다.

![Asynctronous Updates Unexpection](https://imgur.com/ybsUcyn.jpg)

위 이미지와 같이 2번을 호출하는 과정에서 사이드 이펙트가 발생하여 1초만큼 딜레이가 되는 상황이 발생하게 되었을 때, 우리는 기대한 1, 2, 3에 대한 상태를 화면에 반영하는 것을 볼 수 없을 것이다.

이러한 이유로, 상태 반영에 대해 하나의 상태로 관리하는 UDA 구조가 필요하다. UDA에서는 Single State를 통해 상태변경 시 사이드 이펙트가 발생하더라도, 우리가 기대한 화면을 볼 수 있도록 설계할 수 있다.

### Immutable State and UDA

![Immutable State Flow](https://imgur.com/u5Xx82K.jpg)

UDA에서는 상태 변화에 대해 단방향으로 처리하여 상태에 대해 사이드 이펙트가 발생하더라도 우리가 기대한 화면을 보여줄 수 있도록 구현할 수 있다. 그렇다면, UDA에서는 이 문제를 어떤 방식으로 해결했을까?

UDA는 뷰와 모델을 직접 연결하여 위 문제를 해결했다. UDA에서는 모든 상태정보가 모델에서 관리된다. 또한, UDA의 차별화된 특징은 View와 State가 완전히 분리되어 있다.

UDA를 어떤 방식으로 만들더라도 반드시 지켜야 할 것은 단방향 흐름으로 구조화 해야한다는 것이다.



### 안드로이드에서 사용되고 있는 라이브러리

안드로이드에서는 UDA 구조를 갖는 여러 라이브러리가 존재한다. 대표적으로는 페이스북에서 만든 Flux, Flux와 연관 되어 있는 [Redux-Kotlin](https://reduxkotlin.org), [AirBnb사에서 만든 Mavericks(구 MvRx)](https://github.com/airbnb/mavericks), [MVI Mosby](https://github.com/sockeqwe/mosby), [MVI Orbit](https://github.com/orbit-mvi/orbit-mvi), [Roxie](https://github.com/ww-tech/roxie), [Uniflow-kt](https://github.com/uniflow-kt/uniflow-kt/) 등의 라이브러리들이 존재한다.

![MVI Comparison](https://imgur.com/XHZ7BMA.jpg)

이러한 라이브러리들을 소개하고, 라이브러리의 장단점 및 종합하여 어떤 라이브러리가 나은지를 소개한 [미디움 블로그 글](https://appmattus.medium.com/top-android-mvi-libraries-in-2021-de1afe890f27)이 있으니 참고하자,



그래도 대부분의 형태는 현재 안드로이드에서 사용되고 있는 방식인 MVI를 채택하고 있는데, MVI의 동작 방식은 다음과 같이 정리될 수 있다. 

![MVI Flow](https://imgur.com/5jkwWxI.jpg)



**MVI의 동작 흐름**

1. Intent로부터 User의 입력을 가져온다
2. Intent는 Model에서 처리해야하는 동작을 생성한다
3. Model은 Intent에서 동작을 가져온다
4. Model은 Immutable한 새로운 모델을 생성한다.
5. View는 새로운 Model을 가져와 표시한다.

아까 언급되었던 UDA 구조의 특성을 가지는 것을 알 수 있다. 그래서, MVI는 UDA에서 파생된 아키텍쳐라고 소개를한다.



언급되었던 Mosby의 파생된 MVP라이브러리인 Moxy는 UDA의 방식으로 구성돼있으며, 뷰상태가 어떻게 변화하는지 시각적으로 표현하면 다음과 같다. 특징인 부분은 Presenter를 사용함에도 MVI방식을 구현하는데 전혀 문제가 없다는 것이다.

![Mosby Flow](https://imgur.com/bXOAaVt.gif)



그말인 즉, MVI는 필요 시 비즈니스로직에 대한 위임을 ViewModel, Presentter에서 하여 뷰의 상태를 관리를 할 수 있다는 것이다. 안드로이드에서는 이 방식을 더 많이 채택하며, 최근에는 화면에 대한 설정 변경이 생겼을 때, 이를 위해 데이터를 보존하는 AAC ViewModel과 함께도 사용이 되는 추세이다.



### Mosby를 이용한 MVI + Presenter 예제 소개

아까 언급된 Mosby방식의 MVI가 안드로이드에서는 거의 최초로 사용되었다. 그래서 자바 코드로 작성된 것들을 볼 수 있는데, 검색에 대한 로직을 어떻게 표현하는지 보도록하자.








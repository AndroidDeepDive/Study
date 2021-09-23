# MVVM vs MVI Part 4 - MVI for Android

## Android에서의 MVI 사용 방법

### 대표적인 라이브러리들

Android에도 UDA 구조를 갖는 여러 라이브러리가 존재한다.

대표적으로는 Facebook에서 MVC 이슈 해결을 위해 개발한 `Flux` 아키텍쳐 기반의 라이브러리가 있다.

이 Flux를 기반으로 구현체는 `Redux`가 나왔는데, `ReduxKotlin` 라이브러리도 별도로 존재한다.

그 다음으로는 AirBnb에서 만든 `Mavericks`가 있고 `MVI Mosby`, `MVI Orbit` `Roxie`, `Uniflow-kt` 등의 라이브러리가 존재한다.

> **참고**
> [Facebook Flux](https://facebook.github.io/flux/)
> [ReduxKotlin](https://reduxkotlin.org/)
> [Airbnb Mavericks](https://airbnb.io/mavericks/)
> [MVI Mosby](https://github.com/sockeqwe/mosby)
> [MVI Orbit](https://orbit-mvi.org/)
> [WW Tech Roxie](https://github.com/ww-tech/roxie)
> [Uniflow](https://github.com/uniflow-kt/uniflow-kt)

위 라이브러리들의 기능 비교표는 아래 이미지를 참고하자.

![MVI Comparison](https://imgur.com/XHZ7BMA.jpg)

> **출처** [Matthew Dolan's Medium Top Android MVI libraries in 2021](https://appmattus.medium.com/top-android-mvi-libraries-in-2021-de1afe890f27)

#### Moxy Library Flow

Mosby의 파생 라이브러리인 `Moxy`는 UDA 구조로 구현되어있는 MVP 라이브러리이다.

이 Moxy 기반 구조에서 View의 상태가 어떻게 변화하는 지 시각화한 자료가 있어 첨부한다.

![Mosby Flow](https://imgur.com/bXOAaVt.gif)

위의 그림을 보면 Presenter를 사용하고 있음에도, MVI 방식을 구현하는 데 전혀 문제가 없음을 알 수 있다.

이를 통해 MVI 아키텍쳐 패턴은 비즈니스 로직을 ViewModel 혹은 Presenter에 위임하여 View의 상태를 관리할 수 있음을 알 수 있다.

Android 에서는 이러한 방식을 보다 많이 채택하는 추세이며, 최근에는 화면에 대한 설정 변경이 발생하였을 때, 이를 위해 데이터를 보존하는 AAC ViewModel과도 함께 사용되고 있다.


### Android MVI의 동작 흐름

![MVI Flow](https://imgur.com/5jkwWxI.jpg)

대부분의 Android 라이브러리에서는 위의 흐름도에 따라 동작하고 있다.

동작을 순서대로 표기하면 아래와 같다.

1. `Intent`로부터 `User`의 입력을 가져온다
2. `Intent`는 `Model`에서 처리해야하는 동작을 생성한다
3. `Model`은 `Intent`에서 동작을 가져온다
4. `Model`은 `Immutable`한 새로운 모델을 생성한다.
5. `View`는 새로운 `Model`을 가져와 표시한다.

위의 그림과 동작 순서를 살펴보면 UDA 구조를 가지고 있는 것을 확인할 수 있다.

#### Redux 기반 MVI의 동작 흐름

그림 대신 텍스트로 MVI의 흐름을 나타내면 아래와 같이 명세할 수 있다.

`View - Intent - Action - Processor - Result - State - View`

1. `View`에서 사용자의 의도인 `Intent`가 전달된다.
2. `Intent`는 적절한 `Action`으로 변환된다.
3. `Action`은 `Processor`를 통해 `Result`로 주어지며, 이때 `Processor`가 API 통신 및 데이터베이스 쿼리 등의 사이드 이펙트를 수반한 작업도 수행한다.
4. 반환된 `Result`는 `Reducer`를 통해 새로운 `State`를 만들어낸다. 이때 만들어진 `State`는 이전의 `State`와 `Result`를 조합하여 만드는 데, 이를 통해 불변성을 유지하게 된다.
5. 만들어진 `State`가 `View`로 전달되어 사용자에게 렌더링 된다.


## MVI for Android에서의 장점과 단점

### 장점
- View와 Model, View와 ViewModel 사이의 의존성이 없으므로 유닛 테스트가 더 용이하다.
- UDA 구조 특성상 단방향이므로 데이터 흐름을 쉽게 추적하고 예측할 수 있다.
- 하나의 불변 상태를 가지고 있기때문에, 상태 간의 충돌을 회피할 수 있다. (=Thread Safety)
- 상태 자체에 초점을 둔 아키텍쳐 패턴으로 타 패턴에 비해 상대적으로 상태를 관리하기 쉽다.
- 타 패턴에 비해 각 컴포넌트간 커플링이 좀 더 약하다.


### 단점
- 모든 상태에 대해 많은 객체를 만들어야 하므로, 메모리 관리 비용이 많이 든다.
- 라이프사이클을 직접 관리해야 한다.
- 작은 상태 변경일지라도 전부 intent로 정의하기때문에 불편함이 유발될 수 있다.


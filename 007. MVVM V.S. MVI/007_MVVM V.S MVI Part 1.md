# MVVM vs MVI Part 1 - What is MVVM Pattern?

**MVVM** 은 `Model` + `View` + `ViewModel`을 사용하는 아키텍쳐이다.

이러한 구조는 실제 사용자에게 노출되는 화면에서 보이는 영역 즉, **프레젠테이션 레이어** 를 제어하는 것에 그 목적이 있다. 

`View`는 `ViewModel`에 input을 통해 요청을 하고, `ViewModel`은 받은 요청을 핸들링하여 `Model`을 가공한다.

이후 가공한 `Model`을 `View`에 반영하는 것이 **MVVM** 패턴의 기본 구조이다.

이러한 패턴을 사용하기까지 어떤 방식으로 발전해왔는지 살펴보자.

## MVVM의 역사

MVVM 패턴이 대중화되기 이전엔 MVC와 MVP라는 패턴이 존재하였다.

GUI가 대중화 되면서 최초에 `Model`, `View`, `Controller`를 분리하려는 시도가 있었으며 **MVC** 라는 패턴으로 고착화되었다.

![MVP Architecture](https://imgur.com/4DuJsmi.jpg)

이후 **MVC** 의 고질적인 문제인 `View`와 `Model`간의 의존성을 낮추기 위해 `Controller` 대신 `Presenter`라는 개념을 사용하기 시작하였다.

`Presenter`는 `View`와 `Model`을 1:1 관계로 연결해주는 방식으로 동작하여 `View`와 `Model`사이의 의존성을 끊어내는 역할을 하며 이러한 방식 또한 **MVP** 라는 패턴으로 고착화 되었다.

![MVVM Architecture](https://imgur.com/3rbkLy2.jpg)

그러나 **MVP**또한 `View`와 `Presenter`사이의 의존성을 여전히 존재하는 문제점이 있었고, `View`와 `ViewModel`, `View`와 `Model` 사이의 의존성을 끊어내기 위해 커맨드 패턴과 데이터 바인딩을 사용하는 **MVVM** 으로 발전해오게 되었다.

> **참고** [wikipedia#Command pattern](https://en.wikipedia.org/wiki/Command_pattern)

### Presentation Model

위의 **MVP** 와 **MVVM** 의 흐름도를 보면 굉장히 유사해보이는데, **MVVM** 이 **MVP** 의 개선을 거쳐 완성된 패턴이기 때문이다.

![Presentation Model Architecture](https://imgur.com/tiDQQQx.jpg)

이때 쓰인 **PM(Presentation Model)** 이라는 개념은 2004년 마틴 파울러에 의해 발표되었고, 해당 모델은 **MVP** 와 굉장히 유사하다.

> **참고** [Martin Fowler's Presentation Model](https://martinfowler.com/eaaDev/PresentationModel.html)

### MVVM 패턴의 고안

**MVVM** 패턴의 경우 마이크로소프트의 아키텍트인 켄 쿠퍼와 테드 피터스에 의해 발명되었고, 2005년 존 고스만이 자신의 블로그를 통해 발표되었다.

이때 발표된 **MVVM** 패턴은 위에서 언급한 **PM** 패턴의 변형을 통해 만들어졌으며

마틴 파울러가 Presentation Model은 View의 추상화에 목적이 있다고 한 반면, 존 고스만은 **MVVM** 을 WPF의 핵심 기능을 활용하여 UI 제작을 간소화하는 방법이라고 소개한다.

여기서 **MVVM** 은 View의 상태와 동작을 포함하는 추상화를 사용한다는 점에서 Presentation Model과 동일하다.

### MVP vs MVVM

**MVP** 에서 다루는 Presenter와 달리 **MVVM** 의 ViewModel에서는 View에 대한 참조가 필요없다.

View는 ViewModel의 속성에 의해 바인딩되며, ViewModel은 Model에 포함되어있는 데이터를 View에 해당하는 다른 State에 연결해준다.

이러한 연결 방식을 **Data Binding** 이라고 부른다.

**MVVM** 을 구현하기 위해 사용되는 요소들에 대해 간단하게 정리해보면 아래와 같다.

#### 1. View
화면에 표현되는 레이아웃에 대한 관여한다. 

기본적으로는 비즈니스 로직을 배제하지만, UI와 관련된 로직을 수행할수도 있다. 

안드로이드에서는 XML을 통한 레이아웃 작성이 기본적으로 해당되며, 

이를 제어하는 라이프 사이클 컴포넌트인 Activity, Fragment에서 뷰를 인플레이션을 통해 바인딩한다.

#### 2. ViewModel

View에 연결 할 데이터와 명령으로 구성되어있으며 변경 알림을 통해서 View에게 상태 변화를 전달한다. 

전달받은 상태변화를 화면에 반영할지는 View 가 결정한다. 

안드로이드에서는 뒤에서 설명할 AAC ViewModel을 통해 생명주기에 맞게 데이터를 관리하는 역할을 수행한다.

MVVM에서 추구하는 ViewModel은 MVP와는 다르게 View와 ViewModel이 1:n의 관계를 가질 수 있고, 

이에 따라 View에는 여러 비즈니스 로직을 여러 ViewModel로 나눌 수 있다.

하지만, 안드로이드에서는 AAC ViewModel을 기반으로한 MVVM 아키텍쳐 구현을 하는 경우 1:1을 사용하라는 가이드라인이 명시되어 있다. 

이는 하나의 ViewModel이 내부적으로 하나의 라이프사이클 컴포넌트에 대한 생명주기를 제어하기 때문이다.

#### 3. Model

좁은 의미로는 Presentationx 레이어에서 상태를 보존하는 레이어로 볼 수 있지만, 

데이터를 관리하는 DB, 데이터 소스, Repository, UseCase 등 데이터를 가공하는 영역까지를 의미한다. 

MVVM에서는 View는 의존 관계가 없기 때문에 Model을 알지못하고, 직접적인 접근을 하지 않는다.

ViewModel에서 처리 시 데이터를 구독할 수 있게 처리하고, Model은 이에대한 이벤트를 보내 ViewModel에서 데이터를 관리하게 된다.

#### 4. Binder

ViewModel의 속성 값이 변경되면 새 값이 데이터 바인딩을 통해 자동으로 View로 전파된다. 

예를 들어 사용자가 View안에 있는 버튼을 클릭하게된다면, 해당 요청을 수행하기 위해 ViewModel에 있는 명령이 실행이된다. 

View는 절대로 상태에 대한 변화에 직접적으로 제어하지 않으며, ViewModel이 명령에 대한 값에 대한 변화를 일으키게 된다.

MVVM에서는 Controller나 Presenter의 역할을 대신하여 Binder가 View의 연결된 속성과 View 사이의 통신을 자동화 한다. 

이를 통해 알 수 있는 차이점은 기존 MVP의 경우 Presenter가 View에 직접 접근하여 데이터에 대한 변화를 일으켰지만, 

ViewModel은 Binder를 통해 속성에 직접 연결된 채로 업데이트를 주고받을 수 있다.

효율적으로 작동하기 위해서는 바인딩을 하기위한 Boilerplate Code의 자동 생성이 필요하게 된다.

MVVM이 적용된 WPF를 보면, XML 마크업 언어를 통해 데이터 바인딩 기술을 통해 UI에 대한 상태와 ViewModel의 상태를 동기화 한다.

> **참고** [이종철의 블로그#WPF 데이터바인딩 이란?](https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=leejongcheol2018&logNo=221452069250)

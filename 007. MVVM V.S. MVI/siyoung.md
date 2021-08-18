## MVVM

MVVM 패턴은 마틴 파울러의 Presentation 모델 패턴에서 파생된 디자인 패턴으로  MVVM 패턴의 목표는 비즈니스 로직과 프레젠테이션 로직을 UI로부터 분리하는 것이다. 비즈니스 로직과 프레젠테이션 로직을 UI로부터 분리하게 되면, 테스트, 유지 보수, 재사용이 쉬워진다.
MVVM 패턴은 WPF(Windows Presentation Foundation)와 Silverlight 같은 데이터 바인딩을 활용한 환경에서 데이터와 프레젠테이션 로직을 분리하기 위해서 만들어진 디자인 패턴이다.
MVVM 패턴은 Model, View, ViewModel 로 구성되어 있다.



### Model

어플리케이션 도메인 모델을 말한다. 데이터 구조 외에 비즈니스 로직을 표현하는 수단도 모델에 포함되어 있다.

### View

사용자가 화면에서 보는 구조와 배치, 형태를 배치하는 것을 말한다. 모델을 시각적으로 표현하고, 사용자와 뷰와 상호작용(클릭, 키보드, 동작 등)을 수신하여 뷰와 뷰 모델의 연결을 정의하고 있는 (속성, 이벤트 콜백 함수 등의) 데이터 바인딩(data binding, 데이터 연결)을 통하여 ViewModel로 전달한다. 비즈니스 모델과는 로직이 분리되어 있어야 한다.

### ViewModel

ViewModel 은 공용 속성과 공용 명령을 노출하는 View에 대한 추상화(abstraction)이다. MVC 패턴의 컨트롤러나, MVP 패턴의 프리젠터(presenter, 발표자) 를 대신하여, MVVM은 바인더(binder, 연결자)를 가지고 있는데, 이는 ViewModel에 있는 View에 연결된 속성과 View사이의 통신을 자동화 한다. ViewModel은 Model에 있는 데이터의 상태라고 설명했었다. ViewModel과 MVP 패턴에 있는 Presenter 사이의 주요한 차이점은 Presenter는 View에 대한 참조를 가지고 있는 반면, ViewModel은 그렇지 않다는 것이다. 그 대신, View는 ViewModel의 속성에 직접 ‘연결된(binds)’ 채로 업데이트를 주고 받는다.



### 장점

* ViewModel이 Model과 View 사이의 어댑터로서 변경이 생겼을 때 변경을 최소화할 수 있다

* Model과 View , ViewModel과 View 에 의존성이 없기 때문에 유닛 테스트 코드 작성에 유리하다.

### 단점

* 거대하고 복잡한 앱을 위해서 고안된 디자인 패턴인 만큼, 소형 앱에서 사용하게 되면 오버헤드가 커진다



## MVI

MVP 또는 MVVM 과 같이 일반적으로 알려진 다른 패턴과 유사 하지만 Intent(의도) 와 State(상태) 라는 두 가지 새로운 개념이 도입되었다. Intent는 특정 작업을 수행하기 위해 View가 ViewModel에 보내는 이벤트입니다. 사용자 또는 앱의 다른 부분에 의해 트리거 될 수 있습니다. 그 결과, 새로운 상태가 ViewModel에 설정되어 사용자 인터페이스를 업데이트한다. MVI 아키텍처에서 View는 상태를 수신한다. 상태가 변경 될 때마다 View에 알림을 전송한다.



### Model

View 의 상태를 표현한다. View를 올바르게 렌더링하는 데 필요한 모든 정보가 포함되어 있습니다.

### View

View 레이어는 사용자의 행동과 시스템 이벤트를 관찰한다. 결과적으로 트리거된 이벤트에 대한 의도를 설정한다. 또한, 모델의 상태 변화를 듣고 반응한다.

### Intent

모델의 상태를 변경하는 향후 작업의 표현이다.



### 장점

* 상태는 불변성을 가지기 때문에 개발자의 실수나 부수 효과가 발생하지 않는다

* 단방향이므로 데이터 흐름을 쉽게 추적하고 예측할 수 있다.

### 단점

* 모든 상태에 대해 많은 객체를 만들어야 한다. 이로 인해 앱 메모리 관리 비용이 너무 많이 든다.











[https://ko.wikipedia.org/wiki/%EB%94%94%EC%9E%90%EC%9D%B8_%ED%8C%A8%ED%84%B4#%EC%BB%B4%ED%93%A8%ED%84%B0_%EA%B3%BC%ED%95%99%EC%97%90%EC%84%9C%EC%9D%98_%EB%94%94%EC%9E%90%EC%9D%B8_%ED%8C%A8%ED%84%B4](https://ko.wikipedia.org/wiki/%EB%94%94%EC%9E%90%EC%9D%B8_%ED%8C%A8%ED%84%B4#%EC%BB%B4%ED%93%A8%ED%84%B0_%EA%B3%BC%ED%95%99%EC%97%90%EC%84%9C%EC%9D%98_%EB%94%94%EC%9E%90%EC%9D%B8_%ED%8C%A8%ED%84%B4)

[https://gmlwjd9405.github.io/2018/07/06/design-pattern.html](https://gmlwjd9405.github.io/2018/07/06/design-pattern.html)


[https://ko.wikipedia.org/wiki/%EB%AA%A8%EB%8D%B8-%EB%B7%B0-%EB%B7%B0%EB%AA%A8%EB%8D%B8](https://ko.wikipedia.org/wiki/%EB%AA%A8%EB%8D%B8-%EB%B7%B0-%EB%B7%B0%EB%AA%A8%EB%8D%B8)

[https://velog.io/@k7120792/Model-View-ViewModel-Pattern](https://velog.io/@k7120792/Model-View-ViewModel-Pattern)

[https://amsterdamstandard.com/en/post/modern-android-architecture-with-mvi-design-pattern](https://amsterdamstandard.com/en/post/modern-android-architecture-with-mvi-design-pattern)

[https://dhha22.github.io/android/2018/02/07/Model-View-ViewModel.html](https://dhha22.github.io/android/2018/02/07/Model-View-ViewModel.html)

[https://ichi.pro/ko/android-mvi-ban-eung-hyeong-akitegcheo-paeteon-128530942790279](https://ichi.pro/ko/android-mvi-ban-eung-hyeong-akitegcheo-paeteon-128530942790279)
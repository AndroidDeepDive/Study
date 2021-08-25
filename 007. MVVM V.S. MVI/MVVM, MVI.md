# MVVM 소개

![MVVMPattern](MVVMPattern.png?lastModify=1629882713)

**모델-뷰-뷰 모델**(model-view-viewmodel, MVVM)은 소프트웨어 [아키텍처 패턴](https://ko.wikipedia.org/wiki/아키텍처_패턴) 중 하나로 GUI를 갖는 소프트웨어 개발에서 [비즈니스 로직](https://ko.wikipedia.org/wiki/비즈니스_로직) 또는 [백-엔드 로직](https://ko.wikipedia.org/wiki/프런트엔드와_백엔드)로부터 분리시켜서 뷰가 격리시켜 다른 코드에 종속되지 않도록 한다. MVVM에서 VM은 일종의 컨버터인데, 이는 VM이 모델에 있는 [데이터 객체](https://ko.wikipedia.org/wiki/데이터_객체)를 노출(변환)하는 책임을 지기 때문에 객체를 관리하고 표현하기가 쉬워진다는 것을 의미한다. VM은 뷰들의 디스플레이 로직을 제외한 대부분의 것들을 처리한다. 

## MVVM의 유래

MVVM은 [마틴 파울러](https://ko.wikipedia.org/wiki/마틴_파울러)(Martin Fowler)의 '프레젠테이션 모델 디자인 패턴' 의 변형이다. 이는 GUI의 [이벤트-기반 프로그래밍](https://ko.wikipedia.org/wiki/사건_기반_프로그래밍)을 단순화하기 위해 [마이크로소프트](https://ko.wikipedia.org/wiki/마이크로소프트)의 아키텍트인 켄 쿠퍼(Ken Cooper)와 테드 피터스(Ted Peters)에 의해 발명되었다. 이 패턴은 (마이크로소프트의 [닷넷](https://ko.wikipedia.org/wiki/닷넷) 그래픽 시스템인) [윈도우 프레젠테이션 파운데이션 (WPF)](https://ko.wikipedia.org/wiki/윈도우_프레젠테이션_파운데이션) 및 (WPF의 인터넷 응용 프로그램 파생품인) [실버라이트](https://ko.wikipedia.org/wiki/실버라이트)에 통합되었다. 마이크로소프트의 WPF 와 실버라이트 아키텍트인, 존 구스먼(John Gossman)은 2005년 [자신의 블로그에 MVVM을 발표](https://blogs.msdn.microsoft.com/johngossman/2005/10/08/introduction-to-modelviewviewmodel-pattern-for-building-wpf-apps/)하였다.

```
*실버라이트란?
마이크로소프트 실버라이트(Microsoft Silverlight)는 애니메이션, 벡터 그래픽스, 오디오-비디오 재생을 비롯한 리치 인터넷 애플리케이션에 대한 지원을 제공하는 웹 브라우저 플러그인이다. 실버라이트는 어도비 플래시, 어도비 플렉스, 어도비 쇼크웨이브, 자바FX, 애플 퀵타임과 같은 제품과 경쟁하고 있다.
```

## MVVM의 특징

- VM은 View에 의존적이지 않다. View가 변경되도 VM은 변경할 필요가 없다.
- VM과 View는 1:N의 관계를 가질 수 있다. -> 구조 설계의 어려움
- 데이터 바인딩 기법이 필수적이다.

```
데이터 바인딩(data binding)은 데이터를 제공하는 측과 소비하는 측의 데이터를 하나로 결합시켜 동기화하는 기법이다(Single source of truth). 구글에서는 안드로이드를 위한 DataBinding 라이브러리를 제공하고 있으며, 이를 통해 자바/코틀린 언어 레벨의 데이터를 XML 언어 레벨에서 바인딩 할 수 있게 한다.
```

- VM으로 모든 코드가 집중되기 쉬움.

# MVI 아키텍처란?

MVI는 Model - View - Intent의 약어로, 이 패턴은 단방향 흐름을 기반으로 동작한다.

MVI 에서 각각의 컴포넌트가 하는 역할

Model : 다른 패턴들과는 달리 MVI에서 **Model은 UI의 상태를 표현**한다. 예) 데이터 로딩, 에러, 현재 화면의 포지션, 사용자 행동에 따른 UI변경 등. 각 상태는 Model에 상태로 저장된다.

View : MVI에서 View는 Activity 또는 Fragment에 구현될 수 있는 인터페이스다. 이는 다른 모델 상태를 수용하고 UI에 나타낼 수 있는 컨테이너를 갖는 것을 의미한다. Observable한 Intent를 사용하여 사용자의 행동에 반응한다.

Intent : 안드로이드의 Intent가 아니다. 사용자 행동(Action or Event)에 따른 결과를 Intent로 전달 받게 된다. 결과적으로 Intent를 입력받아 Model을 전달하고, View를 통해 나타나게 된다.

## MVI의 워크플로

1. 사용자 액션(Intent) 발생
2. Intent는 모델에 입력할 상태 값
3. Model은 상태를 저장하고 요청된 상태를 View로 전달한다
4. View는 Model로부터 받은 상태를 사용자에게 표현한다.

데이터는 항상 사용자로부터 흐르고 Intent를 통해 사용자와 함께 끝난다. 이를 단방향 아키텍처라고 한다. 사용자가 한 번 더 작업을 수행하면 동일한 주기가 반복되어 순환된다.

![](https://s3.ap-south-1.amazonaws.com/mindorks-server-uploads/mvi_cyclic-49d9f8c2d3fe26b7.png)



## MVI의 장단점

### 장점

- 상태를 관리하기 쉽다. 오로지 상태에 초점을 둔 아케텍처.
- 데이터의 흐름이 단방향이기 때문에 예측하기 쉽다.
- 상태 객체들이 불변이므로 스레드 안정성을 보장한다.
- 에러 발생시 객체 상태를 알기에 디버깅이 쉽다
- 각 컴포넌트들이 자체적으로 책임을 갖기 때문에 좀 더 디커플링 된다.
- 각 상태에 따른 비즈니스 로직을 매핑해두어 앱 테스팅이 쉽다.

### 단점

- 사용자 행동에 대해 상태를 관리해야함에 따라 보일러플레이트 코드가 많이 생긴다.
- 모든 상태에 대한 객체들을 많이 생성해야하기 때문에 앱 메모리 관리 측면에서 비용이 클 수 있다.
- 사이드 이펙트를 다루는 것이 쉽지 않음.


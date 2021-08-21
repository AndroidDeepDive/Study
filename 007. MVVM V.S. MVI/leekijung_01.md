# MVVM V.S. MVI - 1

다들 한번쯤은 안드로이드에서 아키텍쳐라는 것을 채택할 때 들어봤을 법한 MVVM, MVI에 대해 소개한다. 많은 안드로이드 개발자들이 아키텍쳐에 대해 논하지만, 실제로 이 아키텍쳐가 어디서 파생되었는지, 어떻게 사용하는 것이 올바른 방법인지에 대해 정확히 모르는 경우가 많다. 그래서, 하나씩 소개해보고, 비교를 통해 아키텍쳐의 특징에 대해 소개하려고한다.



## MVVM

MVVM은 Model + VIew + ViewModel을 사용하는 아키텍쳐이다. 이는 구조상 프레젠테이션 레이어(실제 화면에서 보이는 영역)를 제어하는 목적이 크다.

![MVVM Architecture](https://imgur.com/3rbkLy2.jpg)

다음과 같이 View는 ViewModel에 Input을 통해 요청을하고, 요청을 받은 ViewModel은 요청을 핸들링하여 Model을 가공한다. 가공된 모델을 통해 View에 반영이되는 방식으로 구성이 돼있다.

그렇다면, 이 방식은 어디서 처음 고안이 되었을까?

### Model-View-ViewModel이 나오기까지의 과정

우리들이 MVVM이라는 아키텍쳐를 접하기 까지 대중적으로 사용해왔던 MVP(Model-View-Presenter)에 대해 먼저 이해를 해보자. MVP는 수십년간 사용된 MVC(Model-View-Controller) 아키텍쳐 패턴의 변형판이다. 

![MVP Architecture](https://imgur.com/4DuJsmi.jpg)



지금 보면, MVP는 MVVM과 구조가 비슷하게 보인다. 그 이유는 당연하게도 MVVM는 MVP의 개선된 방안이기 때문이다. 마틴 파울러(Martin Fowler)는 2004년에 [PM(프레젠테이션 모델)](https://martinfowler.com/eaaDev/PresentationModel.html)에 대해 발표했다. PM모델은 현재 우리가 알고있는 MVP과 유사하다.

![Presentation Model Architecture](https://imgur.com/tiDQQQx.jpg)

PM패턴의 특징적인 부분은 프레젠테이션 모델이라고 하는 뷰의 추상화가 생성되는 것이다. 그 다음에 뷰는 프레젠테이션 모델에 대해 렌더링을 한다.

마틴 파울러는 프레젠테이션 모델이 해당뷰를 업데이트하여 서로 동기화를 유지한다는 것에 대해 설명했다. 동기화에 대한 로직은 Presentation 클래스에 구성이 되어있다.

### PM => MVVM

MVVM은 마이크로소프트의 아키텍트인 켄 쿠퍼(Ken Cooper)와 테드 피터스(Ted Peters)에 의해 발명되었고, 존 고스만(John Gossman)은 2005년에 자신의 블로그에서 [MVVM(Model-View-ViewModel) 패턴](http://blogs.msdn.com/johngossman/archive/2005/10/08/478683.aspx)을 발표했다. MVVM이 만들어진 근원지는 마틴 파울러(Martin Fowler)의 '프레젠테이션 모델 디자인 패턴' 의 변형이다.

마틴 파울러가 프레젠테이션 모델을 뷰에 대한 추상화를 하는 목적에 있다라고 소개한 반면에, 고스만은 MVVM을 WPF의 핵심 기능을 활용하여 UI 제작을 간소화하는 표준화된 방법이라고 소개했다.

MVVM은 뷰의 상태와 동작을 포함하는 뷰의 추상화를 사용한다는 점에서 Fowler의 프레젠테이션 모델과 동일하다. 그래서, MS문서에서는 MVVM을 WPF와 실버라이트 플랫폼을 위해 맞춤 제작된 PM패턴의 개선판이라고 설명한다.

> MVVM 패턴은 WPF(윈도우 프레젠테이션 파운데이션), 그리고 그 파생품인 실버라이트에 통합되었다.

### MVP와 MVVM의 차이점

MVP에서 이야기하는 프레젠터(Presenter)와는 다르게, 뷰-모델(ViewModel)에서는 뷰(View)에 대한 참조가 필요없다. 뷰는 뷰-모델의 속성에 바인드되며, 뷰-모델은 모델 개체에 포함되어 있는 데이터와 뷰에  해당하는 다른 상태와 연결한다. 이것을 연결해주는 기술을 **데이터 바인딩(Data Binding)**이라고 소개한다.

###  Binder

뷰-모델의 속성 값이 변경되면 새 값이 데이터 바인딩을 통해 자동으로 뷰로 전파된다. 예시를 들자면, 사용자가 뷰안에 있는 버튼을 클릭하게된다면, 해당 요청을 수행하기 위해 뷰-모델에 있는 명령이 실행이된다. 뷰는 절대로 상태에 대한 변화에 직접적으로 제어하지 않으며, 뷰-모델이 명령에 대한 값에 대한 변화를 일으키게 된다.

MVVM에서는 컨트롤러(Controller)나 프레젠터(Presenter)의 역할을 대신하여 연결자(Binder)가 뷰의 연결된 속성과 뷰 사이의 통신을 자동화 한다. 이를 통해 알 수 있는 차이점은 기존 MVP의 경우 프레젠터가 뷰에 직접 접근하여 데이터에 대한 변화를 일으켰지만, 뷰-모델은 바인더를 통해 속성에 직접 연결된 채로 업데이트를 주고받을 수 있다.

효율적으로 작동하기 위해서는 바인딩을 하기위한 상용구 코드(Boilerplate Code)의 자동 생성이 필요하게 된다.

MVVM이 적용된 WPF를 보면, XML 마크업 언어를 통해 데이터 바인딩 기술을 통해 UI에 대한 상태와 뷰-모델의 상태를 동기화 한다.

개인적으로 WPF에서 사용하는 데이터 바인딩 기술에 대해 잘 소개한 [블로그 글](https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=leejongcheol2018&logNo=221452069250)을 레퍼런스로 남겨본다.



### Android에서 사용하는 Data Binding 기술

안드로이드에서는 Binder기술을 직접 구현하여 `DataBinding` 이라는 기술을 제공한다.






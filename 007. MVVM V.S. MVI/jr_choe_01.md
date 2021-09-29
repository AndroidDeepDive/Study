# MVVM vs MVI

### Separation of concerns
디자인 패턴에 정의된 각 구성 요소를 정의하면서 관심사를 분리할 수 있다.

### Modulerization
역할이 분리된 각 구성 요소는 모듈화될 수 있어 쉽게 변경 가능하다.

### Testability
각 구성 요소가 고유한 작업을 수행하고 결합 구조가 잘 정의되어 있기 때문에 이러한 모든 패턴을 통해 각 구성 요소를 개별적으로 테스트할 수 있다.

## MVVM
![MVVM](https://cdn.journaldev.com/wp-content/uploads/2018/04/android-mvvm-pattern.png?w=144)

### Model
* Model은 앱에 필요한 데이터를 캡슐화하는 구성 요소이다.  
* Model은 일반적으로 데이터 액세스 및 캐시를 가지고 있고 Repository Pattern으로 구현된다.

### View
* View는 사용자가 화면에서 보는 것의 구조, 레이아웃 및 모양을 정의하는 역할을 한다.  
* View가 정의되는 XML 뿐만 아니라 안드로이드에서 Activity, Fragment 등이 포함될 수 있다. 또한 코드 뒤에 애니메이션과 같이 XML에서 표현하기 어려운 시각적 동작을 구현하는 UI가 포함된다.

### ViewModel
* ViewModel은 View가 필요한 데이터를 가져올 수 있도록 하는 비지니스 로직을 담당한다.  
* ViewModel은 View가 데이터를 감지할 수 있는 속성 및 명령을 구현하고 변경 알림 이벤트를 통해 상태 변경 사항을 뷰에 알립니다.  
* ViewModel은 View의 상태를 알지 못하고 단지 데이터 변경에 대한 이벤트를 발행할 뿐이다. 이러한 특성 때문에 View와 ViewModel은 1:N 관계에 있으며 안드로이드 프레임워크에 대한 의존성이 없기 때문에 테스트가 용이하다.

#### ObservableField vs LiveData
> `ObservableField`는 `android.buildFeatures` 패키지에 있다.  
> `LiveData`는 `androidx.lifecycle` 패키지에 있다.

* LiveData를 observe() 하기위해서는 인자로 LifecycleOwner가 필요하다.
즉, LiveData(MutableLiveData)는 뷰(Activity, Fragment) 라이프사이클을 인지하고 상태를 관리해준다.
* 또한 상태를 자동으로 관리해주기 때문에 옵저버 해제를 ObservableField와 같이 별도로 해제(removeOnPropertyChangedCallback)해주지 않아도 된다.
* LiveData의 이점에 대한 공식문서는 [여기](https://developer.android.com/topic/libraries/architecture/livedata#the_advantages_of_using_livedata)에서 확인할 수 있다.

## 장단점
* 장점
  * View에서 단지 ViewModel을 관찰(바인딩) 하고 있기 때문에 
  * ViewModel의 로직을 수정하지 않더라도 View를 대체(변경)하기 쉽다.
  * View와 의존성이 없기 때문에 유닛 테스트에 집중할 수 있다.

* 단점
  * DataBinding을 사용할 경우 View가 변수와 표현식 모두에 바인딩 될 수 있으므로 시간이 지남에 따라 관계없는 비즈니스 로직이 늘어나고 이를 보완하기 위해 XML에 코드를 추가하게 된다. 이때 코드가 증가된다면 유지보수 단계에서 어려움을 겪을 수 있다.

## MVI
![MVI](https://miro.medium.com/max/1096/1*yqiynGx9AADPPT52b37idQ.png)

MVI는 Intent가 View로부터 사용자의 액션을 관찰하고, Model이 Intent를 관찰한다. 그리고 View가 Model을 관찰하는 특성을 가지고 있다.

### Model
* 앱이 현재 갖고 있는 유일한 상태를 나타낸다. intent()의 결과로 전달된 Intent 즉 유저의 의도를 분석하고 지금 앱의 상태에 맞춰 새로운 불변 객체로 Model을 생성한다.

### View
* model() 함수의 결과물인 Model 즉, 앱의 상태를 전달받아 화면에 UI를 렌더링한다.

### Intent
* 앱의 상태를 변화시키려는 의도를 의미한다. 위 다이어그램에 대입해 보면 user()의 결과 즉, 유져가 하고 싶어 하는 무언가를 나타낸다고 볼 수 있다.

## 장단점
* 장점
  * 단방향 데이터 흐름을 가지고 있기 때문에 관리하기 편하다.
  *  하나의 불변 상태를 가지고 있기 때문에 상태 간의 충돌을 피할 수 있다.

* 단점
  *  MVP와 마찬가지로 Lifecycle을 직접 관리를 해야한다.
  *  작은 UI변경도 intent를 통한 사이클이 필요하기 때문에 때에 따라서 불편한 경우도 있다.

## References
*  _https://docs.microsoft.com/en-us/xamarin/xamarin-forms/enterprise-application-patterns/mvvm_
*  _https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started_
*  _https://www.ericthecoder.com/2020/07/20/battle-of-the-android-architectures-mvp-vs-mvvm-vs-mvi/_
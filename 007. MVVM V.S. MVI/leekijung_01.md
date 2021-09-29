[TOC]



# MVVM V.S. MVI - MVVM

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

그러면, MVVM을 구현하기 위해 View, ViewModel, Model에 대한 정의를 간력히 정리해보도록하자.

### View

화면에 표현되는 레이아웃에 대한 관여한다. 기본적으로는 비즈니스 로직을 배제하지만, UI와 관련된 로직을 수행할수도 있다. 안드로이드에서는 XML을 통한 레이아웃 작성이 기본적으로 해당되며, 이를 제어하는 라이프 사이클 컴포넌트인 Activity, Fragment에서 뷰를 인플레이션을 통해 바인딩한다.

### ViewModel

View에 연결 할 데이터와 명령으로 구성되어있으며 변경 알림을 통해서 View에게 상태 변화를 전달한다. 전달받은 상태변화를 화면에 반영할지는 View 가 결정한다. 안드로이드에서는 뒤에서 설명할 AAC ViewModel을 통해 생명주기에 맞게 데이터를 관리하는 역할을 수행한다.

MVVM에서 추구하는 ViewModel은 MVP와는 다르게 뷰와 뷰-모델이 1:n의 관계를 가질 수 있고, 이에 따라 뷰에는 여러 비즈니스 로직을 여러 뷰-모델로 나눌 수 있다.

하지만, 안드로이드에서는 AAC ViewModel을 기반으로한 MVVM 아키텍쳐 구현을 하는 경우 1:1을 사용하라는 가이드라인이 명시되어 있다. 이는 하나의 뷰모델이 내부적으로 하나의 라이프 사이클 컴포넌트에 대한 생명주기를 제어하기 때문이다.

### Model

좁은 의미로는 프레젠테이션 레이어에서 상태를 보존하는 레이어로 볼 수 있지만, 데이터를 관리하는 DB, 데이터 소스, 레파지토리, 유즈케이스 등 데이터를 가공하는 영역까지를 의미한다. MVVM에서는 View는 Model을 알지못하고, 직접적인 접근을하지 않는다.

뷰-모델에서 처리 시 데이터를 구독할 수 있게 처리하고, 모델은 이에대한 이벤트를 보내 뷰-모델에서 데이터를 관리하게 된다.

###  Binder

뷰-모델의 속성 값이 변경되면 새 값이 데이터 바인딩을 통해 자동으로 뷰로 전파된다. 예시를 들자면, 사용자가 뷰안에 있는 버튼을 클릭하게된다면, 해당 요청을 수행하기 위해 뷰-모델에 있는 명령이 실행이된다. 뷰는 절대로 상태에 대한 변화에 직접적으로 제어하지 않으며, 뷰-모델이 명령에 대한 값에 대한 변화를 일으키게 된다.

MVVM에서는 컨트롤러(Controller)나 프레젠터(Presenter)의 역할을 대신하여 연결자(Binder)가 뷰의 연결된 속성과 뷰 사이의 통신을 자동화 한다. 이를 통해 알 수 있는 차이점은 기존 MVP의 경우 프레젠터가 뷰에 직접 접근하여 데이터에 대한 변화를 일으켰지만, 뷰-모델은 바인더를 통해 속성에 직접 연결된 채로 업데이트를 주고받을 수 있다.

효율적으로 작동하기 위해서는 바인딩을 하기위한 상용구 코드(Boilerplate Code)의 자동 생성이 필요하게 된다.

MVVM이 적용된 WPF를 보면, XML 마크업 언어를 통해 데이터 바인딩 기술을 통해 UI에 대한 상태와 뷰-모델의 상태를 동기화 한다.

개인적으로 WPF에서 사용하는 데이터 바인딩 기술에 대해 잘 소개한 [블로그 글](https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=leejongcheol2018&logNo=221452069250)을 레퍼런스로 남겨본다.

## Android에서의 MVVM 구현

그러면 본격적으로 안드로이드에서 MVVM 아키텍쳐를 구현하기 위해 어떤 기술을 사용하는지 알아보자.

안드로이드에서는 아키텍쳐 소개 시 MVVM 아키텍쳐에 대해 구체적으로 명시하지 않는다.

그 이유는, 안드로이드 생태계에서 MVVM을 구현하기에 적합하지 않은 이유가 있기 떄문이다. 기존에 MVVM 아키텍쳐의 뷰-모델에서는 뷰와 모델 사이에서 데이터를 관리하고, 바인딩 해주는 요소로써 사용되었지만, 해당 패턴을 사용하기엔, 안드로이드에서의 생명주기는 복잡하기 떄문이다. 그래서, 구글에서는 Android-Architecture-Component로 ViewModel(MVVM의 ViewModel과는 다르다)을 제공한다.

안드로이드 공식문서 어디에도 MVVM과 관련된 이야기를 꺼내진 않는다. 다만 우리는 편의상 MVVM으로 부르도록 하자.

이러한 이유로 구글에서 소개하는 아키텍쳐 방법론이 있는데, 아래 이미지로 표현이 가능하다.

![Android Architecture Over View](https://imgur.com/gIkXsiL.jpg)

### AAC ViewModel

안드로이드에서 도구로써 제공하는 뷰-모델은 구글 가이드라인 문서에서는 다음과 같이 소개한다.

> The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way. The ViewModel class allows data to survive configuration changes such as screen rotations.

뷰-모델 클래스는 생명주기를 인지하고 있는 형태로 UI와 연관되어 있는 데이터를 저장하고 관리하기 위해 디자인 되었다.화면 회전같은 설정 변경 시 데이터를 보존하기 위한 역할이다.

따라서, 우리가 일반적으로 알고있는 MVVM의 뷰-모델과는 다른 목적으로 만들어진 것이다.

그렇다고 해서, AAC 뷰-모델을 MVVM 아키텍쳐 구성 시 뷰-모델로 사용하지 못하는 것은 아니다. AAC 뷰-모델이 MVVM의 뷰-모델 역할인 뷰와 모델 사이에서 데이터를 관리하고, 바인딩 해줄 수 있는 것이다. 그리고, 그것을 원할하게 하기 위해서 구글에서는 안드로이드에 적합한 `DataBinding`이라는 기술을 제공한다.

AAC 뷰-모델의 생성방법은 `ViewModelProviders` 를 사용하여 ViewModel을 팩토리패턴을 통해 만들어준다.

과거에 사용하던 방식인 `ViewModelProviders` 는 현재는 **Deprecated**되었다.

```kotlin
ViewModelProviders.of(this).get(AnyViewModel::class.java)
```



#### ViewModel - LifeCycle Extensions

첫번째로 뷰-모델을 생성하는 방법은 가장 쉬운 방법이다.

AndroidX에서 lifecycle-extensions 라이브러리의 의존성을 추가하여 사용한다.

```groovy
dependencies {
  // ...
  implementation "androidx.lifecycle:lifecycle-extensions:${latest_version}"
}
```

현재는 `ViewModelProvider` 를 통해 ViewModel생성을 지원한다.

파라미터가 없는 경우에는 다음과 같이 라이프사이클을 지정하기 위해 다음과 같이 this로 컴포넌트를 생성자로 넣어주고, 이에 따라 `get() `  함수를 통해 뷰모델 인스턴스를 가져온다.

```kotlin
ViewModelProvider(this).get(AnyViewModel::class.java)
```

여기서는 기본적으로 들어가는 this는 `ViewModelStoreOwner`는 AndroidX의 `ComponentActivity`, `Fragment` 에서 구현한 인터페이스로 구성이 되어 있으므로, 사용할 Activity와 Fragment를 넣어주면 된다.

```kotlin 
public interface ViewModelStoreOwner {
  /**
     * Returns owned {@link ViewModelStore}
     *
     * @return a {@code ViewModelStore}
     */
  @NonNull
  ViewModelStore getViewModelStore();
}
```

기본생성자는 다음과 같다.

```kotlin 
/**
  * Creates {@code ViewModelProvider}. This will create {@code ViewModels}
  * and retain them in a store of the given {@code ViewModelStoreOwner}.
  * <p>
  * This method will use the
  * {@link HasDefaultViewModelProviderFactory#getDefaultViewModelProviderFactory() default factory}
  * if the owner implements {@link HasDefaultViewModelProviderFactory}. Otherwise, a
  * {@link NewInstanceFactory} will be used.
  */
public ViewModelProvider(@NonNull ViewModelStoreOwner owner) {
  this(owner.getViewModelStore(), owner instanceof HasDefaultViewModelProviderFactory
       ? ((HasDefaultViewModelProviderFactory) owner).getDefaultViewModelProviderFactory()
       : NewInstanceFactory.getInstance());
}
```

기본 생성자에서는 따로 팩토리 패턴을 통한 코드를 작성하지 않아도 되는데, 이는 내부적으로 `NewInstanceFactory.getInstance()`함수를 통해 팩토리 인스턴스를 생성해주기 때문이다.

이는 실제로 `ViewModelProvider.get()` 함수를 통해 내부적으로 생성된 mFactory인스턴스에 접근하여 `create()` 함수로 넘겨받은 클래스 타입의 뷰-모델을 생성해준다.

![Create ViewModel by Factory](https://imgur.com/G9xb1R7.jpg)



#### ViewModel - ViewModelProvider.**Factory**

이번에 살펴볼 방법은 앞에서 언급한 `ViewModelProvider.NewInstanceFactory`처럼, 직접 팩토리를 `ViewModelProvider.Factory` 인터페이스를 통해 구현하여 호출하는 방법이다(일반적으로는 편의성을 위해 직접적으로 호출하는 일은 없을것이다).

해당방법의 장점은 하나의 팩토리로 다양한 뷰모델 클래스를 관리하고, 예외적인 부분을 제어할 수 있다는 것이다.

코드는 다음과 같다.

```kotlin
ViewModelProvider(this, AnyViewModelFactory())
.get(AntViewModel::class.java)
```

```kotlin 
class AnyViewModelFactory : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return if (modelClass.isAssignableFrom(NoParamViewModel::class.java)) {
      NoParamViewModel() as T
    } else {
      throw IllegalArgumentException()
    }
  }
}
```

만약 파라미터가 있는 경우에는 팩토리, 뷰-모델 생성자에 프로퍼티를 추가로 넣어주면 된다.



#### AndroidViewModel

원래 Android Developer 사이트에서는 가이드라인에서 뷰-모델 사용 시 Context 사용을 권장하지 않는다. 아래 가이드를 참고하자.

![ViewModel Context usage Caution](https://imgur.com/SWxZ7aJ.jpg)



그럼에도 불구하고  ViewModel에서 라이프 사이클 컴포넌트의 Context를 사용을 해야한다면, `AndroidViewModel` 클래스를 통해 사용이 가능하다. 

안드로이드에서는 `AndroidViewModel` 사용 시 인스턴스 생성을 위해 `ViewModelProvider.AndroidViewModelFactory` 클래스를 통해 생성을 지원한다.

예제를 보자.

```kotlin 
class ApplicationContextUsageViewModel(application: Application) : AndroidViewModel(application)
```

보다시시피 선언된 클래스에서는 `Application` 인스턴스를 파라미터로 받는다.(실제로 이런케이스는 우리가 앱 리소스에 접근을 하지 않는 이상 크게 필요는 없을것이다.)

내부적으로 `ViewModelProvider.AndroidViewModelFactory` 클래스는 `ViewModelProvider.NewInstanceFactory` 를 상속하였다.

![AndroidViewModelFactory](https://imgur.com/ED1bKCm.jpg)

다음과 같이 `getInstance()` 함수를 통해 리플렉션을 통해 어플리케이션 인스턴스를 주입한 뷰모델을 생성하게 된다.

추가적으로 파라미터가 필요한 경우 직접 `ViewModelProvider.NewInstanceFactory` 클래스를 상속받은 커스텀 팩토리 클래스를 작성해주면된다.

```kotlin 
class AndroidViewModelWithParamFactory(private val application: Application, private val param: String)
: ViewModelProvider.NewInstanceFactory() {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (AndroidViewModel::class.java.isAssignableFrom(modelClass)) {
      try {
        return modelClass.getConstructor(Application::class.java, String::class.java)
        .newInstance(application, param)
      } catch (e: NoSuchMethodException) {
        throw RuntimeException("Cannot create an instance of $modelClass", e)
      } catch (e: IllegalAccessException) {
        throw RuntimeException("Cannot create an instance of $modelClass", e)
      } catch (e: InstantiationException) {
        throw RuntimeException("Cannot create an instance of $modelClass", e)
      } catch (e: InvocationTargetException) {
        throw RuntimeException("Cannot create an instance of $modelClass", e)
      }
    }
    return super.create(modelClass)
  }
}
```



## Android에서 사용하는 Data Binding 기술

이번에는 ViewModel과 함께 조합이 가능한 DataBinding기술에 대해 소개한다. 안드로이드에서는 Binder기술을 직접 구현하여 `DataBinding` 이라는 기술을 선택적으로 사용할 수 있다.

뷰-모델이 갖고 있는 데이터를 옵저버블 필드로 선언하여 변경이 되었을 때 이를 스트림으로 구독하여 최신 데이터를 반영할 수 있도록 기능을 제공한다.

Android Jetpack에서 제공하는 라이브러리를 통해 

### Binding Layout View with Architecture Component

AndroidX 라이브러리에는 안드로이드 아키텍쳐 컴포넌트(이하 AAC)가 포함되어 있는데, 이를 통해 선언형으로 레이아웃의 UI구성요소를 데이터 소스와 결합할 수 있다.

기존 방식과 비교해보자.

```kotlin 
// 일반적인 방식의 뷰 인스턴스를 바인딩 하는 방법
findViewById<TextView>(R.id.sample_text).apply {
  text = viewModel.userName
}
```

아래는 데이터바인딩을 이용하여 선언형으로 TextView 위젯에 뷰-모델에서 제공하는 필드를 직접 가져오는 방법이다.

```xml
// 데이터 바인딩을 통해 데이터를 가져오는 방법
<TextView
		...
		android:text="@{viewmodel.userName}" />
```

다음 방법을 통해 구성요소를 결합하면 많은 UI 접근 코드를 삭제할 수 있게되어 보일러플레이트 코드를 간소화할 수 있고, 기존에 우리가 뷰를 바인딩하던 라이프사이클 컴포넌트(뷰, 액티비티, 프래그먼트)의 코드가 간소화된다.

### Layout & DataBinding Expression

우리는 데이터 바인딩 표현식을 통해 변수 참조를 위한 import 기능, 변수 할당과 같이 레이아웃에서 다양한 제어를 할 수 있다. 이를 위해서는 `<layout>` 태그를 통한 처리가 필수적이다.

예제는 다음과 같다.

```xml 
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto">
  <data>
    <variable
              name="viewmodel"
              type="com.myapp.data.ViewModel" />
  </data>
  <ConstraintLayout... /> <!-- UI layout's root element -->
</layout>
```

### DataBindingAdapter

모든 레이아웃 표햔식에는 속성 또는 리스너를 설정하는 데 필요한 프레임워크 코드를 호출하는 `DataBindingAdapter`가 존재한다.

`DataBindingAdapter`를 이용하면 `setText(String)` 와 같은 메서드를 `app:text` 와 같은 방식으로 처리가 가능할 것이다. 

어노테이션 `@BindingAdapter` 를 사용하면, 레이아웃 정의를 할 수 있도록 프로퍼티에 접근이 가능하다.

```kotlin
@BindingAdapter("app:visibillty")
fun setVisibillity(view: View, visible: Boolean) {
  view.visibility = if (visible) View.VISIBLE else View.GONE
}

```

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto">
  <TextView
		app:visibillty=@{ true } />
</layout>
```



그 외에도, 데이터 바인딩 기술이 갖고있는 장점은 무수히 많다.

더 자세한 내용을 통해 다양한 방법으로 구성하는 방법에 대해 알고싶다면, 안드로이드 디벨로퍼 문서의 [데이터 바인딩 개요](https://developer.android.com/topic/libraries/data-binding?hl=ko) 문서를 참고하자.

## 안드로이드에서 MVVM 구현 방법



## 주관적인 안드로이드에서 MVVM 아키텍쳐를 구현할 때 장단점

### 장점

- MVP 아키텍쳐와 다르게 뷰라는 인터페이스를 사용하지 않기 때문에 모델과 뷰 사이, 뷰와 뷰-모델 사이에 의존성이 없으므로, 유닛테스트가 더 쉬워진다.
- MVP 아키텍쳐와 같이 테스트를 위한 가상 뷰를 만들지 않고 테스트 코드를 작성할 수 있다.
- 테스트 시 뷰-모델을 이용하여 모델이 변경되는 시점을 관찰하여 뷰-모델에서 사용되는 필드의 값에대한 변화가 의도대로 되었는지만 확인하면 된다.
- 데이터 바인딩 라이브러리를 이용하게 된다면, 뷰컨트롤러(액티비티, 뷰모델)간 의존성을 낮출 수 있으며, 이를 데이터 바인딩 기술을 통해 위임하여 기존에 사용하던 뷰에 대한 핸들링을 위한 보일러 플레이트 코드를 줄일 수 있다.



### 단점

- 안드로이드에서는 AAC 뷰-모델을 이용하는 경우 뷰와 뷰-모델 간 관계에 대해 1:1로 정의할 수 밖에 없다는 단점이 있다. 이는 비즈니스 로직이 비대해지는 것을 방지하고자 하는 뷰-모델의 장점을 제대로 이용하지 못하는 부분이라고 생각한다.
- 데이터 바인딩 기술을 사용하는 경우, 공통으로 사용할 수 있는 바인딩 어댑터를 사용하기 마련인데, 규모가 커질경우 이에 대한 구조적인 설계를 잘 하지 못하면 유지보수 비용이 크고, 바인딩 코드에 대한 관리가 어려워질 수 있다.
- 단순한 로직을 담은 화면(스플래시, 버튼 한두개의 동작에 대한 대응)인 경우, 뷰-모델을 사용하게 되는 경우 오히려 유지보수 비용을 높일 수 있다는 단점이 있다. 그래서 적절히 복잡한 비즈니스 로직이 들어가는 경우에 권장을 한다. 그럼에도 불구하고, 앱 내에서 상태관리가 필수인 화면에서는 뷰-모델 사용을 권장한다.
- MVC, MVP 아키텍쳐 패턴에 비해 러닝커브가 높은편이다. 데이터 바인딩 기술을 익혀야 하고(선택적으로 사용은 가능하다), 뷰와 뷰-모델간 의존성을 끊어야 하기 때문에, 스트림 제어가 가능한 라이브러리(가령, 안드로이드에서는 RxJava, LiveData를 사용하여 스트림 제어를 한다)를 이해해야한다.


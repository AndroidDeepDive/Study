# MVVM vs MVI Part 2 - MVVM for Android

## Android에서의 MVVM Architecture Pattern 

본격적으로 **MVVM** 패턴을 Android에서 구현하기 위해 어떤 기술을 사용해야하는지 알아보자.

Android의 아키텍쳐 소개를 살펴보면 **MVVM** 이 가진 장점들이 무색하게, **MVVM** 에 대해 구체적으로 다루지 않는데,

사실 Android 생태계는 **MVVMM**을 구현하기 적합하지않기 때문이다.

**MVVM** 패턴에서 ViewModel은 View와 Model 사이의 데이터를 관리하고 바인딩 해주는 요소로 사용되지만,

Android의 LifeCycle은 굉장히 복잡하기때문에 Google에서는 **AAC(Android Architecture Component)** 기반의 ViewModel을 제공하고 있다.

![Android Architecture Over View](https://imgur.com/gIkXsiL.jpg)

> **참고** [Android Architecture](https://developer.android.com/topic/libraries/architecture)

### AAC ViewModel

Android에서 제공되는 ViewModel의 정의는 아래와 같다.

> The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way. The ViewModel class allows data to survive configuration changes such as screen rotations.

ViewModel 클래스는 생명주기를 인지하고 있는 형태로 UI와 연관되어 있는 데이터를 저장하고 관리하기 위해 디자인 되었다.

화면 회전같은 설정 변경 시 데이터를 보존하기 위한 역할로 우리가 일반적으로 알고있는 MVVM의 ViewModel과는 다른 목적으로 만들어진 것이다.

다만 그렇다고 해서 AAC ViewModel을 MVVM 패턴 구성 시 ViewModel로 사용하지 못하는 것은 아니다. 

AAC ViewModel이 MVVM의 ViewModel 역할인 View와 Model 사이에서 데이터를 관리하고, 바인딩 해줄 수 있는 것이다. 

이를 원할하게 하기 위해서 구글에서는 안드로이드에 적합한 `DataBinding`이라는 기술을 제공한다.

AAC ViewModel의 생성방법은 `ViewModelProviders` 를 사용하여 ViewModel을 팩토리패턴을 통해 만들어준다.

과거에 사용하던 방식인 `ViewModelProviders` 는 현재는 **Deprecated**되었다.

```kotlin
ViewModelProviders.of(this).get(AnyViewModel::class.java)
```

#### ViewModel - LifeCycle Extensions

첫번째로 View을 생성하는 가장 쉬운 방법을 살펴보자.

AndroidX에서 lifecycle-extensions 라이브러리의 의존성을 추가하여 사용한다.

```groovy
dependencies {
  // ...
  implementation "androidx.lifecycle:lifecycle-extensions:${latest_version}"
}
```

현재는 `ViewModelProvider` 를 통해 ViewModel생성을 지원한다.

파라미터가 없는 경우에는 다음과 같이 라이프사이클을 지정하기 위해 다음과 같이 `this`로 컴포넌트를 생성자로 넣어주고, 

이에 따라 `get()` 함수를 통해 ViewModel 인스턴스를 가져온다.

```kotlin
ViewModelProvider(this).get(AnyViewModel::class.java)
```

여기서는 기본적으로 들어가는 this는 `ViewModelStoreOwner`는 AndroidX의 `ComponentActivity`, `Fragment` 에서 구현한 인터페이스로 구성이 되어 있으므로, 

사용할 Activity와 Fragment를 넣어주면 된다.

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

두 번째 방법은 앞에서 언급한 `ViewModelProvider.NewInstanceFactory`처럼, 

직접 팩토리를 `ViewModelProvider.Factory` 인터페이스를 통해 구현하여 호출하는 방법이다.

일반적으로는 편의성을 위해 직접적으로 호출하는 일은 없을 것이다.

이 방법의 장점은 하나의 팩토리로 다양한 ViewModel 클래스를 관리하고, 예외적인 부분을 제어할 수 있다는 것이다.

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

만약 파라미터가 있는 경우에는 팩토리, ViewModel 생성자에 프로퍼티를 추가로 넣어주면 된다.

#### AndroidViewModel

원래 Android Developer 사이트에서는 가이드라인에서 ViewModel 사용 시 Context 사용을 권장하지 않는다. 

ViewModel 문서에서 아래와 같은 내용을 확인할 수 있다.

![ViewModel Context usage Caution](https://imgur.com/SWxZ7aJ.jpg)

> **출처** [Android Developers#ViewModel Overview](https://developer.android.com/topic/libraries/architecture/viewmodel)

그럼에도 불구하고 ViewModel에서 라이프 사이클 컴포넌트의 Context를 사용을 해야한다면, `AndroidViewModel` 클래스를 통해 사용이 가능하다. 

Android 에서는 `AndroidViewModel` 사용 시 인스턴스 생성을 위해 `ViewModelProvider.AndroidViewModelFactory` 클래스를 통해 생성을 지원한다.

예제를 보자.

```kotlin 
class ApplicationContextUsageViewModel(application: Application) : AndroidViewModel(application)
```

보다시시피 선언된 클래스에서는 `Application` 인스턴스를 파라미터로 받는다.

실제로 이런케이스는 우리가 앱 리소스에 접근을 하지 않는 이상 크게 필요는 없을 것이다.

내부적으로 `ViewModelProvider.AndroidViewModelFactory` 클래스는 `ViewModelProvider.NewInstanceFactory` 를 상속하였다.

```java
/**
 * {@link Factory} which may create {@link AndroidViewModel} and
 * {@link ViewModel}, which have an empty constructor.
 */
public static class AndroidViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static AndroidViewModelFactory sInstance;

    /**
     * Retrieve a singleton instance of AndroidViewModelFactory.
     *
     * @param application an application to pass in {@link AndroidViewModel}
     * @return A valid {@link AndroidViewModelFactory}
     */
     @NonNull
    public static AndroidViewModelFactory getInstance(@NonNull Application application) {
        if (sInstance == null) {
            sInstance = new AndroidViewModelFactory(application);
        }
        return sInstance;
    }

    private Application mApplication;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public AndroidViewModelFactory(@NonNull Application application) { 
        mApplication = application; 
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (AndroidViewModel.class.isAssignableFrom(modelClass)) {
        //noinspection TryWithIdenticalCatches
        try {
            return modelClass.getConstructor(Application.class).newInstance(mApplication);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
    return super.create(modelClass);
    }
}
```

다음과 같이 `getInstance()` 함수를 통해 리플렉션을 통해 어플리케이션 인스턴스를 주입한 ViewModel을 생성하게 된다.

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

## Android의 Data Binding

ViewModel과 함께 조합이 가능한 Data Binding기술에 대해 알아보자.

Android는 Binder기술을 직접 구현하여 `DataBinding` 이라는 기술을 선택적으로 사용할 수 있다.

ViewModel이 갖고 있는 데이터를 Observable 필드로 선언하여 변경이 되었을 때 이를 스트림으로 구독하여 최신 데이터를 반영할 수 있도록 기능을 제공한다.

> The Data Binding Library is a support library that allows you to bind UI components in your layouts to data sources in your app using a declarative format rather than programmatically.

위의 인용문은 Android 공식 문서에서 발췌한 Data Binding에 관한 설명이다.

말 그대로 코드로 직접적으로 컨트롤하는 것이 아닌, 선언형 방식으로 앱안의 데이터 소스와 UI 요소들을 바인딩하는 기술을 데이터바인딩이라고 부른다.

### 1. Binding Layout View with Architecture Component

AndroidX 라이브러리에는 AAC가 포함되어 있는데, 이를 통해 선언형으로 레이아웃의 UI 구성요소를 데이터 소스와 결합할 수 있다.

먼저 기존의 바인딩 방식을 확인해보자.

```kotlin 
// 일반적인 방식의 뷰 인스턴스를 바인딩 하는 방법
findViewById<TextView>(R.id.sample_text).apply {
    text = viewModel.userName
}
```

데이터 바인딩을 사용해서 데이터와 선언형으로 정의된 `TextView` 위젯에 ViewModel에서 제공하는 필드값을 직접 가져는 방식은 아래와 같다.

```xml
<TextView
    ...
    android:text="@{viewmodel.userName}" />
```

이처럼 데이터 바인딩을 통해 데이터와 UI를 결합하면 많은 접근 코드를 제거할 수 있어, 보일러 플레이트를 간소화할 수 있다.

추가적으로 View, Activity, Fragment 등에서 처리하던 바인딩 코드도 간소화되는 효과가 있다.

### 2. Layout & DataBinding Expression

데이터 바인딩에도 표현식이 존재한다.

이 표현식을 통해 변수의 참조를 위한 `import` 처리와 값 할당 등의 처리를 레이아웃 단에서 해결할 수 있다.

이 방법은 `<layout>` 태그가 필수적으로 요구 를 통한 처리 방법

우리는 데이터 바인딩 표현식을 통해 변수 참조를 위한 import 기능, 변수 할당과 같이 레이아웃에서 다양한 제어를 할 수 있다. 이를 위해서는 `<layout>` 태그를 통한 처리가 필수적이다.

예제는 다음과 같다.

```xml 
<layout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="viewmodel"
            type="com.myapp.data.ViewModel" />
    </data>
    <ConstraintLayout... /> <!-- UI layout's root element -->
</layout>
```

#### DataBindingAdapter

모든 레이아웃 표현식에는 속성 또는 리스너를 설정하는 데 필요한 프레임워크 코드를 호출하는 `DataBindingAdapter`가 존재한다.

`DataBindingAdapter`를 이용하면 `setText(String)` 와 같은 메서드를 `app:text` 와 같은 방식으로 처리가 가능하다.

`@BindingAdapter` annotation을 사용하면, 레이아웃 정의를 할 수 있도록 프로퍼티에 접근이 가능하다.

```kotlin
@BindingAdapter("app:visibillty")
fun setVisibillity(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}
```

```xml
<layout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <TextView
        app:visibillty=@{ true } />
</layout>
```

그 외에도, 데이터 바인딩 기술이 갖고있는 장점은 무수히 많다.

좀 더 자세한 내용은 아래 링크를 참조하자.

> **출처** [Android Developers#Data Binding Library](https://developer.android.com/topic/libraries/data-binding)

## MVVM for Android에서의 장점과 단점

### 장점
- View와 Model, View와 ViewModel 사이의 의존성이 없으므로 유닛 테스트가 더 용이하다.
- 테스트를 위한 가상 뷰를 만들지 않고 테스트 코드를 작성할 수 있다.
- 테스트시 ViewModel을 이용하여 Model이 변경되는 시점에 ViewModel에서 사용되는 필드의 값에 대한 변경이 의도대로 되었는지만 확인하면 된다.
- 데이터 바인딩 라이브러리 사용시, View를 컨트롤하는 Activity나 ViewModel 사이의 의존성을 낮출 수 있다.
- 데이터 바인딩 기술을 통해 View의 핸들링을 위한 보일러 플레이트 코드를 간소화할 수 있다.


### 단점
- AAC ViewModel의 경우 View와 ViewModel을 1:1 관계로만 정의할 수 있다.
- 데이터 바인딩을 사용하는 경우, 바인딩 어댑터를 필연적으로 사용하게 되는데, 구조적인 설계가 미비할 경우 유지보수 비용이 커질 수 있다.
- 스플래시 등 단순한 로직만 존재하는 화면에 ViewModel을 사용하게 되는 경우, 오히려 유지보수 비용이 증가할 수 있다.
- MVC, MVP에 비해 러닝커브가 상대적으로 높은 편이다. 데이터 바인딩 기술 및 스트림 제어에 대한 이해가 필요하다.
- 아키텍쳐 패턴 자체가 거대하고 복잡한 애플리케이션을 위해 설계되었기때문에, 소형 애플리케이션의 경우 오히려 오버헤드가 증가할 수 있다.
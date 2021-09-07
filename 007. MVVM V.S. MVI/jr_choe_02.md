# MVI (Part 2)

## Origin
MVI는 Hannes Dorfmann에 의해서 고안된 단방향 아키텍처(UDA)로 View에 표현되는 많은 상태들을 관리, 정의하기 위한 기존 디자인 패턴의 Model에 대한 역할을 정의함으로써 시작으로 된다.  
MVI 패턴은 다음과 같은 Key Concept을 가진다.

- 단방향 데이터 흐름(`A unidirectional cycle of data`)
- 중단되지 않는 흐름의 의도(`Processing of intents is non-blocking`)
- 상태 불변성(`The state is immutable`)

## MVI
![mvi](https://hannesdorfmann.com/images/mvi/mvi-func2.png)

### Model
Hannes Dorfmann은 기존 패턴(MVC, MVP, MVVM)에서 Model이 가지는 역할이 충분하지 않다고 한다.  
즉, `Model`의 역할이 화면에 렌더링(`Render`) 되기 위한 요소를 포함해야 한다고 말한다.
그러므로 MVI에서 Model은 기존의 다른 패턴과는 다르며, View를 통해 표현하는 모든 State(상태)를 포괄하는 개념으로 봐야한다.  
그렇기 때문에 `State(Model)-View-Intent`로 표현될 수 있다.

```kotlin
data class State(
    val isLoading: Boolean,
    val error: Throwable,
    val persons: List<Person>
)
```

### View
Intent의 결과물인 Model에서 반환된 State(상태)에 따라 UI를 렌더링하는 작업을 수행한다.

### Intent
사용자의 행동으로 설명되는 앱의 상태를 변화시키려는 의도를 말한다. 모든 앱 상태의 변화는 Intent로 부터 시작된다.

## MVI in Android
MVI의 Redux를 포함한 개념을 안드로이드에서 구현하게 되었을때 MVI 사이클은 다음과 같다.

`View - Intent - Action - Processor - Result - State - View`

예제나 구현에는 차이가 있겠지만 다음과 같은 사이클을 지나게 된다.
1. View에서 사용자의 행위로 `Intent`가 전달된다.
2. Intent는 적절한 `Action`으로 변환된다.
3. Action은 `Processor`를 통해 Result로 반환되는데 이때, Processor는 API 통신과 데이터베이스 쿼리 등의 Side Effect를 수반한 작업을 수행한다.
4. 반환된 `Result`는 `Reducer`를 통해 불변성을 제공하기 위해 이전 State와 현재 Result를 조합해 새로운 State를 만들어 낸다.
5. 만들어진 `State`는 View로 전달되고 `Render` 된다.

_MVI에서 흔희 볼 수 있는 Redux는자바스크립트에서 상태 관리를 위한 오픈소스 라이브러리이다._  
_Redux에 포함되는 컴포넌트는 State, Action, Reducer이다._

기존 디자인 패턴인 MVVM(정확히는 ViewModel)에 대입한 역할을 보자면 아래와 같다. (각 요소에 대한 설명은 생략)
* **View**
  * Intent
  * Render
* **ViewModel**
  * Action
  * Processor
    * UseCase, Repository, etc
  * Result
    * Reducer
  * State(**Model**)

즉, 위에서 설명한 구조를 도식으로 나타내면 다음과 같다.

![mvi](../007.%20MVVM%20V.S.%20MVI/ART/mvi_01.png)

결국 MVI 패턴의 ViewModel의 의존성은 아래의 코드와 같은 구조를 가질 것이다.
```kotlin
abstract class MviViewModel<I: ViewIntent, A: ViewAction, S: ViewState, R: ViewResult>(
  private val stateMachine: StateMachine<I, A, S, R>
) : BaseViewModel() {

    abstract val viewState: LiveData<S>
    fun subscribeIntents(intents: Observable<I>) = stateMachine.subscribeIntents(intents)
    fun processIntent(intent: I) = stateMachine.processIntent(intent)
}
```
MVI에서 ViewModel의 역할은 전달받은 Intent를 Action으로 변환하는 것을 포함해 Redux의 역할을 수행한다.

## Pros & Cons
* 장점
  * Single Source of Truth, 단일 상태이기 때문에 상태 문제에 직면할 가능성이 적다.
  * 단일 데이터 흐름으로 앱의 상태를 예측하능하고 이해하기 쉽게 해준다.
  * 각 상태의 산출물이 불변성 객체이기 때문에 Thread Safety와 같은 이득을 취할 수 있다.
  * 단방향 데이터 흐름과 상태 불변성으로 앱의 디버깅이 쉽다. 
  * 각 구성요소들이 각각 책임을 가지기 때문에 분리된 논리를 가진다.
  * 비즈니스 로직을 호출하고 상태가 옳바른지 확인만 하면 되기 때문에 테스트가 용이하다.
* 단점
  * 아무리 간단한 액션(상태)라도 MVI의 사이클 안에 있어야 함으로 많은 상용구 코드가 생성된다.
  * 일반적으로 일회성 액션(Toast, SnackBar 등)에 대해서도 MVI 사이클을 순회하여 불변(새로운) 객체를 매번 전달해주어야 한다.

## References
- https://hannesdorfmann.com/android/mosby3-mvi-1/
- https://adambennett.dev/2019/07/mvi-the-good-the-bad-and-the-ugly/
- https://jaehochoe.medium.com/android-mvi-7304bc7e1a84
- https://medium.com/myrealtrip-product/android-mvi-79809c5c14f0
- https://kennethss.medium.com/android-mvi%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90-uda-b16f116c7e34
- https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started
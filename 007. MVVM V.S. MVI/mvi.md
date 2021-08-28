안드로이드 개발자들은 확장과 유지보수성을 위해 MVVM, MVP, MVC 와 같은 아키텍처 패턴을 선택한다. 요즘은 MVVM 아키텍처를 많이 사용하곤 하지만, 우리는 다양한 곳에서 많은 데이터를 받음으로인해 Side Effect가 발생할 수 있으며, State 관리가 미흡하다는 단점이 존재해 MVI가 등장하게 되었다. MVVM에 대한 단점들은 MVI를 비교하면서 적어보려고 한다.

# What is MVI?
MVI는 Model-View-Intent의 형태를 띄우고 있다. MVI는 Hannes Dorfmann가 처음 창시했는데, 행위가 단방향으로 이루어져 있으며`(Uni-directional)`, 그 방향이 사이클을 이루고 있는`(cycle flow)` Cycle.js 프레임워크에 영감을 받아, 안드로이드에서도 사용할 수 있는 새로운 패턴으로 등장하게 되었다. MVI는 기존의 MVC, MVP, MVVM과는 매우 다른방식으로 작동하게 된다.

MVI가 의미하는 바는 아래와 같다.
* Model: Model은 상태를 나타낼 수 있다. MVI에서 Model은 데이터의 플로우가 단방향으로 이루어지기 위해 무조건 불변성을 보장해야 한다.
* View: View는 Activity나 Fragment를 나타내며, 앱의 상태를 전달 받아 화면에 랜더링하는 역할이다.
* Intent: Intent는 앱이나, 사용자가 취하는 행위를 나타내기 위한 의도이다. View는 Intent를 받고, ViewModel은 Intent를 옵저빙하며 Model은 그에따라 새로운 상태로 변환한다.

![](https://images.velog.io/images/jshme/post/4c180926-e2d1-4bfc-859e-ba97647e6465/image.png)


## Models
다른 아키텍처 패턴에서 불리고 있는 `Models` 는 데이터를 가지고 있거나 데이터베이스나 API와 같이 서버와의 통신을 위한 Bridge 역할로써 사용되고 있지만, MVI는 Model이 데이터를 가지고 있는 역할도 하면서, 앱의 상태를 나타내주는 역할도 지니고 있다.

그렇다면 앱의 상태라는 것은 무엇일까?
반응형 프로그래밍에서, 사용자가 UI에 있는 버튼을 클릭하거나 변수의 값이 바뀔 때 앱이 변화할 수 있다. 앱이 변화할 때, 새로운 상태로 전환된다고 할 수 있다. `새로운 상태`라는 것은 간단하게, 어떠한 행위로 인해 UI위에 프로그래스바가 생긴거나, 다른 화면으로 전환되거나, 새로운 리스트의 데이터가 생겨 이전과는 다른 결과를 보여주는 것이다. 
일반적으로 우리가 사용하는 아키텍처 패턴에서 데이터를 나타내는 Movie Class가 있다고 가정하자.

``` kotlin
data class Movie(
  var voteCount: Int? = null,
  var id: Int? = null,
  var video: Boolean? = null,
  var voteAverage: Float? = null,
  var title: String? = null,
  var popularity: Float? = null,
  var posterPath: String? = null,
  var originalLanguage: String? = null,
  var originalTitle: String? = null,
  var genreIds: List<Int>? = null,
  var backdropPath: String? = null,
  var adult: Boolean? = null,
  var overview: String? = null,
  var releaseDate: String? = null
)
```
이 모델은 ViewModel을 통해, 아래처럼 영화 리스트를 보여줄 수 있다.

``` kotlin
class MainPresenter(private var view: MainView?) {    
  override fun onViewCreated() {
    view.showLoading()
    loadMovieList { movieList ->
      movieList.let {
        this.onQuerySuccess(movieList)
      }
    }
  }
  
  override fun onQuerySuccess(data: List<Movie>) {
    view.hideLoading()
    view.displayMovieList(data)
  }
}
```

이 방식도 나쁘지는 않지만, 여전히 여러가지 문제가 존재할 수 있다.

1. `Multiple Inputs`
* MVP, MVVM에서 Presenter나 ViewModel은 관리하기 위한 input과 output의 수가 많을 수 있다. 이 경우에는 많은 백그라운드 스레드를 사용할 수 있는데, 큰 문제로 이어질 수 있다.

2. `Multiple States`
* MVP, MVVM에서 비즈니스로직과 View는 시시각각 다른 상태를 가지고있을 수 있다. 그래서 개발자들은 Observer pattern을 이용해, 상태를 동기화할 수 있는 방법을 사용하곤 하는데 이 방식 또한 충돌을 일으킬 가능성이 존재한다.

이러한 이슈 때문에 MVI에서는 Model을 데이터 보다는 상태를 나타낼 수 있도록 만들었다.
상태를 나타낼 수 있는 모델은 아래와 같이 나타낼 수 있다.

``` kotlin
sealed class MovieState {
  object LoadingState : MovieState()
  data class DataState(val data: List<Movie>) : MovieState()
  data class ErrorState(val data: String) : MovieState()
  data class ConfirmationState(val movie: Movie) : MovieState()
  object FinishState : MovieState()
}
```
API를 통해 영화 리스트를 가져오는 상황에서, 4개의 상태값이 존재할 수 있음을 정의한 상태이다. `LoadingState`는 영화 리스트를 가져오면서 보여줄 수 있는 로딩 상태, `DataState`는 영화리스트를 가져왔을 때 데이터를 담을 수 있는 상태, `ErrorState`는 어떠한 이유로 데이터를 받을 수 없을 때 등장하는 에러상태, `FinishState`는 모든 데이터를 다 받아왔을 때의 상태로 정의할 수 있다.

이런 방식으로 Model을 만든다고 가정했을 때, View, Presenter, Model에서처럼 여러 클래스에서 상태를 관리할 필요가 없어지게 된다. Model은 우리 앱에서 로딩바를 보여줄 것인지, 에러메세지를 보여줄 것인지, 아니면 데이터를 받는데 성공해서 우리에게 리스트를 보여줄 것인지 알려줄 것이기 때문이다.

그렇다면, 아래와 같이 개선해볼 수 있을 것이다.
``` kotlin
class MainPresenter {
  
  private val compositeDisposable = CompositeDisposable()
  private lateinit var view: MainView

  fun bind(view: MainView) {
    this.view = view
    compositeDisposable.add(observeMovieDeleteIntent())
    compositeDisposable.add(observeMovieDisplay())
  }
  
  fun unbind() {
    if (!compositeDisposable.isDisposed) {
      compositeDisposable.dispose()
    }
  }
  
  private fun observeMovieDisplay() = loadMovieList()
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe { view.render(MovieState.LoadingState) }
      .doOnNext { view.render(it) }
      .subscribe()
}
```


현재 ViewModel에서는 View의 상태를 위한 하나의 output을 가지고 있는 형태가 되며, `render()` 를 통해 현재의 상태를 인자로 담아 뷰에 보내주는 것이다.

MVI의 또다른 신기한 특징은, Single source of truth로써 비즈니스 로직을 유지하기 위해 모델은 `불변성(Immutable)`을 지니고 있어야 한다는 것이다. 이 방식은 여러 클래스에서 모델이 수정되지 않음을 보장하기 위해서인데, 앱의 전체 라이프사이클동안 단일 상태를 유지할 수 있게 된다.
(불변성을 가지고 있다는 의미는 모델을 수정할 수 없기 때문에, 내부의 Property는 바꿀 수는 없고 모델 자체를 copy()해서 새로 생성하는 방법이 필요하다.)

![](https://images.velog.io/images/jshme/post/cdcdf61b-e7d5-49a6-8c7d-bc726158f49f/image.png)

 Presenter(ViewModel) 에서 영화 리스트를 더하거나, 제거하는 행위가 이루어질 때 Business Logic에서는 각각 다른 상태를 가지고 있는 모델을 새로 생성하고, View에서는 유저 액션(영화 리스트를 더하거나 제거한 행위)을 옵저빙하고, UI Rendering 역할을 한다.


이런 Cycle을 유지하는 플로우 덕분에 우리는 다양한 이점을 얻을 수 있다.

1) `Single State`
* 불변성을 가지고 있는 데이터 구조는 한 곳에서만 데이터를 관리할 수 있다는 이점을 가지고 있기 때문에, 관리하기가 매우 용이하며 앱의 모든 layer에 단일 상태를 보장할 수 있다.

2) `Thread Safety`
* RxJava나 LiveData, Coroutines같은 비동기 라이브러리와 사용할 때 특히 유용하다. 모델의 내부를 수정할 수 있는 방법이 없기 때문에, 변화가 일어나는 하나의 장소를 유지하거나, 아니면 새로 다시 만들어야 한다. 이건 다른 객체가 우리의 모델을 다른 스레드로부터 수정하는 사이드이펙트를 방지할 수 있다.


## View and Intents

MVP 패턴에서 처럼, MVI도 View의 Interface로써 정의된다. MVP패턴의 경우에는 일반적으로 다른 input, output을 정의하기위해 많은 메소드를 사용해야하곤 한다. 하지만 MVI에서 View는 화면을 랜더링 하기위한 상태를 허용하는 하나의 render() 함수를 가지고 있는 편이며, View는 유저의 행동에 반응하기 위해 감지할 수 있는 intent()라는 함수를 사용하게 된다. 
>(여기서 언급하는 Intent는 `android.content.Intent class`가 절대 아닌, 앱의 상태 변화에 대해, 행동을 취해주는 방법으로서 표현되며 Event와 비슷한 의미이다.)

MVI는 아래와 같이 적용할 수 있다.
``` kotlin
class MainActivity : MainView {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }
    
  //1 MainView에 버튼에 대한 Observable을 생성한다.
  override fun displayMoviesIntent() = button.clicks()
    
  //2 View에 올바른 메소드를 연결시키기 위해 ViewState를 1대1 매핑한다.
  override fun render(state: MovieState) {
    when(state) {
      is MovieState.DataState -> renderDataState(state)
      is MovieState.LoadingState -> renderLoadingState()
      is MovieState.ErrorState -> renderErrorState(state)
    }
  }
    
  //3 View에 Model data를 랜더링하는 과정이다. 
  // data는 날씨 데이터, 영화 리스트, 에러 등등 모든 것이 될 수 있다.
  private fun renderDataState(dataState: MovieState.DataState) {
      //Render movie list
  }
    
  //4 뷰에 로딩상태를 보여주는 과정이다.
  private fun renderLoadingState() {
      //Render progress bar on screen
  }
	
  //5 뷰에 에러 메세지를 보여주는 과정이다.
  private fun renderErrorState(errorState: MovieState.ErrorState) {
      //Display error mesage
  }
}
```
이 예제를 통해, Presenter, ViewModel에서 하나의 `render()` 함수가 여러개의 상태를 받을 때 어떻게 대응이 되고있는지, 그리고 Intent(Event)가 Button click을 트리거하는지에 대해 알 수 있다. 이 코드는 에러메세지나 로딩바, 리스트를 보여주는 UI의 변화를 보여줄 수 있다.

가변 객체를 지니는 모델에서는, `model.insert(items)` 와 같은 메소드를 부르게 되면 근원이 되는 데이터를 변경하거나, 업데이트하거나, 추가하기가 쉬워지기 때문에 앱의 상태변화도 쉽다.

MVI 패턴을 알아보았을 때 Model은 불변해야한다는 것을 알기 때문에, 우리는 앱이 변할 때마다 객체를 새로 생성해줘야할 것이다. 즉, 새로운 데이터를 보여주고 싶다면 새로운 모델을 다시 만들어야 한다는 이야기이다. 그렇다면 혹시 과거의 상태로부터 정보가 필요한 경우에는 어떻게 해야할까?

# State Reducers
State Reducer의 컨셉은 반응형 프로그래밍의 Recuder 함수로부터 유래되었다. Reducer 함수는 우리에게 데이터를 합치고, 누적해주는 기능을 제공하기 때문에 편리하게 쓸 수 있고, 대부분의 표준 라이브러리들은 불변 객체구조를 위해 recuder와 비슷한 메소드가 구현되어 있다. 예시로 Kotlin의 List는 reduce()라는 메소드를 포함하고 있는데, 리스트의 처음 요소부터 시작하여 값을 누적하는 기능을 가지고 있다.

``` kotlin
val myList = listOf(1, 2, 3, 4, 5)

// accumulator : 누적된 총 값, 
// currentValue : iterator 를 통해 들어온 현재 위치의 value
var result = myList.reduce { accumulator, currentValue ->
  println("accumulator = $accumulator, currentValue = $currentValue")
  accumulator + currentValue }
println(result)

// result
accumulator = 1, currentValue = 2
accumulator = 3, currentValue = 3
accumulator = 6, currentValue = 4
accumulator = 10, currentValue = 5
15
```

그렇다면, State Recuder와 MVI 패턴이 무슨 관계를 가지고 있을까?

## Tying It All Together
값들을 함께 묶을 수 있다는 점이다. State Recuder 는 위에서 설명한 recuder 함수와 동작이 매우 비슷하지만 주요 차이점은 State Recuder는 과거상태에 기반하여, 새로운 상태를 만들며 변경사항을 유지하면서 현재 상태를 만들지만 reduce 함수는 일반적으로 collection 함수 내부에서 모든 행위가 이루어진다는 점이다.

이 과정은 Rxjava를 사용하여 아래와 같이 만들 수도 있을 것이다.

1) 우리 앱에 변경사항을 나타낼 수 있는`ParticalState` 라고 불리는 새로운 상태를 생성한다. 
2) 새로운 Intent가 시작 지점으로써 과거의 상태를 요구할 때, 새로운 `ParticalState`를 생성한다.
3) 새로운 과거 상태와 `ParticalState` 를 가져오는 reduce() 함수를 생성하고, 이 둘을 merge하고 새로운 상태로 나타낼 수 있는 코드를 정의한다.
4) 앱의 초기 상태에 reduce()를 실행하기 위해 Rxjava의 scan()을 사용하고, 새로운 상태를 리턴한다.


## Result

Model-View-Intent 방식은 유지보수와 확장성 관점에서 강한 장점을 지니고 있다. 그 중에선 단방향사이클(Uni-directional)을 지니고 있는 데이터 플로우(cycle flow)를 가지고 있다는 점이다. 그로인해 View의 lifecycle 동안 일관적인 상태를 유지할 수 있고, 불변 객체를 강제화해 Thread safety를 제공하기 때문에 신뢰성이 증가하게된다. 단점으로는, 다른 아키텍처 패턴보다는 러닝커브가 클 수 있다는 점이며 반응형 프로그래밍, 멀티쓰레드 방식에 대한 지식이 필요하다는 것이다.
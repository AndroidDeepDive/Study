# (Android Deep Dive) Coroutines Part 1

## 프로세스, 스레드, 코루틴

우리가 코루틴을 알아보기 전, 먼저 프로세스가 무엇인지, 스레드가 무엇인지 인지를 하고 공부를 해보면 좋을것이다. 간략한 개념을 보도록하자.

### Process | 프로세스

프로세스는 컴퓨터에서 연속적으로 실행되고 있는 프로그램을 의미한다. 또한, 프로그램이 각 할당된 Heap 메모리에 적재되어 실행되는 인스턴스이기도하다.

프로세스의 특징적인 부분은 서로 완벽히 독립적인 공간을 가진다는 것이다. 프로세스는 각자의 스택과 데이터영역을 갖고, 보호받을 수 있다.

따라서, 한 프로세스가 예외적인 상황으로 종료되어도, 다른 프로세스에는 전혀 지장이없다.

### Thread | 스레드

쓰레드는 하나의 프로세스에 실행되는 여러 흐름의 단위이다. 쓰레드는 프로세스 내 별도의 메모리 영역을 가진다. 프로세스는 Heap 메모리에 할당되지만, 그 위에 Stack이라는 메모리 영역 위에 올라간다. 즉 100개의 쓰레드가 만들어지면, 100개의 스택 메모리에 각각 할당되는 것이다.

쓰레드는 본질적으로 프로세스 내 속해있기 때문에 쓰레드간 자원을 공유할 수 있다라는 특징이 있다.

## Concurrency & Parallelism

우리는 먼저 **Concurrency | 동시성**과 **Parallelism 병렬성**에 대해 알아볼 필요가 있다.

### Concurrency | 동시성

동시성은 여러개의 Task가 있는데, 여러개의 Task를 각각 쪼개서 실행할 수 있다. 예를 들면 A Task에 1분이 걸리고, B Task에 5분이 걸린다고 가정하자. 동시성 처리는 A Task와 B Task를 조금씩 나누어 처리하게 됨으로써 총 6분이 소요되게된다. 다만, 기존에 절차지향과 다르게 동시에 실행이된다라는 특징이 있다. 또한 Context Switching이 발생한다라는 특징이 있다.

### Parallelism | 병렬성

Task 수 만큼 쓰레드가 필요하다. 쓰레드를 이용하게 되면 프로세스 내에서 자원이 공유되기 때문에 Context Switching이 필요하지않다. 따라서, 병렬적으로 실행되는 방식으로는 예를들어 A Task에 1분이 걸리고, B Task에 5분이 걸린다면, 총 5분이 걸리게 된다.

### Thread V.S. Coroutine

쓰레드와 코루틴은 둘다 동시성 (Interleaving) 를 보장하기 위한 기술이다. 여러개의 작업을 동시에 수행할 때 쓰레드는 각 작업에 해당하는 메모리를 할당하지만, 여러 작업을 동시에 실행하다보면 쓰레드 풀의 한정된 수에 따라 OS레벨에서 적절히 스케쥴링이 필요하다. 따라서, 쓰레드를 많이 생성하는 것은 그만큼 많은 비용을 생성한다. 그런 의미에서 코루틴은 최대한 처리 비용을 줄이면서, Context Switching 비용을 줄이기 위해 고안된 기술이다. **Light-Weight Thread**라 불릴만큼 동작하는 결과는 쓰레드와 유사하게 보이는데, 실제로 쓰레드를 생성하는 것이 아닌 Object를 할당해주는 방식이기 때문에 해당 Object를 스위칭하여 Context Switching 오버헤드를 줄였다.

## Hello Concurrent World!

각 언어별로 지원하는 코루틴 스킬이 다른데, **Java**의 경우 대표적으로 API 8의 Future가 대표적이고, **JavaScript**의 경우 Promise 등을 제공하여 실행을 관리한다.

**Kotlin**의 경우 굉장히 쉽고 다양한 방식으로 동시성 프로그래밍을 제공한다. **Kotlin**의 코루틴도 여느 다른 언어들과 마찬가지로 언어레벨에서 키워드를 통해 제공한다.

코루틴을 이용하면 루틴을 진행하는 시점에 특정 상황에서 코루틴 **Scope | 범위**로 이동하게되고, 기존에 실행하고 있던 루틴을 정지하도록 만들 수 있다.

이를 위해서 Kotlin 코루틴에서는 약간의 핵심 키워드를 보면된다.

일단 코루틴을 쓰기이전에 우리가 어떻게 코드를 짜왔는지 비교해본다면, 코루틴은 비동기 프로그래밍에 단비같은 존재라는 것을 느낄 수 있다.

### Callback Code 

![Callback Code](https://imgur.com/5KwV6fO.jpg)

그나마 Rx와 같은 비동기 프로그래밍 라이브러리를 사용하면, 훨씬 더 쉬운 방법으로 스트림을 제어할 수 있다.

### RxKotlin Code

![RxKotlin Code](https://imgur.com/2e5uFMY.jpg)

그럼에도 불구하고, 여전히 모나드라는 Data Hold Instance에 갇혀, 흐름제어를 한다는 것은 무언가 한꺼풀 신경써야 하는 부분이 생긴다.

### Coroutines Code

코루틴을 사용하면, 코드를 읽을 때 분명 순차적으로 코드 흐름이 실행되는 것으로 보이지만, 실제로는 비동기적으로 흐름을 제어할 수 있다.

![Coroutines Code](https://imgur.com/UEeP8UQ.jpg)

### Suspend - 일시 중단 함수

```kotlin
suspend fun sum(val1: Int, val2: Int): Int {
    delay(2000)
    return val1 * val2
}
```

**Kotlin** 코루틴은 언어레벨에서 공식적으로 지원하는 키워드이다. 이 키워드를 이용하였을 때 결과적으로 코루틴의 실행을 일시 중단 / 재개 할 수 있다.

한번 원리를 보도록 하자.

**`suspend`**라는 키워드 하나를 붙였음에도 이 함수의 반환은 2초나 걸리게 된다. 내부적으로 어떤 마법이 일어나는 것일까?

suspend가 붙은 함수는 컴파일러에서 특별한 처리가 이뤄지게 된다. 

## CPS(Continuation Passing Style)

```kotlin
fun postItem(item: Item) {
  val token = requestToken() // suspend function
  val post = createPost(token, item) // suspend function
  processPost(post)
}

suspend fun requestToken() {
  ...
}

suspend fun createPost(token: String, item: Item): Post {
  ...
}
```

다음과 같이 suspend function으로 구성되어 있는 함수를 요청하는 `postItem(Item)` 이라는 함수가 있다. 이함수를 컴파일러에서 컴파일 시 위코드는 CPS 방식으로 아래와 같이 변환되게 된다.

```kotlin
fun postItem(item: Item) {
  requestToken { token ->
		val post = createPost(token, item) // Continuation
		processPost(post) // Continuation
	}
}
```

CPS로 변환된 코드를 보면 콜백함수와 굉장히 유사한 형태를 띄는것을 알 수 있다.

### How does it works?

그렇다면, CPS로 변환된 코드가 눈으로 보았을 때는 순차적이지만, 어떻게 비동기적으로 동작하고 중단(suspend) 했다가 재개(resume)할 수 있는것일까?

```kotlin
suspend fun createPost(token: String, item: Item): Post { ... }
```

위와 같이 작성되었던 함수는 Reverse Compile하면 아래와 같이 Object로 반환을 하는 함수로 변환이 된다. 그리고 맨 마지막 매개변수에 Continuation이라는 타입의 객체가 인자로 추가된 것을 알 수 있는데, 이 객체가 흐름에 대한 제어를 할 수 있는 핵심요소이다.

```java
Object createPost(Token token, Item item, Continuation<Post> cont) { ... }
```

### Label

```kotlin
suspend fun postItem(item: Item) {
// LABEL 0
  val token = requestToken()
// LABEL 1
  val post = createPost(token, item)
// LABEL 2
	processPost(post)
}
```

suspend 키워드가 붙은 함수는 컴파일되면서 다음과 같이 주석으로 LABEL ${index}가 추가되는데, 코루틴은 함수가 중지/재개될 수 있도록 LABEL을 통해 중단/재개 지점을 정한다.

```kotlin
suspend fun postItem(item: Item) { 
  switch (label) {
    case 0:
    	val token = requestToken()
    case 1:
    	val post = createPost(token, item)
    case 2:
    	processPost(post)
  }
}
```

그리고, 위 코드는 LABEL이 다 추가되면 CPS 형태로 변환되면서 아래와 같이 변하게 된다.

```kotlin
fun postItem(item: Item, cont: Continuation) {
  val sm = object: CoroutineImpl { ... }
  switch (sm.label) {
    case 0:
    	requestToken(sm)
    case 1:
    	createPost(token, item, sm)
    case 2:
	    processPost(post)
  }
}
```

**Continuation** 객체는 콜백 인터페이스 넘겨줌으로써 재개해주는 콜백 인터페이스다. 여기서 sm은 State Machine을 의미하는데, 각 함수를 호출할때는 지금까지 한 연산의 결과를 취합하여 넘겨준다. 그렇기에 코루틴으로 구성된 함수들은 sm이라는 변수 이름으로 매개변수를 넘겨받게 된다. 이러한 것을 우리는 Continuation이라고 하며, 어떤 정보 값을 가진 형태로 전달되어 


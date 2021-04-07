
## Android Coroutine 은 무엇인가?



## 동기와 비동기는 무엇인가?

### * 동기(synchronous)

어떤 작업을 수행하고 그 결과가 나올때까지 다른 작업을 수행하지 않습니다. 먼저 수행한 작업이 끝나고 난 뒤 다른 작업을 수행합니다. 작업을 수행하면서 특정 요청을 하고 그 요청에 대한 응답을 기다리는 경우, 아무것도 하지 않으면서 자원만을 차지하고 있어서 비효율적으로 동작하는 할 때가 있습니다.

### * 비동기(Asynchronous)

어떤 작업을 수행하고 또 다른 작업을 수행할 수있습니다. 여러 작업을 동시에 수행할 수 있어서 자원을 효율적으로 사용할 수 있습니다. 하지만 각각의 작업을 완료하는 시간도 각기 다르고 여러 작업이 각각 수행하면서 동일한 자원에 접근하는 경우, 원치 않는 결과가 나타날 수 있기 때문에 작업을 수행하고 결과를 처리하는 과정이 복잡해질 수 있습니다.



## 블락킹과 논블락킹

### * 블락킹

…

### * 논블락킹

…

## 메인 스레드와 ANR

### * 메인 스레드

안드로이드에서 애플리케이션은 하나의 스레드에서 실행됩니다. 이 스레드를 메인 스레드(Main Thread) 라고 부르며, 이 스레드를 통해 여러 작업을 수행할 수 있습니다. 그리고 오직 메인 스레드 에서만 UI 와 관련된 작업을 수행할 수 있습니다. 그래서 메인 스레드는 UI Thread 라고 부르기도 합니다.

### * ANR

안드로이드 시스템에서 메인 스레드에서 작업을 처리하면서 너무 오랜 시간 블락되는 경우, ANR(Application Not Responding) 에러가 발생할 수 있다. 그래서 오랜 작업시간이 걸리는 작업의 경우, 비동기로 처리해야 한다. 메인 스레드에서 워커 스레드(Worker Thread)를 생성해서 작업을 수행하고, 작업이 완료 되면 그 결과를 메인 스레드에 받아서 처리해야한다. 안드로이드에서는 Thread 와 Handler 통신, RxJava(RxKotlin), Coroutine 등 다양한 방법을 사용해서 비동기처리를 할 수 있다.

## Android Coroutine?
>  A *coroutine* is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously. [Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html) were added to Kotlin in version 1.3 and are based on established concepts from other languages.

코루틴은 비동기적으로 실행되는 코드를 간소화하기 위해 Android에서 사용할 수 있는 병행(동시 실행) 설계 패턴입니다. 코루틴은 코틀린 1.3 버전에 추가되었으며 다른 언어에서 설립된 개념을 기반으로 합니다.

* 경량 : 스레드를 막지 않는 중단(suspend)를 지원함으로 하나의 싱글 스레드에서 많은 코루틴을 실행 할 수 있습니다. block 은 메모리를 차지하고 있는 반면에 suspend 는 메모리를 차지하지 않기 때문에 메모리를 절약할 수 있습니다.

* 적은 메모리 누수 : 구조화된 병행(동시 실행)을 범위내에서 사용할 수 있습니다.

* 취소 지원 : 실행되고 있는 코루틴의 계층을 통해 취소를 자동으로 전달할 수 있습니다.

* Jetpack 지원 : Jetpack 라이브러리에 코루틴을 지원하는 라이브러리가 있습니다. 일부 라이브러리는 구조화된 병행(동시 실행)을 사용하는 코루틴 스코프(Coroutin scope) 도 지원합니다

기본적으로 코루틴은 경량 스레드 입니다. 코루틴은 일부 코루틴 스코프 내의 컨텍스트에서 launch 코루틴빌더와 함께 실행됩니다. 새로운 코루틴을 GlobalScope 에서 실행하는것은, 새로운 코루틴의 생명이 전체 애플리케이션의 수명에 제한된다는 것을 의미합니다.



코루틴은 아래와 같이 작성이 가능합니다. 여기서 delay 는 특별한 중단 함수로써 스레드를 블락하지 않고 코루틴을 중단할 수 있습니다. 스레드에서는 사용할 수 없고, 코루틴에서만 사용 가능합니다. delay 와 달리 Thread.sleep 은 스레드를 블락하는 방법으로 코루틴을 중단합니다.

    fun main() {
      GlobalScope.launch {
        delay(1000L)
        println(“World!”)
      }
      println(“Hello,”)
      Thread.sleep(2000L)
    }



코루틴에는 runBlocking 을 사용하면 Thread.sleep 와 같이 스레드를 블락할 수 있습니다. runBlocking 을 사용하면 블락과 넌블락 코드를 직관적으로 작성할 수 있습니다.

    fun main() {
      GlobalScope.launch {
        delay(1000L)
        println("World!")
      }
      println("Hello,")
      runBlocking {
        delay(2000L)
      }
    }



다른 코루틴이 수행중인 동안에 지연시키는 것은 좋은 접근이 아닙니다. join 을 사용하면 논블락 방식으로 백그라운드 잡을 완료하기 전까지 기다립니다.

    fun main() = runBlocking {
      val job = GlobalScope.launch {
        delay(1000L)
        println("World!")
      }
      println("Hello,")
      job.join()
    }

GlobalScope.launch 을 사용하면 최상위 코루틴을 만들수 있습니다.  그것은 가벼울뿐만 아니라 실행함에 있어 메모리의 일부만을 사용합니다.







There is still something to be desired for practical usage of coroutines. When we use GlobalScope.launch, we create a top-level coroutine. Even though it is light-weight, it still consumes some memory resources while it runs

















[https://developer.android.com/kotlin/coroutines](https://developer.android.com/kotlin/coroutines)
[https://kotlinlang.org/docs/coroutines-guide.html](https://kotlinlang.org/docs/coroutines-guide.html)



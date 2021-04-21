
## 프로세스와 스레드

프로세스는 운영체제로부터 자원을 할당받는 작업의 단위이고 스레드는 프로세스가 할당받은 자원을 이용하는 실행의 단위이다.
프로세스는 실행될 때 운영체제로부터 프로세서를 할당받고, 운영되기 위해 필요한 주소 공간, 메모리 등 자원을 할당받는다. 스레드란 한 프로세스 내에서 동작되는 여러 실행의 흐름으로, 프로세스 내의 주소 공간이나 자원들을 같은 프로세스 내에 스레드끼리 공유하면서 실행된다.



## 동기와 비동기는 무엇인가?

### * 동기(synchronous)

어떤 작업을 수행하고 그 결과가 나올때까지 다른 작업을 수행하지 않는다. 먼저 수행한 작업이 끝나고 난 뒤 다른 작업을 수행한다. 작업을 수행하면서 특정 요청을 하고 그 요청에 대한 응답을 기다리는 경우, 아무것도 하지 않으면서 자원만을 차지하고 있어서 비효율적으로 동작하는 할 때가 있다.

### * 비동기(Asynchronous)

어떤 작업을 수행하고 또 다른 작업을 수행할 수있다. 여러 작업을 동시에 수행할 수 있어서 자원을 효율적으로 사용할 수 있다. 하지만 각각의 작업을 완료하는 시간도 각기 다르고 여러 작업이 각각 수행하면서 동일한 자원에 접근하는 경우, 원치 않는 결과가 나타날 수 있기 때문에 작업을 수행하고 결과를 처리하는 과정이 복잡해질 수 있다.



## 메인 스레드와 ANR

### * 메인 스레드

안드로이드에서 애플리케이션은 하나의 스레드에서 실행된다. 이 스레드를 메인 스레드(Main Thread) 라고 부르며, 이 스레드를 통해 여러 작업을 수행할 수 있다. 그리고 오직 메인 스레드 에서만 UI 와 관련된 작업을 수행할 수 있다. 그래서 메인 스레드는 UI Thread 라고 부른다.

### * ANR

안드로이드 시스템에서 메인 스레드에서 작업을 처리하면서 너무 오랜 시간 블락되는 경우, ANR(Application Not Responding) 에러가 발생할 수 있다. 그래서 오랜 작업시간이 걸리는 작업의 경우, 비동기로 처리해야 한다. 메인 스레드에서 워커 스레드(Worker Thread)를 생성해서 작업을 수행하고, 작업이 완료 되면 그 결과를 메인 스레드에 받아서 처리해야한다. 안드로이드에서는 Thread 와 Handler 통신, RxJava(RxKotlin), Coroutine 등 다양한 방법을 사용해서 비동기처리를 할 수 있다.



## Android Coroutine?
>  *A coroutine* is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously. [Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html) were added to Kotlin in version 1.3 and are based on established concepts from other languages.

코루틴은 비동기적으로 실행되는 코드를 간소화하기 위해 Android에서 사용할 수 있는 병행(동시 실행) 설계 패턴이다. 코루틴은 코틀린 1.3 버전에 추가되었으며 다른 언어에서 설립된 개념을 기반으로 한다.

* 경량 : 스레드를 막지 않는 중단(suspend)를 지원함으로 하나의 싱글 스레드에서 많은 코루틴을 실행 할 수 있습니다. block 은 메모리를 차지하고 있는 반면에 suspend 는 메모리를 차지하지 않기 때문에 메모리를 절약할 수 있다.

* 적은 메모리 누수 : 구조화된 병행(동시 실행)을 범위내에서 사용할 수 있다.

* 취소 지원 : 실행되고 있는 코루틴의 계층을 통해 취소를 자동으로 전달할 수 있다.

* Jetpack 지원 : Jetpack 라이브러리에 코루틴을 지원하는 라이브러리가 있습니다. 일부 라이브러리는 구조화된 병행(동시 실행)을 사용하는 코루틴 스코프(Coroutin scope) 도 지원한다.



기본적으로 코루틴은 경량 스레드다. 코루틴은 일부 코루틴 스코프 내의 컨텍스트에서 launch 코루틴빌더와 함께 실행된다. 새로운 코루틴을 GlobalScope 에서 실행하는것은, 새로운 코루틴의 생명이 전체 애플리케이션의 수명에 제한된다는 것을 의미한다.



코루틴은 아래와 같이 작성이 가능하다. 여기서 delay 는 특별한 중단 함수로써 스레드를 블락하지 않고 코루틴을 중단할 수 있다. 스레드에서는 사용할 수 없고, 코루틴에서만 사용 가능하다. delay 와 달리 Thread.sleep 은 스레드를 블락하는 방법으로 코루틴을 중단한다.

    fun main() {
      GlobalScope.launch {
        delay(1000L)
        println(“World!”)
      }
      println(“Hello,”)
      Thread.sleep(2000L)
    }



코루틴에는 runBlocking 을 사용하면 Thread.sleep 와 같이 스레드를 블락할 수 있다. runBlocking 을 사용하면 블락과 넌블락 코드를 직관적으로 작성할 수 있다.

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



다른 코루틴이 수행중인 동안에 지연시키는 것은 좋은 접근이 아니다. join 을 사용하면 논블락 방식으로 백그라운드 잡을 완료하기 전까지 기다린다.

    fun main() = runBlocking {
      val job = GlobalScope.launch {
        delay(1000L)
        println("World!")
      }
      println("Hello,")
      job.join()
    }

GlobalScope.launch 을 사용하면 최상위 코루틴을 만들수 있다. 그것은 가벼울뿐만 아니라 실행함에 있어 메모리의 일부만을 사용한다.









## 채널(Channel)은 무엇인가?

채널은 서로 다른 코루틴 사이에 지연된 값을 스트림 형태로 전달할 수 있는 방법을 제공한다. 채널은 개념적으로 BlockingQueue 와 비슷하다. 한가지 다른점이 있다면 블록킹 put 대신 일시중단된 send 를 가지고, 블록킹 take 대신 일시중단된 receive 를 가진다.
큐와 달리, 채널은 값이 더이상 전달되지 않도록 닫을수 있고, 수신하는 쪽에서는 채널로 부터 값을 for 와 같은 반복 연산을 통해 받아 편리하게 사용할 수 있다.
개념상으로 클로즈는 특별한 클로즈 토큰을 채널에 보내는것과 같다. 이 클로즈 토큰을 수신하는 즉시 반복은 중단되며, 클로즈 토큰을 받기 전에 보낸 모든 값이 정상적으로 수신되도록 보장한다.

### * 채널 프로듀서

어디서든 코루틴은 연속적인 값을 만들어 내는 것이 일반적이다. 이는 동시성 코드에서 자주 볼 수 있는 소비자와 생산자의 패턴의 한 부분이다. 함수내에 채널을 매개변수로 사용하는 프로듀서(Producer)를 추상화 할 수 있습니다, 하지만 일반적으로 “함수는 반드시 결과를 반환” 해야 한다는 상식과는 상반됩니다.

### * 파이프라인

파이프라인(Pipeline)은 하나의 코루틴에서 값의 스트림을 무한하게 만들수 있는 패턴이다.

### * Fan-out / Fan-In

여러 코루틴은 아마 같은 채널로 부터 수신하며, 코루틴간의 작업을 분해하며, Fan-out 이라고 한다. 여러 코루틴은 같은 채널로 송신할 수 있으며, Fan-In 이라고 한다.



## 채널 유형과 배압

일반적인 채널은 버퍼를 가지지 않다. 언버퍼드 채널은 각각 다른 송신자(Sender) 와 수신자(Receiver)가 만날때 값을 전송한다. 만약 송신(Send)가 먼저 호출되면 수신(Receive)이 호출되기 전까지 송신이 중단된다. 만약 수신(Receive) 가 먼저 호출되면 송신이 호출 되기 전까지 수신이 중단된다.
채널 팩토리 함수와 프로듀스 빌더는 버퍼 사이즈를 정의하는 capacity 를 옵셔널로 가지고 있다. BlockingQueue 에서 정의된 capacity 와 비슷하게 버퍼는 capacity 블록의 버퍼가 모두 차면 일시 중단하기 전에 송신자가 여러 값을 보낼 수 있도록 한다.
언버퍼드 채널(Unbuffered channel)은 한번에 모든 값을 전달하는 반면에 버퍼드 채널(Buffered channel)은 capacity 를 정의하고 그 크기에 따라 값을 전달할 수 있다. 버퍼드 채널은 많은 데이터를 나눠서 주기적으로 보낼때 사용하면 좋다.



## 채널과 상호 작용

여러 코루틴으로부터 채널에 송신과 수신 동작은 순서에 따라 공정하게 작동한다. 이는 FIFO(first-in first-out order) 에 따라 동작한다. 여러개의 코루틴이 있는 경우, 첫번째 코루틴이 값을 받기 위해 receive 를 부른다.









## 플로우(Flow)

일반적인 지연 함수는 비동기적으로 하나의 값을 반환하지만, 여러의 값을 반환이 필요할 때가 있다. 시퀀스를 사용하거나 지연함수에 콜렉션을 반환하는 방법이 있다. 혹은 플로우를 사용할 수도 있다. 플로우는 시퀀스나 지연함수와 달리 메인 쓰레드의 블록킹 없이 사용할 수 있다.

* flow {…} 내의 코드 블락은 일시 중단 가능하다.

* suspend 키워드는 필요하지 않다.

* emit 함수를 통해 결과 값을 보낼 수 있다.

* collect 함수를 통해 값을 수집할 수 있다.



## 콜드

플로우는 시퀀스와 비슷한 콜드 스트림입니다. 플로우가 값이 수집되기 전까지는 플로우 빌더 내부의 코드는 실행되지 않는다.



## 취소

플로우는 코루틴의 일반적 협력적인 취소를 준수한다. 일반적으로 플로우가 delay 와 같은 취소 가능한 중단 함수안에서 플로우가 중단되었을때 플로우 콜렉션은 취소할 수 있다 .



## 플로우 연산자(Intermediate flow operators)

플로우는 콜렉션과 시퀀스와 같이 연산자(operator)와 함께 변환할 수 있다. 연산자는 업스트림 플로우에 적용되고 다운스트림 플로우를 반환한다. 플로우와 같이 연산자는 콜드이며 오퍼테이터를 호출하는 것은 그 자체가 중단되는 것은 아니다. 빠르게 수행되며 새로 변환된 플로우를 반환한다.
기본 연산자에는 map 과 filter 와 같이 친숙한 이름의 연산자가 있다. 시퀀스와 중요한 차이는 이러한 연산자는 코드 블럭 내에서 중단 함수를 호출할 수 있다.

### * Transform

플로우 변환 연산자중에 가장 일반적인것중에 하나이다. map 이나 filter 와 단순한 변환을 모방할뿐만 아니라 복잡한 변환도 사용할 수 있다. transform 연산자를 사용하면, 임의의 값을 임의의 횟수만큼 내보낼수 있다.

### * Size-limiting operators

take 와 같은 크기 제한 연산자는 정해진 제한에 도달한 때 실행을 플로우 취소할 수 있다. 코루틴에서 취소는 항상 exception 을 수행하며, 모든 리소스 관리 함수(try { … } finally { … } 와 같은)가 취소시 정상적으로 작동한다.



## 플로우 구성(Composing multiple flows)

여러 플로우를 많은 방법으로 구성 가능하다.

### * Zip

코틀린의 Sequence.zip 확장 함수와 같이 플로우는 zip 연산자를 사용해서 두 개의 플로우의 값을 조합할 수 있다.

### * Combine

플로우는 가장 최근의 변수나 연산의 값을 나타낼 때, 플로우가 방출한 데이터를 연산하거나 업스트림 플로우에 방출된 데이터에 따라 다시 연산이 필요할때는 Combine 을 사용할 수 있다.







[https://developer.android.com/kotlin/coroutines](https://developer.android.com/kotlin/coroutines)
[https://kotlinlang.org/docs/coroutines-guide.html](https://kotlinlang.org/docs/coroutines-guide.html)
[https://myungpyo.medium.com/reading-coroutine-official-guide-thoroughly-part-0-20176d431e9d](https://myungpyo.medium.com/reading-coroutine-official-guide-thoroughly-part-0-20176d431e9d)

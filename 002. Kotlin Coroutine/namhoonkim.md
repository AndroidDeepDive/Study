
## (Android Deep Dive) Kotlin Coroutine 

### Kotlin Coroutine이란?

원활한 비동기 프로그래밍 개발을 위하여 Kotlin은 언어 레벨에서 Coroutine 이라는 도구를 제공한다.

Coroutine은 비동기적으로 실행되는 코드를 간소화하기 위해 Android에서 사용할 수 있는 동시 실행 설계 패턴으로, Kotlin 버전 1.3에 추가되었으며 다른 언어에서 이미 확립된 개념을 기반으로 한다.

Coroutine이라는 개념은 1963년에 이미 출판본에서 확인할 수 있으며, subroutine이나 thread 등과 비교한 글들도 쉽게 찾아 볼 수 있다.

> **참고** [Design of a Separable Transition-Diagram Compiler(1963)](http://melconway.com/Home/pdf/compiler.pdf)

### Android의 비동기프로그래밍 관점에서의 Coroutine 기능
- **Lightweight** : 경량
  - 코루틴을 실행 중인 스레드를 차단하지 않는 정지를 지원하므로 단일 스레드에서 많은 코루틴을 실행할 수 있다. 
  - 정지는 많은 동시 작업을 지원하면서도 차단보다 메모리를 절약할 수 있다.
- **Fewer memory leaks** : 메모리 누수 감소
  - 구조화된 동시 실행을 사용하여 범위 내에서 작업을 실행한다.
- **Built-in cancellation support** : 기본으로 제공되는 취소 지원
  - 실행 중인 코루틴 계층 구조를 통해 자동으로 취소가 전달된다.
- **Jetpack integration** : Jetpack 통합
  - 많은 Jetpack 라이브러리에 코루틴을 완전히 지원하는 확장 프로그램이 포함되어 있다. 
  - 일부 라이브러리는 구조화된 동시 실행에 사용할 수 있는 자체 코루틴 범위도 제공한다.

### Kotlin Basic

#### launch

coroutine을 이용해 비동기로 실행할 부분을 `launch` 블록으로 감싸면 비동기로 실행이 된다.

```kotlin
import kotlinx.coroutines.*

fun main() {
    GlobalScope.launch { // launch a new coroutine in background and continue
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main thread continues while coroutine is delayed
    Thread.sleep(2000L) // block main thread for 2 seconds to keep JVM alive
}
```

출력 결과는 아래와 같다.

```
Hello,
World!
```

이 `launch`는 `job`이라는 객체를 반환하는데 이를 통해, 비동기 코드의 완료를 명시적으로 기다리거나 중단시킬 수 있다.

```kotlin
val job = GlobalScope.launch { // launch a new coroutine and keep a reference to its Job
    delay(1000L)
    println("World!")
}
println("Hello,")
job.join() // wait until child coroutine completes
```

#### runBlocking

`runBlocking`을 사용하면 jvm의 matin thread를 블록내의 코드를 다 수행할때까지 block시킨다.

```kotlin
import kotlinx.coroutines.*

fun main() { 
    GlobalScope.launch { // launch a new coroutine in background and continue
        delay(1000L)
        println("World!")
    }
    println("Hello,") // main thread continues here immediately
    runBlocking {     // but this expression blocks the main thread
        delay(2000L)  // ... while we delay for 2 seconds to keep JVM alive
    } 
}
```

출력 결과는 아래와 같다.

```
Hello,
World!
```

#### coroutineScope

`GlobalScope.launch`만으로 작업한다면 job에 대한 join을 관리하면서 오류가 발생할 확률이 높다.

이를 방지하기 위해 Scope 내부에서 또 다른 Scope를 생성할 수 있다.

Coroutine 외부에 존재하는 블럭은 내부에서 실행되는 모든 coroutine이 종료되어야 실행된다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking { // this: CoroutineScope
    launch { 
        delay(200L)
        println("Task from runBlocking")
    }
    
    coroutineScope { // Creates a coroutine scope
        launch {
            delay(500L) 
            println("Task from nested launch")
        }
    
        delay(100L)
        println("Task from coroutine scope") // This line will be printed before the nested launch
    }
    
    println("Coroutine scope is over") // This line is not printed until the nested launch completes
}
```

출력 결과는 아래와 같다.

```
Task from coroutine scope
Task from runBlocking
Task from nested launch
Coroutine scope is over
```

#### suspend
Coroutine에서 사용되는 코드들을 외부로 빼려면 `suspend` 키워드를 사용한다.

`suspend`로 지정된 메서드는 내부에서 Coroutine api를 호출할 수 있게 된다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch { doWorld() }
    println("Hello,")
}

// this is your first suspending function
suspend fun doWorld() {
    delay(1000L)
    println("World!")
}
```

출력 결과는 아래와 같다.

```
Hello,
World!
```


---



- async

### DLC
- iterator
- sequence
- Channel
- Flow


## References
https://kotlinlang.org/docs/coroutines-overview.html
https://developer.android.com/kotlin/coroutines




---

## 👏코루틴의 동시성 내부
`suspend function`은 `CPS`로 수행된다. 이는 호출되는 함수에 continuation을 보내는 것을 전제로 하고있어, 함수가 완료되는대로 continuation을 계속 호출할 것이다.(이를 callback으로도 생각할 수 있다.) suspend function이 다른 suspend function을 호출할 때마다 완료 혹은 오류가 발생했을 때 호출되어야 하는 continuation을 전달하게 된다. 
suspend function은 상태 머신으로 변환되는데 상태를 저장하고 복구하며 한번에 코드의 한 부분을 실행하게 된다. 그래서 suspend function을 재개할 때마다 중단된 위치에서 상태를 복구하고 실행을 지속할 수 있는 것이다. CPS와 상태머신이 결합하면 컴파일러는 다른 연산이 완료되기를 기다리는 동안 suspend 가능한 function을 생성하게 된다. 

### Continuation 
모든 것은 suspend function의 빌딩 블록이라고 볼 수 있는 `연속체(continuation)`에서부터 시작하게 된다. 이 연속체는 코루틴을 재개할 수 있는 중요한 인터페이스이다.

``` kotlin
interface Continuation<in T> {
  val context: CoroutineContext

  // 일시 중단을 일으킨 작업의 결과이다. 
  // 해당 함수가 Int 를 반환하는 함수를 호출하기위해 일시중지가 되면, T 값은 Int 가 된다.
  fun resume(value: T)

  // 예외를 전파할 때 사용된다.
  fun resumeWithExceptioin(exception: Throwable)
} 
```

### suspend 
코루틴에서는 `suspend`라는 한정자를 추가해서, 주어진 범위의 코드가 연속체를 사용해 동작하도록 컴파일러에게 지시할 수 있도록 만들어졌다. 그래서, suspend function이 컴파일 될 때마다 바이트코드가 하나의 커다란 연속체가 된다.

``` kotlin
  suepend function getUserSummary(id: Int): UserSummary {
    logger.log("fetching summary of $id")
    val profile = fetchProfile(id) // suspend function
    val age = calcuateAge(profile.dateOfBirth)
    val terms = validateTerms(profile.country, age) //suspend function
    return UserSummary(profile, age, terms)
}
```
suspend function 인 `getUserSummary()`은 실행을 제어하기 위해 연속체를 사용할 것이며, 두 번의 일시중지가 이루어질 것이다.



## 👏상태 머신
컴파일러가 위의 suspend function 코드를 분석하는 방식에 대해 알아보자.

### 1. 라벨
컴파일러는 실행이 시작되는 부분과 실행이 재개될 수 있는 부분에 라벨을 포함하게 된다.

``` kotlin
  suepend function getUserSummary(id: Int): UserSummary {
    // label 0 -> 첫 번째 실행
    logger.log("fetching summary of $id")
    val profile = fetchProfile(id) // suspend function
    
    // label 1 -> resume
    val age = calcuateAge(profile.dateOfBirth)
    val terms = validateTerms(profile.country, age) //suspend function

    // label 2 -> resume
    return UserSummary(profile, age, terms)
}
```
이는 when 구문으로 분리되어 표현된다.

```kotlin
when(label) {
  0 -> {
    logger.log("fetching summery of $id")
    fetchProfile(id)
    return
  }
  
  1 -> {
    calculateAge(profile.dateOfBirth)
    validateTerms(profile.country, age)
    return
  }
  
  2-> UserSummary(profile, age, terms)
}
```

### 2. 연속체
다른 지점에서 실행을 재개할 수 있도록 코드가 변환이 되었다면, 이제 함수의 라벨로 어떻게 도달해야하는지 찾아야 한다. 
라벨의 속성을 포함하고 있는 연속체의 추상체를 구현하고 있는  `CoroutineImpl` 를 구현해보자. 

```kotlin
suspend fun getUserSummary(id: Int, cont: Continuation<Any?>): UserSummary {
  val sm = object : CoroutineImpl {
    override fun doResume(data: Any?, exception: Thowable?) {
      getUserSummary(id, this)
    }

    val state = sm as CoroutineImpl
    when(state.label) {
      ....
    }
}
```
`doResume()` 이 `getUserSummary()`로 콜백을 전달할 수 있게 된다. 호출하는 쪽이, 중단과 재개도 같이 이루어져야하기 때문에 resume에서 `getUserSummary()`가 완료되었을 때 cont 만을 인자로 받는 것이다. 

### 3. 콜백 & 라벨 증가
라벨을 이용해 특정 시점에서 재개가 가능하게되었다면, `getUserSummary()` 로부터 호출된 다른 suspend function 이 CoroutineImple를 전달받을 수 있어야 한다. 
``` kotlin
when(state.label) {
0 -> {
    logger.log("fetching summery of $id")
    sm.label = 1
    fetchProfile(id, sm)
    return
  }
  
  1 -> {
    calculateAge(profile.dateOfBirth)
    sm.label = 2
    validateTerms(profile.country, age, sm)
    return
  }
  
  2-> UserSummary(profile, age, terms)
}
```
`fetchProfile()` 과 `validateTerms()`가 `Continuation<Any?>` 를 수신하고, 실행이 완료되는 시점에 `doResume()`이 호출되도록 수정되었다. 그러면, 이 두개의 함수가 실행이 끝날 때마다 수신하는 연속체를 호출하게 되는데, `getUserSummary()`에 구현된 연속체에서 실행을 재개할 수 있다.

또한 다른 suspend function이 호출되기전에 라벨을 증가시켜 다른 함수를 호출시킬 수 있도록 만든다.

## Android Coroutine 은 무엇인가?



## 동시성(concurrency)과 병렬성(parallelism)

코틀린 코루틴은 비동기 프로그래밍을 위한 툴로서 비동기 또는 넌블럭킹 프로그래밍을 지원한다. 비동기(asynchrony)는 동시성과 병렬성라는 용어와 연관이 있다. 코틀린 코루틴은 원래 병렬성이 아닌 동시성과 대부분 관련이 있다. 코루틴의 정교한 구조화된 코드는 고도의 동시성을 실행할 수 있도록 도와준다. 뿐만 아니라 

### * 동시성

많은 사람들이 잘못 믿고 있는 병렬적으로 실행되거나 또는 같은 시간에 실행되는 것에 대해서 동시성이라고 잘못 알고 있다. 동시성은 독립적으로 실행 가능한 작업(task)의 구성이라고 할 수 있다. 동시성 프로그래은 동시에 처리중인 다수의 작업을 다루는 것을 말하며, 동시에 실행될 필요는 없다. 동시성은 병렬성이 아니다. 동시에 실행할 필요가 없는 작업을 나누며, 기본 목적은 병렬성이 아닌 구조이다.

### * 병렬성

종종 동시성의 동의어로 잘못 사용되는 병렬성은 여러 일이 동시에 실행되는 것이다. 동시성이 구조에 관한것이라면, 병렬성은 다수의 작업을 실행에 관한것이다. 동시성은 병렬성의 사용을 쉽게 만든다라고 말하지만, 동시성 없이 병렬성을 가질수 있기 때문에 동시성이 필요 조건은 아니다. 

동시성은 한번에 여러일을 다루는 것이며, 병렬성은 한번에 여러 일을 하는 것이다.





## 동시성이 어려운 이유

코루틴은 [Dispatchers.Default](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-default.html) 와 같은 다중 스레드 디스패처에서 동시적으로 실행될 수 있다. 여기서 일반적은 동시성의 문제가 나타단다. 공유된 가변 상태(mutable state) 접근에 대한 동기화가 주요한 문제이다.



코 루틴은 Dispatchers.Default와 같은 다중 스레드 디스패처를 사용하여 동시에 실행할 수 있습니다. 모든 일반적인 동시성 문제를 나타냅니다. 주요 문제는 공유 변경 가능 상태에 대한 액세스 동기화입니다. 코 루틴 영역에서이 문제에 대한 일부 솔루션은 다중 스레드 세계의 솔루션과 유사하지만 다른 솔루션은 고유합니다.

Coroutines can be executed concurrently using a multi-threaded dispatcher like the [Dispatchers.Default](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-default.html). It presents all the usual concurrency problems. The main problem being synchronization of access to **shared mutable state**. Some solutions to this problem in the land of coroutines are similar to the solutions in the multi-threaded world, but others are unique.





## 코루틴 동시성 어떻게 동작할까?

### * Continuation Passing Style (CPS)

코드 측면에서 연속의 의미를 부여한다. 어떠한 프로시저(procedure)도 호출자에게 반환(return) 할 수 없다. 프로시저는 반환 값을 호출하기 위해 콜백을 받을수 있다. 프로시저가 호출자에게 반환할 준비가 되었을 때, 반환값에 대해 현재 연속 콜백을 호출한다. 코루틴은 Continuation Passing Style 로 동작하며, Continuation을 지원하는 언어을 사용한다는 것은, 프로그래머가 예외, 백트래킹, 스레드, 제네레이터 와 같은 컨트롤을 구성할 수 있다는 것을 의미한다.

* Direct Style
```
    fun postItem(item: Item) {
      val token = requestToken()
      val post = createPost(token, item)
      processPost(post)
    }
```
* Continuation Passing Style 
```
    fun postItem(item: Item) {
      requestToken { token -> 
        val post = createPost(token, item)
        processPost(post)
      }
    }
```
코틀린 코루틴은 Direct Style 로 작성하면 Continuation Passing Style 와 같이 컴파일 단계에서 코드가 변환된다. 프로그래머가 CPS 를 작성하지 않아도 컴파일러가 CPS 로 작동하게 만들어 준다. 

### * Continuation

코루틴의 컨티뉴에이션은 아래와 같이 구성된다. 

    interface Continuation {
      val context: CoroutineContext
      fun resume(value: T) 
      fun resumeWithException(exception: Throwable)
    }

* CoroutineContext
>  The context of this scope. Context is encapsulated by the scope and used for implementation of coroutine builders that are extensions on the scope. Accessing this property in general code is not recommended for any purposes except accessing the [Job](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/index.html) instance for advanced usages.

컨텍스트(Context)는 범위(Scope)에 의해 캡슐화되어 있으며 범위 확장인 코루틴 빌더의 구현을 위해 사용한다. 생성된 코루틴들이 실행될 때 사용하는 스레드를 정보를 담고 있는 디스패처(Dispatcher)도 여기에 포함되어 있다.

* resume

코루틴이 일시 중지되었을 때, 결과 값을 가지고 있다

* resumeWithException

코루틴이 중지되거나 예외가 발생한 경우, 예외를 전달한다.

### * Suspend
>  pauses the execution of the current coroutine, saving all local variables.

코루틴에서 suspend 를 추가하면, 현재 실행중인 코루틴을 일시 중단하거나 결과 값을 저장할 수 있다. 또한 주어진 범위내의 코드가 연속체를 사용해 동작하도록 컴파일러에게 지시할 수 있도록 만들어졌다.



리스트 전체의 총합과 평균 값을 구하는 코드를 아래와 같이 작성할 수 있다.

    import kotlinx.coroutines.*

    fun main() = runBlocking<Unit> {
        launch {
            val list = listOf(10, 20, 40)
            val sum = getSum(list)
            val avg = getAvg(sum, list.size)
            showAvg(avg)
        }
    }

    suspend fun getSum(list: List<Int>): Int {
      return list.sum()
    }

    suspend fun getAvg(sum: Int, size: Int): Float {
      return (sum / size).toFloat()
    }

    private fun showAvg(avg: Float) {
      print("Avg: $avg")
    }

위에 코드를 디컴파일하면 아래와 같이 변경되었음을 확인할 수 있다.

    BuildersKt.runBlocking$default((CoroutineContext)null, (Function2)(new Function2((Continuation)null) {
       // $FF: synthetic field
       private Object L$0;
       int label;
    
       @Nullable
       public final Object invokeSuspend(@NotNull Object var1) {
          Object var3 = IntrinsicsKt.*getCOROUTINE_SUSPENDED*();
          switch(this.label) {
          case 0:
             ResultKt.*throwOnFailure*(var1);
             CoroutineScope $this$runBlocking = (CoroutineScope)this.L$0;
             return BuildersKt.launch$default($this$runBlocking, (CoroutineContext)null, (CoroutineStart)null, (Function2)(new Function2((Continuation)null) {
                Object L$0;
                int label;
    
                @Nullable
                public final Object invokeSuspend(@NotNull Object $result) {
                   Object var10000;
                   label17: {
                      Object var5 = IntrinsicsKt.*getCOROUTINE_SUSPENDED*();
                      List list;
                      Main var6;
                      **switch(this.label) {
                      case 0:**
                         ResultKt.*throwOnFailure*($result);
                         list = CollectionsKt.*listOf*(new Integer[]{Boxing.*boxInt*(10), Boxing.*boxInt*(20), Boxing.*boxInt*(40)});
                         var6 = Main.this;
                         this.L$0 = list;
                         **this.label = 1;**
                         var10000 = var6.getSum(list, this);
                         if (var10000 == var5) {
                            return var5;
                         }
                         break;
                      **case 1:**
                         list = (List)this.L$0;
                         ResultKt.*throwOnFailure*($result);
                         var10000 = $result;
                         break;
                      **case 2:**
                         ResultKt.*throwOnFailure*($result);
                         var10000 = $result;
                         **break label17;**
                      **default:**
                         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                      **}**
    
                      int sum = ((Number)var10000).intValue();
                      var6 = Main.this;
                      int var10002 = list.size();
                      this.L$0 = null;
                      **this.label = 2;**
                      var10000 = var6.getAvg(sum, var10002, this);
                      if (var10000 == var5) {
                         return var5;
                      }
                   }
    
                   float avg = ((Number)var10000).floatValue();
                   Main.this.showAvg(avg);
                   return Unit.*INSTANCE*;
                }
    
                @NotNull
                public final Continuation create(@Nullable Object value, @NotNull Continuation completion) {
                   Intrinsics.*checkNotNullParameter*(completion, "completion");
                   Function2 var3 = new <anonymous constructor>(completion);
                   return var3;
                }
    
                public final Object invoke(Object var1, Object var2) {
                   return ((<undefinedtype>)this.create(var1, (Continuation)var2)).invokeSuspend(Unit.*INSTANCE*);
                }
             }), 3, (Object)null);
          default:
             throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
          }
       }
    
       @NotNull
       public final Continuation create(@Nullable Object value, @NotNull Continuation completion) {
          Intrinsics.*checkNotNullParameter*(completion, "completion");
          Function2 var3 = new <anonymous constructor>(completion);
          var3.L$0 = value;
          return var3;
       }
    
       public final Object invoke(Object var1, Object var2) {
          return ((<undefinedtype>)this.create(var1, (Continuation)var2)).invokeSuspend(Unit.*INSTANCE*);
       }
    }), 1, (Object)null);

    // $FF: synthetic method
    final Object getSum(List list, Continuation $completion) {
       return Boxing.*boxInt*(CollectionsKt.*sumOfInt*((Iterable)list));
    }
    
    // $FF: synthetic method
    final Object getAvg(int sum, int size, Continuation $completion) {
       return Boxing.*boxFloat*((float)(sum / size));
    }
    
    private final void showAvg(float avg) {
       String var2 = "Avg: " + avg;
       boolean var3 = false;
       System.*out*.print(var2);
    }

코드를 잘 살펴보면 라벨(Label) 이라는 항목이 있는데, 이 라벨은 실행이 시작되거나 재개될 수 있는 각 부분을 알려준다. 따라서 특정 범위의 실행이 끝나면 라벨 값을 변경하고 콜백을 통해 실행을 재개할 부분을 호출할 수 있도록 알려준다.

코루틴의 동작을 다시 설명하면 코루틴에서 suspend 한정자를 만나는 경우, 실행 또는 재개되어 실행할 수 있도록 컨티뉴이에션이 라벨과 함께 만들어진다. 각 컨티뉴이에션은 실행이 끝나면 라벨을 변경하고 콜백을 통해 다음 컨티뉴이에션이 연속적으로 실행되도록 작동한다.







참고자료

[https://kotlinexpertise.com/kotlin-coroutines-concurrency/](https://kotlinexpertise.com/kotlin-coroutines-concurrency/)

[https://resources.jetbrains.com/storage/products/kotlinconf2017/slides/2017+KotlinConf+-+Deep+dive+into+Coroutines+on+JVM.pdf](https://resources.jetbrains.com/storage/products/kotlinconf2017/slides/2017+KotlinConf+-+Deep+dive+into+Coroutines+on+JVM.pdf)

코틀린 동시성 프로그래밍 - 에이콘













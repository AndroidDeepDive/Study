## 코루틴?
### 루틴(routine)
  - 컴퓨터 프로그램의 일부로서, 특정한 일을 실행하기 위한 일련의 명령.

### 코루틴(coroutine)
  - 여러개의 루틴들이 협력(co)한다는 의미.

### suspend
   - 실행 중 코루틴을 차단하지 않고 정지시킴.
   - 모든 로컬 변수를 저장함.

### resume
   - 정지된 위치부터 정지된 코루틴을 계속 실행.
  
### dispatchers - 코루틴 실행 위치를 지정.
   - `Dispatchers.Main` : Android UI 쓰레드
   - `Dispatchers.IO` : 입출력 쓰레드
   - `Dispatchers.Default` : 기본 쓰레드

### launch - 결과를 반환하지 않음, 실행 후 삭제되는 작업
### async - await 정지함수로 결과를 반환하는 작업

### runBlocking
 코루틴을 실행하고 완료되기 전까지 현재 쓰레드를 블로킹.

### Flow
 비동기식으로 계산할 수 있는 데이터 스트림

  - emit : 수동으로 데이터 스트림 Flow를 만듬
  - map : 데이터를 변환 가능함
  - collect : 스트림의 모든 값을 가져옴
  - flowOn : Flow의 Context 변경함
  - StateFlow : 식별 가능한 변경 가능 상태를 유지(항상 활성 상태)

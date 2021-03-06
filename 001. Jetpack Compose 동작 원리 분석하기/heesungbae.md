# Compose 동작원리 정리

## Compose 란?

- 선언형 UI 도구 키트이다
- 기존에 뷰를 업데이트 할 때 수동적으로 조작(여러 위치에서 데이터 변경을 통한 렌더링)하면 오류가 발생할 가능성이 높습니다
- Compose는 필요한 지점에 대해서만 뷰를 다시 그려주는 방식으로 동작합니다
- @Composable 어노테이션을 사용한 함수를 사용합니다

```kotlin
@Composable
fun Greeting(name: String) {
	Text("Hello $name")
}
```

- UI를 구성하는 것이 아닌 화면 상태를 설명하는 것이므로 아무것도 반환할 필요가 없습니다ㅣ

## Compose 사용 주의사항

- 최대한 재사용성이 높게 설계되어야합니다
- 뷰 모델과 비지니스 모델 분리가 필요합니다 → 지속적으로 재사용하는데 ViewModel과 같은 비지니스 모델이 수행되면 무거워집니다 → 매개변수나 람다를 이용합니다
- 각 함수는 동립적으로 설계가 되어아합니다 호출 순서가 보장되지 않아 동시에 실행가능하기 때문입니다
- Composable 매개변수가 변경되면 다시 그립니다

## Compose의 상태

- remember  예약어를 통해 메모리에 단일 객체를 저장 가능합니다 → 해당 상위 컴포넌트가 재구성되기 전까지 상태를 가지고 있습니다
- Stateless 컴포저블은 내부에 상태 값을 가지고 있지 않는 컴포저블 입니다 → 외부를 통해서 매개변수나 람다를 사용하여 상태를 받아 사용합니다
- Stateful 컴포저블은 내부에 상태 값을 가지고 있는 컴포저블 입니다 → State<T> 관찰가능한 상태를 내장되어 있어 사용을 권장합니다 → 런타임시 통합됩니다
- 이전 상태를 복원하기 위해서는 rememberSaveable 예약어를 사용합니다
- Bundle에 데이터가 자동으로 저장됩니다

## Compose 정리

- 뷰를 제작함에 있어서 선언형 프로그래밍은 좋은 효율을 낼 수 있을 것 같다 → Flutter나 SwiftUI를 사용해보면 뷰 제작 속도가 얼마나 다른지 알 수 있습니다(개개인의 차이가 있을 수 있음)
- 요즘 제일 많이 사용하는 MVVM 아키텍처도 적용 가능하지만 이게 적합한지는 잘 모르겠습니다 → MVI 같은 아키텍처가 오히려 더 잘 맞을 수 있다고 생각합니다

# MVI (Part 3)

## MVI Library
MVI를 라이브러리 형태로 제공하는 기존 라이브러리를 살펴본다.  

#### _[2021-MVI-Library](https://appmattus.medium.com/top-android-mvi-libraries-in-2021-de1afe890f27)_

(mvi 예제 작성하면서 분석)

위 포스팅에서는 현존하는 라이브러리에 대해서 MVI 주요 접근 방식을 얼마나 지원하는지 그리고 어떤 방식으로 지원하는지에 대해서 분석하고 있다.

## Hello, MVI
아래의 조건을 충족하는 예제를 작성해보자.
- Intent와 Model을 이어주는 요소로 AAC ViewModel을 사용한다.
- Intent에서 시작된 데이터 흐름은 Redux에 포함되는 개념인 Action, Reduce를 거쳐서 최종적으로 State 형태로 View로 전달된다.
- RxJava(Kotlin)을 사용하지 않고 Coroutines Flow와 Channel을 주요 기술로 사용해 작성해본다.

_https://github.com/peterchoee/android-mvi_

(예제 작성중)

## Conclusion
- MVP, MVVM을 처음 접한 그 시절이 떠오른다.
- 모든 아키텍처, 디자인패턴이 그렇겠지만 애플리케이션 전반에 걸쳐 얼마나 통일성있게 작성되었는지가 더 중요한 것 같다.
  - 그런 부분에서는 함께하는 동료들이 얼마나 MVI를 이해하고 있는지 그리고 공통된 목표를 공유하고 있는지가 중요할 것 같다.
  - 프로덕트 레벨에서 MVP를 MVVM으로 마이그레이션 할때와 같이 기존 프로젝트를 MVI로 변경한다고 했을때 이전과 같이 풍부한 레퍼런스를 확보하진 못할 것 같다.
- 실제 예제를 작성해본다면 뭔가 심경의 변화가 있을지도 모르겠다.

## References
- https://amsterdamstandard.com/en/post/modern-android-architecture-with-mvi-design-pattern
- https://appmattus.medium.com/top-android-mvi-libraries-in-2021-de1afe890f27
- https://github.com/LeeOhHyung/android-mvi
- https://quickbirdstudios.com/blog/android-mvi-kotlin-coroutines-flow/
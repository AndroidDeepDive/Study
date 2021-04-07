# Jetpack Compose Part 3 - Retrospect

**Author in [AndroidDeepDive Study](https://github.com/AndroidDeepDive/Study)**
- 김남훈 @Naver
- 배희성 @RocketPunch
- 송시영 @SmartStudy
- 이기정 @BankSalad

### Jetpack Compose의 Coupling과 Cohesion

비단 Android 애플리케이션뿐만 아니라, 소프트웨어를 잘 개발하기 위해 지켜야할 원칙 중 하나는 **관심사의 분리(Separation of concerns)** 라는 개념이다.

흔히 복잡성을 극복하기 위해 여러가지 디자인 패턴을 적용하여 View와 Controller를 분리하기 위해 애쓰는 사람들이 많은 것도 결국 프로덕트의 복잡성을 (완전히 없앨 수는 있을까?) 최소화하기 위해서이다.

Compose 또한 이 복잡성의 극복을 위해 나온 도구라고 봐도 무방할 것이다.

따라서 Compose를 적용하면서 계속 머릿속에 캐시해두어야할 개념은 **Coupling** 과 **Cohesion** 이다.

흔히들 커플링이 심하다라고 표현하긴 하는데, 굳이 한글로 번역하자면 각각, 어떠한 컴포넌트들간의 **결속** 과 분리된 각 모듈 내부의 **응집** 을 뜻한다고 볼 수 있다.

![](https://drive.google.com/uc?export=view&id=18L_Hb3HB5wIIBS-lD1pu3uuA2FsCxvoJ)

> **출처** [How Cohesion and Coupling Correlate](https://blog.ttulka.com/how-cohesion-and-coupling-correlate)

위의 세 그림을 아래 개념을 뜻한다.

- A : Low cohesion, tight coupling
- B : High cohesion, tight coupling
- C : High cohesion, loose coupling

결국 Compose를 잘 쓴다는 것은 UI/UX 입장에서 뷰를 잘 분리하여 재사용을 통해 우리가 개발하려는 프로덕트가 C의 구조가 되게끔 작성하는 것이다.

### Compose 사용시 주의사항

- 최대한 재사용성이 높게 설계되어야 한다.
- 뷰 모델과 비지니스 모델 분리가 필요하다.
  - 지속적으로 재사용하는데 ViewModel과 같은 비지니스 모델이 수행되면 무거워지기 때문
  - 매개변수나 람다를 이용하자.
- 호출 순서가 보장되지 않으므로, 각 함수는 독립적으로 설계해야 한다.

### Compose의 State

**remember**
- 예약어를 통해 메모리에 단일 객체를 저장 가능하다.
- 상위 컴포넌트가 재구성되기 전까지 상태를 가지고 있다.

**Stateless**
- Satateless Composable은 말그대로 상태값을 가지고 있지 않다.
- 외부에서 매개변수나 람다를 통해 상태를 받아 사용한다.

**Stateful**
- 상태 값을 가지고 있는 Composable
- `State<T>` 객체를 통해 관찰가능한 값을 가지고 있으므로 사용이 권장된다.
- Runtime시 통합된다.
- 이전 상태를 복원하기 위해서는 `rememberSaveable` 키워드를 사용한다.
- `Bundle`에 데이터가 자동으로 저장된다.

#### Compose Samples

코드랩 뿐만 아니라 다양한 Compose 샘플들을 구글이 모아두었다.

아래 링크를 참조하자.

> [Github#Android Compose Samples](https://github.com/android/compose-samples)

현재 작업하고 있는 뷰와 제일 유사한 것들을 통해 Compose를 이해하는 것도 하나의 방법이 될 수 있다.

### 마무리하며

- 뷰를 제작함에 있어서 선언형 프로그래밍은 좋은 효율을 낼 수 있을 것 같다
  - Flutter나 SwiftUI를 사용해보면 뷰 제작 속도가 얼마나 다른지 알 수 있다. (개개인의 차이가 있을 수 있음)

- 요즘 제일 많이 사용하는 MVVM 아키텍처도 적용 가능하지만 이게 적합한지는 잘 모르겠다.
  - MVI 같은 아키텍처가 오히려 더 잘 맞을 수 있다고 생각한다.

- 안드로이드에서 전혀 생각지도 못한 선언형 프로그래밍 방식을 도입할 수 있던 것이 신선하다고 생각한다.
  - 선언형 프로그래밍도 결국 유행하지않을까?
  - 기존에 React나 Flutter를 통해 선언형 프로그래밍을 쉽게 접했던 개발자들은 더 쉽게 안드로이드 개발을 도전해보지 않을까 싶다. 
  - 오히려 반대로 기존에 XML로 레이아웃 코드를 짜던 개발자들은 생소한 방식이라 적응하는데 시간이 꽤나 걸릴 것이다.

- 다만, 아직도 alpha버전이라 바뀔 수 있는 것이 너무나도 많은 상황이다.
  - 앞으로 좋은 예시들이 나오고, 좀 더 기존 안드로이드 개발자들이 익숙해 할만한 컴포넌트로 제공이 되면, 좀 더 많은 개발자들이 선언형 프로그래밍으로 넘어갈 수 있지 않을까 싶다.
- 당장 기존에 있던 코드와 공존하면서 쓰기에는 여간 불편할것 같다.
  - Flutter의 경우 선언형 프로그래밍 방식으로 렌더링을 지원하기 때문에 처음부터 개발을 선언형으로 하게되니, 굳이 이럴바에는 Flutter로 서비스를 따로 구현하는 게 낫지 않을까라는 생각이 들었다.


## References

### Members of Study
- https://namhoon.kim/2021/03/14/android_deep_dive/001/
- https://namhoon.kim/2021/03/21/android_deep_dive/002/
- https://sysys.medium.com/jetpack-compose-%EC%B2%B4%ED%97%98%EA%B8%B0-8f90aff89f47
- https://soda1127.github.io/introduce-jetpack-compose/

### Official
- [Android Developers#Jetpack Compose](https://developer.android.com/jetpack/compose/)
- [Android Developers#Compose 이해](https://developer.android.com/jetpack/compose/mental-model?hl=ko)
- [Android Developers#Compose Cource](https://developer.android.com/courses/pathways/compose)
- [Android#Compose Samples](https://github.com/android/compose-samples)

### Etc
- [Understanding Jetpack Compose - part 1 of 2](https://medium.com/androiddevelopers/understanding-jetpack-compose-part-1-of-2-ca316fe39050)
- [Understanding Jetpack Compose - part 2 of 2](https://medium.com/androiddevelopers/under-the-hood-of-jetpack-compose-part-2-of-2-37b2c20c6cdd)
- [foso.github.io](https://foso.github.io/Jetpack-Compose-Playground/)

# Kotlin Symbol Processing API 

## Part2 Kotlin Symbol Processing API

### KSP

> Kotlin Symbol Processing (KSP) is an API that you can use to develop lightweight compiler plugins. KSP provides a simplified compiler plugin API that leverages the power of Kotlin while keeping the learning curve at a minimum. Compared to KAPT, annotation processors that use KSP can run up to 2x faster.

KSP는 경량 컴파일러 플러그인을 개발하는 데 사용할 수 있는 API이다. KSP는 최소한의 러닝커브로 Kotlin의 기능을 활용하는 단순화 된 컴파일러 플러그인 API 를 제공한다. KAPT에 비해 KSP를 사용하는 Annotation Processor는 최대 2배 빠르게 실행할 수 있다.

KSP API는 코틀린의 특성 - 확장 함수, 로컬 함수 같은 기능을 이해한다. 또한 KSP는 타입을 명시적으로 다루고, 기본적인 타입 검사와 동등성 검사를 지원한다.

API는 코틀린 문법에 따라 symbol 수준에서 코틀린 프로그램 구조를 모델링 한다. KSP 기반 플러그인이 소스 프로그램을 처리할 때 클래스, 클래스 멤버, 함수 및 관련 매개 변수와 같은 구성은 프로세서에서 쉽게 접근할 수 있지만, if 블록 및 for 루프는 그렇지 않다.

개념적으로 KSP는 Kotlin 리플렉션의 KType과 유사하다. API 를 사용하면 프로세서가 클래스 선언에서 특정 타입 인자가 있는 해당 타입, 또는 그 반대로 탐색할 수 있다.

KSP를 생각하는 또 다른 방법은 코틀린 프로그램의 전처리기 프레임워크이다. 만약 KSP 기반 플러그인을 symbol processors 혹은 단순히 processors라고 한다면, 컴파일 타임에서 데이터 흐름은 다음과 같습니다.

1. Processor는 소스 프로그램과 리소스를 읽고 분석
2. Processor는 코드 또는 다른 형태의 출력을 생성
3. Kotlin 컴파일러는 생성된 코드와 함께 소스 프로그램을 컴파일함  

Full-Fledged 컴파일러 플러그인과 달리, Processor는 코드를 수정할 수 없다. 언어 의미를 변경하는 컴파일러 플러그인은 때때로 매우 혼란스러울 수 있는데, KSP는 소스 프로그램을 읽기 전용으로 처리하여 이를 방지한다.

### Why KSP

컴파일러 플러그인은 코드 작성 방법을 크게 향상시킬 수 있는 강력한 메타프로그래밍 도구이다. 컴파일러 플러그인은 컴파일러를 라이브러리로 직접 호출하여 입력 프로그램을 분석하고 편집한다. 이러한 플러그인은 다양한 용도로 쓰일 수 있는 산출물을 생성한다. 예를 들어, bolierplate 코드를 생성할 수 있고 Parcelble과 같은 특별히 마크된 프로그램 요소에 대한 전체 구현을 생성할 수도 있다. 플러그인은 다양한 요옫로 사용되며 언어로 직접 제공되지 않는 기능을 구현하고 미세 조정하는 데 사용할 수도 있다. 

컴파일러 플러그인은 강력하지만, 역시 대가는 따른다. 가장 간단한 플러그인을 작성하려고만 해도, 컴파일러 배경 지식이 있어야 하며 특정 컴파일러의 구현 세부 사항에 대해 어느 정도 익숙해져야 한다. 또 다른 문제는 플러그인이 특정 컴파일러 버젼과 밀접하게 연관되어 있다는 점이다. 즉 최신 버젼의 컴파일러를 지원할 때 마다 플러그인을 업데이트 해야 할 수도 있다. 

KSP는 컴파일러 변경 사항을 숨기도록 설게되어 KSP를 사용하는 Processor의 유지 보수가 최소화 된다. KSP는 또한 JVM에 종속되지 않도록 설계되어 향후 다른 플랫폼에 쉽게 적응할 수 있기도 하다. KSP는 빌드 시간을 최소화 시키도록 설계되었다. Glide 와 같은 일부 Processor의 경우 KSP는 KAPT와 비교할 때 컴파일 시간을 25%까지 줄인다. 

KSP는 자체적으로 컴파일러 플러그인으로 구현된다. 

### Comparison to KAPT

KAPT는 많은 양의 자바 Annotation Processor 코틀린 프로그램에서 사용할 수 있는 솔루션이다. KAPT에 비해 KSP의 주요 장점은 JVM과 관련이 없는 향상된 빌드 성능, 보다 관용적인 Kotlin API 및 Kotlin symbol을 이해하는 기능이다. 

자바 Annotation Processor를 수정하지 않고 실행하기 위해 KAPT는 코틀린 코드를 자바 Annotation Processor가 관심을 갖는 정보를 유지하는 자바 스텁으로 컴파일한다. 이러한 스텁을 만들기 위해 KAPT가 코틀린 프로그램의 모든 symol을 확인해야 한다. 스텁 생성 비용은 전체 코드 분석 1/3과 동일한 코드 생성 순서이다. 많은 Annotation Processor의 경우, 이는 Processor 자체에 소요되는 시간보다 훨씬 길다. 

KAPT와 달리, KSP Processor는 자바의 관점에서 입력 프로그램을 보지 못한다. KSP는 KAPT 처럼 javac에 위임하지 않기 때문에 JVM 특정 동작을 가정하지 않으며 잠재적으로 다른 플랫폼에서 사용할 수 있다.

### Limitation

KSP는 대부분의 일반 사용 사례에 대해 간단한 솔루션이 되려고 하지만, 다른 플러그인 솔루에 비해 몇 가지 trade-off가 있다. 다음 사항들은 KSP의 목표가 아니다.

- 소스코드의 표현 수준 정보를 검토하는 것
- 소스코드를 변경하는 것
- 자바 Annotation Processing API과 100% 호환되는 것 

### Reference

- https://github.com/google/ksp
- https://github.com/google/ksp/blob/main/docs/why-ksp.md
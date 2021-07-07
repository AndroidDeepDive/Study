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



### Reference

- https://github.com/google/ksp
- https://github.com/google/ksp/blob/main/docs/why-ksp.md
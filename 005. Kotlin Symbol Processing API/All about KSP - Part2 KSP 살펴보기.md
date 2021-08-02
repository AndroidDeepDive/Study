# All about KSP - Part2: KSP 살펴보기

## KSP란 무엇인가?

**KSP(Kotlin Symbol Processing)**은 2021년 2월 10일 구글이 발표한 코틀린에서 경량화 된 컴파일러 플러그인을 개발할 수 있는 API다. 학습곡선을 최소한으로 줄이고, 코틀린의 기능을 활용할 수 있는 단순화된 API를 제공한다. 여러 플랫폼에 호환성을 염두하고 만들어졌으며, 코틀린 1.4.30 버전 이상부터 호환된다. 

KSP는 코틀린 언어가 갖는 특징인 확장 함수, 로컬 함수 같은 기능을 이해한다. 또한 KSP는 타입을 명시적으로 다루고, 기본적인 타입 검사와 동등성 검사를 지원한다. 

API는 코틀린 문법에 따라 symbol 수준에서 코틀린 프로그램 구조를 모델링 한다. KSP 기반 플러그인이 소스 프로그램을 처리할 때 클래스, 클래스 멤버, 함수 및 관련 매개 변수와 같은 구성은 프로세서에서 쉽게 접근할 수 있기 때문에 코틀린 개발자들에게 편리하다. 개념적으로 KSP는 Kotlin 리플렉션의 KType과 유사하다. API 를 사용하면 프로세서가 클래스 선언에서 특정 타입 인자가 있는 해당 타입, 또는 그 반대로 탐색할 수 있다.

KAPT와 비교했을 때 KSP를 사용하는 [애노테이션 프로세서](https://www.charlezz.com/?p=1167)는 최대 **2배** 더 빠르게 실행할 수 있다. 자세한 내용은  [KSP Github 리포지토리](https://github.com/google/ksp)에서 오픈 소스 코드 및 문서를 확인할 수 있다. 

## KSP를 왜 사용해야 할까?

컴파일러 플러그인은 코드 작성 방법을 크게 향상시킬 수 있는 강력한 메타프로그래밍 도구이다. 컴파일러 플러그인은 컴파일러를 라이브러리로 직접 호출하여 입력 프로그램을 분석하고 편집한다. 이러한 플러그인은 다양한 용도로 쓰일 수 있는 산출물을 생성한다. 예를 들어, bolierplate 코드를 생성할 수 있고 Parcelble과 같은 특별히 마크된 프로그램 요소에 대한 전체 구현을 생성할 수도 있다. 플러그인은 다양한 용도로 사용되며 언어로 직접 제공되지 않는 기능을 구현하고 미세 조정하는 데 사용할 수도 있다. 

컴파일러 플러그인은 강력하지만, 역시 대가는 따른다. 가장 간단한 플러그인을 작성하려고만 해도, 컴파일러 배경 지식이 있어야 하며 특정 컴파일러의 구현 세부 사항에 대해 어느 정도 익숙해져야 한다. 또 다른 문제는 플러그인이 특정 컴파일러 버젼과 밀접하게 연관되어 있다는 점이다. 즉 최신 버젼의 컴파일러를 지원할 때 마다 플러그인을 업데이트 해야 할 수도 있다. 

KSP는 컴파일러 변경 사항을 숨기도록 설게되어 KSP를 사용하는 Processor의 유지 보수가 최소화 된다. KSP는 또한 JVM에 종속되지 않도록 설계되어 향후 다른 플랫폼에 쉽게 적응할 수 있기도 하다. KSP는 빌드 시간을 최소화 시키도록 설계되었다. Glide 와 같은 일부 Processor의 경우 KSP는 KAPT와 비교할 때 컴파일 시간을 25%까지 줄인다. 

#### `kotlinc` 컴파일러 플러그인과의 비교

`kotlinc` 컴파일러 플러그인의 경우 강력한 기능을 제공하는 것은 맞지만, 그만큼 컴파일러에 대한 의존성이 크기때문에 유지보수에 대한 용이성이 떨어진다. 반면 KSP는 대부분의 컴파일러 변경사항을 은닉하여 api를 통해 접근할 수 있도록 해준다. 물론 한 단계를 더 건너야하는 만큼 `kotlinc`의 모든 기능을 지원하지는 않지만, 기술 부채를 고려하였을 때 합리적인 선택이 될 것이다.

#### `kotlin.reflect`와의 비교

KSP는 `kotlin.reflect`와 유사하게 생겼지만, KSP는 타입에 대한 참조를 명시적으로 지정해주어야 한다.

#### KAPT와의 비교

앞선 포스팅에서 언급했듯 KAPT는 Kotlin 코드를 Java Annotation Processor를 수정하지 않기 위해 컴파일시 Java로 된 Stub을 생성하게 된다. Stub을 생성하는 것은 kotlinc의 분석 비용의 3분의 1이나 차지하므로, 빌드시 많은 오버헤드가 발생하게 된다. Glide를 기준으로 KSP로 전환시 컴파일타임이 25% 감소했다고 한다. KAPT와 달리 KSP는 Java 관점이 아닌 Kotlin의 관점에서 접근하며, `top-level function`과 같은 Kotlin의 고유 기능에 더 적합하다.

## KSP의 한계점

KSP는 일반적인 유즈케이스에 대한 간단한 솔루션이 되고자 한다. 다른 플러그인 솔루션에 비해 몇가지 절충점(trade-off)이 있다.

 **다음은 KSP의 목표가 아니다.**

1. 소스 코드의 표현 수준 정보를 조사하기
2. 소스 코드 수정하기
3. Java Annotation Processing API와 100% 호환하기
4. IDE와 통합하기 (현재는 IDE가 생성 된 코드를 읽지 못함)

안드로이드 스튜디오(IDE)에서도 코드를 읽지 못하기 때문에 다음의 경로를 명시해야 한다.

```
build/generated/ksp/debug/kotlin
```

build.gradle.kts 예시

```
android {
    buildTypes {
        getByName("debug") {
            sourceSets {
                getByName("main") {
                    java.srcDir(File("build/generated/ksp/debug/kotlin"))
                }
            }
        }
    }
}
```

## KSP 내부 살펴보기

![](https://github.com/google/ksp/raw/main/docs/ClassDiagram.svg)

> **참고** [KSP API definition](https://github.com/google/ksp/blob/main/api/src/main/kotlin/com/google/devtools/ksp)
> **참고** [KSP Symbol definition](https://github.com/google/ksp/blob/main/api/src/main/kotlin/com/google/devtools/ksp/symbol)

KSP 모델에 대한 딥다이브를 해보자.

KSP에서 타입에 대한 참조는 몇 가지 예외를 제외하면 명시적으로 지정하도록 되어있다.

`KSFunctionDeclaration.returnType` 혹은 `KSAnnotation.annotationType`과 같이 타입을 참조하는 경우, 타입은 항상 annotation과 modifier가 포함된 `KSReferenceElement` 기반의 `KSTypeReference`이다.

```kotlin
interface KSFunctionDeclaration : ... {
  val returnType: KSTypeReference?
  ...
}

interface KSTypeReference : KSAnnotated, KSModifierListOwner {
  val type: KSReferenceElement
}
```

`KSTypeReference`는 Kotlin의 타입 시스템의 `KSType`으로 `resolve()`할 수 있고, Kotlin 문법과 일치하는 `KSReferenceElement`를 가지고 있다.

이번엔 `KSReferenceElement`다.


```kotlin
interface KSReferenceElement : KSNode {
    val typeArguments: List<KSTypeArgument>
}
```

`KSReferenceElement`는 유용한 정보를 많이 포함하고 있는 `KSClassifierReference` 혹은 `KSCallableReference`가 될 수 있다.

```kotlin
interface KSClassifierReference : KSReferenceElement {
    val qualifier: KSClassifierReference?
    fun referencedName(): String

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitClassifierReference(this, data)
    }
}
```

예를 들어 `KSClassifierReference`는 `referencedName`라는 속성을 가지고 있으며,

```kotlin
interface KSCallableReference : KSReferenceElement {
    val receiverType: KSTypeReference?
    val functionParameters: List<KSValueParameter>
    val returnType: KSTypeReference

    override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
        return visitor.visitCallableReference(this, data)
    }
}
```

`KSCallableReference`는 `receiverType`과 `functionArguments` 그리고 `returnType`을 가지고 있다.

`KSTypeReference`에서 참조되는 타입의 선언이 필요한 경우 아래와 같은 순서로 접근한다.

```kotlin
KSTypeReference -> .resolve() -> KSType -> .declaration -> KSDeclaration
```

`resolve()`를 통해 `KSType`으로 접근하고, `declaration` 속성을 통해 `KSDeclaration` 객체를 획득한다.

### Java Annotation Processing에 대응하는 KSP 레퍼런스

기존에 Annotation processor를 작성해 본 경험이 있다면 아래의 내용을 참조하면 좋다. 내용이 방대하여 링크로 대체한다.

**참고** [Github ksp#referencs](https://github.com/google/ksp/blob/main/docs/reference.md)


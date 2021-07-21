# About KSP..

![pasted%2Bimage%2B0%2B%2834%29](https://1.bp.blogspot.com/-tl-oC11ymb0/YCLDMYK4v_I/AAAAAAAAQEM/C_qeIE53MZQTtRITP66rN83kwRIl_uEygCLcBGAsYHQ/s0/pasted%2Bimage%2B0%2B%252834%2529.png)

## 애노테이션과 애노테이션 프로세서

**애노테이션(annotation)**은 소스 코드에 추가 할 수 있는 메타데이터의 한 형태로 컴파일러가 이를 참조 할 수 있도록 한다.

**애노테이션 프로세서(annotation processor)**는 컴파일러의 플러그인의 일종으로 컴파일 타임에 애노테이션에 대한 코드베이스를 검사, 수정하거나 코드를 생성하는데 사용된다. 애노테이션 프로세서를 사용하는 대표적인 라이브러리로 Room, Dagger 등이 있다.

## kapt

코틀린 프로젝트를 컴파일 할 때는 javac가 아닌 kotlinc로 컴파일을 하기 때문에 Java 애노테이션 프로세서가 동작하지 않는다. 그렇기 때문에 코틀린에서는 애노테이션을 처리하기 위해 **kapt(kotlin annotation processing tool)**라는 것을 사용한다. 코틀린 프로젝트에서 kapt를 사용하기 위해서 build.gradle 파일에 다음과 같은 라인을 추가하면 된다.

```groovy
apply plugin: 'kotlin-kapt'
```

또한 기존 annotationProcessor 키워드를 다음과 같은 예시처럼 **kapt**로 변경해야 한다

```groovy
dependencies{
  //annotationProcessor "com.google.dagger:hilt-android-compiler:$hilt_version"
  kapt "com.google.dagger:hilt-android-compiler:$hilt_version"
}
```

## KSP란?

KSP(Kotlin Symbol Processing)는 코틀린에서 경량화 된 컴파일러 플러그인을 개발할 수 있는 API다. KSP는 코틀린 1.4.30 버전 이상부터 호환 되며, [KSP Github 리포지토리](https://github.com/google/ksp)에서 오픈 소스 코드 및 문서를 확인할 수 있다.

### Why KSP?

코틀린 코드를 컴파일 할 때 가장 큰 문제 중 하나는 코틀린 용 애노테이션 프로세싱 시스템이 없다는 점이다. 안드로이드 개발에서 Room, Dagger 등과 같은 라이브러리는 필수적이며, 이러한 라이브러리들은 kapt를 통한 자바 애노테이션 프로세서에 의존한다. 문제는 kapt가 자바 애노테이션 프로세싱을 위해 중간에 자바 Stub들을 생성한다는 점이다. Stub 생성 비용은 전체 코드 분석의 대략 1/3정도의 비용이 들어간다. 

### KSP의 특징

KSP는 코틀린 코드를 직접 파싱하기 위해 강력하면서도 간단한 API를 제공하기 때문에 빌드시간을 단축할 수 있다. 실제로 Room 라이브러리를 기준으로 벤치마킹했을 때, KSP사용시 kapt보다 약 2배 빨라지는 결과를 확인할 수 있다. Glide와 같은 일부 프로세서의 경우전체 컴파일 시간을 최대 25%까지 줄인다.

KSP는 컴파일러 변경사항을 숨기도록 설계되어 이를 사용하는 프로세서의 유지비용을 최소화 한다. 그러나 컴파일러 또는 코틀린 언어의 주요 변경 사항은 여전히 API를 사용하는 사용자에게 노출되어야 한다. 

KSP는 JVM에 의존하지 않도록 설계되어 향후 다른 플랫폼에 보다 쉽게 적용할 수 있다. 

**kotlinc 컴파일러 플러그인과의 비교**
KSP는 일반적인 사례들을 해결하기 위해 단순한 API를 제공하고, 이 기능들은 kotlinc 플러그인의 하위 집합일 뿐이다. kotlinc는 표현식과 구문을 검사하고 코드를 수정할 수도 있지만 KSP는 할 수 없다.

**리플렉션과의 비교**
KSP의 API는 kotlin.reflect와 유사하다. 이들 간의 주요 차이점은 KSP의 타입 참조를 명시적으로 확인해야 한다는 것이다. 이것이 이 둘간에 인터페이스가 공유되지 않는 이유 중 하나다.

**다음은 KSP의 목표가 아님**

- 소스코드의 표현식 수준의 정보들을 검토
- 소스코드를 수정
- Java Annotation Processing API와 100% 호환
- IDE 통합
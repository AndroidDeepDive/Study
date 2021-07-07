# Kotlin Symbol Processing API - 1 

## Part1 - Annotation Processor란 무엇인가?

### Annotation Processor

자바나 코틀린으로 개발할 때 종종 클래스나 메소드, 변수 등에 @Override, @Nullable 등의 @ 이 붙은걸 본 적이 있을 것이다. 일종의 라벨을 붙여주는 의미인데, 이참에 제대로 살펴보자.

> Annotations, a form of metadata, provide data about a program that is not part of the program itself. Annotations have no direct effect on the operation of the code they annotate.

오라클 문서에서 Annotation에 대해서 설명하는 부분을 해석해보면 어노테이션은 일종의 메타데이터의 형태이고, 프로그램 그 자체가 아닌 프로그램에 대한 정보를 제공한다. 또 Annotation이 달린 코드의 동작에 직접적인 영향을 미치지 않는다. 여기서 Annotation이란 '@' 문자 뒤에 뒤따르는 문자열을 말한다. Annotation은 다음과 같은 용도가 있는데

- compiler가 오류 혹은 경고를 찾아내는 데 사용할 수 있다.
- Annotation information을 처리하여 코드나 xml 파일등을 생성할 수 있다.
- 일부 Annotation은 runtime에 검사할 수 있다.

- Annotation은 Element를 가질 수 있는데, 이름을 지정하거나 해당 요소에 대한 값이 있을 수 있다. 또한 element가 하나이면 생략할 수도 있고 element가 아예 없다면 괄호도 생략 할 수 있다.

- 여러개의 Annotation을 갖는것을 repeating annotation이라 한다.

- Java SE 8 부터 Annotation은 Type을 지정할 때도 적용할 수 있다. 

  ```java
  myString = (@Nonnull String) strl;
  ```



### Predefined Annotation Type

Java.lang package에 자바 언어에서 제공하는 Annotation들이 있다. 몇 가지 살펴보자.

- @Deprecated : deprecated 됐음을 의미하며 더 이상 쓰지 말 것을 권장할 때 사용한다. 이 Annotation이 달린 코드를 사용하면 컴파일러는 경고를 내뱉는다.

- @SuppressWarnings : 이 Annotation은 컴파일러가 생성할 경고를 억제하도록 지시한다.

  ```java
  @SuppressWarnings("deprecation")
  void useDeprecatedMethod() {
    // deprecation warning
    // - suppressed
    objectOne.deprecatedMethod();
  }
  ```

Java.lang.annotation package에 있는 Annotation을 알아보자. 해당 패키지에는 다른 Annotation에 적용될 수 있는 Annotation들이 있으며 이를 메타 Annotation이라 한다.

- @Retention : Retention Annotation은 표기된 Annotation이 저장되는 방법을 지정한다.
  - RetentionPolicy.SOURCE : source level에서만 유지되며 Compiler에서 무시된다.
  - RetentionPolicy.CLASS : compile time에 Compiler에 의해 유지되지만, JVM에서는 무시된다.
  - RetentionPolicy.RUNTIME : JVM에 의해서 유지되므로 runtime에서 사용가능하다.
- @Documented : Annotation이 사용될 때 마다 해당 element가 Javadoc에 문서화 되어야 함을 나타낸다.
- @Target : Annotation을 적용할 수 있는 Java Elements 종류를 제한한다.
- @Inherited : super class 로부터 상속될 수 있는 Annotation 타입이다. class 선언시에만 적용.



### KAPT(Kotlin Annotation Processing Tool)

먼저 JSR 269 얘기를 해보자. JSR 269는 Annotation Processor를 위한 특별한 자바 컴파일러 API 이다. 이러한 플러그인은 "@Foo로 주석 처리 된 코드(클래스, 메서드, 필드)는 무엇인가?" 라고 컴파일러에게 물어볼 수 있고, 컴파일러는 Annotation Processing(@Foo)이 달린 요소를 가진 컬렉션을 반한한다. 이후, Processor는 이를 검사하고 Annotation이 추가된 코드와 동일한 단계에서 컴파일 될 **새로운 코드**를 생성할 수 있다.

Annotation Processing을 Java 이외의 언어에서 제공하기 위해 몇 가지 옵션이 있을 수 있는데 해당 블로그 글을 참고하자. 이 중 한 가지 옵션을 선택하여 **kapt**라고 이름을 붙이게 되었고, 이 옵션만 살펴보자.

이 옵션은 Kotlin 바이너리가 자바 소스라고 가정하는 것이다. 일반적으로 코틀린 컴파일러가 먼저 실행되고 자바 컴파일러는 코틀린 코드를 바이너리 .class 파일로 인식한다. 따라서 Annotation이 달린 자바 소스만 가져오는 대신, Processor는 Annotation이 달린 코틀린 바이너리를 가져올 수 있지만 사용 가능한 API를 통해 차이를 인식하지 못한다. 그러나 자바 컴파일러는 이를 자동으로 수행하지는 않고, 자바 컴파일러와 Annotation Processor 사이를 연결하여 바이너리 요소를 직접 찾아 javac에서 일반적으로 반환하는 소스 요소에 추가할 수 있다. 

이러한 방식의 큰 장점은 약간의 바이트코드를 생성하긴 하지만 구현이 쉽다는 점이다. 

Kotlin에서는 Annotation Processor를 위해 **kapt**라는 컴파일 플러그인을 지원한다. 간단히 말해 kapt를 통해서 Dagger나 Databinding을 코틀린 프로젝트에서 쓸 수 있는 것이다. 공식 홈페이지에서 kapt를 사용하기 위한 설정들을 살펴보자. 만약 Annotation Processor를 쓰기 위해 Android Support를 사용하고 있다면 annotationProcessor를 kapt로 전환하면 된다. 자바 클래스도 지원이 된다. 

```groovy
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'
```

이 때 kotlin-android 설정을 먼저 해줘야 kotlin-kapt를 쓸 수 있다.



### Reference

- https://docs.oracle.com/javase/tutorial/java/annotations/index.html
- https://imstudio.medium.com/kotlin-kotlin-plugin-should-be-enabled-before-kotlin-kapt-d5879f45f09d
- https://kotlinlang.org/docs/kapt.html#using-in-gradle
- https://www.charlezz.com/?p=1167
- https://blog.jetbrains.com/kotlin/2015/05/kapt-annotation-processing-for-kotlin/
- https://medium.com/@workingkills/pushing-the-limits-of-kotlin-annotation-processing-8611027b6711#id_token=eyJhbGciOiJSUzI1NiIsImtpZCI6ImI2ZjhkNTVkYTUzNGVhOTFjYjJjYjAwZTFhZjRlOGUwY2RlY2E5M2QiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJuYmYiOjE2MjU2NDU5NTcsImF1ZCI6IjIxNjI5NjAzNTgzNC1rMWs2cWUwNjBzMnRwMmEyamFtNGxqZGNtczAwc3R0Zy5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwODA0NTQ0MzA1Njg1NTUxMTcyNiIsImVtYWlsIjoidWtoeXVuOTFAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF6cCI6IjIxNjI5NjAzNTgzNC1rMWs2cWUwNjBzMnRwMmEyamFtNGxqZGNtczAwc3R0Zy5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsIm5hbWUiOiLsmrHtmIQiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EtL0FPaDE0R2dTdmExaU9VeEEtTFJHbHFhTE1uby11d1E2emhVU01SekUwaXNnamc9czk2LWMiLCJnaXZlbl9uYW1lIjoi7Jqx7ZiEIiwiaWF0IjoxNjI1NjQ2MjU3LCJleHAiOjE2MjU2NDk4NTcsImp0aSI6IjY0NTBmZGU3ZTcyZDVmZTJiZmFlMWY0YjdiYTVhYTliYmU5NTRlZjYifQ.R3LNpWB5dPc2tKzQSdN_W12Z4q1SCD7mFPecGyiShxHfohSatHbGEKgdanBEEKzCuD8Jhxg6bFRXTbliGDB6GzKpWbqommoiUbpMH6xkPDiWP_k1RETpewj9Mi4j5-UNZ9dI1-XrqUC8-cIw9qPvGYy5lJV78KIsac159EgskLb-wW8J5AAFCnt8KJyCnHYNI_H1rwd4XJbmj2rb5CMLFrJs7XxUChG60J59ye_lPgZCp0FPUWSAQzYuroGXHedG0F_9H4y4m6JmH_Y9UnEXXgdC20QOVL9t-pWKHFvmIqYHZPMTyP66Mnay_w8TSf5-jjV8jLaH90_7OZyYkW2FXQ
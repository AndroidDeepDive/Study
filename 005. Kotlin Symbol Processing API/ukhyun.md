# Kotlin Symbol Processing API - 1 

## Part1 - Annotation Processor란 무엇인가?

### Annotation Processor

자바나 코틀린으로 개발할 때 종종 클래스나 메소드, 변수 등에 @Override, @Nullable 등의 @ s ymbol이 붙은걸 본 적이 있다. 일종의 라벨을 붙여주는 의미인데, 이참에 제대로 알아보기로 하자.

> Annotations, a form of metadata, provide data about a program that is not part of the program itself. Annotations have no direct effect on the operation of the code they annotate.

오라클 문서에서 Annotation에 대해서 설명하는 부분을 해석해보면 어노테이션은 일종의 메타데이터의 형태이고, 프로그램 그 자체가 아닌 프로그램에 대한 정보를 제공한다. 또 Annotation이 달린 코드의 동작에 직접적인 영향을 미치지 않는다. Annotation은 다음과 같은 용도가 있는데

> - information for the compiler - Annotations can be used by the compiler to detect errors or suppress warnings.
> - Compile-time and deployment-time processing - Software tools can process annotation information to generate code, XML files, and so forth.
> - Runtime processing - Some annotations are available to be examined at runtime

- compiler가 오류 혹은 경고를 찾아내는 데 사용할 수 있다.
- Annotation information을 처리하여 코드나 xml 파일등을 생성할 수 있다.
- 일부 Annotation은 runtime에 검사할 수 있다.



### KAPT 

- 코틀린에서 KAPT 사용시 단점
- 왜 Stub을 생성하는가?





### Reference

- https://docs.oracle.com/javase/tutorial/java/annotations/index.html
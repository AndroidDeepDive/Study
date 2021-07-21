## Kapt 와 KSP 살펴보기


## 어노테이션 프로세싱(Annotation Processing)

JSR 269 는 자바 컴파일러, 어노테이션 프로세스를 위한 특별한 종류의 플러그인을 위한 API를 정의한다. 이러한 플러그인은 컴파일러에게 어떠한 코드 요소(클래스, 메소드, 필드)가 @foo로 주석되어 있는지 물어볼 수 있다. 컴파일러는 주석된 요소를 나타내는 객체의 콜렉션을 반환한다. 그 후 프로세서는 콜렉션을 검사하고 주석 추가되어 있는 코드와 동일한 단계에서 컴파일 될 새로운 코드를 생성한다. 이렇게 생성된 코드는 컴파일러가 동작을 시작할 때 존재하지 않더라도 손으로 직접 작성한 코드를 사용할 수 있게 된다. 어노테이션을 통해 직접 작성한 코드 기반으로 새로운 코드가 작성될 수 있고 손쉽게 기능을 구현하고 사용할 수 있게 된다.

## 어노테이션 설정

자바 이외의 언어에서 어노테이션을 지원하기 위해서는 몇가지 옵션이 있다.

### 1. JSR 269 API 을 다시 구현

JSR 269 를 다시 구현하는것은 약간의 작업이 필요하지만 어렵지는 않다. 문제는 자바와 코틀린이 혼합된 프로젝트에서는 도움이 되지 않는다. 자바와 코틀린 모든 코드의 요소에 주석을 처리해야하며, 코틀린에서만 JSR 269 지원하는 것은 큰 이득이 아니다.

### 2. Groovy 사용

코틀린 소스로부터 자바 소스로 완벽히 변환하여 프로세스를 실행중인 자바 컴파일러에 전달하는 것은 매우 힘들다. 그러나 실제 필요한 선언만 해주면 Groovy 는 코드를 변환하고 컴파일러에 전달해준다. Groovy 코드로부터 자바 스텁(stubs) 을 생성하고 자바 컴파일러에 전달한다. 코틀린에서도 작동은 하지만, 컴파일러가 두번 실행된다. 첫번째는 스텁을 생성하고, 두번째는 생성된 코드를 완전히 컴파일 한다. 프로세스에 생성된 클래스를 참조할 수 있지만, 경우에 따라서 문제가 발생할 수 있다. 예를 들면 스텁을 생성하는 동안 우항(right-hand-side expression) 에 추론된 프로퍼티(property)/펑션(function) 타입을 생성된 코드에 사용할 때 경우에 따라 문제가 발생할 수 있다.

### 3. Kapt(Kotlin Annotation Processing) 사용

코틀린 바이너리를 자바코드라고 가정한다. 일반적으로 코틀린 컴파일러가 먼저 실행되고 자바 컴파일러는 바이너리 .class 파일로 코틀린 코드를 인식된다. 물론 소스와 바이너리의 모든 코드 요소는 자바 컴파일러 내에서 균일하게 표현됩니다. 따라서 주석 처리된 자바 소스만 가져오는 대신에 프로세서는 또한 주석처리된 코틀린 바이너리 가져올 수 있지만 사용가능한 API 를 통해서 차이를 알 수 없다. 안타깝게도 Javac 자동적으로 실행되지 않지만, 자바 컴파일러와 어노테이션 프로세서 사이를 연결하고 바이너리 요소를 찾아서 javac에서 일반적으로 반환하는 소스 요소에 추가할 수 있다. 큰 장점은 솔루션이 구현하기 쉽다. ~~하지만 몇 가지 중요한 제한 사항이 있다. 코틀린 코드는 프로세서에 의해 생성된 선언을 참조할 수 없고 소스에 주석은 바이너리를 통해 표시되지 않는다.~~

## Kapt 설정 및 사용 방법

Gradle plugin 에서 kotlin-kapt 를 활성화 할 수 있다.

    - Groovy 에서 설정
    plugins {
      id “org.jetbrains.kotlin.kapt” version “1.5.20”
    }
    
    - Kotlin 에서 설정
    plugins {
      kotlin("kapt") version "1.5.20"
    }
    
    - apply plugin 문법을 적용해서 설정
    apply plugin: 'kotlin-kapt'

그 다음에 각각의 의존성을 kapt 설정을 사용해서 dependencies 에 추가한다.

    dependencies {
      kapt 'groupId:artifactId:version'
    }
    
    dependencies {
        implementation "com.google.dagger:hilt-android:2.28-alpha"
        kapt "com.google.dagger:hilt-android-compiler:2.28-alpha"
    }

만약에 Android support 를 사용하기 위해 어노테이션 프로세서를 사용했다면, annotationProcessor 설정을 kapt 로 변경한다. 만약 프로젝트에 자바 클래스가 포함되어 있다면, kapt 가 그 또한 처리한다.

만약 androidTest 또는 test 소스를 위한 어노테이션 프로세서를 사용한다면, 각각 kaptAndroidTest 와 kaptTest 이름으로 kapt 설정을 한다. kaptAndroidTest 와 kaptTest 는 kapt 를 확장한 것이며, kapt 의존성을 제공할 수 있고 프로덕션 소스와 테스트 모두 어노테이션 프로세서를 사용할 수 있게 한다.

arguments{} 블록을 사용해서 어노테이션 프로세서에 인자를 전달할 수 있다.

    kapt {
      arguments {
        arg("key", "value")
      }
    }

kapt 어노테이션 프로세싱 작업은 gradle 안에서 기본적으로 캐시된다. 그러나 어노테이션 프로세서가 입력에서 출력으로 변환이 필요 없는 임의의 코드 실행하고, gradle 등에서 추적하지 않는 파일에 접근하고 수정할 수 있다. 어노테이션 프로세서가 적절한 캐시를 빌드하지 못하는 경우, 캐시를 하지 않도록 설정할 수 있다.

    kapt {
      useBuildCache = false
    }

kapt 로 늘어나는 빌드 시간을 개선하기 위해 gradle compile avoidance 를 할 수 있다. compile avoidance 가 활성화 되어 있다면, gradle 은 프로젝트를 리빌딩할 때 어노테이션 프로세싱을 건너 뛸 수 있다. 
특히 다음과 같은 때 어노테이션 프로세싱은 건너 뛴다.

* 프로젝트의 소스 파일이 변경되지 않았을 때

* 종속성의 변경이 ABI 와 호환되는 경우
ex) 메소드 바디내의 변경만 있는 경우

그러나 어노테이션 프로세서는 컴파일 클래스패스 안에서 발견되는 어떠한 변경에 어노테이션 프로세싱 작업이 수행되므로 compile avoidance 사용할 수 없다.
compile avoidance 와 함께 kapt 를 실행하려면 아래와 같이 설정한다.

* 어노테이션 프로세서 의존성에 kapt* 를 추가한다.

컴파일 클래스패스에서 어노테이션 프로세서의 탐색을 비활성화 하기 위해서는 gradle.properties 파일에 아래와 같은 설정을 추가한다.

    kapt.include.compile.classpath=false

kapt 는 incremental compilation 을 기본적으로 활성화된다. incremental compilation 는 소스 파일과 빌드 사이에 변경을 추적하고, 오직 변경에 영향을 받는 파일만 컴파일하도록 한다.

incremental compilation 비활성화 하기 위해서는 gradle.properties 파일에 아래와 같이 설정을 추가한다.

    kapt.incremental.apt=false

kapt 에서 어노테이션 프로세서 실행을 위해 자바 컴파일러를 사용할 수 있다. javac 에 임의의 옵션을 전달하는 방법은 아래와 같이 설정한다.

    kapt {
      javacOptions {
        // Increase the max count of errors from annotation processors.
        // Default is 100.         
        option("-Xmaxerrs", 500)
      }
    }

AutoFactory 와 같은 일부 어노테이션 프로세서는 선언된 서명에 정확한 타입에 의존한다. 기본적으로 kapt 는 알수없는 모든 타입을 NonExistentClass 로 변경한다. 이를 막기 위해 스텁에서 추론 타입 에러를 활성화할 수 있다. build.gradle 파일에 아래와 같이 설정할 수 있다.

    kapt { 
      correctErrorTypes = true
    }

kapt 는 코틀린 소스를 생성할 수 있다. 생성된 코틀린 소스파일을 processingEnv.options["kapt.kotlin.generated"] 에 지정된 경로를 작성하면, 이 파일들은 메인소스와 함께 컴파일된다.

기본적으로 kapt 는 모든 어노테이션 프로세서 실행하고 javac 에 의해 어노테이션 프로세서를 비활성화 한다. 그러나 javac 의 어노테이션 프로세서 작업이 필요할 수도 있다. gradle 빌드 파일에서 keepJavacAnnotationProcessors 옵션을 사용한다.

    kapt {
        keepJavacAnnotationProcessors = true
    }







[https://kotlinlang.org/docs/kapt.html](https://kotlinlang.org/docs/kapt.html)

[https://blog.jetbrains.com/kotlin/2015/05/kapt-annotation-processing-for-kotlin/](https://blog.jetbrains.com/kotlin/2015/05/kapt-annotation-processing-for-kotlin/)

[https://github.com/google/ksp/blob/main/docs](https://github.com/google/ksp/blob/main/docs)


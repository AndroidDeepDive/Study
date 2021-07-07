## KSP (Kotlin Symbol Processing)

코틀린 심볼 프로세싱(KSP) 는 경량 컴파일러 플러그인을 개발하는데 사용할 수 있는 API 이다. KSP 는 학습 곡선을 최소한으로 유지하면서 코틀린의 기능을 활용하는 단순화 된 컴파일러 플러그인 API 를 제공한다. KAPT 에 비해 KSP 를 사용하는 주석 프로세서는 최대 2배 더 빠르게 실행할 수 있다.



## KSP 왜 사용해야 하는가?

### KSP 가 해결하는 문제

컴파일러 플러그인은 코드 작성 방법을 크게 향상시킬수 있는 강력한 메타프로그래밍툴이다. 컴파일러 플러그인은 입력 프로그램을 분석하고 편집할 수 있는 라이브러리로서 컴파일러를 직접 호출할 수 있다. 이러한 플러그인은 또한 다양한 용도로 출력을 생성 할 수 있다. 예를 들어 보일러플레이트 코드를 생성하고 Parcelable 과 같이 특별히 표시된 프로그램 요소에 대한 전체 구현을 생성 할 수도 있다. 플러그인은 다양한 용도를 가지고 있으며 언어로 직접 제공되지 않는 기능을 구현하고 미세 조정하는데 사용한다.
컴파일러 플러그인은 강력하지만, 그 강력함은 큰 비용에 따른다. 간단한 플러그인을 작성하기 위해 컴파일러에 대한 배경 지식과 특정 컴파일러의 구현 세부 사항에 대해 어느 정도 익숙하게 알아야 한다. 또 다른 이슈로 플러그인 특정 컴파일러의 버전과 밀접하게 연결되어 있다. 즉, 새로운 버전의 컴파일러를 지원할 때마다 플러그인을 업데이트해야 할 수도 있다.

### 경량 컴파일러 플러그인

KSP 는 경량 컴파일러 플러그인을 더 쉽게 만들 수 있다.
KSP 컴파일러 변경 사항을 숨기도록 설계되어 사용하는 프로세서의 유지 비용을 최소화한다. KSP 는 JVM 에 연결되어 있지 않도록 설계되어 미래에 다른 플랫폼에도 좀 더 쉽게 적용할 수 있다. 또한 KSP 는 빌드 시간을 최소화하도록 설계되어 있다. Glide 와 같은 일부 프로세서의 경우, KAPT 와 비교하여 KSP 는 전체 컴파일 시간이 최대 25%까지 줄었다
KSP 는 컴파일러 플러그인으로서 자체적으로 구현된다. 구글 메이븐 저장소에는 프로젝트를 직접 빌드하지 않고 다운로드하여 사용할 수 있는 미리 빌드되어 있는 패키지(prebuild packages)가 있다.

### kotlinc 컴파일러와 비교

kotlinc 컴파일러 플러그인은 컴파일러로 부터 거의 모든것에 접근할 수 있고 최대의 성능과 유연성을 제공한다. 반면에 이러한 플러그인은 잠재적으로 컴파일러의 모든것에 의존하며, 컴파일러의 변화에 민감하며 유지보수가 빈번하게 일어날 수 있다. 이러한 플러그인은 또한 kotlinc 구현에 대해 깊은 이해가 필요하며 학습 곡선이 가파를 수 있다.
KSP 는 잘 정의된 API 를 통해 대부분 컴파일러의 변경을 숨기는데 목표로 하지만, 컴파일러 또는 코틀린 언어의 주요 변경은 여전히 사용자에게 노출되어야 할 수도 있다.
KSP 단순성과 힘을 교환하는 API 를 제공하여 일반적인 사용 사례를 충족시키려고 시도한다. 이 기능은 일반 kotlinc 플러그인의 엄격한 하위 집합이다. 예를 들어 kotlinc 식과 문법을 검사하고 코드를 수정할 수 있지만, KSP 는 할 수 없다.
kotlinc 플러그인은 작성하는 것은 매우 재미있을 수 있지만, 많이 시간이 걸린다. kotlinc 구현을 배울 수 있는 상태가 아니거나 소스 코드를 수정하거나 표현식을 읽을 필요가 없다면 KSP 가 적합하다.

### 리플렉션과 비교

KSP 의 API 는 kotlin.reflect 와 유사하다. 둘 사이의 큰 차이점은 KSP 에서 타입 참조는 명시적으로 해결해야 한다. 이것은 인터페이스가 공유되지 않는 이유 중 하나이다.

### KAPT 와 비교

KAPT 는 코틀린 프로그램에서 많은 양의 자바 어노테이션 프로세서를 작동하도록 하는 놀라운 솔루션이다. KAPT 는 뛰어 넘는 KSP 의 가장 큰 장점은 빌드 성능의 향상, 관용적인 코틀린 API, 코틀린 전용 심볼을 이해하는 기능이다.
자바 어노테이션 프로세서를 수정하지 않고 실행하기 위해서, KAPT 는 코틀린 코드를 자바 어노테이션 프로세서가 관심을 가지는 정보를 유지하는 자바 스텁으로 컴파일 한다. 스텁을 생성하기 위해, KAPT 는 코틀린 프로그램 내에 모든 심볼을 확인해야 한다. 스텁 생성 비용은 전체 kotlinc 분석의 1/3 이며 동일한 kotlinc 코드 생성의 동일한 순서이다. 많은 어노테이션 프로세서에서, 프로세서 자체에서 소요되는 시간보다 훨씬 길다. 예를 들어 Glide 는 매우 제한적인 클래스의 사전 정의된 어노테이션를 살펴보고 코드 생성이 꽤 빠르다. 거의 대부분의 빌드 오버헤드는 스텁 생성 단계에 있다. KSP 로 전환하면 컴파일러에서 소요되는 시간이 25% 까지 즉시 줄일 수 있다.
성능 평가를 위해, KSP 에서 Glide 의 간소화 버전을 구현하여 [Tachiyomi](https://github.com/inorichi/tachiyomi) 프로젝트로부터 코드를 생성했다. 프로젝트의 총 코틀린 컴파일 시간은 테스트 기기에서 22.55초 였지만, KAPT  에서 코드를 생성하는 시간은 8.67초 걸렸고, KSP 구현에서 코드를 생성하는데는 1.15초 걸렸다.
KAPT 와 달리, KSP 의 프로세서는 자바의 관점에서 입력 프로그램을 보지 못한다. API 는 코틀린에 더 자연스럽다. 특히 탑레벨 평션과 같은 코틀린 관련 기능과 같은 경우 더욱 그렇다. KSP 는 KAPT 처럼 javac 에 위임하지 않기 때문에 JVM 특정 동작을 가정하지 않으며 잠재적으로 다른 플랫폼에서 사용할 수 있다.

### 제한 사항

KSP 대부분의 일반적인 사용 사례에서 간단한 솔루션이며, 다른 플러그인 솔루션에 비교해 몇가지 절충안을 만들었다. 아래는 KSP 의 목표가 아니다.

* 소스 코드의 표현식 수준의 정보를 검토

* 소스 코드의 수정

* 자바 어노테이션 프로세싱 API 와 100% 호환성



## KSP 설정 및 사용 방법

### 프로세서의 생성

* 빈 gradle 프로젝트를 생성

* 다른 프로젝트 모듈에서 사용할 수 있도록 루트 프로젝트 코틀린 플러그인의 버전을 1.5.20 으로 지정한다.

    plugins {
        kotlin("jvm") version "1.5.20" apply false
    }
    
    buildscript {
        dependencies {
            classpath(kotlin("gradle-plugin", version = "1.5.20"))
        }
    }

* 프로세서 호스팅을 위한 모듈 추가

* 모듈의 build.gradle.kts 파일은 아래와 같다.
Gradle 이 플러그인을 찾을수 있도록 google()을 레포지토리에 추가한다.
코틀린 플러그인을 적용한다.
KSP API 를 dependencies 에 추가한다.

    plugins {
        kotlin("jvm")
    }
  
    repositories {
        mavenCentral()
        google()
    }
  
    dependencies {
        implementation("com.google.devtools.ksp:symbol-processing-api:1.5.20-1.0.0-beta04")
    }

* [com.google.devtools.ksp.processing.SymbolProcessor] 과 [com.google.devtools.ksp.processing.SymbolProcessorProvider] 구현이 필요하다. SymbolProcessorProvider 의 구현은 구현한SymbolProcessor 를 인스턴스화하는 서비스로 로드된다.
- [SymbolProcessorProvider.create()] 구현하여 SymbolProcessor 만듭니다. 프로세서 필요한 종속성은 SymbolProcessorProvider.create() 매개변수를 통해 전달한다.
- 메인 로직은 [SymbolProcessor.process()] 메소드에 있어야 한다.
- 완전한 어노테이션의 이름이 주어지면 resolver.getSymbolsWithAnnotation() 을 사용하여 처리할 심볼을 가져온다.
- KSP 에 일반적인 사용 사례는 심볼 작업을 위해 customized visitor (interface com.google.devtools.ksp.symbol.KSVisitor)를 구현한다.
com.google.devtools.ksp.symbol.KSDefaultVisitor 에서 간단한 visitor 템플릿을 살펴 볼 수 있다.
- SymbolProcessorProvider 와 SymbolProcessor 인터페이스의 간단한 구현은 샘플 프로젝트의 아래 파일에서 확인할 수 있다.
 > src/main/kotlin/BuilderProcessor.kt
 > src/main/kotlin/TestProcessor.kt 
- processor 의 작성후에 resources/META-INF/services/com.google.devtools.ksp.processing.SymbolProcessorProvider 에 정규화된 이름을 포함하여 프로세서 프로바이더를 패키지에 등록한다.


### 프로젝트에서 프로세서 사용 

 1. Kotlin DSL 사용하여 설정

* 프로세스를 사용해 볼 워크로드가 포함된 다른 모듈을 만든다.

* 프로젝트에 settings.gradle.kts 에 google() 을 KSP 플러그인에 repositories 에 추가한다.

    pluginManagement {
        repositories {
           gradlePluginPortal()
           google()
        }
    }

* 새로운 모듈의 build.gradle.kts 다음과 같이 설정한다.
- 지정된 버전과 함께 com.google.devtools.ksp 플러그인을 적용한다.
- 의존성 목록에 ksp(<your processor>) 를 추가한다.

*  ./gradlew build 를 실행한다. build/generated/source/ksp 아래에 생성된 코드를 찾을 수 있다.

* KSP 플러그인을 워크로드에 적용하기 위한 build.gradle.kts 샘플은 아래에서 볼 수 있다.

    plugins {
        id("com.google.devtools.ksp") version "1.5.20-1.0.0-beta04"
        kotlin("jvm") 
    }
    
    version = "1.0-SNAPSHOT"
    
    repositories {
        mavenCentral()
        google()
    }
    
    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(project(":test-processor"))
        ksp(project(":test-processor"))
    }

2. Groovy사용하여 설정

* 프로젝트에 settings.gradle 에 google() 을 KSP 플러그인에 repositories 에 추가한다.

    pluginManagement {
      repositories {
          gradlePluginPortal()
          google()
      }
    }

* 프로젝트 build.gradle 파일에서 KSP 플러그인을 포함하는 플러그인 블록을 추가한다.

    plugins {
      id "com.google.devtools.ksp" version "1.5.20-1.0.0-beta04"
    }

* 모듈 build.gradle 에서 아래와 같이 추가한다.
- com.google.devtools.ksp 플러그인은 적용한다.
- 의존성 목록에 ksp(<your processor>) 를 추가한다.

    apply plugin: 'com.google.devtools.ksp'

    
    
    dependencies {
        implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
        implementation project(":test-processor")
        ksp project(":test-processor")
    }

### 프로세스에 옵션 전달

SymbolProcessorEnvironment.options 에서 프로세서 옵션은 gradle 빌드 스크립트에 지정할 수 있다.

    ksp {
        arg("option1", "value1")
        arg("option2", "value2")
        ...
      }

### IDE 에서 생성된 코드 인식

기본적으로 인텔리제이 또는 다른 IDE 는 생성된 코드에 관해 알 수 없다. 따라서 이생성된 심볼에 대한 참조는 확인할 수 없음으로 표시된다. 예를 들어 인텔리제이는 생성된 심볼에 대해 추론하려면 아래와 같이 다음 경로를 생성된 소스 루트로 표시해야 한다.

    build/generated/ksp/main/kotlin/
    build/generated/ksp/main/java/

IDE 에서 지원하는 경우, 아래와 같은 리소스 디렉토리가 있다.

    build/generated/ksp/main/resources















[https://kotlinlang.org/docs/kapt.html](https://kotlinlang.org/docs/kapt.html)

[https://blog.jetbrains.com/kotlin/2015/05/kapt-annotation-processing-for-kotlin/](https://blog.jetbrains.com/kotlin/2015/05/kapt-annotation-processing-for-kotlin/)

[https://github.com/google/ksp/blob/main/docs](https://github.com/google/ksp/blob/main/docs)
[**KSP uses a first experience**
*Recently, Android officially released the Alpha version of Kotlin Symbol Processing (KSP). Announcing Kotlin Symbol…*programmersought.com](https://programmersought.com/article/73988239490/)
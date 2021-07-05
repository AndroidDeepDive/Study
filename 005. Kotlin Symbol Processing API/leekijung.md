# Part 2 - Kotlin Symbol Processing API

## Introduce Kotlin Symbol Processing

**KSP(Kotlin Symbol Processing)**은 2021년 2월 10일 알파버전으로 구글에서 발표된 기술이다.

KSP는  **[KAPT](https://kotlinlang.org/docs/reference/kapt.html)**와 비슷한 기능을 제공하지만, 속도가 최대 2배 더 빠르고, Kotlin 컴파일러에 직접 액세스할 수 있고, 여러 플랫폼에 호환성을 염두하고 만들어진 도구이다.

KSP는 Kotlin 1.4.30버전 이상과 호환되며,  자세한 내용은 [KSP GitHub 저장소](http://goo.gle/ksp)에서 확인할 수 있다.

KSP를 쉽게 정의하자면, Kotlin 프로그램을 동작시키기 위한 전처리 프레임워크라고 볼 수 있다. KSP가 동작하는 흐름에 대해 설명하자면 아래의 세 단계로 정리해볼 수 있다.

- 프로세서는 소스 프로그램과 리소스를 읽고 분석한다.
- 프로세서는 코드 또는 다른 어떠한 형태의 출력을 만든다.
- Kotlin 컴파일러는 생성 된 코드와 함께 소스 프로그램을 컴파일한다.



이번 글에서는 KSP를 사용해야 하는 이유, 사용했을 때 비교정도만 간단히 해보는 것을 목적으로 소개해본다.



### KSP를 써야 하는 이유

#### KSP 정의

**KSP**는 **Compiler Plugin(컴파일러 플러그인)**이다. 컴파일러 플러그인은 코드 작성 방법을 자동화하여 생산성을 높일 수 있는 메타 프로그래밍 도구이다.

컴파일러 플러그인은 컴파일러를 라이브러리로 직접 호출하여 입력할 프로그램을 분석하고 편집할 수 있다. 그 덕에 다양한 용도로 사용할 수 있다.

#### KSP 장점

##### 상용구에 대한 불필요한 작성이 필요없다.

대표적으로 안드로이드에서 사용되는 어노테이션인 `@Parcelize`를 생각해보라, 우리가 자바를 통해 구현할때는 Parcelable 인터페이스를 직접 구현하여 데이터 송수신 시 읽고 쓰는 코드를 직접 구현했다. 이후에 코틀린을 사용하게 되면서 컴파일러 플러그인을 통해 프로퍼티에 대한 읽고 쓰는 **Boilerplate(상용구)** 코드를 직접 구현할 필요가 없어졌다.

물론 달콤한 과일을 먹는데는 대가가 따르듯이, 간단한 플러그인 하나도 작성하려면 컴파일러에 대한 배경지식이 어느정도 필요하며, 특정 컴파일러에 대한 배경지식, 세부사항에 대해 이해하고 있고 잘 활용할 줄 알아야한다.

이러한 어려움을 최대한 줄이기 위해, KSP는 컴파일러에 대한 변경사항들을 숨겨 추상화했다. 쉽게 말해 API화하여 프로세서에 대한 유지보수를 줄였다는 것에 의의가 크다.

쉽게 말해, 우리가 대표적으로 사용하는 JVM계열의 코틀린에 대해서만 컴파일러 플러그인을 사용할 수 있는 것이 아닌 다른 플랫폼에도 사용이 가능하다는 것이다.

##### 빌드시간에 대한 축소

기존에 사용하던 Annotation Processing Tool과 비교하여 가장 큰 장법은 컴파일 타임이 굉장히 줄어든다는 것이다. 대표적인 안드로이드 로컬 데이터베이스 라이브러리인 [**Room**](http://d.android.com/room)을 예시로 들어보자.

Room은 KAPT(Kotlin Annotation Processing Tool)의 도움을 받아 Java단의 Annotation Processing 시스템을 사용한다. 하지만, 이를 위해 Java Stubs를 생성해야 하므로, 실행 속도가 느려질 수 밖에 없다.

KSP는 Kotlin단에서 어노테이션 프로세싱이 어떻게 동작할지 고려헀다. KSP의 가장 큰 특징은 Kotlin 코드를 직접 파싱하기위한 API를 제공하여, KAPT의 Stub생성으로 인해 빌드속도가 저하되는 문제를 크게 줄여줄 수 있다.

##### KAPT와의 비교

KAPT는 Java 어노테이션 프로세서가 코틀린 프로그램에서 동작할 수 있도록 하는 솔루션이다.
KAPT와 비교하여 KSP의 장점은 JVM과 관련이 없기 떄문에 향상된 빌드 성능을 보인다는 것이고, 관용적인 Kotlin API(후술할 KSP Feature에 정리했다) 및 Kotlin Symbol에 대해 이해할 수 있다는 것이다.

장점이자 단점으로 볼 수 있는 것은 KAPT와 달리 KSP는 Java 관점에서의 소스에 대해 인지하지 못한다. API 또한 Kotlin을 사용하는 데 특화되었다.

KSP는 KAPT처럼 javac에 위임하지 않기 때문에 다른 플랫폼에서 사용할 수 있다는 것이다.

#### KSP의 한계

KSP가 범용적으로 여러 플랫폼에 사용하기 좋은 솔루션은 맞지만, 추상적으로 만들어진 API덕에 한계 또한 있다. 아래 3가지는 KSP가 고려하는 사항이 아니다.

- 소스 코드의 표현 수준에 대해 검토하는 것
- 소스코드를 직접 접근하여 수정하는 것
- Java Annotation Processing API와 100% 호환성을 갖추는 것

KSP에서는 추가적으로 IDE에 기능적으로 통합에 대해서도 고려하고 있지만, 현재는 지원이 되고있지는 않다.



### KSP Feature

KSP API는 코틀린 프로그램을 관용적으로 처리한다. KSP는 코틀린의 특징적인 요소에 대해 인지할 수 있는데, **Extension Functions(확장 함수), Declaration-Site Variance(제네릭이 정의 된 지점), Local Functions(지역함수)**에 대해 인지할 수 있다.

또한, 타입에 대해 명시적 모델링과 **Equivalence(동등성), Assign-Compatibility(할당 호환성)**과 같은 기본 타입 검사에 대한 기능을 제공한다.



개념적으로는 Kotlin 리플렉션의 KType과 유사한 방법이다. KSP API를 사용하는 경우 프로세서가 클래스 선언 시 특정 타입의 인자가 있는 것에, 또는 그 반대의 경우에 타입을 탐색할 수 있다.



이를 통해 인자 타입을 대체하고, 변화에 대한 명시, Star-Proejction(스타 프로젝션), Nullable한 타입에 대한 표시에 대해서도 기능을 제공한다.



## KAPT  V.S. KSP in Android Project

### Let's apply this!

Room 라이브러리를 보고 비교하였을 때, KSP가 KAPT보다 대략 2배정도 빨리 동작했다.

내가 테스트 한 조건은 이렇다. 매우 간단한 프로젝트를 하나 만들었고, 해당 프로젝트는 Kotlin으로 작성돼있으며, 총 Room Database를 상속받은 1개의 클래스와, 3개의 DAO 클래스, 3개의 Entity 클래스를 구현하였다. 

App 모듈에는 다음과 같이 kotlin 플러그인으로 kapt를 설정해두었다.

```kotlin
plugins {
  ...
  id 'kotlin-kapt'
  ...
}
```

또한, 하단에 App 모듈에 들어가는 의존성을 아래와 같이 추가해두었다.

```kotlin
// Room library
implementation "androidx.room:room-runtime:2.3.0"
kapt "androidx.room:room-compiler:2.3.0"
implementation "androidx.room:room-ktx:2.3.0"
```

kapt를 사용하기 떄문에, 다음과 같이 kapt에 대한 의존성을 추가하였다. 이를 통해 Room라이브러리는 Kotlin 어노테이션을 파싱하여 Sqlite를 사용하기 위한 Java Stub파일로 바뀌게 된다.

아래는 RoomDatabase 클래스이다.

```kotlin
@Database(
  entities = [AEntity::class, BEntity::class, CEntity::class],
  version = 1,
  exportSchema = false
)

abstract class ApplicationDatabase: RoomDatabase() {

  companion object {
    const val DB_NAME = "ApplicationDataBase.db"
  }

  abstract fun ADao(): ADao

  abstract fun BDao(): BDao

  abstract fun CDao(): CDao

}
```

Kotlin 코드는 다음과 같은 Java Stub 파일로 생성되게 된다.

```java
...
  
@SuppressWarnings({"unchecked", "deprecation"})
public final class ApplicationDatabase_Impl extends ApplicationDatabase {
  private volatile ADao _aDao;

  private volatile BDao _bDao;

  private volatile CDao _cDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        ...
      }
    }
                                                                              
...                                                                              
```



### Compile Time Comparison With KAPT & KSP

이렇게 KAPT의 힘을 빌려 Generated되는 시간은 아래와 같다.

![Compile Time by KAPT](https://imgur.com/rcWvgv0.jpg)

클린빌드 시 kaptGenerateStubsDebugKotlin으로 빌드 시 발생하는 시간은 다음과 같이 16.6s정도가 소요되었다.



이번에는 KSP를 직접 프로젝트에 적용해보기로 했다.

프로젝트 단위의 build.gradle 설정에서는 아래와 같이 의존성을 하나 더 추가해준다.

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  ext.kotlin_version = "1.4.21"
  repositories {
    google()
  }
  dependencies {
    ...
    classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.5.20-1.0.0-beta04")

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle files
  }
}
```



App 모듈에서는 plugin으로 KSP를 사용하기 위한 플러그인을 추가한다.

```kotlin
plugins {
  ...
  id 'kotlin-kapt'
  id 'com.google.devtools.ksp'
  ...
}
```



Room 라이브러리에 대한 의존성은 `kapt`에 대한 키워드를 `ksp`로 교체해주면 된다.

```kotlin
// Room library
implementation "androidx.room:room-runtime:2.3.0"
//kapt "androidx.room:room-compiler:2.3.0"
ksp "androidx.room:room-compiler:2.3.0"
implementation "androidx.room:room-ktx:2.3.0"
```



Room Annotation Processing에 대한 의존성을 KAPT에서 KSP로 교체 후 빌드 시 결과에 대해 보았다.

![Compile Time by KSP](https://imgur.com/HyjEy1l.jpg)

예상치 못한 결과로 KSP와 KAPT의 총 합이 5.5s + 12.54로 대략 18.04s 정도가 소요되어 오히려 빌드시간이 많이 소요되었는데, 그 이유를 보니 [블로그 원문](https://android-developers.googleblog.com/2021/02/announcing-kotlin-symbol-processing-ksp.html)에서 알려진 이슈에 대해 확인하고 이해할 수 있었다.

> That said, using KAPT and KSP in the same module will likely slow down your build initially, so during this alpha period, it is best to use KSP and KAPT in separate modules.

하지만, 같은 모듈에서 KAPT와 KSP를 같이 사용하면 처음에는 빌드 속도가 느려질 수 있다라고 명시되어있다.

Alpha에서는 별개의 모듈에서 사용하는 것이 좋다고 나와있다.



그래서, 현재 만들어진 프로젝트에서 완전히 룸 외에 다른 KAPT를 사용하는 라이브러리 의존성을 제거하고, Room에 대한 KAPT, KSP만 온전히 비교해보기로 했다.

다른 라이브러리 제외 오직 Room을 사용했을 때 KAPT로 Generate되는 시간을 산정해보았을 때 대략 8.04s가 소요되었다.



![Compile Time By KAPT only using Room](https://imgur.com/NjWXyw6.jpg)

KSP에 대한 결과는 다음과 같다. App 모듈에서 결과는 KSP플러그인을 사용하고, KSP를 사용한 Room으로 빌드 시 0.71ms로 약 11.3배정도로 속도가 빨라져 놀랍도록 빌드시간이 줄어든것을 확인할 수 있었다.



---

### 참고자료

- https://android-developers.googleblog.com/2021/02/announcing-kotlin-symbol-processing-ksp.html
- https://github.com/google/ksp
- https://jsuch2362.medium.com/my-first-kotlin-symbol-processing-tool-for-android-4eb3a2cfd600






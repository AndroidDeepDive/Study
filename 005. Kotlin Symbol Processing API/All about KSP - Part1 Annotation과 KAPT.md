# All about KSP - Part1: Annotation과 KAPT

KSP를 시작하기 전에 선행학습 되어야 할 몇가지 내용을 설명하고자 한다.

## Annotation

개발을 하다보면 아래와 같은 코드를 쉽게 접할 수 있다.

```java
@Overide
@NonNull
@Nallable
```

이러한 코드를 Annotation이라고 한다. 컴파일 타임에 컴파일러에게 특정 정보를 전달하거나, 미리 지정된 코드를 생성하기 위한 용도로 사용된다. Android 뿐만 아니라 Spring Framework 등을 개발할때에도 자주 활용한다.

다음의 오라클 문서에서 발췌한 Annotation의 정의를 살펴보자.

> Annotations, a form of metadata, provide data about a program that is not part of the program itself. Annotations have no direct effect on the operation of the code they annotate.

문서의 내용을 해석하면 

> "Annotation은 일종의 메타데이터 형태이며, 프로그램의 일부가 아니라 프로그램에 대한 정보를 제공한다.
> Annotation 자체로는 실행되는 코드에 직접적인 영향을 미치지 않는다."

Annotation은 Java 5부터 지원하고 있으며, 주로 다음과 같은 용도로 사용된다.

1. **Information for the compiler** : 컴파일러가 에러를 검출하거나, 경고를 표시하지 않도록 사전에 정보를 전달한다.
2. **Compile-time and deployment-time processing** : 코드나 xml 파일 등을 컴파일 타임에 생성할 수 있도록 처리한다.
3. **Runtime processing** : 몇몇 annotation들은 런타임에도 검사를 수행하도록 처리해준다.

### 이미 정의된 Annotation 살펴보기

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

안드로이드 앱 개발시 `androidx.annotation` 패키지를 참조하면 다양한 Annotation을 활용할 수 있다.

`androidx.annotation` 패키지에 속한 annotation 목록을 참고하자.

> [Android Developers#androidx.annotation](https://developer.android.com/reference/androidx/annotation/package-summary)

Java.lang.annotation package에 있는 Annotation을 알아보자. 해당 패키지에는 다른 Annotation에 적용될 수 있는 Annotation들이 있으며 이를 **메타 Annotation**이라 한다.

- @Retention : Retention Annotation은 표기된 Annotation이 저장되는 방법을 지정한다.
  - RetentionPolicy.SOURCE : 소스 코드에서만 유지되며, 컴파일러에서는 무시된다.
  - RetentionPolicy.CLASS : 컴파일 타임에 컴파일러에 의해 유지되지만, JVM에서는 무시된다.
  - RetentionPolicy.RUNTIME : JVM에 의해서 유지되므로 런타임에서 사용가능하다.
- @Documented : Annotation이 사용될 때 마다 해당 element가 Javadoc에 문서화 되어야 함을 나타낸다.
- @Target : Annotation을 적용할 수 있는 Java Elements 종류를 제한한다.
- @Inherited : super class 로부터 상속될 수 있는 Annotation 타입이다. class 선언시에만 적용.

## Annotation processor

앞서 소개한 Annotation의 용도를 사용하기 위해서 Annotation Processor가 필요하다. 

Annotation processor는 Java 컴파일러 플러그인의 일종으로, 컴파일러에게 어떠한 요소(클래스, 메서드, 필드 등)에 annotation이 추가 되어있는지 확인하게 된다. 컴파일 타임에 이런 과정을 통해 코드베이스를 검사하거나 확인된 정보를 통해 새로운 코드를 생성 할 수 있다. Annotation processor를 잘 활용한다면 사용자가 정의한 태스크를 자동화하거나 보일러 플레이트 작성을 줄일 수 있다.

Annotation processor의 특징을 정리하자면 다음과 같다.

- 컴파일 타임에 특정 작업을 수행한다.
- 리플렉션없이 프로그램의 의미 및 구조를 파악할 수 있게 된다.
- 자동으로 보일러 플레이트를 생성할 수 있게 된다.

### Annotation processor 실행 순서

Annotation processor는 여러 라운드에 걸쳐 수행된다.

![charles-2019-12-21-오후-4.57.09-1024x575](https://www.charlezz.com/wordpress/wp-content/uploads/2019/04/charles-2019-12-21-%EC%98%A4%ED%9B%84-4.57.09-1024x575.png)

실행 순서를 간단히 정리하자면 다음과 같다.

1. 등록된 Annotation processor들과 함께 컴파일러가 시작된다.
2. Annotation processor들이 작성된 Annotation을 기반으로 코드 검사 및 생성을 수행한다.
3. 컴파일러가 모든 Annotation processor의 작업이 끝났는지 확인하고, 그렇지 않다면 2번을 반복한다.
4. 모든 처리가 끝난다면 전체 코드에 대한 컴파일을 시작한다. (이후 프로세스는 기존과 동일)

### Android에서 Annotation Processor를 사용하는 라이브러리들

#### Room 

Android에서 SQLite에 대한 추상화를 제공하는 Room 라이브러리에도 Annotation Processor가 적용되어 있다.

아래는 대표적인 예제인 User 관련 코드이다.

```kotlin
// User.kt
@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)
```

```kotlin
// UserDao.kt
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}
```

```kotlin
// AppDatabase.kt
@Database(entities = arrayOf(User::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
```

위의 에졔에서 쓰인 Annotation들은 `@Entity`, `@PrimaryKey`, `@ColumnInfo`, `@Dao`, `@Query`, `@Insert`, `@Delete`이다.

이 Annotation들의 구현체를 확인해보자.

#### Entity.java

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Entity {
    String tableName() default "";
    Index[] indices() default {};
    boolean inheritSuperIndices() default false;
    String[] primaryKeys() default {};
    ForeignKey[] foreignKeys() default {};
    String[] ignoredColumns() default {};
}
```

#### PrimaryKey.java

```java
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface PrimaryKey {
    boolean autoGenerate() default false;
}
```

#### ColumnInfo.java

```java
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface ColumnInfo {
    String name() default INHERIT_FIELD_NAME;
    @SuppressWarnings("unused") @SQLiteTypeAffinity int typeAffinity() default UNDEFINED;
    boolean index() default false;
    String defaultValue() default VALUE_UNSPECIFIED;
    String INHERIT_FIELD_NAME = "[field-name]";


    int UNDEFINED = 1;
    int TEXT = 2;
    int INTEGER = 3;
    int REAL = 4;
    int BLOB = 5;
    @IntDef({UNDEFINED, TEXT, INTEGER, REAL, BLOB})
    @Retention(RetentionPolicy.CLASS)
    @interface SQLiteTypeAffinity {
    }

    int UNSPECIFIED = 1;
    int BINARY = 2;
    int NOCASE = 3;
    int RTRIM = 4;
    @RequiresApi(21)
    int LOCALIZED = 5;
    @RequiresApi(21)
    int UNICODE = 6;
    @IntDef({UNSPECIFIED, BINARY, NOCASE, RTRIM, LOCALIZED, UNICODE})
    @Retention(RetentionPolicy.CLASS)
    @interface Collate {
    }
    String VALUE_UNSPECIFIED = "[value-unspecified]";
}
```

이 외에도 `@Dao`, `@Query`, `@Insert`, `@Delete`과 같은 Annotation들은 각자 인터페이스, 구현체 값들을 이미 가지고 있다. **Room** 뿐만 아니라 범용적으로 사용되는 **Dagger**, **Glide**와 같은 라이브러리들도 Annotation(Processor)을 기반으로 동작한다. 

## KAPT (Kotlin Annotation Processing Tool)

기존에 자바 컴파일러의 플러그인으로 동작하던 Annotation processor는 자바 언어에서만 동작하므로 코틀린 코드로 작성된 소스 코드로는 작업을 수행할 수 없다. 그래서 코틀린에서는 **KAPT**라는 컴파일러 플러그인과 함께 Annotation processor를 지원한다. 간단히 말해서 KAPT를 사용하면 코틀린 프로젝트에서도 Dagger 또는 Databinding과 라이브러리를 사용할 수 있다는 것이다.

KAPT를 사용하기 위해 모듈의 build.gradle에 다음과 같은 코드를 삽입할 수 있다.

```groovy
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'
```

이 때 kotlin-android 설정을 먼저 해줘야 kotlin-kapt를 쓸 수 있다.

Annotation processor가 포함된 라이브러리를 추가한다면 다음과 같이 의존성을 추가한다.

```groovy
dependencies {
 // 기존 annotationProcessor 대신 kapt로 대체
 // annotationProcessor 'groupId:artifactId:version'
    kapt 'groupId:artifactId:version'
}
```

하지만 KAPT는 한가지 단점이 존재한다. 기존에 작성된 Java Annotation processor를 사용하기 위해 컴파일타임에 Kotlin으로 작성된 소스 코드로 부터 Java로 된 Stub을 생성하게 된다. Stub을 생성하는 것은 kotlinc의 분석 비용의 3분의 1이나 차지하므로, 빌드시 많은 오버헤드가 발생하게 된다. 이러한 문제를 해결하기 위해 KSP가 등장하게 되었다. 

[다음 글 보러가기 : All about KSP-Part2:KSP 살펴보기](다음글 링크)


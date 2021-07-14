## Annotation Processor란 무엇인가?

Kotlin Symbol Processing API(이하 `KSP`)를 분석하기 전에, `KSP`의 전신이라고 할 수 있는 `Annotation Processor`에 대해서 분석해보자.

`Annotation Processor`이란 용어는 낯설더라도, 아래와 같은 코드들은 개발하면서 종종 보았을 것이다.

```java
@Overide
@NonNull
@Nallable
```

이러한 형식을 `Annotation`이라고 부르며, 컴파일 타임에 컴파일러에게 특정 정보를 전달하거나, 미리 지정된 코드를 생성하기 위한 용도로 사용된다.

비단 Android 뿐만 아니라 Spring Framework 등을 개발할때에도 자주 활용하게 되는 문법이다.

Java의 공식 문서의 Annotation 정의는 아래와 같다.

Annotation은 메타데이터의 한 형태로 프로그램에 대한 데이터를 제공하는 데 사용되며, 코드의 동작이 직접적인 영향을 주지 않는다.

Annotation은 크게 3가지 용도로 쓰인다.

1. **Information for the compiler** : 컴파일러가 에러를 탐지하거나, 경고를 표시하지 않도록 사전에 정보를 전달한다.
2. **Compile-time and deployment-time processing** : 코드나 xml 파일 등을 컴파일 타임에 생성할 수 있도록 처리한다.
3. **Runtime processing** : 몇몇 annotation들은 런타임에도 검사를 수행하도록 처리해준다.

전문은 아래 링크를 참조하자.

> **참고** [Oracle JavaDoc#Annotations](https://docs.oracle.com/javase/tutorial/java/annotations/index.html)


특히 구글에서 제공되는 `androidx.annotation`을 참조하면 매우 다양한 Annotation을 활용할 수 있다.

`androidx.annotation` 패키지에 속한 annotation 리스트들은 아래의 링크를 참고하자.

> **참고** [Android Developers#androidx.annotation](https://developer.android.com/reference/androidx/annotation/package-summary)


Annotation은 Java 5부터 지원하고 있으며, `AbstractProcessor`클래스를 상속받아 구현할 수 있다.

> 구현 예제를 추가하는 것이 좋을까?
> 추가하는 것이 좋다면 예시로 하나 작업해서 Deep Dive 해보도록 하겠음.

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

이 외에도 `@Dao`, `@Query`, `@Insert`, `@Delete`과 같은 Annotation들은 각자 인터페이스, 구현체 값들을 이미 가지고 있다.

비단 **Room** 뿐만 아니라 **Butterknife**, **Dagger**, **Retrofit** 같은 라이브러리들도 Annotation을 기반으로 동작한다.

그렇다면 이미 상용화되어 쓰이고 있는 라이브러리말고 직접 만들어 쓸 수는 없는 것일까?

Java 기반의 Android도 물론 지원가능하지만 Kotlin 기반의 **KAPT** 를 이용해 작성할 수 있다.

### KAPT (Kotlin Annotation Processing Tool)

KAPT는 위의 제목의 약자에서 알 수 있듯, 코틀린에서 생성한 코드를 참조하기 위해 추가해야하는 의존성이다.

다만 이 KAPT는 Kotlin 코드를 Java Annotation Processor를 수정하지 않기 위해 컴파일시 Java로 된 Stub을 생성하게 된다.

Stub을 생성하는 것은 kotlinc의 분석 비용의 3분의 1이나 차지하므로, 빌드시 많은 오버헤드가 발생하게 된다.

따라서, 이 컴파일 타임을 획기적으로 줄여주는 솔루션이 나오게 되니, 이게 바로 이번 주제에서 다루게 될 KSP이다.

다음 포스트에서 KSP에 대해 자세히 살펴보도록 하자.

## References
- https://developer.android.com/reference/androidx/annotation/package-summary
- https://hannesdorfmann.com/annotation-processing/annotationprocessing101/
- https://kotlinlang.org/docs/kapt.html
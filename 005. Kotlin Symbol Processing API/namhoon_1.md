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

```java
 @Entity
 public class Song {
    @PrimaryKey
    private final long id;
    private final String name;
    @ColumnInfo(name = "release_year")
    private final int releaseYear;

    public Song(long id, String name, int releaseYear) {
        this.id = id;
        this.name = name;
        this.releaseYear = releaseYear;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getReleaseYear() {
        return releaseYear;
    }
}
```

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


Room / Butterknife / Dagger2

> 사례를 코드 레벨로 찾아서 찾아서 올리는 것이 끌린다!


## References

- https://developer.android.com/reference/androidx/annotation/package-summary
- https://docs.oracle.com/javase/tutorial/java/annotations/index.html
- https://hannesdorfmann.com/annotation-processing/annotationprocessing101/
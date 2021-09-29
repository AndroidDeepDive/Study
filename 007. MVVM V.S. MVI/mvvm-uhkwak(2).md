# Data Binding

이번 편에서는 데이터바인딩이 무엇이고 어떻게 사용하는지 간단하게 알아보고, 동작 원리를 알아볼 것이다. 그리고 MVVM 아키텍쳐에서 Data Binding이 어떤 의미를 가지는지도 알아보자. 

### Data Binding 이란?

> The Data Binding Library is a support library that allows you to bind UI components in your layouts to data sources in your app using a declarative format rather than programmatically.

안드로이드 공식 문서에서 발췌한 Data Binding 에 관한 설명인데, 번역해 보자면 프로그래머틱한(코드로서 각 UI 요소들에게 적용할 값을 직접 컨트롤) 방식 대신 선언형 포맷을 사용함으로서 앱 안의 데이터 소스와 UI 컴포넌트들을 바인딩 할 수 있는 라이브러리가 바로 Data Binding 인 것이다. 

Data Binding을 사용하기 위해서는 앱 수준의 build.gradle 파일에서 아래와 같이 설정해줘야 한다.

```xml
android {
    ...
    buildFeatures {
        dataBinding true
    }
}
```

그리고 xml 파일에서 <layout> 태그를 최상위에 선언해줘야 한다. 

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto">
    <data>
        <variable
            name="viewmodel"
            type="com.myapp.data.ViewModel" />
    </data>
    <ConstraintLayout... /> <!-- UI layout's root element -->
</layout>
```

해당 xml 파일 이름이 activity_main.xml 이라면, Data Binding 라이브러리는 ActivityMainBinding으로 generated class 를 만들게 된다. 

### Data Binding 을 사용하면 뭐가 좋을까

- programmaic 한 방식 보다 더 짧은 코드로 UI 컴포넌트에 데이터를 바인딩 할 수 있고, 읽기가 쉬워지며 findViewById()를 쓰지 않아도 된다.
- View에 접근할 때 type-safety를 보장받을 수 있다. 
  - 이는 잘못된 타입을 바인딩 할 경우 컴파일 단계에서 에러를 던지기 때문에 타입을 알맞게 수정할 수 있다는 의미이다.
- MVVM 구조에 잘 들어맞는 기술이다.
- XML에 expression으로서 간단한 로직을 작성할 수 있다. 

Data Binding은 분명 '잘 쓰면' 좋은 기술이다. 

### Data Binding 의 동작 원리

Data Binding을 deep dive 하기 위해서 generated class가 어떤 것인지 살펴보고, 어떤 원리로 Data Source(Data Binding의 입장) 의 데이터만 바뀌어도 UI 컴포넌트가 바뀌는지 살펴보자. 

먼저 Generated Class는 아래와 같은 폴더 구조에 생성된다.

<img src="/Users/ukhyuns/Library/Application Support/typora-user-images/image-20210825195525962.png" alt="image-20210825195525962" style="zoom:50%;" />

build > generated > ... > databinding 에 ActivityMainBinding 추상클래스가 생겼다.

ActivityMainBinding 클래스는 아래 처럼 되어 있다.

![image-20210825191805657](/Users/ukhyuns/Library/Application Support/typora-user-images/image-20210825191805657.png)

해당 xml 파일에는 rootView가 ConstraintLayout으로 되어있고, 자식 뷰로서 FragmentContainerView가 있는걸 알 수 있다. @NonNull 필드로 되어 있으므로 null 관련 처리를 해주지 않아도 되는 이점이 있다.

이 ActivityMainBinding은 ViewDataBinding을 상속받고 있고, ActivityMainBindingImpl 클래스가 이 추상 클래스를 상속 받아 구현한다. 

```java
package knowre.homework.picsum.databinding;
import knowre.homework.picsum.R;
import knowre.homework.picsum.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMainBindingImpl extends ActivityMainBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.nav_host_fragment, 1);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMainBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 2, sIncludes, sViewsWithIds));
    }
    private ActivityMainBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (androidx.fragment.app.FragmentContainerView) bindings[1]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}
```

해당 클래스에서 눈여겨 볼 부분은 executeBindings() 메소드이다. executeBindings() 메소드에서 현재 필드에 해당하는 값을 가져와서 setting 하게 된다.

### MVVM에서 Data Binding 의 의미

![img](https://miro.medium.com/max/1286/1*6gp5XD5qY8H3Wz-GcxW0cA.png)



Data Binding의 주요 목적 중 하나는 View 와 ViewModel 간의 인터랙션을 돕고, ViewModel로부터 View에게 데이터가 변경 된 사실을 알려주기 위함이다. 그러면 View는 도메인 계층에서 받은 Data를 따로 후처리 할 필요 없이 화면에 UI 만 그리면 된다. 이러한 구조는 새로운 기능을 추가해야 할 때 UI/UX 나 도메인 관련 로직에만 집중할 수 있다는 이점이 있다. MVVM 아키텍쳐에서는 레이어가 분리되어 있으므로 유지보수 또한 다소 편해지는 경향이 있다. 
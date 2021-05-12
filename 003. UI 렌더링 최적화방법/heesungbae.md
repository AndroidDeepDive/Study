## UI 렌더링 성능 개선
---

### 레이아웃 계층 구조 최적화
![image](https://miro.medium.com/max/692/1*abc0UlGj1myFD0eph4pZjQ.png)
 - 레이아웃은 View와 ViewGroup으로 이뤄진다
 - RootView를 시작으로 트리 구조로 뷰를 생성한다
 - View의 중첩이 많아질수록 뷰를 그리는 시간이 증가한다
 - requestLayout() 함수를 적게 사용한다
 - invalidate() : text, color 변경, 뷰가 터치되었을 때 onDraw() 함수를 재호출
 - requestLayout() : 뷰의 사이즈가 변경되었을 때 onMeasure() 함수를 재호출 -> 시간이 오래걸림

### 레이아웃 재사용
 - include, merge를 통해 뷰를 재사용한다

 - include로만 뷰를 재사용하면 다음과 같이 뷰가 중첩될 수 있다
![before-merge](https://miro.medium.com/max/1000/1*Grxj64w7gmVrJxDsk4qDcA.png)

 - merge를 같이 사용하여 중첩을 줄여준다
![before-merge](https://miro.medium.com/max/1000/1*FyzCjMY3e8eUMHzbobRTzg.png)


## 렌더링 최적화 기법
---

### 오버드로잉 방지(백그라운드)
보통 앱의 배경 색상을 적용하기 위해서 `android:background="@color/white"` 해당 뷰의 배경색상을 적용한다
```xml
<!-- /res/layout/activity_main.xml -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:background="@color/white">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

허나 이렇게 배경색을 지정하면 아래과 같이 Overdraw가 생긴다

![example1](https://i.imgur.com/Sfjx4BJ.png)

왜 Overdraw가 생기는지 확인을 위해 앱 테마 값을 확인해보았다.
```xml
<!-- /res/values/themes.xml -->
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.UITest" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
    </style>
</resources>
```

특별히 지정해준 사항이 없어 해당 테마의 Parent를 따라가 보았다
```
Theme.MaterialComponents.DayNight.DarkActionBar > 
Theme.MaterialComponents.Light.DarkActionBar > 
Base.Theme.MaterialComponents.Light.DarkActionBar >
Base.Theme.MaterialComponents.Light > 
Base.V14.Theme.MaterialComponents.Light
```

해당 소스에서 아래와 같이 배경 색상을 지정하는 걸 확인하였고
```xml
<item name="android:colorBackground">@color/design_default_color_background</item>
``` 

해당 값을 확인해보니 아래와 같이 흰색으로 설정하고 있었다
```xml
<color name="design_default_color_background">#FFFFFF</color>
```

한마디로 배경 색상을 흰색으로 칠해주고 그 위에 다시 흰색으로 칠해주고 있었다.
해당 Overdraw를 없애고 내가 원하는 색상으로 설정하기 위해 테마 파일을 아래와 같이 
`<item name="android:windowBackground">@color/beige</item>` 코드를 입력하였다

```xml
<!-- /res/values/themes.xml -->
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.UITest" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <!-- Primary brand color. -->
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <!-- Secondary brand color. -->
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <!-- Status bar color. -->
        <item name="android:statusBarColor" tools:targetApi="l">?attr/colorPrimaryVariant</item>
        <!-- Customize your theme here. -->
        <item name="android:windowBackground">@color/beige</item>
    </style>
</resources>
```
비교를 위해 `/res/layout/activity_main.xml` 파일에 `android:background="@color/beige"` 코드를 입력 후 Overdraw를 확인하였다

![example2](https://i.imgur.com/s5LAvBX.png)

배경 색상 지정을 테마를 통해 지정하면 Overdraw를 줄일 수 있다


---
 ### Reference 
  - [https://proandroiddev.com/writing-performant-layouts-3bf2a18d4a61](https://proandroiddev.com/writing-performant-layouts-3bf2a18d4a61)
  - [https://jungwoon.github.io/android/2019/10/02/How-to-draw-View.html](https://jungwoon.github.io/android/2019/10/02/How-to-draw-View.html)
  - [https://android-developers.googleblog.com/2009/03/window-backgrounds-ui-speed.html](https://android-developers.googleblog.com/2009/03/window-backgrounds-ui-speed.html)
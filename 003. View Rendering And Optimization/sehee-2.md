# View가 그려지는 순서에 대해 알아보자
`UI가 렌더링 되는 과정`에서 View Lifecycle 에 대해 알아보았다. Measure, Draw, Layout 단계를 거치면서 View의 크기가 측정되고, 어느 위치에 놓여질 지 결정되며, 비로소 그려지게 된다. 

그렇다면, Lifecycle을 이용해 아래처럼 다양한 View Depth 를 가지는 Layout 에 대해서 어느 순서로 Child View 의 크기가 측정되며, 어느 순서로 그려지는지 코드를 통해 알아보자.

## 👏 실험
``` xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    android:id="@+id/root1"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/constraintContainer2"
        android:layout_width="400dp"
        android:layout_height="400dp"
        android:background="@color/teal_200"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent">

        <TextView
            android:id="@+id/constraintTextView3"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="First TextView"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent" />

        <LinearLayout
            android:id="@+id/linearContainer4"
            android:layout_width="200dp"
            android:layout_height="200dp"
            android:orientation="vertical"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintEnd_toEndOf="parent">

            <ImageView
                android:id="@+id/linearImageView5"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@drawable/ic_launcher_foreground"/>

            <TextView
                android:id="@+id/linearTextView5"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Second TextView"/>

            <RelativeLayout
                android:id="@+id/relativeContainer5"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@color/purple_500">

                <TextView
                    android:id="@+id/relativeTextView6"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Thired TextView"/>

            </RelativeLayout>

        </LinearLayout>

    </androidx.constraintlayout.widget.ConstraintLayout>

    <FrameLayout
        android:id="@+id/frameContainer2"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:background="@color/teal_200_30"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

root Container 를 부모 레이아웃으로 삼아 다양한 depth 의 View 가 쌓여져 있고, 이는 아래처럼 그래프로 나타낼 수 있다. 

![](https://images.velog.io/images/jshme/post/31b049e8-aa03-4584-be0d-025e7aba3260/Untitled%20Diagram.png)

각각의 View, ViewGroup에 onLayout 시점을 포착할 수 있는 코드를 구현해, 어느 순서로 View 가 그려지는지 확인해보자.

> doOnLayout, doOnDraw, doOnXXX 등을 통해 원하는 라이프사이클 시점을 포착할 수 있다.

### OnLayout : 
``` kotlin
        linearContainer4.doOnLayout {
            println("linearContainer4 doOnLayout")
        }

        constraintContainer2.doOnLayout {
            println("ConstraintContainer2 doOnLayout")
        }

        constraintTextView3.doOnLayout {
            println("constraintTextView3 doOnLayout")
        }

        root1.doOnLayout {
            println("root1 doOnLayout")
        }

        relativeContainer5.doOnLayout {
            println("relativeContainer5 doOnLayout")
        }

        relativeTextView6.doOnLayout {
            println("relativeTextView6 doOnLayout")
        }

        linearImageView5.doOnLayout {
            println("linearImageView5 doOnLayout")
        }

        linearTextView5.doOnLayout {
            println("linearTextView5 doOnLayout")
        }

        frameContainer2.doOnLayout {
            println("frameContainer2 doOnLayout")
        }
        
```

<img src="https://images.velog.io/images/jshme/post/80fdce92-5afd-4375-b41e-6fabdf03323a/image.png" width="50%">

👉 onLayout 시점에는 트리구조로 이루어진 View & ViewGroup 이 **후위순회**를 하며 **측정이 된다**는 것을 알 수 있다.

### OnDraw :

``` kotlin

        linearContainer4.doOnPreDraw {
            println("linearContainer4 doOnPreDraw")
        }

        constraintContainer2.doOnPreDraw {
            println("ConstraintContainer2 doOnPreDraw")
        }

        constraintTextView3.doOnPreDraw {
            println("constraintTextView3 doOnPreDraw")
        }

        root1.doOnPreDraw {
            println("root1 doOnPreDraw")
        }

        relativeContainer5.doOnPreDraw {
            println("relativeContainer5 doOnPreDraw")
        }

        relativeTextView6.doOnPreDraw {
            println("relativeTextView6 doOnPreDraw")
        }

        linearImageView5.doOnPreDraw {
            println("linearImageView5 doOnPreDraw")
        }

        linearTextView5.doOnPreDraw {
            println("linearTextView5 doOnPreDraw")
        }

        frameContainer2.doOnPreDraw {
            println("frameContainer2 doOnPreDraw")
        }
        
```
<img src="https://images.velog.io/images/jshme/post/06bd54ea-e19a-437a-9eca-24977e678709/image.png" width="50%">

👉 onDraw 시점에는 트리구조로 이루어진 View & ViewGroup 이 **전위순회**를 하며 **그려진다는 것**을 알 수 있다.

## 👏 결론
Layout 내에 존재하는 Chile View가 lifecycle 를 거치면서 크기가 측정되는 순서와, 그려지는 순서는 다르다. 

1. Child View 들의 크기가 정해지고 Parent View 가 그 측정값을 알아야지 자신의 크기를 정할 수 있는 것이고, `(Layout 단계)`
2. 도화지가 준비되어야 그림을 그릴 수 있듯이, Parent View 가 먼저 그려져야 Child View 가 그려질 수 있기 때문이다. `(Draw 단계)`
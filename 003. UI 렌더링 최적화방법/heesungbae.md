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

---
 ### Reference 
  - [https://proandroiddev.com/writing-performant-layouts-3bf2a18d4a61](https://proandroiddev.com/writing-performant-layouts-3bf2a18d4a61)
  - [https://jungwoon.github.io/android/2019/10/02/How-to-draw-View.html](https://jungwoon.github.io/android/2019/10/02/How-to-draw-View.html)
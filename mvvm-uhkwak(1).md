# MVVM

## MVVM의 역사

### 2000년대 초반 Microsoft에서 탄생함

- Silverlight, WPF 와 같은 XAML(마크업 언어)를 이용하여 디자인과 개발을 편하게 하기 위해(분리?) Microsoft에서 MVVM을 발표함
- MVVM이 나오기 전에는, 디자이너들은 Expression Blend 툴을 이용하여 쉽게 UI를 디자인 했고, 개발자들은 각 뷰들을 위한 코드를 작성함
- 이는 자연스럽게 뷰와 비지니스 로직의 결합도가 높아지는 단점이 생겼음
- 그래서 Microsoft에서 뷰와 비지니스 로직을 분리하기 위해서 MVVM을 창시함

### 모바일 생태계에서의 MVVM

- 현재 모바일 생태계는 그때의 상황과는 달리, 스마트폰 보급화에 따라 앱 구조가 복잡해지고 개발자가 UI 및 비지니스 로직 일부를 같이 개발하게 됨
- 모바일 개발 환경에서는 모델과 뷰를 분리하는 것으로 MVVM을 사용함
  - 어쨋든 얻게 되는 이점은 동일.
- UI 변경으로 인해 비지니스 로직을 변경하지 않게 됨

### MVVM의 개념 (feat. Reactive 아키텍쳐)

![The MVVM pattern](https://docs.microsoft.com/en-us/xamarin/xamarin-forms/enterprise-application-patterns/mvvm-images/mvvm.png)

- MVVM은 View, ViewModel, Model 이렇게 세 개의 레이어로 구성됨

- View 는 ViewModel의 변화에 반응한다.
- ViewModel은 Model의 데이터 상태를 기반으로 업데이트 한다. 
- View와 Model 레이어는 ViewModel을 통해서만 통신한다.

### Model

- 데이터에 대해서 접근하고 검증(Validation) 하는 로직을 포함.
- 데이터를 읽고 쓰는 방법
  - Push and Pull
  - Observe and Push
- 데이터 변경 발생 시, 이를 ViewModel에게 알려줌

### View

- 스타일 및 레이아웃, UI 요소들과 같이 화면을 그리는 것이 주된 목적
- 어떠한 비지니스 또는 검증(Validation)로직을 모름
- **그 대신, 시각적인 요소를 ViewModel의 Property에 바인딩하여 상태 변화에 반응함**
- ViewModel의 상태가 변경되기 까지는 뷰는 업데이트 하지 않는다.
- 입력 및 버튼 터치 같은 사용자 인터랙션을 ViewModel에 알려줌

### ViewModel

- View Hadling
  - View의 상태를 가진다.
  - UI 요소들을 다루기 위한 메서드들을 가짐
  - 이런 식으로 다양한 UI 요소들을 바인딩 함
- Model Hadling
  - 모델에 데이터를 읽고 쓰기 위한 메서드를 호출함
  - 모델의 데이터가 변경되었을 때 뷰에게 알려준다.
  - 모델을 뷰 형식에 맞게 transform 
- ViewModel은 UI 없이 테스트가 가능하다.
  - 모든 테스트는 input과 output 신호가 명확할 때, 테스트가 용이한데, ViewModel은 이를 제공해눈다. 
- ViewModel과 View 사이의 깔끔한 경계/깔끔한 mapping이 중요
- ViewModel은 일종의 추상화 된 View 이므로 UI 가 필요하지 않다.

## Reference

- https://docs.microsoft.com/en-us/xamarin/xamarin-forms/enterprise-application-patterns/mvvm
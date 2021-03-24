
## (Android Deep Dive) Jetpack Compose Part 1

### Jetpack Compose란 무엇인가?

Compose는 Native UI를 코드레벨로 구현할 수 있는 tookit이다.

Jetpack Compose 사이트에 나와있는 아래 그림이 Compose가 뭔지를 굉장히 직관적으로 보여준다.

![](https://drive.google.com/uc?export=view&id=1vXOGJD7inmGfDYiPu6wMVrpu5xFnov38)

> **출처** [Android Developers#Jetpack Compse](https://developer.android.com/jetpack/compose)

공식 서비 소개스 영상도 첨부한다.

<div style="width: 560px; height: 315px; float: none; clear: both;">
  <embed
    src="https://www.youtube.com/embed/U5BwfqBpiWU"
    wmode="transparent"
    type="video/mp4"
    width="100%" height="100%"
    allow="autoplay; encrypted-media; picture-in-picture"
    allowfullscreen
  >
</div>

구글에서 어필하는 Compose의 장점은 아래 4가지다.

- Less Code : 코드 감소
  - 적은 수의 코드로 더 많은 작업을 하고 전체 버그 클래스를 방지할 수 있으므로 코드가 간단하며 유지 관리하기 쉽습니다.
- Intuitive : 직관적
  - UI만 설명하면 나머지는 Compose에서 처리합니다. 앱 상태가 변경되면 UI가 자동으로 업데이트됩니다.
- Accelerate Development : 빠른 개발 과정
  - 기존의 모든 코드와 호환되므로 언제 어디서든 원하는 대로 사용할 수 있습니다. 실시간 미리보기 및 완전한 Android 스튜디오 지원으로 빠르게 반복할 수 있습니다.
- Powerful : 강력한 성능
  - Android 플랫폼 API에 직접 액세스하고 머티리얼 디자인, 어두운 테마, 애니메이션 등을 기본적으로 지원하는 멋진 앱을 만들 수 있습니다.

늘 그렇듯 구글에서 말하는 설명만 보면 안 쓸 이유가 없어보이고, 대세가 된다면 Android 개발자에게 또 하나의 러닝커브로 작용할 것이다.

#### 제한사항

Compose는 아직 정식으로 릴리즈되지 않은 기능이므로 Android Studio Canary에서 프로젝트를 구성하며 몇 가지 제한사항이 존재한다.

1. Android Studio Canary 

Canary는 아래 링크에서 다운 받을 수 있다.

> [Android Studio Preview](https://developer.android.com/studio/preview)

2. 최신 버전의 Kotlin plugin 

```gradle
ext.kotlin_version = '1.4.31'
```

3. buildFeatures 및 composeOption 활성화

```gradle
android {
    ...
    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion 1.0.0-beta02
    }
}

dependencies {
    ...
    implementation "androidx.compose.ui:ui:1.0.0-beta02"
    implementation "androidx.activity:activity-compose:1.3.0-alpha03"
    implementation "androidx.compose.material:material:1.0.0-beta02"
    implementation "androidx.compose.ui:ui-tooling:1.0.0-beta02"
    ...
}
```

#### Codelab

위의 환경 구축까지 끝냈다면 아래 코드랩을 수행해보자.

> [Jetpack Compose basics](https://developer.android.com/codelabs/jetpack-compose-basics#0)

#### After Codelab

코드랩을 수행하고나면 사람마다 제각각의 인상을 받을 수 있다.

아래는 코드랩 수행 후 본인이 느꼈던 감상 리스트이다.

- 과연 `R.layout.**`를 걷어낼 수 있을까?
- Raw한 레이아웃 구성에 대해서는 이해가 ㅠ되었는데, 양방향 데이터바인딩을 적용한 뷰를 바꿀 수 있을까?
- Flutter하는 기분인데?
- Kotlin도 Swift처럼 되나?
  - "Xcode 게섯거라"도 가능할 것인가? (정확히는 스토리보드겠지만)
- 너무 먼 미래겠지만 xml을 완전히 제거할 수 있으면 좋을텐데, Value 계열 리소스에 대해서는 어떤 방식으로 제거할 수 있을까?
- 만약 Compose가 대중화된다면 DI에 대한 이해가 필수적으로 요구될 수 있겠다.
  - Butter knife를 지나 Dagger 를 넘어서서 Dagger Hilt까지 요구하게 될까?
- 신규 앱이면 설계가 굉장히 중요해질 것이고, 기존 앱이면 마이그레이션 비용을 막대하게 치르게 될 것 같다.
  - 기존 앱의 경우 어떤 디자인 패턴을 썼고, 얼마나 정확하게 적용해두었는지가 성패를 가르지않을까?
  - 리소스가 별로 없는 경우엔 MVP와 MVVM이 혼재되었던 것처럼, Compose도 같이 섞여버릴 것 같다.
- 뷰의 재사용성을 염두해둔 디자인과 설계의 중요성이 대두될 것이다.

#### Compose Samples

코드랩 뿐만 아니라 다양한 Compose 샘플들을 구글이 모아두었다.

아래 링크를 참조하자.

> [Github#Android Compose Samples](https://github.com/android/compose-samples)

현재 작업하고 있는 뷰와 제일 유사한 것들을 통해 Compose를 이해하는 것도 하나의 방법이 될 수 있다.

### Deep Dive

**Android Deep Dive** 스터디의 목적은 단순히 새로운 것, 혹은 구현 방법에 대한 정보 전달보다는 실무나 새로운 것들을 해보면서 얻은 의구심 혹은 경험들에 대해 조금 깊게 파보자는 취지이므로 위의 감상을 한 번 고찰 해보도록 하겠다.

#### xml 기반의 레이아웃을 없앨 수 있을까?
Q. `R.layout.**`를 없앨 수 있을까?
Q. Raw한 레이아웃 구성에 대해서는 이해가 되었는데, 양방향 데이터바인딩을 적용한 뷰를 바꿀 수 있을까?

이분법적으로 Compose가 layout xml을 대치할 수 있다 없다는 사실 무의미하다.

개발팀의 규칙으로 모두 Compose로 해보자! 가 아닌 이상 현재 상태에서는 xml로 구성하는 것이 속도면에서 어느정도 우월할 수 있기 때문이다.

특히 디바이스의 성능이 좋아지고, 뛰어난 인터랙션이 많은 요즘 앱들의 뷰를 완벽하게 대치하기란 쉽지 않을 것이다.

xml을 통해 inflate하는 View들의 마이그레이션이 문제고, 데이터바인딩이 문제가 될것이며, Kotlin과 함께 등장한 Anko로도 충분히 간결한 뷰를 작성할 수 있기 때문이다.

압도적인 편익이 없다고 해야할까?

특히 나같은 경우 이해도가 떨어지는 MotionLayout과 ConstraintLayout을 완벽하게 대치하려면 어느정도 고도화가 필요할 것이다.

어쩌면 이 부분에 대한 기술 장벽이 있어서 정규 릴리즈까지 1년은 더 남았다고 예측하는 사람이 많은 것 아닐까?

#### versus Flutter
Q. Flutter하는 기분인데?

구글에서 만든 크로스플랫폼인 Flutter와 매우 유사하다.

Flutter를 해본 적은 없지만 Compose를 공부하면서 살짝 맛을 보았다.

크로스플랫폼은 Xamarin 이후에 처음 접해보는데, 매우 많이 발전한 것을 알 수 있었다.

Flutter 또한 UI 툴킷인데, 유사한 compose widget을 제공하고 있는 것을 확인해보면 매우 유사한 관점으로 접근하고 있음을 알 수 있다.

Compose도 Widget catalog를 잘 모아서 제공하면 UI 생산성이 엄청나게 올라갈 것으로 기대한다.

이 여세를 몰아 Product Language를 구축할 수 있는 플러그인도 나온다면?

공부할 게 늘어나긴 하겠지만, 생산성에 지대한 공헌을 할 것으로 기대 된다.

#### Preview Performance
Q. Kotlin도 Swift처럼 되나?

코드 한 줄 안 짜고 앱을 짜본 경험이 있어서 Xcode의 스토리보드 기능은 정말 끝내준다고 생각하고 있었는데, 

Android Studio에서 Compose를 통한 Preview 성능에 대해 어느정도 기대하긴 한 것은 사실이다.

개발 기기가 15인치 맥북프로 모델이었는데, 조만간 드론이 될 것 같다.

ADT 쓰던 시절보다는 낫지만 개발도구 최적화를 좀 해주면 좋을텐데.. 매우 아쉬운 부분이다.

#### DI에 대한 기본 소양
Q. 만약 Compose가 대중화된다면 DI에 대한 이해가 필수적으로 요구될까?

라이브러리에 대해서 무작정 사용하는 것으로 이해하지말고 직접 Provider를 구현하여 IoC를 개발해보는 것을 검토해보아야 겠다.

Raw한 DI를 만들어보고 Dagger에 대한 분석을 수행한다면 많은 자산이 될 것으로 기대 된다.

#### 끝 없는 디자인 패턴 논쟁
Q. 신규 앱이면 설계가 굉장히 중요해질 것이고, 기존 앱이면 마이그레이션 비용을 막대하게 치르게 될 것 같다. 
Q. 뷰의 재사용성을 염두해둔 디자인과 설계의 중요성이 대두될 것이다.

MVP vs MVVM vs MVI의 끝없는 논쟁이 이어지는 가운데 Compose가 고고하게 떠있다.

완전히 뷰를 분리할 수 있는 디자인패턴에 적용하는 것이 좋아보이는데, 과연 나는 특정 디자인패턴을 완전히 격리해서 구현할 수 있을까?

특정 뷰를 Compose로 구현하는 것을 과제로 내주었을때, 얼마나 컴포넌트 격리를 잘 수행했는지가 평가의 척도가 될 수 있어 보인다.

### 마치며

Compose라는 라이브러리 자체에 대한 고찰은 여기까지 수행하기로 하고, 다음 파트는 동작 원리에 대해 파악해보도록 하자.
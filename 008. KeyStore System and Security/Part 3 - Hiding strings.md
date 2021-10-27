## Hiding strings

앱에서 비밀번호나 API 키 등의 민감한 정보를 사용할 수 있다. 다음과 같이 일반적인 정보를 숨기기 위한 전략이 있다.

### Hard Cording

다음과 같이 소스 코드내에 숨길수 있다.

    class AndroidDeepDive **{
      private val groupName = "ADD"
      private val groupGithub = "https://github.com/AndroidDeepDive"
    }

AptTool 을 사용해서 디컴파일할 수 있으며, 그 결과, classes(숫자).dex 형태의 소스파일에서 dex2jar 를 사용해서 dex 파일을 jar 파일로 변환한다. JD GUI 툴을 통해서 소스 코드에서 해당 내역을 확인할 수 있다.

### App Data

XML 리소스에 숨길수도 있다. 

    <string name="group_name">AndroidDeepDive</string>
    <string name="group_github">https://github.com/AndroidDeepDive</string>

[strings](https://en.wikipedia.org/wiki/Strings_(Unix)) 을 이용해서 검색을 할 수 있다. strings 를 이용하면 smash-and-grab style API 키를 탐색하여 도용하기 쉽다.

### Build Config

Android Gradle 플러그인의 BuildConfig에서 숨길수도 있다.

    // build.gradle
    buildTypes {
      debug {
        minifyEnabled true
        buildConfigField "String", "groupName", "\"${groupName}\""
        buildConfigField "String", "groupGithub", "\"${groupGithub}\""
      }
    }



    // gradle.properties
    groupName=ADD
    groupGithub=https://github.com/AndroidDeepDive

버전 관리 시스템에 비밀이 노출될 위험을 최소화할 수 있는 장점이 있긴 하지만 BuildConfig 코드로 방출되기 때문에 쉽게 노출될 수 있다.

### Native C/C++ JNI

데이터를 Java 에서 네이티브 라이브러리로 옮기는 전략으로 여러 계층의 복잡서을 추가하기 때문에 리버스 엔지니어링 시도를 방해하는데 더 효과적인 전략중 하나이다. 하지만 여전히 완벽한 방법은 아니며, strings 을 통해 문자열에 취약점을 발견할 수 있다.





[https://rammic.github.io/2015/07/28/hiding-secrets-in-android-apps/](https://rammic.github.io/2015/07/28/hiding-secrets-in-android-apps/)

[https://medium.com/google-developer-experts/a-follow-up-on-how-to-store-tokens-securely-in-android-e84ac5f15f17](https://medium.com/google-developer-experts/a-follow-up-on-how-to-store-tokens-securely-in-android-e84ac5f15f17)
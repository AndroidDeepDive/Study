# KeyStore System and Security Part 3 : Hiding strings

Android 앱을 개발하다보면 특정 key를 포함해서 배포하는 경우가 있다.

APK 파일의 특성상 기본적인 압축해제부터 디컴파일까지, 다양한 수단으로 기기의 바이너리 코드에 접근할 수 있는데,

이떄 포함된 key값이 노출될 우려가 있다.

어떻게하면 key를 숨길 수 있을까?

완벽한 보안은 없지만, 최선을 다해서 숨겨보자.

### 1. Hide in the `strings.xml`

제일 먼저 문자열을 보관하는 리소스 파일인 `strings.xml`에 숨겨보자.

아래와 같은 파일 구조로 작성이 되는 경우로 볼 수 있는데

```xml
<resources>
    <string name="key">value</string>
</resources>
```

value의 일부를 알고 있는 경우, 디컴파일할 것도 없이 `grep` 명령어만 이용해도 추출할 수 있다.

디컴파일을 하게 되는 경우 `strings.xml` 파일에 원형 그대로 노출된다.

따라서 이 방법은 **패키징만 되어있을 뿐**, 보안 관점에서는 매우 나쁜 방법이다.

> jadx 예시 추가 필요

### 2. Hide in the `*.kt` (or `*.java`)

소스 코드에 숨긴다고 하여도 결국 리소스 파일과 똑같이 관리되고, 리터럴 문자열은 난독화 대상이 아니기 때문에 마찬가지로 `grep` 명령어로 추출이 가능하다.

리터럴 문자열이 아닌 별도의 `ByteArray`로 문자를 쪼개서 저장하더라도 컴파일러의 최적화로 인해 이또한 추출이 가능하다.

즉 1번과 2번 방법은 모두 보안에 신경 썼다고 말하기 어려운 방법이며, 바꾸어 말하면

숨겨야할 key(혹은 문자열)은 1번과 2번 방법으로 처리해선 안된다고도 해석할 수 있다.

### 3. Hide in the `BuildConfig.java`

`build.gradle`과 `gradle.properties`에 숨겨보는 건 어떨까?

이 또한 컴파일 후 1번, 2번 케이스와 동일해지므로 쓰면 안되는 방법이다.

### 4. Hide in the Proguard

Proguard 혹은 R8의 난독화 기능을 믿고 소스코드에 숨겨보는 건 어떨까?

key를 포함시켰다는 건, 어딘가에 해당 key를 사용하기 떄문일 것이다.

즉, key를 사용하는 메서드나 클래스등은 난독화되더라도, 중요한 key의 값은 리터럴로 관리되어서 난독화되지 않는다.

따라서 코드가 완전 공개된 것은 아닐지라도, 약간의 수고로움만 거친다면 역시 값을 찾을 수 있다.

### 5. Hide using Base64
### 6. Hide using JNI
### 7. Android KeyBox
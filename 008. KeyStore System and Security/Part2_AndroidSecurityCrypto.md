### SharedPreferences

저장하려는 key-value 의 모음이 상대적으로 작으면 사용하는 API

Context.getSharedPreferences(String, int)에 의해 반환된 preference 데이터에 접근하고 수정하기 위한 인터페이스.

preferences의 모든 특정 셋에 대해, 모든 클라이언트가 공유하는 이 클래스의 단일 인스턴스가 있다.

preferences에 대한 수정은 preferences의 값이 일관된 상태로 유지되고 저장소에 커밋될 때 제어가 유지되도록 Editor 객체를 만들어야 한다. (즉, Editor는 SharedPreferences 객체의 값을 수정하기 위해 사용되는 인터페이스이다. ) 다양한 get 메서드에서 반환된 객체는 어플리케이션에서 변경 불가능한 것으로 처리해야만 한다.

이 클래스는 강력한 일관성을 보장한다. 비싼 작업을 사용하므로 앱 속도를 저하시킬 수 있다. 손실을 묵인할 수 있는 속성 또는 자주 변경하는 속성은 다른 메카니즘을 사용해야 한다. 

### EncryptedSharedPreferences

key와 value를 암호화하는 SharedPreference의 구현으로, Android 6.0(API 23) 이상에서만 사용 가능하다.

MasterKey를 AES256_GCM_SPEC 를 통해 생성해 해당 키를 토대로 값을 암호화해 저장한다.

단순 사용법은 아래와 같다.

```java
String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

  SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
      "secret_shared_prefs",
      masterKeyAlias,
      context,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  );

  // use the shared preferences and editor as you normally would
  SharedPreferences.Editor editor = sharedPreferences.edit();
```

EncryptedSharedPreferences에서 제공하는 메서드는 thread에 안전하지 않다.

### DataStore

*프로토콜 버퍼를 사용해 key - value 또는 객체를 저장할 수 있는 데이터 저장 솔루션이다. Kotlin Coroutines or Flow를 사용해 비동기적이고 일관된 트랜잭션 방식으로 데이터를 저장한다. 소규모 단순 데이터 세트에 적합하며 부분 업데이트나 참조 무결성은 지원하지 않는다.

- Preferences DataStore : 키를 사용해 데이터를 저장하고 접근한다. 이 구현은 유형 안전성을 제공하지 않으며, 사전 정의된 스키마가 필요 없다.
- Ptoro DataStore : 맞춤 데이터 유형의 인스턴스로 데이터를 저장한다. 이 구현은 유형 안전성을 제공하며, 프로토콜 버퍼를 사용해 스키마를 정의해야 한다.

그래들 설정 방법 : [https://developer.android.com/topic/libraries/architecture/datastore?hl=ko#groovy](https://developer.android.com/topic/libraries/architecture/datastore?hl=ko#groovy) 

### Google Tink Library

애플리케이션에서 암호화를 사용하는 것이 어둠 속에서 전기톱을 저글링하는 것처럼 느껴질 필요는 없다. Tink는 구글의 암호학자와 보안 엔지니어 그룹에 의해 작성된 암호화 라이브러리이다. 구글의 제품 팀과 함께 작업하며, 구현의 약점을 수정하고, 암호화 배경 필요 없이 안전하게 사용할 수 있는 간단한 API를 제공하는 확장된 경험을 바탕으로 만들어졌다.

Tink는 올바르게 사용하기 쉽고 오용하기 어려운 보안 APIs를 제공한다. 사용자 중점 설계, 신중한 구현과 코드 리뷰, 확장된 테스트를 통해 일반적인 암호화 위험을 줄인다. 구글에서 Tink는 표준 암호화 라이브러리 중 하나이며, 수백 가지의 제품과 시스템에 배포되어 있다. 

설정 방법 : [https://github.com/google/tink](https://github.com/google/tink)

---

참고

- Protocol buffers : 구조화된 데이터를 직렬화하기 위한 구글의 언어 중립적, 플랫폼 중립적, 확장 가능한 메커니즘이다. XML을 생각할 수 있지만, 더 작고 더 빠르고 더 간단하다. 데이터가 구조화되는 방법을 한 번 정의한 다음, 특별히 생성된 소스 코드를 사용해 다양한 데이터 스트림과 다양한 언어를 사용하여 구조화된 데이터를 쉽게 쓰고 읽을 수 있다.
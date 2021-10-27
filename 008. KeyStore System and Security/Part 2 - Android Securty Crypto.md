### Android Security Crypto

#### SharedPreferences

저장하려는 키-값 컬렉션이 비교적 작은 경우 SharedPreferences API를 사용한다. SharedPreferences 객체는 키-값 쌍이 포함된 파일을 가리키며 키-값 쌍을 읽고 쓸 수 있는 간단한 메서드를 제공한다. SharedPreferences 파일은 프레임워크에서 관리하며 비공개이거나 공유일 수 있다.
다음과 같이 SharedPreferences 를 설정해서 사용할 수 있다.

```
val sharedPreferences = getSharedPreferences("filename", MODE_PRIVATE)

val editor = sharedPreferences.edit()
editor.putString("name", "value")
editor.apply();
val name = sharedPreferences.getString("name", "default")
Log.d("AndroidDeepDive", "$name")
```

SharedPreferences 에서 설정한 이름으로 xml 파일을 생성하고 위치는 다음과 같다.

```
/data/data/{package_name}/shared_prefs/{Name_of_preference}.xml
```

생성된 xml 은 다음과 같은 구조로 되어있다.

```
<?xml version='1.0' encoding='utf-8' statndalone='yes' ?>
<map>
  <string name="key">value</string>
  <string name="team">AndroidDeepDive</string>
</map>
```

민감한 데이터를 작업할때, SharedPreferences 가 데이터를 일반 텍스트로 저장한다는 점에서 눈에 띄지 않도록 민감한 데이터 암호화를 고려하는 것은 중요하다.



#### EncryptedSharedPreferences

SDK 23 이상에서 Android KeyStore 를 사용해서 암호화된 SharePrefereces 를 제공하는 EncryptedSharedPreferences를 사용할 수 있다.
EncryptedSharedPreferences 사용하기 위해 Dependency 를 추가한다.

```
dependencies {
  implementation "androidx.security:security-crypto:1.1.0-alpha03"
}
```

다음과 같이 MasterKey Alias 를 생성하고, EncryptedSharedPreferences 를 생성해서 사용할 수 있다.

```
val masterKeyAlias = MasterKey.Builder(applicationContext)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val sharedPreferences = EncryptedSharedPreferences.create(
    applicationContext,
    "filename",
    masterKeyAlias,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
val editor = sharedPreferences.edit()
editor.apply {
    putString("name", "value")
    putString("team", "AndroidDeepDive")
}
editor.apply()
val team = sharedPreferences.getString("team", "ADD")
Log.d("AndroidDeepDive", "$team")
```

SharedPreferences 에서 설정한 이름으로 xml 파일을 생성하고 위치는 다음과 같다.

```
/data/data/{package_name}/shared_prefs/{Name_of_preference}.xml
```

생성된 xml 은 다음과 같은 구조로 되어있으며, 기존 SharedPreferences 과 달리 Key와 Value 모두 암호화되어 어떤 Key 와 Value 로 구성되어 있는지 알 수 없다.

```
<map>
    <string name="ATeMCIHgmWFozVbCxpV4QsS3Mgf2xPEonQ==">AR0kWCC+wheaW5se/p3Yb2CO6nk0BccqRwKMxg8n3KEzl3NBd1sh5o4IXmZH8g==</string>
    <string name="__androidx_security_crypto_encrypted_prefs_key_keyset__">12a9016701bb6048ea6201847aa4aea20cff30a28d7b2348c25103f918f5ee017bcc75258196eb57da96a6a272ae0175d62b08602c3a64acb5e67b737d2ca8d8dc59f7d7703689e64e4004168655c13c6149e9858d917331c34ec7c2122ba89d1496c279504cd70e06122ed4a61a441efd4f852e876f04399a8b3b84e5b83ced488a0dd6bba662d71f2f1df959ef04a0341715fdca6d2ee720b47b8753e04fe249101be5616d4c3ba097828a1a44088191b0bc03123c0a30747970652e676f6f676c65617069732e636f6d2f676f6f676c652e63727970746f2e74696e6b2e4165735369764b65791001188191b0bc032001</string>
    <string name="ATeMCIH3kh3PWarzw2hxp/xSXRJzX4NQIg==">AR0kWCAPa5U7x9s/8eAAW5HyqWL1bPr5jsEckeY91UIJox5q7SBSb8MaW/SxjBjLjpWobSjBFak=</string>
    <string name="__androidx_security_crypto_encrypted_prefs_value_keyset__">12880113126e04edc22f0b68588e44eaba4bfedf7c83c07457a852f35bba74ae209a935d41f96c1fa67997282784342a51c3c8a87f7a59bb945fc9218bda2964c1fadb88d8b9239242244c6ba6cd4204e3cde89720a8ed2563a37fc816e82e51a690e2218861e4e0f4874905c8d68012ac5979604ae80b8c737fe0fe44a45ba75635dae629e18956c968541a4408a0b091e901123c0a30747970652e676f6f676c65617069732e636f6d2f676f6f676c652e63727970746f2e74696e6b2e41657347636d4b6579100118a0b091e9012001</string>
</map>
```



#### DataStore

Jetpack DataStore 는 key-value 쌍 또는 object 타입을 protocal buffers 와 함께 저장할 수 있는 데이터 저장 솔루션이다. DataStore 는 코틀린 coroutines 과 Flow 를 사용해서 비동기적이고 일관된 트랜잭션 방식으로 저장한다. SharedPreferences 의 단점을 극복하고 대체할 것이다. DataStore 는 작고 단순한 데이터 세트에 이상적이며 부분 업데이트 또는 참조 무결성은 지원하지 않는다.
DataStore 에는 Preferences DataStore 와 Proto DataStore 두가지 종류가 있다.

- Preferences DataStore
  SharedPreferences 와 마찬가지로 키를 사용해서 데이터를 저장하고 액세스한다. 스키마를 정의하거나 키가 올바른 타입으로 액세스되도록 할 수 있는 방법이 없다.

Preferences DataStore 사용하기 위해 Dependency 를 추가한다.

```
dependencies {
  implementation "androidx.datastore:datastore-preferences:1.0.0"
}
```

아래와 같이 이름을 설정해서 최상위 레벨에 DataStore 선언한다.

```
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DataStoreSample")
```

DataStore 에 접근할 key 를 설정해서 데이터를 저장하고 flow 를 통해 가져오도록 아래와 같이 작성한다. 

```
val counter = intPreferencesKey("counter")
val exampleCounterFlow: Flow<Int> = dataStore.data
  .catch { exception ->
    if (exception is IOException) {
      emit(emptyPreferences())
    } else {
      throw exception
    }
  }
  .map { preferences ->
    preferences[counter] ?: 0
  }
binding.button.setOnClickListener {
    GlobalScope.launch {
        incrementCounter()
    }
}
GlobalScope.launch {
    exampleCounterFlow.collect {
        Log.d("AndroidDeepDive", "Count = $it")
    }
}
suspend fun incrementCounter() {
  val counter = intPreferencesKey("counter")

  dataStore.edit { preferences ->
    val currentCounterValue = preferences[counter] ?: 0
    preferences[counter] = currentCounterValue + 1
  }
}
```



- Proto DataStore
  사용자 정의 데이터 유형의 인스턴스로 데이터를 저장한다. Protocol buffers 를 사용해서 스키마를 정의해야 한다. Protobufs 를 사용하면 강력한 형식 데이터를 유지할 수 있다. XML 및 다른 유사한 데이터 포맷보다 빠르고 작고 단순하고 덜 모호하다.

Proto DataStore 사용하기 위해 Dependency 를 추가한다.

```
plugins {
    id "com.google.protobuf" version "0.8.17"
}
dependencies {
  implementation  "androidx.datastore:datastore-core:1.0.0"
  implementation  "com.google.protobuf:protobuf-javalite:3.14.0"
}
protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:3.14.0"
  }

  generateProtoTasks {
    all().each { task ->
      task.builtins {
        java {
          option 'lite'
        }
      }
    }
  }
}
```

app/src/main/proto/ 디렉토리의 proto 파일에 미리 정의된 스키마를 생성해야 한다. 아래와 같이 app/src/main/proto 에 user.proto 라는 파일을 생성한다. proto 스키마 정의에 대한 자세한 내용은 [protobuf language guide](https://developers.google.com/protocol-buffers/docs/proto3) 를 참조한다.

```
syntax = "proto3";

option java_package = "com.passionvirus.keystore";
option java_multiple_files = true;

message UserSettings {
  enum BgColor {
    COLOR_WHITE = 0;
    COLOR_BLACK = 1;
  }
  BgColor bgColor = 1;
}
```

user.proto 파일을 생성후에 gradle 프로젝트를 리빌드하면 app/build/generated/source/proto/ 아래에 UserSettings.java 파일이 자동으로 생성되는것을 볼 수 있다.

Serializer<T>를 구현하는 클래스 만든다. 여기서 T 는 proto 파일에 정의된 형식이다. 여기서는 UserSettings 타입이다. serializer 는 데이터 유형을 읽고 쓰는 방법을 DataStore 에 알려준다. 아래와 같이 코드를 작성할 수 있다.

```
object ProtoSettingsSerializer : Serializer<UserSettings> {
  override suspend fun readFrom(input: InputStream): UserSettings {
    try {
      return UserSettings.parseFrom(input)
    } catch (ex: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", ex)
    }
  }

  override suspend fun writeTo(t: UserSettings, output: OutputStream) {
    t.writeTo(output)
  }

  override val defaultValue: UserSettings
    get() = TODO("Not yet implemented")
}
```

아래와 같이 이름을 설정해서 최상위 레벨에 Proto DataStore 선언한다. 

```
private val Context.dataStore: DataStore<UserSettings> by dataStore(fileName = "user_settings.pb", serializer = ProtoSettingsSerializer)
```

DataStore 에 데이터를 저장하고 flow 를 통해 가져오도록 아래와 같이 작성한다.

```
val userSettingsFlow: Flow<UserSettings> = dataStore.data.catch { ex ->
  if (ex is IOException) {
    emit(UserSettings.getDefaultInstance())
  } else {
    throw ex
  }
}
GlobalScope.launch {
  userSettingsFlow.collect {
    when(it.bgColor) {
      UserSettings.BgColor.COLOR_WHITE -> binding.main.setBackgroundColor(
        ContextCompat.getColor(
          this@MainActivity,
          android.R.color.white
        )
      )
      UserSettings.BgColor.COLOR_BLACK -> binding.main.setBackgroundColor(
        ContextCompat.getColor(
          this@MainActivity,
          android.R.color.black
        )
      )
    }
  }
}
suspend fun updateColor(bgColor: UserSettings.BgColor) {
  dataStore.updateData { userSetting ->
    userSetting.toBuilder().setBgColor(bgColor).build()
  }
}
```



#### Google Tink Library

Tink 는 암호화 및 보안 엔진니어가 작성한 오픈소스 암호화 라이브러이다. Tink의 안전하고 간단한 API 는 유저 중심 설계, 신중한 구현 및 코드 리뷰, 광범위한 테스트를 통해 일반적인 위험을 줄인다.
Tink 는 암호화에 배경 지식이 없는 사용자가 일반적인 암호화작업을 안전하게 구현할 수 있도록 도와준다. 

Tink 를 사용해야 하는 이유

- 사용하기 쉽다

암호화는 제대로하기 어렵다. Tink 를 사용하면 단 몇 줄의 코드로 암호화하거나 서명할 수 있으며 내장된 보안 보장으로 위험을 회피할수 있다.

- 안전성

Tink는 BoringSSL 및 Java Cryptography Architecture와 같은 잘 알려진 라이브러리 위에 보안 보호 기능을 추가하고 이를 인터페이스에 바로 표시하므로 감시자와 도구가 신속하게 격차를 찾을 수 있다. 또한 Tink 는 잠재적으로 위험한 API 를 분리하여 모니터링 할 수 있다.

- 호환성

Tink 암호문은 기존 암호화 라이브러리와 호환된다. 또한 Tink 는 Amazon KMS, Google Cloud KMS, Android Keystore, and iOS Keychain 에서 키의 암호화 및 저장을 지원한다.







https://striban.tistory.com/66

https://medium.com/androiddevelopers/data-encryption-on-android-with-jetpack-security-e4cb0b2d2a9

https://wise4rmgodadmob.medium.com/data-encryption-on-android-with-jetpack-security-78a230f2366

https://newbedev.com/how-to-check-if-sharedpreferences-file-exists-or-not

https://developer.android.com/training/data-storage/shared-preferences

https://proandroiddev.com/welcome-datastore-good-bye-sharedpreferences-fdeb831a1e58

https://youngest-programming.tistory.com/369

https://developers.google.com/tink
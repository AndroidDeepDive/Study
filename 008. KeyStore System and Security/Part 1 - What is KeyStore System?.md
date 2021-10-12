## Android Keystore

Android Keystore 시스템을 사용하면 암호화 키를 컨테이너에 저장하여 기기에서 키를 추출하기 어렵게 할 수 있다. 키 저장소에 키가 저장되면, 키 자료는 내보낼 수 없는 상태로 유지하면서 키를 암호화 작업에 사용할 수 있다. 이 시스템에서는 키 사용 시기와 사용 방법을 제한하는 기능도 제공한다. 예를 들어 키 사용을 위해 사용자 인증을 요구하거나, 특정 암호화 모드에서만 키를 사용하도록 제한할 수 있다. Keystore 시스템은 Android 4.0(API 수준 14)에서 도입된 KeyChain API에서 사용한다. Android 4.3(API 수준 18)에서 도입된 Android Keystore 제공자 기능 및 Jetpack의 일부로 제공되는 보안 라이브러리에서도 사용한다.

### 보안기능

* 추출차단: Android Keystore는 애플리케이션 프로세스와 Android 기기 전체에서 키 자료의 추출을 차단하여 Android 기기 외부에서 키 자료의 무단 사용을 줄인다.

* 키 사용 승인: Android Keystore는 앱에서 승인된 키 사용처를 지정하도록 하여 앱 프로세스 외부에 적용하는 방식으로 Android 기기에서 키 자료의 무단 사용을 줄인다.

### 키체인 또는 Android Keystore provider

시스템 수준의 사용자 인증 정보를 원하는 경우 KeyChain API를 사용한다. 앱에서 KeyChain API를 통해 사용자 인증 정보 사용을 요청하는 경우 사용자는 설치된 사용자 인증 정보 중 앱이 액세스할 수 있는 사용자 인증 정보를 시스템 제공 UI를 통해 선택한다. 이렇게 하면 사용자 동의를 받아 여러 앱이 동일한 사용자 인증 정보 집합을 사용할 수 있다.
개별 앱이 전용으로 액세스할 수 있는 고유한 사용자 인증 정보를 저장할 수 있게 하려면 Android Keystore 제공자를 사용한다. Android Keystore 제공자를 통해 앱에서 전용으로 사용할 수 있는 사용자 인증 정보를 관리할 수 있을 뿐만 아니라 KeyChain API가 시스템 수준의 사용자 인증 정보에 제공하는 것과 동일한 보안 이점을 얻을 수 있다. 이 방법은 사용자 인증 정보를 선택하기 위한 사용자 상호작용이 필요 없다.

* Dependency 를 추가
  security-crypto 1.1.0-alpha01 이상부터 사용할 수 있다.

    implementation "androidx.security:security-crypto:1.1.0-alpha03"

* 새 비공개 키 생성
  새 PrivateKey를 생성하려면 자체 서명 인증서에 포함될 초기 X.509 속성도 지정해야 한다. 

    val mainKey = MasterKey.Builder(applicationContext)
      .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
      .build()

    // KeyPairGenerator 는 MinSDK 23+
    val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
      KeyProperties.KEY_ALGORITHM_EC,
      "AndroidKeyStore"
    )
    val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
      "keystoreAlias",
      KeyProperties.PURPOSE_SIGN // or KeyProperties.PURPOSE_VERIFY
    ).run {
      setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
      build()
    }

    kpg.initialize(parameterSpec)

    val kp = kpg.generateKeyPair()

* 안전하게 암호화된 키 가져오기
  Android 9(API 수준 28) 이상에서는 ASN.1로 인코딩된 키 형식을 사용하여 암호화된 키를 Keystore로 안전하게 가져올 수 있다.

    KeyDescription ::= SEQUENCE {
      keyFormat INTEGER,
      authorizationList AuthorizationList
    }

    SecureKeyWrapper ::= SEQUENCE {
      wrapperFormatVersion INTEGER,
      encryptedTransportKey OCTET_STRING,
      initializationVector OCTET_STRING,
      keyDescription KeyDescription,
      secureKey OCTET_STRING,
      tag OCTET_STRING
    }

* 키 저장소 항목 작업

  val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
    load(null)
  }
  val aliases: Enumeration<String> = ks.aliases()

* 데이터 서명 및 확인

  // 데이터 서명
  val alias = "AndroidDeepDive"
  var privateKey : PrivateKey? = null

  val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").*apply *{**
    **load(null)
  }**

  **if (ks.containsAlias(alias)) {
    Log.d("AndroidDeepDive", "Alias exist already")
    val entry: KeyStore.Entry = ks.getEntry(alias, null)
    if (entry is KeyStore.PrivateKeyEntry) {
      privateKey = entry.*privateKey
    *}
  }
  else {
    Log.d("AndroidDeepDive", "Alias not exist")
    val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
      KeyProperties.*KEY_ALGORITHM_EC*,
      "AndroidKeyStore"
    )

    val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
      alias,
      KeyProperties.*PURPOSE_SIGN *// or KeyProperties.PURPOSE_VERIFY
  // KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
  ).*run *{
      setDigests(
        KeyProperties.*DIGEST_SHA256*,
        KeyProperties.*DIGEST_SHA512
      *)
      build()
    }**

    **kpg.initialize(parameterSpec)

    val kp = kpg.generateKeyPair()
    privateKey = kp.*private
  *}

  val data = "blahblah".*toByteArray*(Charset.defaultCharset())
  val signature: ByteArray = Signature.getInstance("SHA256withECDSA").*run ***{
    **initSign(privateKey)
    update(data)
    sign()
  **}**

  // 데이터 확인
  val entry = ks.getEntry(alias, null) as? KeyStore.PrivateKeyEntry
  if (entry != null) {
    val valid: Boolean = Signature.getInstance("SHA256withECDSA").*run *{**
    **initVerify(entry.*certificate*)
    update(data2)
    verify(signature)
  }**

    **Log.d("AndroidDeepDive", "signature: $valid")
  }
  else {
      Log.w("AndroidDeepDive", "Not an instance of a PrivateKeyEntry")
  }



## Trusted Execution Environment(TEE)

TEE 는 메인 프로세세의 보안 영역이다. 내부에 로드된 코드와 데이터가 기밀성과 무결성과 관련하여 보호되도록 보장한다. 격리된 실행 환경인 TEE 는 격리된 실행, TEE 로실행되는 어플리케이션의 무결성과 같은 보안 기능을 제공한다. 일반적으로 TEE 는 rich OS 보다 디바이스에서 실행되는 신뢰할 수 있는 응용프로그램에 대해 더 높은 수준의 보안을 제공하고 SE(secure element) 보다 더 많은 기능을 제공하는 실행 공간을 제공한다. 

A **trusted execution environment** (**TEE**) is a secure area of a [main processor](https://en.wikipedia.org/wiki/Central_processing_unit). It guarantees code and data loaded inside to be protected with respect to confidentiality and integrity[[*clarification needed](https://en.wikipedia.org/wiki/Wikipedia:Please_clarify)*].[[1]](https://en.wikipedia.org/wiki/Trusted_execution_environment#cite_note-oulpita.com-1) A TEE as an isolated execution environment provides security features such as isolated execution, integrity of applications executing with the TEE, along with confidentiality of their assets.[[2]](https://en.wikipedia.org/wiki/Trusted_execution_environment#cite_note-2) In general terms, the TEE offers an execution space that provides a higher level of security for trusted applications running on the device than a rich operating system (OS) and more functionality than a ‘secure element’ (SE).

TEE(신뢰할 수 있는 실행 환경)는 주 프로세서의 보안 영역입니다. 내부에 로드된 코드와 데이터가 기밀성과 무결성과 관련하여 보호되도록 보장합니다[설명 필요].[1] 격리된 실행 환경인 TEE는 자산의 기밀성과 함께 격리된 실행, TEE로 실행되는 애플리케이션의 무결성과 같은 보안 기능을 제공합니다.[2] 일반적으로 TEE는 풍부한 운영 체제(OS)보다 장치에서 실행되는 신뢰할 수 있는 응용 프로그램에 대해 더 높은 수준의 보안을 제공하고 ‘보안 요소’(SE)보다 더 많은 기능을 제공하는 실행 공간을 제공합니다.





https://developer.android.com/training/articles/keystore?hl=ko

https://source.android.com/security/keystore?hl=ko

https://en.wikipedia.org/wiki/Trusted_execution_environment

https://linsoo.pe.kr/archives/28119

https://linsoo.pe.kr/archives/28144
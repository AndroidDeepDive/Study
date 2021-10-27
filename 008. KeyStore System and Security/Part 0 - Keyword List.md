## Keystore System & Security

## Security Basic

* Encrypt/Decrypt (암호화 / 복호화)
  * Encrypt: 암호화에서, 암호화는 정보를 인코등하는 프로세스이다. 일반 텍스트로 알려진 정보의 원래 표현을 암호문으로 알려진 대체 형식으로 변환한다. 이상적으로는 승인된 사람만이 암호문을 다시 평문으로 해독가능하며 원본 정보에 접근 가능하다. 그 자체로 간섭을 방지하는 것이 아니라 잠재적인 인터셉터가 이해할 수 있는 콘텐츠 자체를 거부한다.
  * Decrypt: 암호화된 데이터를 사람이 읽고 이해할 수 있는 형태로 변환하는 프로세스이다. 텍스트를 수동으로 암호화를 해체하거나 원본 데이터를 암호화하는데 사용되는 키를 사용하여 복호화를 수행한다.

* Symmetric Key (대칭키)
  대칭 키 암호에서는 암호화를 하는 측과 복호화를 하는 측이 같은 암호 키를 공유한다. 대부분의 대칭 키 암호는 공개 키 암호와 비교하여 계산 속도가 빠르다. 대칭 키 암호는 암호화하는 단위에 따라 Block Cipher 와 Stream Cipher 가 있다.
  
  * DES: 암호화 및 복호화 키가 동일하고 비대칭에 비해 속도가 빠르다.
  * AES: DES를 대체한 암호 알고리즘이며 암호화와 복호화 과정에서 동일한 키를 사용한다.
  
  * Block Cipher: 한번에 텍스트를 블록 단위로 가져와서 암호화한다. 64 bit 또는 64 bit이상을 사용한다. 암호화의 복잡성은 간단하다. 복호화하는 과정이 어렵다. 스트림 방식보다 느리다.
  * Stream Cipher: 한번에 텍스트를 1바이트 단위로 가져와서 암호화한다. 8 bit 를 사용한다. 암호화의 복잡성은 복잡하다. 복호화하는 과정이 쉽다. 블락 방식보다 빠르다.
  
* Hash
  프로그램이나 파일이 원본 그대로인지를 확인하는 무결성 검사 등에 사용된다.
  * MD5: 임의의 길이의 메시지(variable-length message)를 입력받아, 128비트짜리 고정 길이의 출력값을 낸다. 암호화 결함이 발견되어 보안 용도로 사용할 때에는 SHA와 같은 다른 알고리즘을 사용하는 것이 권장된다.
  * sha-256: SHA(Secure Hash Algorithm) 알고리즘의 한 종류로서 어떤 길이의 값을 입력하더라도 256 bit 의 고정된 결과값을 반환한다. 단방향 암호화 방식이기 때문에 복호화가 불가능하다. 복호화를 하지 않아도 되기 때문에 속도가 빠르며, 비밀번호를 확인하는 용도등으로 사용 가능하다.

* Checksum
우발적인 변경을 방지하기 위한것이다. 1 byte 가 변경되면 체크섬이 변경된다. 체크섬은 악의적인 변경으로부터 보호하기에는 안전하지 않다. 특정 체크섬으로 파일을 만드는 것은 매우 쉽다.

* MAC : Message Authentication Code
  MAC(메시지 인증코드) 또는 태그는 컴퓨터 사용자가 계정이나 포털에 액세스하기 위해 입력하는 보안 코드이다. 이 코드는 사용자가 보낸 메시지 또는 요청에 첨부된다. MAC 은 사용자 액세스 권한을 부여하기 위해 수신된 시스템에서 인식할 수 있어야 한다.
  * HMAC(Hash-based MAC): 송신자, 수신자 공유된 비밀키를 가지면 송신자가 키와 메시지를 해시를 생성해서 메시지와 해시를 전송하면 수신자는 수신된 해시와 수신 받은 메시지를 통해 생성한 해시를 비교한다. 
  * CMAC(Cipher-based MAC): 누군가가 중간에서 메시지와 MAC을 가로채서, 메시지를 변조시키고, MAC도 다시 새롭게 만들어서 보낼 수 있다. 이런 경우에 수신자는 메시지의 MAC과 받은 MAC이 일치하므로 메시지가 변조된지 알 길이 없다. 이러한 단점을 보안한 것이 CMAC 이며, 메시지 암호화 과정에서 나온 결과물을 MAC으로 사용한다. 

* Asymmetric Key (비대칭키)
  비대칭암호화는 두 개의 별개의 관련 키를 사용한다. 하나의 키인 공개 키는 암호화에 사용되고 다른 하나인 개인 키는 복호화에 사용한다. 이름에서 알 수 있듯이 개인키는 인증된 수신자만 메시지를 해독할 수 있도록 비공개로 되어있다.
  * Diffie-Hallman: 상대방의 공개키와 나의 개인키를 이용하여 계산을 하면 비밀키가 나온다. 이 비밀키를 사용해서 데이터를 암호화해서 보내고 복호화해서 해석한다.
  * RSA: 공개키 암호 알고리즘의 하나로서 세계적으로 사실상의 표준이다. 인수분해 문제 해결의 높은 난이도를 이용해 암호화뿐만 아니라 전자 서명의 용도로도 사용된다. 모두에개 공개하는 공개키(Public key)와 공개해서는 안되는 개인키(Private Key) 로 구성된다. 공개키는 메시지를 암호화할때 사용하고, 개인키는 암호화된 메시지를 복호화할때 사용한다.
  * ECC: 암호키 길이가 길어지면 보안 강도는 높아지지만 속도가 느려집니다. 하지만, ECC(Elliptic Curve Cryptography) 를 사용하면 짧은 키로도 동일한 암호 성능을 가지는데, 이는 컴퓨터 성능이 낮아도 암호 성능을 유지할 수 있다. 이러한 이유로 RSA를 대체할 차세대 공개키 암호기술로 부상하고 있다.







https://en.wikipedia.org/wiki/Encryption

[https://ko.wikipedia.org/wiki/%EB%8C%80%EC%B9%AD_%ED%82%A4_%EC%95%94%ED%98%B8](https://ko.wikipedia.org/wiki/대칭_키_암호)

https://www.geeksforgeeks.org/difference-between-block-cipher-and-stream-cipher/

https://newbedev.com/checksum-vs-hash-differences-and-similarities

https://findanyanswer.com/what-is-checksum-byte

https://stackoverflow.com/questions/460576/hash-code-and-checksum-whats-the-difference

https://bamdule.tistory.com/233

https://ddongwon.tistory.com/38

https://cheapsslsecurity.com/blog/what-is-asymmetric-encryption-understand-with-simple-examples/

https://www.crocus.co.kr/1233

[https://yjshin.tistory.com/entry/%EC%95%94%ED%98%B8%ED%95%99-%EB%8C%80%EC%B9%AD%ED%82%A4-%EC%95%94%ED%98%B8-DESData-Encryption-Standard](https://yjshin.tistory.com/entry/암호학-대칭키-암호-DESData-Encryption-Standard)

https://www.crocus.co.kr/1230

[https://yjshin.tistory.com/entry/%EC%95%94%ED%98%B8%ED%95%99-%EB%B9%84%EB%8C%80%EC%B9%AD%ED%82%A4-%EC%95%94%ED%98%B8-RSA-%EC%95%94%ED%98%B8%EC%8B%9C%EC%8A%A4%ED%85%9C](https://yjshin.tistory.com/entry/암호학-비대칭키-암호-RSA-암호시스템)

https://medium.com/humanscape-tech/blockchain-elliptic-curve-cryptography-ecc-49e6d7d9a50a

https://developer-mac.tistory.com/83

## ๐์ฝ๋ฃจํด์ ๋์์ฑ ๋ด๋ถ
`suspend function`์ `CPS`๋ก ์ํ๋๋ค. ์ด๋ ํธ์ถ๋๋ ํจ์์ continuation์ ๋ณด๋ด๋ ๊ฒ์ ์ ์ ๋ก ํ๊ณ ์์ด, ํจ์๊ฐ ์๋ฃ๋๋๋๋ก continuation์ ๊ณ์ ํธ์ถํ  ๊ฒ์ด๋ค.(์ด๋ฅผ callback์ผ๋ก๋ ์๊ฐํ  ์ ์๋ค.) suspend function์ด ๋ค๋ฅธ suspend function์ ํธ์ถํ  ๋๋ง๋ค ์๋ฃ ํน์ ์ค๋ฅ๊ฐ ๋ฐ์ํ์ ๋ ํธ์ถ๋์ด์ผ ํ๋ continuation์ ์ ๋ฌํ๊ฒ ๋๋ค. 
suspend function์ ์ํ ๋จธ์ ์ผ๋ก ๋ณํ๋๋๋ฐ ์ํ๋ฅผ ์ ์ฅํ๊ณ  ๋ณต๊ตฌํ๋ฉฐ ํ๋ฒ์ ์ฝ๋์ ํ ๋ถ๋ถ์ ์คํํ๊ฒ ๋๋ค. ๊ทธ๋์ suspend function์ ์ฌ๊ฐํ  ๋๋ง๋ค ์ค๋จ๋ ์์น์์ ์ํ๋ฅผ ๋ณต๊ตฌํ๊ณ  ์คํ์ ์ง์ํ  ์ ์๋ ๊ฒ์ด๋ค. CPS์ ์ํ๋จธ์ ์ด ๊ฒฐํฉํ๋ฉด ์ปดํ์ผ๋ฌ๋ ๋ค๋ฅธ ์ฐ์ฐ์ด ์๋ฃ๋๊ธฐ๋ฅผ ๊ธฐ๋ค๋ฆฌ๋ ๋์ suspend ๊ฐ๋ฅํ function์ ์์ฑํ๊ฒ ๋๋ค. 

### Continuation 
๋ชจ๋  ๊ฒ์ suspend function์ ๋น๋ฉ ๋ธ๋ก์ด๋ผ๊ณ  ๋ณผ ์ ์๋ `์ฐ์์ฒด(continuation)`์์๋ถํฐ ์์ํ๊ฒ ๋๋ค. ์ด ์ฐ์์ฒด๋ ์ฝ๋ฃจํด์ ์ฌ๊ฐํ  ์ ์๋ ์ค์ํ ์ธํฐํ์ด์ค์ด๋ค.

``` kotlin
interface Continuation<in T> {
  val context: CoroutineContext

  // ์ผ์ ์ค๋จ์ ์ผ์ผํจ ์์์ ๊ฒฐ๊ณผ์ด๋ค. 
  // ํด๋น ํจ์๊ฐ Int ๋ฅผ ๋ฐํํ๋ ํจ์๋ฅผ ํธ์ถํ๊ธฐ์ํด ์ผ์์ค์ง๊ฐ ๋๋ฉด, T ๊ฐ์ Int ๊ฐ ๋๋ค.
  fun resume(value: T)

  // ์์ธ๋ฅผ ์ ํํ  ๋ ์ฌ์ฉ๋๋ค.
  fun resumeWithExceptioin(exception: Throwable)
} 
```

### suspend 
์ฝ๋ฃจํด์์๋ `suspend`๋ผ๋ ํ์ ์๋ฅผ ์ถ๊ฐํด์, ์ฃผ์ด์ง ๋ฒ์์ ์ฝ๋๊ฐ ์ฐ์์ฒด๋ฅผ ์ฌ์ฉํด ๋์ํ๋๋ก ์ปดํ์ผ๋ฌ์๊ฒ ์ง์ํ  ์ ์๋๋ก ๋ง๋ค์ด์ก๋ค. ๊ทธ๋์, suspend function์ด ์ปดํ์ผ ๋  ๋๋ง๋ค ๋ฐ์ดํธ์ฝ๋๊ฐ ํ๋์ ์ปค๋ค๋ ์ฐ์์ฒด๊ฐ ๋๋ค.

``` kotlin
  suepend function getUserSummary(id: Int): UserSummary {
    logger.log("fetching summary of $id")
    val profile = fetchProfile(id) // suspend function
    val age = calcuateAge(profile.dateOfBirth)
    val terms = validateTerms(profile.country, age) //suspend function
    return UserSummary(profile, age, terms)
}
```
suspend function ์ธ `getUserSummary()`์ ์คํ์ ์ ์ดํ๊ธฐ ์ํด ์ฐ์์ฒด๋ฅผ ์ฌ์ฉํ  ๊ฒ์ด๋ฉฐ, ๋ ๋ฒ์ ์ผ์์ค์ง๊ฐ ์ด๋ฃจ์ด์ง ๊ฒ์ด๋ค.



## ๐์ํ ๋จธ์ 
์ปดํ์ผ๋ฌ๊ฐ ์์ suspend function ์ฝ๋๋ฅผ ๋ถ์ํ๋ ๋ฐฉ์์ ๋ํด ์์๋ณด์.

### 1. ๋ผ๋ฒจ
์ปดํ์ผ๋ฌ๋ ์คํ์ด ์์๋๋ ๋ถ๋ถ๊ณผ ์คํ์ด ์ฌ๊ฐ๋  ์ ์๋ ๋ถ๋ถ์ ๋ผ๋ฒจ์ ํฌํจํ๊ฒ ๋๋ค.

``` kotlin
  suepend function getUserSummary(id: Int): UserSummary {
    // label 0 -> ์ฒซ ๋ฒ์งธ ์คํ
    logger.log("fetching summary of $id")
    val profile = fetchProfile(id) // suspend function
    
    // label 1 -> resume
    val age = calcuateAge(profile.dateOfBirth)
    val terms = validateTerms(profile.country, age) //suspend function

    // label 2 -> resume
    return UserSummary(profile, age, terms)
}
```
์ด๋ when ๊ตฌ๋ฌธ์ผ๋ก ๋ถ๋ฆฌ๋์ด ํํ๋๋ค.

```kotlin
when(label) {
  0 -> {
    logger.log("fetching summery of $id")
    fetchProfile(id)
    return
  }
  
  1 -> {
    calculateAge(profile.dateOfBirth)
    validateTerms(profile.country, age)
    return
  }
  
  2-> UserSummary(profile, age, terms)
}
```

### 2. ์ฐ์์ฒด
๋ค๋ฅธ ์ง์ ์์ ์คํ์ ์ฌ๊ฐํ  ์ ์๋๋ก ์ฝ๋๊ฐ ๋ณํ์ด ๋์๋ค๋ฉด, ์ด์  ํจ์์ ๋ผ๋ฒจ๋ก ์ด๋ป๊ฒ ๋๋ฌํด์ผํ๋์ง ์ฐพ์์ผ ํ๋ค. 
๋ผ๋ฒจ์ ์์ฑ์ ํฌํจํ๊ณ  ์๋ ์ฐ์์ฒด์ ์ถ์์ฒด๋ฅผ ๊ตฌํํ๊ณ  ์๋  `CoroutineImpl` ๋ฅผ ๊ตฌํํด๋ณด์. 

```kotlin
suspend fun getUserSummary(id: Int, cont: Continuation<Any?>): UserSummary {
  val sm = object : CoroutineImpl {
    override fun doResume(data: Any?, exception: Thowable?) {
      getUserSummary(id, this)
    }

    val state = sm as CoroutineImpl
    when(state.label) {
      ....
    }
}
```
`doResume()` ์ด `getUserSummary()`๋ก ์ฝ๋ฐฑ์ ์ ๋ฌํ  ์ ์๊ฒ ๋๋ค. ํธ์ถํ๋ ์ชฝ์ด, ์ค๋จ๊ณผ ์ฌ๊ฐ๋ ๊ฐ์ด ์ด๋ฃจ์ด์ ธ์ผํ๊ธฐ ๋๋ฌธ์ resume์์ `getUserSummary()`๊ฐ ์๋ฃ๋์์ ๋ cont ๋ง์ ์ธ์๋ก ๋ฐ๋ ๊ฒ์ด๋ค. 

### 3. ์ฝ๋ฐฑ & ๋ผ๋ฒจ ์ฆ๊ฐ
๋ผ๋ฒจ์ ์ด์ฉํด ํน์  ์์ ์์ ์ฌ๊ฐ๊ฐ ๊ฐ๋ฅํ๊ฒ๋์๋ค๋ฉด, `getUserSummary()` ๋ก๋ถํฐ ํธ์ถ๋ ๋ค๋ฅธ suspend function ์ด CoroutineImple๋ฅผ ์ ๋ฌ๋ฐ์ ์ ์์ด์ผ ํ๋ค. 
``` kotlin
when(state.label) {
0 -> {
    logger.log("fetching summery of $id")
    sm.label = 1
    fetchProfile(id, sm)
    return
  }
  
  1 -> {
    calculateAge(profile.dateOfBirth)
    sm.label = 2
    validateTerms(profile.country, age, sm)
    return
  }
  
  2-> UserSummary(profile, age, terms)
}
```
`fetchProfile()` ๊ณผ `validateTerms()`๊ฐ `Continuation<Any?>` ๋ฅผ ์์ ํ๊ณ , ์คํ์ด ์๋ฃ๋๋ ์์ ์ `doResume()`์ด ํธ์ถ๋๋๋ก ์์ ๋์๋ค. ๊ทธ๋ฌ๋ฉด, ์ด ๋๊ฐ์ ํจ์๊ฐ ์คํ์ด ๋๋  ๋๋ง๋ค ์์ ํ๋ ์ฐ์์ฒด๋ฅผ ํธ์ถํ๊ฒ ๋๋๋ฐ, `getUserSummary()`์ ๊ตฌํ๋ ์ฐ์์ฒด์์ ์คํ์ ์ฌ๊ฐํ  ์ ์๋ค.

๋ํ ๋ค๋ฅธ suspend function์ด ํธ์ถ๋๊ธฐ์ ์ ๋ผ๋ฒจ์ ์ฆ๊ฐ์์ผ ๋ค๋ฅธ ํจ์๋ฅผ ํธ์ถ์ํฌ ์ ์๋๋ก ๋ง๋ ๋ค.
## **성능 테스트**

테스트할 라이브러리 종류는 총 3가지(Coil, Glide, Fresco)다.
디바이스는 `Pixel 4a`, 값 체크는 `Android Studio Profiler`를 사용하였다.

```
 각 테스트는 동일한 조건에서 테스트 하였고 추가 옵션 없이 기본 세팅으로 테스트하였다.
 이미지 로딩 전 후로 CPU, Memory, Disk를 10번 체크 후 평균에 대한 값을 확인하였다.
 단독 실행으로 진행되었으며 스토리지가 증가한 이후에는 스토리지를 비우고 다시 확인하였다.
```

```
Picasso도 테스트 하였지만 대부분의 큰 이미지를 잘 불러오지 못해 제외하였다.
```

### **거대한 이미지 하나를 출력해보는 경우**
---

이미지는 7,680 x 4,320(24-bit color) 4.29MB 이미지를 사용하였다.

![one big image profiling](https://i.imgur.com/m7itaqy.png)

|이름| CPU | Memory | Disk |
|---|---|---|---|
|Coil|38.3%|4.26MB|X|
|Glide|40.8%|3.41MB|0.21MB|
|Fresco|40.2%|8.14MB|X|

```
1개의 커다란 이미지를 로딩할 때에는 큰 성능 차이는 찾지 못했다.
```


### **동일한 이미지를 여러 번 출력해보는 경우 (Chacing)**
---
확실한 성능 차이를 확인하기 위해,
위에서 사용한 이미지 50개 ScrollView에 추가해서 확인해보았다.

![50 big image profiling](https://i.imgur.com/BntHJ2Y.png)

|이름| CPU | Memory | Disk |
|---|---|---|---|
|Coil|93.6%|350.8MB|X|
|Glide|45.4%|4.6MB|0.21MB|
|Fresco|72.4%|8.4MB|X|

Coil의 Cpu, Memory 사용량이 매우 크게 증가하였다.

Glide는 앱을 끈 후 다시 로딩했을 때 딜레이가 없었어 재로딩 했을 때 매우 효율적으로 동작하였다.

아래 세부 프로파일링을 보면 Coil이 Coroutine을 사용할 때 CPU, Memory가 정말 많이 사용된다는 것을 알 수 있다.

![50 big image profiling coil](https://i.imgur.com/rb1bMMt.png)
```
    Coil 세부 프로파일링
```
![50 big image profiling glide](https://i.imgur.com/0oDUU69.png)
```
    Glide 세부 프로파일링
```

```
Glide가 하나의 이미지를 여러개 불러올 때 제일 효율이 좋아보였다.
```

### **여러 이미지를 각각 출력해보는 경우**
---
동일한 이미지가 아니라 각각 다른 이미지 여러개를 출력할 때에는 어떻게 다른지 확인해보았다.
2~10MB의 큰 이미지 10개를 ScrollView에 추가해서 확인해보았다.

![10 different image profiling](https://i.imgur.com/TIrybR8.png)

|이름| CPU | Memory | Disk |
|---|---|---|---|
|Coil|88.6%|800MB|X|
|Glide|89%|303.3MB|4.6MB|
|Fresco|92.6%|276MB|X|


```
대체적으로 모두 CPU, Memory사용량이 올라갔다.
그 중 Coil이 제일 많은 Memory를 사용하였다.
```

### **RecyclerView 기반으로 출력해보는 경우 (Chacing / Disk)**
---
위에서 사용한 이미지(2~10MB의 큰 이미지 10개)들을 ScrollView가 아닌 RecyclerView에 추가해서 확인해보았다.

![10 different image recyclerView profiling](https://i.imgur.com/QafKUB1.png)

|이름| CPU | Memory | Disk |
|---|---|---|---|
|Coil|94%|800MB|X|
|Glide|94%|270MB|4.52MB|
|Fresco|94.6%|281MB|X|

```
Coil의 경우 ScrollView에 로딩할 때보다 CPU사용량이 올라갔다.
```


### 성능 테스트 소감
---
Coil이 Coroutine으로 만들어져 기대를 많이 하고 테스트를 해보았는데 생각보다 CPU, Memory의 사용량이 많았다.

Gilde는 정말로 모든 부분에서 최적화가 잘 되어있다고 생각했다.

Picasso는 잘 사용하고 싶다면 정말 커스텀을 많이, 잘 해야될 필요가 있어보인다.

Fresco는 최적화가 정말 잘 되어있지만 러닝커브가 높다. 퍼포먼스 측면에서는 Glide와 제일 비슷하였다.

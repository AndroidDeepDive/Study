## **성능 테스트**

테스트할 라이브러리 종류는 총 3가지(Coil, Glide, Fresco) 입니다.

각 테스트는 동일한 조건에서 테스트 하였고 추가 옵션 없이 기본 세팅으로 사용하였습니다.

이미지 로딩 전 후로 CPU, Memory, Disk를 10번 체크 후 평균에 대한 값입니다.

-> 값 체크는 `Android Studio Profiler`를 사용하였습니다.

단독 실행으로 진행되었으며 스토리지가 증가한 이후에는 스토리지를 비우고 다시 확인하였습니다.

```
Picasso도 테스트 하였지만 대부분의 큰 이미지를 잘 불러오지 못하였습니다.
```

### **거대한 이미지 하나를 출력해보는 경우**
---

이미지는 7,680 x 4,320(24-bit color) 4.29MB 이미지를 사용하였습니다.

|이름| CPU | Memory | Disk |
|---|---|---|---|
|Coil|21%|10MB|X|
|Glide|20%|4MB|0.21MB|
|Fresco|27%|10MB|X|

```
1개의 이미지를 출력하니 커다란 차이점은 찾지 못했습니다.
```


### **동일한 이미지를 여러 번 출력해보는 경우 (Chacing)**
---
7,680 x 4,320(24-bit color) 4.29MB 이미지를 50개 ScrollView에 추가해서 확인해보았습니다.

|이름| CPU | Memory | Disk |
|---|---|---|---|
|Coil|92%|420MB|0.01MB|
|Glide|37%|6MB|0.21MB|
|Fresco|50%|10MB|X|

Coil의 Cpu, Memory 사용량이 매우 많았습니다.

Storage 사용량이 증가한 라이브러리들 중 Glide는 앱을 끈 후 다시 로딩했을 때 딜레이가 없었는데

Coil은 다시 불러오는듯이 딜레이가 있었습니다.

```
Glide가 하나의 이미지를 여러개 불러올 때 제일 효율이 좋아보였습니다.
```

### **여러 이미지를 각각 출력해보는 경우**
---

2~10MB의 큰 이미지 10개를 ScrollView에 추가해서 확인해보았습니다.

|이름| CPU | Memory | Disk |
|---|---|---|---|
|Coil|78%|800MB|X|
|Glide|85%|324MB|4.6MB|
|Fresco|89%|300MB|X|

```
대체적으로 모두 CPU, Memory사용량이 올라갔습니다.
```

### **RecyclerView 기반으로 출력해보는 경우 (Chacing / Disk)**
---
2~10MB의 큰 이미지 10개를 RecyclerView에 추가해서 확인해보았습니다.

|이름| CPU | Memory | Disk |
|---|---|---|---|
|Coil|91%|800MB|0.01MB|
|Glide|82%|270MB|4.5MB|
|Fresco|89%|290MB|X|

```
Coil의 경우 ScrollView에 로딩할 때보다 CPU사용량이 올라갔습니다.
```


### 성능 테스트 소감
---
Coil이 Coroutine으로 만들어져 기대를 많이 하고 테스트를 해보았는데 생각보다 CPU, Memory의 사용량이 많았습니다.

Gilde는 정말로 모든 부분에서 최적화가 잘 되어있다고 생각했습니다.

Picasso는 잘 사용하고 싶다면 정말 커스텀을 많이, 잘 해야될 필요가 있어보입니다.

Fresco는 최적화가 정말 잘 되어있지만 러닝커브가 높다. 퍼포먼스 측면에서는 Glide와 제일 비슷하였습니다.
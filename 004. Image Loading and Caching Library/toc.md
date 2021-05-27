## Image Loading and Caching Library TOC

### Part 1 - 이미지 라이브러리를 사용해야하는 이유
- ImageView / Bitmap
  - Widget 및 Component 설명
- Andorid api만으로 이미지 렌더링하기
  - 고려해야하는 점
    - Out of memory
    - Slow Loading
        - Bitmap Caching
        - Memory Cache
        - LRU Algorithm
    - Unresponse UI
        - Gabarge collector issue
        - SoftReference / WeakReference
- Image Library 소개
  - Picasso
  - Glide
  - Coil
  - Fresco
  - AMUL
  - Volley

### Part 2 - Image Library별 특징과 취사 선택
- Picasso
- Glide
- Coil
  - Jetpack Compose
  - kotlin base
- Fresco
  - WebP
- 어떤 케이스에 어떤 라이브러리가 적합한가?

### Part 3 - 성능 테스트
- 1. 거대한 이미지 하나를 출력해보는 경우
- 2. 동일한 이미지를 여러 번 출력해보는 경우 (Chacing) 
- 3. 여러 이미지를 각각 출력해보는 경우
- 4. RecyclerView 기반으로 출력해보는 경우 (Chacing / Disk)
- 5. Trasform

### Part 4 - 마무리, 대표적인 서비스들은 어떤 이미지 라이브러리를 쓰고 있을까?

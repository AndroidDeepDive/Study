

## 🏃‍♂️ Android Memory
이미지 로더 라이브러리를 사용하지 않은 상태에서 많은 이미지를 사용하거나 고해상도 이미지를 이미지뷰에 로드해야하는 경우 메모리 부족으로 OOM이 발생하게 된다. 요즘 나오는 스마트폰은 굉장히 많은 메모리를 가지고 태어나는데 `왜 이정도도 못 버티지(?)` 라는 생각을 한다면, 안드로이드는 앱 내에서 사용할 수 있는 힙 메모리가 정해져있기 때문이다. 

![](https://images.velog.io/images/jshme/post/49b4df11-5ceb-4492-9d4f-f40655655291/IMG_06A0242D2ABE-1.jpeg)

Android의 메모리 모델은 운영체제 버전에 따라 두가지로 나뉘게 된다. 

* Dalvik heap 영역 : java 객체를 저장하는 메모리
* External 영역 : native heap의 일종으로 네이티브의 비트맵 객체를 저장하는 메모리
Dalvik heap 영역와 External 영역의 `Dalvik heap footprint + External Limit`을 합쳐 프로세스당 메모리 한계를 초과하면 OOM이 발생하게 된다. 

### 🏃‍♂️ Footprint
> java의 footprint는 한 번 증가하면 크기가 다시 감소되지 않기 때문에, footprint가 과도하게 커지지 않게끔 잘 관리해야한다. 

![](https://images.velog.io/images/jshme/post/840b5317-446b-413e-933b-20e50307c18f/image.png)

Dalvik VM은 처음에 동작에 필요한 만큼만 프로세스에 Heap을 할당하게 되고, 프로세스에 할당된 메모리보다 많은 메모리를 필요하게될 때마다 Dalvit Footfrint도 증가하게된다. 하지만 증가된 Footfrint는 결코 감소하지 않기 때문에 java 객체가 사용가능한 메모리 공간의 여유가 있어도, External heap의 크기가 증가되면 OOM이 발생할 수 있다. 하지만 이런 문제도 Honeycomb 이후부터는 Dalvik heap 과 External 영역이 합쳐졌기 때문에, 고려할 필요가 없어졌다. 

External 영역을 사용하는 Honeycomb 미만 버전에서는 이미지를 많이 사용하고있는 화면에서 화면을 전환하는 행동이 발생했을 때도 OOM이 발생하면서 앱이 중지될 것이다. 화면을 전환하면 이전 액티비티 인스턴스에 있던 이미지뷰나 할당되었던 비트맵이 함께 소멸되어 메모리가 회수되고 새로운 액티비티 인스턴스를 생성할텐데, 이 과정에서 이전 액티비티 인스턴스의 비트맵 객체가 회수되지 않아 메모리 누수가 발생했기 때문이다. 

비트맵 객체에 대한 참조가 없는데도 왜 회수가 될 수 없을까?🤔 Honeycomb 미만 버전에서는 Java 비트맵 객체는 실제 비트맵 데이터를 가지고 있는 곳을 가리키는 포인터일 뿐이고 실제 데이터는 External 영역인 Native Heap 영역에 저장되기 때문이다. Java 비트맵 객체는 참조가 없을 때 GC에 의해 회수되지만 Native Heap 영역은 GC 수행영역 밖이기 때문에 메모리 소멸 시점이 다르다. 

이러한 문제점 때문에, Honeycomb 이후 버전에서는 External 영역이 없어지면서 Dalvik heap 영역에 비트맵 메모리를 올릴 수 있게 되었고 GC도 접근할 수 있게 되었다.

다시 돌아와, 만약 고해상도 이미지를 로드할 때 OOM이 발생하는 경우 BitmapFactory 객체를 이용해 다운샘플링, 디코딩 방식을 선택해 적절하게 뷰에 로드하면 된다. 하지만 많은 이미지를 사용하게 되면서 OOM이 발생한다면, `이미지 캐싱`을 이용해보는 것이 어떨까?

## 🏃‍♂️ Bitmap Caching

이미지가 화면에서 사라지고 다시 구성할 때 이미지를 매번 로드하는 것은 성능상으로나 사용자 경험에 좋지 않다. 이럴 때 메모리와 디스크 캐시를 이용하여 어디에선가 저장되어있던 비트맵을 다시 가져온다면 다시 로드하는 시간도 단축시킬 수 있으며 성능 개선도 가능할 것이다. 이 때 캐싱을 위해 `Memory Cache`와 `Disk Cache` 사용을 추천하는데 두가지가 어떤 차이점이 있는지 알아보자.

### 1. Memory Cache
Memory Cache는 어플리케이션 내에 존재하는 메모리에 비트맵을 캐싱하고, 필요할 때 빠르게 접근가능하다. 하지만 Memory Cache도 곧 어플리케이션 용량을 키우는 주범이 될 수 있기 때문에 많은 캐싱을 요구하는 비트맵의 경우에는 Disk Cache에 넣는 것이 더 좋을 수 있다.


### 2. Disk Cache
Memory Cache에 넣기엔 많은 캐시를 요구하는 경우, 혹은 앱이 백그라운드로 전환되어도 적재한 캐시가 삭제되지 않기를 바란다면 Disk Cache를 이용하는 것이 좋다. 하지만 Disk로부터 캐싱된 비트맵을 가져올 때는 Memory에서 로드하는 것보다 오랜시간이 걸린다.


## 🏃‍♂️ BitmapPool
Memory Cache의 예시를 위해 소개할 것은 `BitmapPool`이다. `BitmapPool`의 원리는 사용하지 않는 Bitmap을 리스트에 넣어놓고, 추후에 동일한 이미지를 로드할 때 다시 메모리에 적재하지 않고 pool에 있는 이미지를 가져와 재사용하는 것이다. 

보통 BitmapPool을 이용해 재사용 Pool을 만들게 될 때, LRU 캐싱 알고리즘으로 구현된 LinkedList `(lruBItmapList)`와 Byte Size 순으로 정렬된 LinkedList`(bitmapList)`를 사용하여 구현하게 된다. 이 둘은 들어있는 비트맵의 순서만 다를 뿐, 같은 비트맵이 담기게된다.

``` kotlin
     private val lruBitmapList = LinkedList<Bitmap>()
     private val bitmapList = LinkedList<Bitmap>()
```

LRU 알고리즘을 이용해 오랫동안 참조되지않은 비트맵 객체는 맨 뒤로 밀리게되고, `맨 뒤에있는 객체를 회수`하면서 BitmapPool을 유지시키는 것이다. LRU 알고리즘을 이용하지 않는다면 처음 BitmapPool이 가득 찰 때까지는 문제없이 동작하지만, 비트맵을 재사용하는 시점부터는 특정 비트맵만 재사용될 수 있으며, 앱이 끝날 때까지 메모리가 줄어들지 않게된다. 자세한 내용은 [이 블로그](https://jamssoft.tistory.com/195)를 참고하길 바란다. :) 



대표적인 이미지 로더 라이브러리인 `Glide`에서 구현한 LruBitmapPool Class 내부를 보며, LruBitmapList와 bitmapList가 어떻게 쓰이고있는지 살펴보자. 

``` java
public class LruBitmapPool implements BitmapPool {
  private static final String TAG = "LruBitmapPool";
  private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;

  private final LruPoolStrategy strategy;
  private final Set<Bitmap.Config> allowedConfigs;
  private final BitmapTracker tracker;
  
  // Pool에 들어올수 있는 최대 크기
  private long maxSize;
  // 현재 pool에 들어간 bitmap byte 크기
  private long currentSize;
```

Glide내 LruBitmapPool Class 에서는 strategy(LruPoolStrategy)가 곧 LRU 기반으로 구현된 리스트이며, tracker(BitmapTracker)가 Bitmap size 순으로 정렬된 리스트이다. <br>

``` java
 @Override
  public synchronized void put(Bitmap bitmap) {
    if (bitmap == null) {
      throw new NullPointerException("Bitmap must not be null");
    }
    // isRecycled: 재활용된 bitmap인지 여부
    if (bitmap.isRecycled()) {
      throw new IllegalStateException("Cannot pool recycled bitmap");
    }
    // isMutable: canvas를 얻을 수 있는 bitmap 인지 여부.
    if (!bitmap.isMutable()
        || strategy.getSize(bitmap) > maxSize
        || !allowedConfigs.contains(bitmap.getConfig())) {
      if (Log.isLoggable(TAG, Log.VERBOSE)) {
        Log.v(
            TAG,
            "Reject bitmap from pool"
                + ", bitmap: "
                + strategy.logBitmap(bitmap)
                + ", is mutable: "
                + bitmap.isMutable()
                + ", is allowed config: "
                + allowedConfigs.contains(bitmap.getConfig()));
      }
      bitmap.recycle();
      return;
    }
    // pool에 넣으려는 bitmap의 사이즈를 얻어온다.
    final int size = strategy.getSize(bitmap);
    strategy.put(bitmap);
    tracker.add(bitmap);

    puts++;
    currentSize += size;

    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      Log.v(TAG, "Put bitmap in pool=" + strategy.logBitmap(bitmap));
    }
    dump();

    evict();
  }
```
Bitmap을 Pool에 넣을 수 있는 조건을 충족시킨다면, strategy & tracker에 bitmap이 들어가게된다. 

>fyi. Lru 기반인 strategy 는 최근에 들어온 bitmap일수록 리스트의 맨 앞으로 배치시켜야하는데, 그 로직이 `LruPoolStrategy` 구현체 내부에 존재하게 된다.

<br>

```java
// Pool의 최대 사용량을 넘기면 크기를 줄여준다.
  private synchronized void trimToSize(long size) {
    while (currentSize > size) {
    //LRU List에서 삭제한다.
      final Bitmap removed = strategy.removeLast();
      if (removed == null) {
        if (Log.isLoggable(TAG, Log.WARN)) {
          Log.w(TAG, "Size mismatch, resetting");
          dumpUnchecked();
        }
        currentSize = 0;
        return;
      }
      // BitmapList에서 삭제한다. 
      tracker.remove(removed);
      currentSize -= strategy.getSize(removed);
      evictions++;
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Evicting bitmap=" + strategy.logBitmap(removed));
      }
      dump();
      removed.recycle();
    }
  }
```
trimToSize()를 이용하면 pool이 최대사용량을 넘어선 경우 참조를 가장 적게하고 있는 lastIndex 부터 객체를 지워가며 크기를 줄여줄 수 있다. 
<br>
``` java
  @Override
  @Nullable
  public Bitmap get(int width, int height, Bitmap.Config config) {
    final int size = Util.getBitmapByteSize(width, height, config);
    Key key = keyPool.get(size);

    Integer possibleSize = sortedSizes.ceilingKey(size);
    if (possibleSize != null && possibleSize != size && possibleSize <= size * MAX_SIZE_MULTIPLE) {
      keyPool.offer(key);
      key = keyPool.get(possibleSize);
    }

    final Bitmap result = groupedMap.get(key);
    // 비트맵을 찾은 경우, pool에서 제거한다.
    if (result != null) {
      result.reconfigure(width, height, config);
      decrementBitmapOfSize(possibleSize);
    }

    return result;
  }
```
이미지를 로드하기 위해 pool에서 필요한 비트맵을 가져온다.
<br>

## 🏃‍♂️ End
`Glide`의 `BitmapPool` 내부만 잠깐 보았는데도 불구하고, 이미지 처리에 대한 많은 프로세스가 담겨져있다. 이미지뷰에 이미지를 바로 로드하려고 한다면, Out of memory, Slow Loading, Bitmap Caching, Memory Cache, Gabarge collector 이슈 등등... 개발자가 고려해야하는 요소가 굉장히 많기에 이를 third party library에게 위임하는 것이다. 그렇다면 위에서 설명한 Glide를 포함해 안드로이드에서 사용할 수 있는 이미지로더 라이브러리 종류는 무엇이 있으며, 각각 어떤 차이점이 있는지 다음 포스트에서 살펴보자.
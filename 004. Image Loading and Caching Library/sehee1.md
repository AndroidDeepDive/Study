

## ๐โโ๏ธ Android Memory
์ด๋ฏธ์ง ๋ก๋ ๋ผ์ด๋ธ๋ฌ๋ฆฌ๋ฅผ ์ฌ์ฉํ์ง ์์ ์ํ์์ ๋ง์ ์ด๋ฏธ์ง๋ฅผ ์ฌ์ฉํ๊ฑฐ๋ ๊ณ ํด์๋ ์ด๋ฏธ์ง๋ฅผ ์ด๋ฏธ์ง๋ทฐ์ ๋ก๋ํด์ผํ๋ ๊ฒฝ์ฐ ๋ฉ๋ชจ๋ฆฌ ๋ถ์กฑ์ผ๋ก OOM์ด ๋ฐ์ํ๊ฒ ๋๋ค. ์์ฆ ๋์ค๋ ์ค๋งํธํฐ์ ๊ต์ฅํ ๋ง์ ๋ฉ๋ชจ๋ฆฌ๋ฅผ ๊ฐ์ง๊ณ  ํ์ด๋๋๋ฐ `์ ์ด์ ๋๋ ๋ชป ๋ฒํฐ์ง(?)` ๋ผ๋ ์๊ฐ์ ํ๋ค๋ฉด, ์๋๋ก์ด๋๋ ์ฑ ๋ด์์ ์ฌ์ฉํ  ์ ์๋ ํ ๋ฉ๋ชจ๋ฆฌ๊ฐ ์ ํด์ ธ์๊ธฐ ๋๋ฌธ์ด๋ค. 

![](https://images.velog.io/images/jshme/post/49b4df11-5ceb-4492-9d4f-f40655655291/IMG_06A0242D2ABE-1.jpeg)

Android์ ๋ฉ๋ชจ๋ฆฌ ๋ชจ๋ธ์ ์ด์์ฒด์  ๋ฒ์ ์ ๋ฐ๋ผ ๋๊ฐ์ง๋ก ๋๋๊ฒ ๋๋ค. 

* Dalvik heap ์์ญ : java ๊ฐ์ฒด๋ฅผ ์ ์ฅํ๋ ๋ฉ๋ชจ๋ฆฌ
* External ์์ญ : native heap์ ์ผ์ข์ผ๋ก ๋ค์ดํฐ๋ธ์ ๋นํธ๋งต ๊ฐ์ฒด๋ฅผ ์ ์ฅํ๋ ๋ฉ๋ชจ๋ฆฌ
Dalvik heap ์์ญ์ External ์์ญ์ `Dalvik heap footprint + External Limit`์ ํฉ์ณ ํ๋ก์ธ์ค๋น ๋ฉ๋ชจ๋ฆฌ ํ๊ณ๋ฅผ ์ด๊ณผํ๋ฉด OOM์ด ๋ฐ์ํ๊ฒ ๋๋ค. 

### ๐โโ๏ธ Footprint
> java์ footprint๋ ํ ๋ฒ ์ฆ๊ฐํ๋ฉด ํฌ๊ธฐ๊ฐ ๋ค์ ๊ฐ์๋์ง ์๊ธฐ ๋๋ฌธ์, footprint๊ฐ ๊ณผ๋ํ๊ฒ ์ปค์ง์ง ์๊ฒ๋ ์ ๊ด๋ฆฌํด์ผํ๋ค. 

![](https://images.velog.io/images/jshme/post/840b5317-446b-413e-933b-20e50307c18f/image.png)

Dalvik VM์ ์ฒ์์ ๋์์ ํ์ํ ๋งํผ๋ง ํ๋ก์ธ์ค์ Heap์ ํ ๋นํ๊ฒ ๋๊ณ , ํ๋ก์ธ์ค์ ํ ๋น๋ ๋ฉ๋ชจ๋ฆฌ๋ณด๋ค ๋ง์ ๋ฉ๋ชจ๋ฆฌ๋ฅผ ํ์ํ๊ฒ๋  ๋๋ง๋ค Dalvit Footfrint๋ ์ฆ๊ฐํ๊ฒ๋๋ค. ํ์ง๋ง ์ฆ๊ฐ๋ Footfrint๋ ๊ฒฐ์ฝ ๊ฐ์ํ์ง ์๊ธฐ ๋๋ฌธ์ java ๊ฐ์ฒด๊ฐ ์ฌ์ฉ๊ฐ๋ฅํ ๋ฉ๋ชจ๋ฆฌ ๊ณต๊ฐ์ ์ฌ์ ๊ฐ ์์ด๋, External heap์ ํฌ๊ธฐ๊ฐ ์ฆ๊ฐ๋๋ฉด OOM์ด ๋ฐ์ํ  ์ ์๋ค. ํ์ง๋ง ์ด๋ฐ ๋ฌธ์ ๋ Honeycomb ์ดํ๋ถํฐ๋ Dalvik heap ๊ณผ External ์์ญ์ด ํฉ์ณ์ก๊ธฐ ๋๋ฌธ์, ๊ณ ๋ คํ  ํ์๊ฐ ์์ด์ก๋ค. 

External ์์ญ์ ์ฌ์ฉํ๋ Honeycomb ๋ฏธ๋ง ๋ฒ์ ์์๋ ์ด๋ฏธ์ง๋ฅผ ๋ง์ด ์ฌ์ฉํ๊ณ ์๋ ํ๋ฉด์์ ํ๋ฉด์ ์ ํํ๋ ํ๋์ด ๋ฐ์ํ์ ๋๋ OOM์ด ๋ฐ์ํ๋ฉด์ ์ฑ์ด ์ค์ง๋  ๊ฒ์ด๋ค. ํ๋ฉด์ ์ ํํ๋ฉด ์ด์  ์กํฐ๋นํฐ ์ธ์คํด์ค์ ์๋ ์ด๋ฏธ์ง๋ทฐ๋ ํ ๋น๋์๋ ๋นํธ๋งต์ด ํจ๊ป ์๋ฉธ๋์ด ๋ฉ๋ชจ๋ฆฌ๊ฐ ํ์๋๊ณ  ์๋ก์ด ์กํฐ๋นํฐ ์ธ์คํด์ค๋ฅผ ์์ฑํ ํ๋ฐ, ์ด ๊ณผ์ ์์ ์ด์  ์กํฐ๋นํฐ ์ธ์คํด์ค์ ๋นํธ๋งต ๊ฐ์ฒด๊ฐ ํ์๋์ง ์์ ๋ฉ๋ชจ๋ฆฌ ๋์๊ฐ ๋ฐ์ํ๊ธฐ ๋๋ฌธ์ด๋ค. 

๋นํธ๋งต ๊ฐ์ฒด์ ๋ํ ์ฐธ์กฐ๊ฐ ์๋๋ฐ๋ ์ ํ์๊ฐ ๋  ์ ์์๊น?๐ค Honeycomb ๋ฏธ๋ง ๋ฒ์ ์์๋ Java ๋นํธ๋งต ๊ฐ์ฒด๋ ์ค์  ๋นํธ๋งต ๋ฐ์ดํฐ๋ฅผ ๊ฐ์ง๊ณ  ์๋ ๊ณณ์ ๊ฐ๋ฆฌํค๋ ํฌ์ธํฐ์ผ ๋ฟ์ด๊ณ  ์ค์  ๋ฐ์ดํฐ๋ External ์์ญ์ธ Native Heap ์์ญ์ ์ ์ฅ๋๊ธฐ ๋๋ฌธ์ด๋ค. Java ๋นํธ๋งต ๊ฐ์ฒด๋ ์ฐธ์กฐ๊ฐ ์์ ๋ GC์ ์ํด ํ์๋์ง๋ง Native Heap ์์ญ์ GC ์ํ์์ญ ๋ฐ์ด๊ธฐ ๋๋ฌธ์ ๋ฉ๋ชจ๋ฆฌ ์๋ฉธ ์์ ์ด ๋ค๋ฅด๋ค. 

์ด๋ฌํ ๋ฌธ์ ์  ๋๋ฌธ์, Honeycomb ์ดํ ๋ฒ์ ์์๋ External ์์ญ์ด ์์ด์ง๋ฉด์ Dalvik heap ์์ญ์ ๋นํธ๋งต ๋ฉ๋ชจ๋ฆฌ๋ฅผ ์ฌ๋ฆด ์ ์๊ฒ ๋์๊ณ  GC๋ ์ ๊ทผํ  ์ ์๊ฒ ๋์๋ค.

๋ค์ ๋์์, ๋ง์ฝ ๊ณ ํด์๋ ์ด๋ฏธ์ง๋ฅผ ๋ก๋ํ  ๋ OOM์ด ๋ฐ์ํ๋ ๊ฒฝ์ฐ BitmapFactory ๊ฐ์ฒด๋ฅผ ์ด์ฉํด ๋ค์ด์ํ๋ง, ๋์ฝ๋ฉ ๋ฐฉ์์ ์ ํํด ์ ์ ํ๊ฒ ๋ทฐ์ ๋ก๋ํ๋ฉด ๋๋ค. ํ์ง๋ง ๋ง์ ์ด๋ฏธ์ง๋ฅผ ์ฌ์ฉํ๊ฒ ๋๋ฉด์ OOM์ด ๋ฐ์ํ๋ค๋ฉด, `์ด๋ฏธ์ง ์บ์ฑ`์ ์ด์ฉํด๋ณด๋ ๊ฒ์ด ์ด๋จ๊น?

## ๐โโ๏ธ Bitmap Caching

์ด๋ฏธ์ง๊ฐ ํ๋ฉด์์ ์ฌ๋ผ์ง๊ณ  ๋ค์ ๊ตฌ์ฑํ  ๋ ์ด๋ฏธ์ง๋ฅผ ๋งค๋ฒ ๋ก๋ํ๋ ๊ฒ์ ์ฑ๋ฅ์์ผ๋ก๋ ์ฌ์ฉ์ ๊ฒฝํ์ ์ข์ง ์๋ค. ์ด๋ด ๋ ๋ฉ๋ชจ๋ฆฌ์ ๋์คํฌ ์บ์๋ฅผ ์ด์ฉํ์ฌ ์ด๋์์ ๊ฐ ์ ์ฅ๋์ด์๋ ๋นํธ๋งต์ ๋ค์ ๊ฐ์ ธ์จ๋ค๋ฉด ๋ค์ ๋ก๋ํ๋ ์๊ฐ๋ ๋จ์ถ์ํฌ ์ ์์ผ๋ฉฐ ์ฑ๋ฅ ๊ฐ์ ๋ ๊ฐ๋ฅํ  ๊ฒ์ด๋ค. ์ด ๋ ์บ์ฑ์ ์ํด `Memory Cache`์ `Disk Cache` ์ฌ์ฉ์ ์ถ์ฒํ๋๋ฐ ๋๊ฐ์ง๊ฐ ์ด๋ค ์ฐจ์ด์ ์ด ์๋์ง ์์๋ณด์.

### 1. Memory Cache
Memory Cache๋ ์ดํ๋ฆฌ์ผ์ด์ ๋ด์ ์กด์ฌํ๋ ๋ฉ๋ชจ๋ฆฌ์ ๋นํธ๋งต์ ์บ์ฑํ๊ณ , ํ์ํ  ๋ ๋น ๋ฅด๊ฒ ์ ๊ทผ๊ฐ๋ฅํ๋ค. ํ์ง๋ง Memory Cache๋ ๊ณง ์ดํ๋ฆฌ์ผ์ด์ ์ฉ๋์ ํค์ฐ๋ ์ฃผ๋ฒ์ด ๋  ์ ์๊ธฐ ๋๋ฌธ์ ๋ง์ ์บ์ฑ์ ์๊ตฌํ๋ ๋นํธ๋งต์ ๊ฒฝ์ฐ์๋ Disk Cache์ ๋ฃ๋ ๊ฒ์ด ๋ ์ข์ ์ ์๋ค.


### 2. Disk Cache
Memory Cache์ ๋ฃ๊ธฐ์ ๋ง์ ์บ์๋ฅผ ์๊ตฌํ๋ ๊ฒฝ์ฐ, ํน์ ์ฑ์ด ๋ฐฑ๊ทธ๋ผ์ด๋๋ก ์ ํ๋์ด๋ ์ ์ฌํ ์บ์๊ฐ ์ญ์ ๋์ง ์๊ธฐ๋ฅผ ๋ฐ๋๋ค๋ฉด Disk Cache๋ฅผ ์ด์ฉํ๋ ๊ฒ์ด ์ข๋ค. ํ์ง๋ง Disk๋ก๋ถํฐ ์บ์ฑ๋ ๋นํธ๋งต์ ๊ฐ์ ธ์ฌ ๋๋ Memory์์ ๋ก๋ํ๋ ๊ฒ๋ณด๋ค ์ค๋์๊ฐ์ด ๊ฑธ๋ฆฐ๋ค.


## ๐โโ๏ธ BitmapPool
Memory Cache์ ์์๋ฅผ ์ํด ์๊ฐํ  ๊ฒ์ `BitmapPool`์ด๋ค. `BitmapPool`์ ์๋ฆฌ๋ ์ฌ์ฉํ์ง ์๋ Bitmap์ ๋ฆฌ์คํธ์ ๋ฃ์ด๋๊ณ , ์ถํ์ ๋์ผํ ์ด๋ฏธ์ง๋ฅผ ๋ก๋ํ  ๋ ๋ค์ ๋ฉ๋ชจ๋ฆฌ์ ์ ์ฌํ์ง ์๊ณ  pool์ ์๋ ์ด๋ฏธ์ง๋ฅผ ๊ฐ์ ธ์ ์ฌ์ฌ์ฉํ๋ ๊ฒ์ด๋ค. 

๋ณดํต BitmapPool์ ์ด์ฉํด ์ฌ์ฌ์ฉ Pool์ ๋ง๋ค๊ฒ ๋  ๋, LRU ์บ์ฑ ์๊ณ ๋ฆฌ์ฆ์ผ๋ก ๊ตฌํ๋ LinkedList `(lruBItmapList)`์ Byte Size ์์ผ๋ก ์ ๋ ฌ๋ LinkedList`(bitmapList)`๋ฅผ ์ฌ์ฉํ์ฌ ๊ตฌํํ๊ฒ ๋๋ค. ์ด ๋์ ๋ค์ด์๋ ๋นํธ๋งต์ ์์๋ง ๋ค๋ฅผ ๋ฟ, ๊ฐ์ ๋นํธ๋งต์ด ๋ด๊ธฐ๊ฒ๋๋ค.

``` kotlin
     private val lruBitmapList = LinkedList<Bitmap>()
     private val bitmapList = LinkedList<Bitmap>()
```

LRU ์๊ณ ๋ฆฌ์ฆ์ ์ด์ฉํด ์ค๋ซ๋์ ์ฐธ์กฐ๋์ง์์ ๋นํธ๋งต ๊ฐ์ฒด๋ ๋งจ ๋ค๋ก ๋ฐ๋ฆฌ๊ฒ๋๊ณ , `๋งจ ๋ค์์๋ ๊ฐ์ฒด๋ฅผ ํ์`ํ๋ฉด์ BitmapPool์ ์ ์ง์ํค๋ ๊ฒ์ด๋ค. LRU ์๊ณ ๋ฆฌ์ฆ์ ์ด์ฉํ์ง ์๋๋ค๋ฉด ์ฒ์ BitmapPool์ด ๊ฐ๋ ์ฐฐ ๋๊น์ง๋ ๋ฌธ์ ์์ด ๋์ํ์ง๋ง, ๋นํธ๋งต์ ์ฌ์ฌ์ฉํ๋ ์์ ๋ถํฐ๋ ํน์  ๋นํธ๋งต๋ง ์ฌ์ฌ์ฉ๋  ์ ์์ผ๋ฉฐ, ์ฑ์ด ๋๋  ๋๊น์ง ๋ฉ๋ชจ๋ฆฌ๊ฐ ์ค์ด๋ค์ง ์๊ฒ๋๋ค. ์์ธํ ๋ด์ฉ์ [์ด ๋ธ๋ก๊ทธ](https://jamssoft.tistory.com/195)๋ฅผ ์ฐธ๊ณ ํ๊ธธ ๋ฐ๋๋ค. :) 



๋ํ์ ์ธ ์ด๋ฏธ์ง ๋ก๋ ๋ผ์ด๋ธ๋ฌ๋ฆฌ์ธ `Glide`์์ ๊ตฌํํ LruBitmapPool Class ๋ด๋ถ๋ฅผ ๋ณด๋ฉฐ, LruBitmapList์ bitmapList๊ฐ ์ด๋ป๊ฒ ์ฐ์ด๊ณ ์๋์ง ์ดํด๋ณด์. 

``` java
public class LruBitmapPool implements BitmapPool {
  private static final String TAG = "LruBitmapPool";
  private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;

  private final LruPoolStrategy strategy;
  private final Set<Bitmap.Config> allowedConfigs;
  private final BitmapTracker tracker;
  
  // Pool์ ๋ค์ด์ฌ์ ์๋ ์ต๋ ํฌ๊ธฐ
  private long maxSize;
  // ํ์ฌ pool์ ๋ค์ด๊ฐ bitmap byte ํฌ๊ธฐ
  private long currentSize;
```

Glide๋ด LruBitmapPool Class ์์๋ strategy(LruPoolStrategy)๊ฐ ๊ณง LRU ๊ธฐ๋ฐ์ผ๋ก ๊ตฌํ๋ ๋ฆฌ์คํธ์ด๋ฉฐ, tracker(BitmapTracker)๊ฐ Bitmap size ์์ผ๋ก ์ ๋ ฌ๋ ๋ฆฌ์คํธ์ด๋ค. <br>

``` java
 @Override
  public synchronized void put(Bitmap bitmap) {
    if (bitmap == null) {
      throw new NullPointerException("Bitmap must not be null");
    }
    // isRecycled: ์ฌํ์ฉ๋ bitmap์ธ์ง ์ฌ๋ถ
    if (bitmap.isRecycled()) {
      throw new IllegalStateException("Cannot pool recycled bitmap");
    }
    // isMutable: canvas๋ฅผ ์ป์ ์ ์๋ bitmap ์ธ์ง ์ฌ๋ถ.
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
    // pool์ ๋ฃ์ผ๋ ค๋ bitmap์ ์ฌ์ด์ฆ๋ฅผ ์ป์ด์จ๋ค.
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
Bitmap์ Pool์ ๋ฃ์ ์ ์๋ ์กฐ๊ฑด์ ์ถฉ์กฑ์ํจ๋ค๋ฉด, strategy & tracker์ bitmap์ด ๋ค์ด๊ฐ๊ฒ๋๋ค. 

>fyi. Lru ๊ธฐ๋ฐ์ธ strategy ๋ ์ต๊ทผ์ ๋ค์ด์จ bitmap์ผ์๋ก ๋ฆฌ์คํธ์ ๋งจ ์์ผ๋ก ๋ฐฐ์น์์ผ์ผํ๋๋ฐ, ๊ทธ ๋ก์ง์ด `LruPoolStrategy` ๊ตฌํ์ฒด ๋ด๋ถ์ ์กด์ฌํ๊ฒ ๋๋ค.

<br>

```java
// Pool์ ์ต๋ ์ฌ์ฉ๋์ ๋๊ธฐ๋ฉด ํฌ๊ธฐ๋ฅผ ์ค์ฌ์ค๋ค.
  private synchronized void trimToSize(long size) {
    while (currentSize > size) {
    //LRU List์์ ์ญ์ ํ๋ค.
      final Bitmap removed = strategy.removeLast();
      if (removed == null) {
        if (Log.isLoggable(TAG, Log.WARN)) {
          Log.w(TAG, "Size mismatch, resetting");
          dumpUnchecked();
        }
        currentSize = 0;
        return;
      }
      // BitmapList์์ ์ญ์ ํ๋ค. 
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
trimToSize()๋ฅผ ์ด์ฉํ๋ฉด pool์ด ์ต๋์ฌ์ฉ๋์ ๋์ด์  ๊ฒฝ์ฐ ์ฐธ์กฐ๋ฅผ ๊ฐ์ฅ ์ ๊ฒํ๊ณ  ์๋ lastIndex ๋ถํฐ ๊ฐ์ฒด๋ฅผ ์ง์๊ฐ๋ฉฐ ํฌ๊ธฐ๋ฅผ ์ค์ฌ์ค ์ ์๋ค. 
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
    // ๋นํธ๋งต์ ์ฐพ์ ๊ฒฝ์ฐ, pool์์ ์ ๊ฑฐํ๋ค.
    if (result != null) {
      result.reconfigure(width, height, config);
      decrementBitmapOfSize(possibleSize);
    }

    return result;
  }
```
์ด๋ฏธ์ง๋ฅผ ๋ก๋ํ๊ธฐ ์ํด pool์์ ํ์ํ ๋นํธ๋งต์ ๊ฐ์ ธ์จ๋ค.
<br>

## ๐โโ๏ธ End
`Glide`์ `BitmapPool` ๋ด๋ถ๋ง ์ ๊น ๋ณด์๋๋ฐ๋ ๋ถ๊ตฌํ๊ณ , ์ด๋ฏธ์ง ์ฒ๋ฆฌ์ ๋ํ ๋ง์ ํ๋ก์ธ์ค๊ฐ ๋ด๊ฒจ์ ธ์๋ค. ์ด๋ฏธ์ง๋ทฐ์ ์ด๋ฏธ์ง๋ฅผ ๋ฐ๋ก ๋ก๋ํ๋ ค๊ณ  ํ๋ค๋ฉด, Out of memory, Slow Loading, Bitmap Caching, Memory Cache, Gabarge collector ์ด์ ๋ฑ๋ฑ... ๊ฐ๋ฐ์๊ฐ ๊ณ ๋ คํด์ผํ๋ ์์๊ฐ ๊ต์ฅํ ๋ง๊ธฐ์ ์ด๋ฅผ third party library์๊ฒ ์์ํ๋ ๊ฒ์ด๋ค. ๊ทธ๋ ๋ค๋ฉด ์์์ ์ค๋ชํ Glide๋ฅผ ํฌํจํด ์๋๋ก์ด๋์์ ์ฌ์ฉํ  ์ ์๋ ์ด๋ฏธ์ง๋ก๋ ๋ผ์ด๋ธ๋ฌ๋ฆฌ ์ข๋ฅ๋ ๋ฌด์์ด ์์ผ๋ฉฐ, ๊ฐ๊ฐ ์ด๋ค ์ฐจ์ด์ ์ด ์๋์ง ๋ค์ ํฌ์คํธ์์ ์ดํด๋ณด์.
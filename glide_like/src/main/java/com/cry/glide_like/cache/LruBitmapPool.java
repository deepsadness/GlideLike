package com.cry.glide_like.cache;

import android.content.ComponentCallbacks2;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * bitmap 缓存复用池
 */
public class LruBitmapPool extends LruCache<Integer, Bitmap> implements BitmapPool {

    private boolean isRemoved;

    //可以筛选的Set
    NavigableMap<Integer, Integer> map = new TreeMap<>();
    private static final int MAX_OVER_SIZE_MULTI = 2;

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        int size = 0;
        if (android.os.Build.VERSION.SDK_INT >= KITKAT) {
            size = value.getAllocationByteCount();
        } else {
            size = value.getByteCount();
        }
        return size;
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        map.remove(key);

        //如果不是手动移除的，是被内存回收的。则需要将其recycle
        if (!isRemoved) {
            oldValue.recycle();
        }
    }

    @Override
    public void put(Bitmap bitmap) {
        //如果不能复用，就直接GG
        if (!bitmap.isMutable()) {
            bitmap.recycle();
            return;
        }

        int size = 0;
        if (android.os.Build.VERSION.SDK_INT >= KITKAT) {
            size = bitmap.getAllocationByteCount();
        } else {
            size = bitmap.getByteCount();
        }

        //如果当前的Size 太大了，也不能放入
        if (size >= maxSize()) {
            bitmap.recycle();
            return;
        }

        put(size, bitmap);
        map.put(size, 0);
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        //先计算大概的使用的内存大小
        int perBytes = config == Bitmap.Config.ARGB_8888 ? 4 : 2;
        int size = width * height * perBytes;

        if (Build.VERSION.SDK_INT >= KITKAT) {
            //取出一个比它大的值
            Integer ceiling = map.ceilingKey(size);
            if (ceiling != null && ceiling <= size * MAX_OVER_SIZE_MULTI) {
                //只有在2倍的范围内可以使用
                isRemoved = true;
                Bitmap remove = remove(ceiling);
                return remove;
            }
        } else {
            boolean isReuse = map.keySet().contains(size);
            if (isReuse) {
                return remove(size);
            }
        }
        return null;
    }

    @Override
    public void clearMemory() {
        //就是清除所有
        evictAll();
    }

    @Override
    public void trimMemory(int level) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory();
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            trimToSize(maxSize() / 2);
        }
    }
}

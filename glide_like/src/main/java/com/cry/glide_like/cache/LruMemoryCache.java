package com.cry.glide_like.cache;

import android.content.ComponentCallbacks2;
import android.util.LruCache;

/**
 * 直接使用Android自带的LruCache，就只要确定最大的值就可以
 * 通常需要重写2个函数
 * 1. sizeOf 来确定资源的大小
 * 2. entryRemoved 当节点被回收时的回调
 */
public class LruMemoryCache extends LruCache<Key, Resource> implements MemoryCache {
    private ResourceRemoveListener listener;
    private boolean isRemoving;

    public LruMemoryCache(int maxSize) {
        super(maxSize);
    }

    public void setListener(ResourceRemoveListener listener) {
        this.listener = listener;
    }

    @Override
    protected int sizeOf(Key key, Resource value) {
        int size;
        //如果大于4.4则要AllocationByteCount.这里是Bitmap的复用机制的
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            size = value.getBitmap().getAllocationByteCount();
        } else {
            size = value.getBitmap().getByteCount();
        }
        return size;
    }

    /**
     * 当值被移除的时候
     */
    @Override
    protected void entryRemoved(boolean evicted, Key key, Resource oldValue, Resource newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        //当资源被移除的时候，就要回调我们的函数
        //不能被我们主动删除的回调触发
        if (listener != null && !isRemoving) {
            listener.onResourceRemoved(oldValue);
        }
    }

    public Resource remove2(Key key) {
        //手动移除的，不能走回调。
        isRemoving = true;
        Resource remove = remove(key);
        isRemoving = false;
        return remove;
    }

    @Override
    public void clearMemory() {
        //移除所有
        evictAll();
    }

    @Override
    public void trimMemory(int level) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            //如果是后台发起的。就可以清楚所有
            evictAll();
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            trimToSize(maxSize() / 12);
        }
    }
}

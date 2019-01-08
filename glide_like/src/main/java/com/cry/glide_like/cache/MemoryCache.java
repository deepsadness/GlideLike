package com.cry.glide_like.cache;

/**
 * 内存操作的接口
 */
public interface MemoryCache {

    Resource put(Key key, Resource resource);

    Resource remove(Key key);

    void clearMemory();

    void trimMemory(int level);

    /**
     * 当资源被移除掉的回调
     */
    interface ResourceRemoveListener {
        void onResourceRemoved(Resource removed);
    }
}

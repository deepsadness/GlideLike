package com.cry.glide_like;

import com.cry.glide_like.cache.ActiveResource;
import com.cry.glide_like.cache.BitmapPool;
import com.cry.glide_like.cache.Key;
import com.cry.glide_like.cache.LruMemoryCache;
import com.cry.glide_like.cache.MemoryCache;
import com.cry.glide_like.cache.Resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MemoryUnitTest implements MemoryCache.ResourceRemoveListener, Resource.ResourceReleaseListener {

    private BitmapPool bitmapPool;
    private LruMemoryCache lruMemoryCache;
    private ActiveResource activeResource;

    //获取资源时候的操作
    @Test
    public Resource getCache(Key key) {
        lruMemoryCache = new LruMemoryCache(10);
        activeResource = new ActiveResource(this);
        lruMemoryCache.setListener(this);

        Resource resource = null;
        //先从活动缓存中取
        resource = activeResource.get(key);
        if (resource != null) {
            return resource;
        }

        //再从lruCache中取
        resource = lruMemoryCache.get(key);
        if (resource != null) {
            //如果内存中有的话
            //1，将其从lruCache中移除
            lruMemoryCache.remove2(key);
            //1.1 增加Resource的引用计数
            resource.acquire();
            //2. 加入activeResouce
            activeResource.activate(key, resource);
            return resource;
        }
        return null;
    }

    //当资源自动被回收时的回调
    @Override
    public void onResourceRemoved(Resource removed) {
        //这个是被内存移除的时候。需要放到bitmapPool?
        bitmapPool.put(removed.getBitmap());
    }

    //当资源被释放时的回调。表示的是当前的资源没有被引用
    @Override
    public void onResourceReleased(Key key, Resource released) {
        //这个时候，需要从活动的资源中移除
        Resource deactivate = activeResource.deactivate(key);
        //加入lruMemoryCache
        lruMemoryCache.put(key, deactivate);
    }
}
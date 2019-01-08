package com.cry.glide_like.load;

import com.cry.glide_like.GlideContext;
import com.cry.glide_like.cache.ActiveResource;
import com.cry.glide_like.cache.BitmapPool;
import com.cry.glide_like.cache.Key;
import com.cry.glide_like.cache.LruMemoryCache;
import com.cry.glide_like.cache.MemoryCache;
import com.cry.glide_like.cache.Resource;
import com.cry.glide_like.request.Request;
import com.cry.glide_like.request.ResourceCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Engine
 * 数据的流转
 * 线程的流转
 */
public class Engine implements Resource.ResourceReleaseListener {

    private ActiveResource activeResource;
    private LruMemoryCache memoryCache;
    private BitmapPool bitmapPool;
    private HashMap<Key, EngineJob> jobs = new HashMap<>();
    private ThreadPoolExecutor executor;

    @Override
    public void onResourceReleased(Key key, Resource released) {

    }

    public LoadStatus load(GlideContext context, Object model, int width, int height, ResourceCallBack callBack) {
        EngineKey engineKey = new EngineKey(model, width, height);
        //先去缓存里面查
        Resource resource = activeResource.get(engineKey);
        if (resource != null) {
            resource.acquire();
            callBack.onResourceReady(resource);
            return null;
        }

        resource = memoryCache.remove2(engineKey);
        if (resource != null) {
            //还要往active里面保存
            activeResource.activate(engineKey, resource);
            resource.acquire();
            resource.setResourceRemoveListener(engineKey, this);
            callBack.onResourceReady(resource);
            return null;
        }

        //就要开启去加载了
        EngineJob engineJob = jobs.get(engineKey);
        if (engineJob != null) {
            //任务正在运行中，只需要注册监听就可以了
            engineJob.addCallback(callBack);
            return new LoadStatus(callBack, engineJob);
        }


        //如果没有，则创建一个新的
        engineJob = new EngineJob(executor, engineKey, this);
        engineJob.addCallback(callBack);

        //还需要创建的decodeJob
        DecodeJob decodeJob = new DecodeJob(context, model, width, height, engineJob);
        engineJob.start(decodeJob);
        jobs.put(engineKey, engineJob);
        return new LoadStatus(callBack, engineJob);
    }


    public static class LoadStatus {

        private final ResourceCallBack callBack;
        private final EngineJob engineJob;

        public LoadStatus(ResourceCallBack callBack, EngineJob engineJob) {
            this.callBack = callBack;
            this.engineJob = engineJob;
        }
    }

}

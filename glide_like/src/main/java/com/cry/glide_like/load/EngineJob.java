package com.cry.glide_like.load;

import com.cry.glide_like.cache.Resource;
import com.cry.glide_like.request.ResourceCallBack;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class EngineJob implements DecodeJob.Callback {
    private final Engine engine;
    private final EngineKey engineKey;
    private final ThreadPoolExecutor executor;
    private ArrayList<ResourceCallBack> callBacks = new ArrayList<>();
    private DecodeJob decodeJob;

    public EngineJob(ThreadPoolExecutor executor, EngineKey engineKey, Engine engine) {
        this.engine = engine;
        this.engineKey = engineKey;
        this.executor = executor;
    }

    public void addCallback(ResourceCallBack callBack) {
        callBacks.add(callBack);
    }

    //开始解码的任务
    public void start(DecodeJob decodeJob) {
        this.decodeJob = decodeJob;
        executor.execute(decodeJob);
    }

    @Override
    public void onResourceReady(Resource resource) {

    }

    @Override
    public void onLoadFailed(Throwable e) {

    }
}

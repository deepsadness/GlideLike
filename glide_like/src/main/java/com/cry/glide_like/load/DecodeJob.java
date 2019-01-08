package com.cry.glide_like.load;

import com.cry.glide_like.GlideContext;
import com.cry.glide_like.cache.Resource;

public class DecodeJob implements Runnable {

    private final GlideContext context;
    private final Object model;
    private final int width;
    private final int height;
    private final Callback callback;


    private boolean isCancel;

    private Stage stage;
    private DataGenerator currentGenerator;
    private DataGenerator nextGenerator;

    public DecodeJob(GlideContext context, Object model, int width, int height, Callback callback) {
        this.context = context;
        this.model = model;
        this.width = width;
        this.height = height;
        this.callback = callback;
    }

    @Override
    public void run() {
        //开始加载资源
        try {
            if (isCancel) {
                if (callback != null) {
                    callback.onLoadFailed(new RuntimeException("Decode job is canceled!!"));
                }
                return;
            }

            //获取下一步的状态
            stage = getNextStage(Stage.INITIALIZE);
            //获取下一步的Generator
            currentGenerator = getNextGenerator();
            //运行gn
            runGenerator();
        } catch (Throwable e) {

        }
    }

    private void runGenerator() {

    }

    private Stage getNextStage(Stage stage) {
        switch (stage) {
            case INITIALIZE:
                return Stage.DATA_CACHE;
            case DATA_CACHE:
                return Stage.SOURCE;
            case SOURCE:
            case FINISHED:
                return Stage.FINISHED;
            default:
                throw new IllegalArgumentException("");
        }
          }

    public DataGenerator getNextGenerator() {
        return nextGenerator;
    }


    public enum Stage {
        INITIALIZE,
        DATA_CACHE,
        SOURCE,
        FINISHED
    }

    public interface Callback {
        void onResourceReady(Resource resource);

        void onLoadFailed(Throwable e);
    }
}

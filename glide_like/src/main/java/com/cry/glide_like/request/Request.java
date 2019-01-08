package com.cry.glide_like.request;

import com.cry.glide_like.cache.Resource;
import com.cry.glide_like.load.Engine;
import com.cry.glide_like.GlideContext;

public class Request implements Target.SizeReadyCallBack,ResourceCallBack {

    private GlideContext glideContext;
    private Object model;
    private Target target;
    private Status loadStatus;
    private Status status;
    private Engine engine;
    private RequestOption requestOption;


    public Request(GlideContext glideContext, Object model, Target target) {
        this.glideContext = glideContext;
        this.model = model;
        this.target = target;
    }


    public boolean isCompleted() {
        return status == Status.COMPLETE;
    }

    public boolean isCancel() {
        return status == Status.CANCELED;
    }

    public boolean isRunning() {
        return status == Status.RUNNING || status == Status.WAITING_FOR_SIZE;
    }

    public void begin() {
        status = Status.WAITING_FOR_SIZE;
        //开始加载
        target.onLoadStated();
        //如果已经制定更好了尺寸，就不需要再去计算了。
        if (requestOption.getOverrideWidth() > 0 && requestOption.getOverrideHeight() > 0) {
            onSizeReady(requestOption.getOverrideWidth(), requestOption.getOverrideHeight());
        } else {
            target.getSize(this);
        }
    }

    public void pause() {
        clear();
        status = Status.PAUSED;
    }

    public void cancel() {
        target.cancel();
        status = Status.CANCELED;
    }

    //将保存的状态清除，并取消任务
    public void clear() {
        if (status == Status.CLEARED) {
            return;
        }
        cancel();
        status = Status.CLEARED;
    }

    //就是回收，将所有的变量都清空
    public void recycle() {
        glideContext = null;
        model = null;
        target = null;
        loadStatus = null;
    }

    @Override
    public void onSizeReady(int w, int h) {
        status = Status.RUNNING;

        //进行加载图片
        engine.load(glideContext,model,w,h,this);

    }

    @Override
    public void onResourceReady(Resource resource) {

    }


    private enum Status {
        //等待
        PENDING,
        //运行
        RUNNING,
        //运行阶段1 - 等待测量
        WAITING_FOR_SIZE,
        //完成
        COMPLETE,
        //失败
        FAILED,

        //取消
        CANCELED,
        //clear?
        CLEARED,
        //暂停
        PAUSED,
    }
}

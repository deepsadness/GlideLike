package com.cry.glide_like.request;

import android.widget.ImageView;

import com.cry.glide_like.GlideContext;


public class RequestBuilder {

    private final GlideContext glideContext;
    private final RequestManager requestManager;
    private RequestOption requestOption;
    private Object model;

    public RequestBuilder(GlideContext glideContext, RequestManager requestManager) {
        this.glideContext = glideContext;
        this.requestManager = requestManager;
    }

    public RequestBuilder apply(RequestOption requestOption) {
        this.requestOption
                = requestOption;
        return this;
    }

    public RequestBuilder load(String url) {
        this.model = url;
        return this;
    }

    public void into(ImageView view) {
        Target target = new Target(view);
        Request request = new Request(glideContext, model, target);
        //track开始就运行我们的request
        requestManager.track(request);
    }
}

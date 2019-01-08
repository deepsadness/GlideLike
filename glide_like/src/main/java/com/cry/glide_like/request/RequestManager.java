package com.cry.glide_like.request;

import com.cry.glide_like.GlideContext;
import com.cry.glide_like.lifecycle.ActivityFragmentLifecycle;
import com.cry.glide_like.lifecycle.LifeCycleListener;
import com.cry.glide_like.lifecycle.RequestTracker;

/**
 * 集合生命周期，
 * 将Request分发给RequestTracker
 */
public class RequestManager implements LifeCycleListener {
    private final ActivityFragmentLifecycle lifecyle;
    private final RequestTracker requestTracker;
    private GlideContext glideContext;

    public RequestManager(ActivityFragmentLifecycle activityFragmentLifecycle) {
        this.lifecyle = activityFragmentLifecycle;
        requestTracker = new RequestTracker();
        lifecyle.addListener(this);
    }

    public RequestBuilder load(String url) {
        return new RequestBuilder(glideContext, this).load(url);
    }

    @Override
    public void onStart() {
        resumeRequests();
    }

    private void resumeRequests() {
        requestTracker.resumeRequests();
    }

    @Override
    public void onStop() {
        pauseRequests();
    }

    private void pauseRequests() {
        requestTracker.pauseRequests();
    }

    @Override
    public void onDestroy() {
        lifecyle.removeListener(this);
        requestTracker.clearRequests();
    }

    public void track(Request request) {
        requestTracker.runRequest(request);
    }
}

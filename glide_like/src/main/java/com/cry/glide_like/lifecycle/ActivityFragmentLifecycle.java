package com.cry.glide_like.lifecycle;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * ActivityFragment的生命周期的管理
 * <p>
 * 这个是不是可以直接和Fragment集合在一起
 */
public class ActivityFragmentLifecycle implements LifeCycleHolder {

    private Set<LifeCycleListener> lifeCycleListeners = Collections.newSetFromMap(new WeakHashMap<LifeCycleListener, Boolean>());

    private boolean isStart;
    private boolean isDestroyed;


    @Override
    public void addListener(LifeCycleListener lifeCycleListener) {
        lifeCycleListeners.add(lifeCycleListener);
        //添加的时候，发送一次当前的生命周期的状态
        if (isDestroyed) {
            for (LifeCycleListener f : lifeCycleListeners) {
                f.onDestroy();
            }
        } else if (isStart) {
            for (LifeCycleListener f : lifeCycleListeners) {
                f.onStart();
            }
        } else {
            for (LifeCycleListener f : lifeCycleListeners) {
                f.onStop();
            }
        }
    }

    @Override
    public void removeListener(LifeCycleListener lifeCycleListener) {
        lifeCycleListeners.remove(lifeCycleListener);
    }

    void onStart() {
        isStart = true;
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onStart();
        }
    }

    void onStop() {
        isStart = false;
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onStop();
        }
    }

    void onDestroy() {
        isDestroyed = true;
        for (LifeCycleListener lifeCycleListener : lifeCycleListeners) {
            lifeCycleListener.onDestroy();
        }
    }
}

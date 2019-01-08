package com.cry.glide_like.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 这在使用的图片资源
 * <p>
 * 获取资源的时候
 * 1. 先在这个ActiveResource中取
 * 2. 如果没有，在继续在lruMemory中取。
 * 如果有的话，则增加资源的引用计数，并加入ActiveResource
 * 3. 如果继续没有，则会取磁盘获取
 * <p>
 * <p>
 * 释放资源的时候
 * - 弱引用被回收了(从CleanUp thread的监听中知道，从map中移除)
 * - 手动调用了release方法，导致引用计数减少为0。手动调用移除的方法
 * <p>
 * 都会触发ResourceRelease的回调。
 * 之后会在将资源加入lru的Cache .在Lru中会因为内存管理接口的调用，进行减少。
 * <p>
 * 触发ResourceRemoved
 * 之后，因为Android Bitmap的复用机制，所以还会加入BitmapPool中。
 * <p>
 * 内存部分的移动，就是这样
 */
public class ActiveResource {

    //使用Map来对其进行换成
    private ReferenceQueue<? super Resource> queue;
    private Resource.ResourceReleaseListener resourceReleaseListener;
    private Map<Key, ResourceWeakReference> activeRes = new HashMap<>();
    private Thread cleanReferenceThread;
    //是否关闭ActiveResource
    private boolean isShutDown;

    public ActiveResource(Resource.ResourceReleaseListener resourceReleaseListener) {
        this.resourceReleaseListener = resourceReleaseListener;
    }

    /**
     * 激活这个活动缓存
     */
    public void activate(Key key, Resource resource) {
        resource.setResourceRemoveListener(key, resourceReleaseListener);
        activeRes.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
    }

    /**
     * 主动将活动缓存移除掉
     */
    public Resource deactivate(Key key) {
        ResourceWeakReference resourceWeakReference = activeRes.remove(key);
        if (resourceWeakReference != null) {
            return resourceWeakReference.get();
        }
        return null;
    }

    public Resource get(Key key) {
        ResourceWeakReference resourceWeakReference = activeRes.remove(key);
        if (resourceWeakReference != null) {
            return resourceWeakReference.get();
        }
        return null;
    }

    private ReferenceQueue<? super Resource> getReferenceQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();
            //开始循环
            cleanReferenceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isShutDown) {
                        try {
                            ResourceWeakReference remove = (ResourceWeakReference) queue.remove();
                            //当被系统回收时，从ActiveResource中清楚
                            activeRes.remove(remove.key);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, "clear-acitve-reference");
            cleanReferenceThread.start();
        }
        return queue;
    }

    //关闭
    public void shutDown() {
        isShutDown = true;
        if (cleanReferenceThread != null) {
            cleanReferenceThread.interrupt();
            //等待5 s，等待线程关比
            try {
                cleanReferenceThread.join(TimeUnit.SECONDS.toMillis(5));
                //如果线程还未关闭，则抛出异常
                if (cleanReferenceThread.isAlive()) {
                    throw new RuntimeException("CleanReferenceThread:Failed to join in time!!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static class ResourceWeakReference extends WeakReference<Resource> {

        private final Key key;

        //这里传入我们自己的ReferenceQueue来监听，当weakreference被移除
        public ResourceWeakReference(Key key, Resource referent, ReferenceQueue<? super Resource> queue) {
            super(referent, queue);
            this.key = key;
        }
    }
}

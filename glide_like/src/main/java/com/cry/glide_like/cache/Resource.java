package com.cry.glide_like.cache;

import android.graphics.Bitmap;

/**
 * 存放的Resource
 */
public class Resource {

    private ResourceReleaseListener releaseListener;
    private Key key;

    private Bitmap bitmap;
    private int refCount;

    public void setResourceRemoveListener(Key key, ResourceReleaseListener releaseListener) {
        this.key = key;
        this.releaseListener = releaseListener;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void release() {
        if (--refCount == 0) {
            releaseListener.onResourceReleased(key, this);
        }
    }

    public void acquire() {
        if (bitmap.isRecycled()) {
            throw new IllegalStateException("Acquire a recycled resource");
        }
        refCount++;
    }

    public void recycle() {
        //如果引用计数还大于0，则表示还有引用
        if (refCount > 0) {
            return;
        }
        //当引用计数小于0时，recycle
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public interface ResourceReleaseListener {
        void onResourceReleased(Key key, Resource released);
    }
}

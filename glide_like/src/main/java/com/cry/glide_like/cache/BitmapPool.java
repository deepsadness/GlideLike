package com.cry.glide_like.cache;

import android.graphics.Bitmap;

/**
 * 因为Android中Bitmap的复用机制，所以建立的BitmapPool。目的是为了得到适合大小的被回收的Bitmap,来进行复用
 */
public interface BitmapPool {
    void put(Bitmap bitmap);

    Bitmap get(int width, int height, Bitmap.Config config);

    void clearMemory();

    void trimMemory(int level);
}

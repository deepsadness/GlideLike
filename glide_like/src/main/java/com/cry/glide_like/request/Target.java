package com.cry.glide_like.request;

import android.widget.ImageView;

public class Target {

    private final ImageView view;

    public Target(ImageView view) {
        this.view = view;
    }


    public interface SizeReadyCallBack {
        void onSizeReady(int w, int h);
    }
}

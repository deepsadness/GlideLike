package com.cry.glide_like.request;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.cry.glide_like.lifecycle.SupportRequestManagerFragment;

import java.util.HashMap;

public class RequestManagerRetriever {
    private final static String FRG_TAG = "RequestManagerRetriever";
    private static final int REMOTE_PENDING_FRAGMGENT = 1;
    private final Handler mMainHandler;
    private HashMap<FragmentManager, SupportRequestManagerFragment> pendingSupports = new HashMap<>();

    public RequestManagerRetriever() {
        mMainHandler = new Handler(Looper.getMainLooper(), new MainHanderCallBack());
    }

    public RequestManager get(FragmentActivity context) {
        //需要将我们的SupportRequestManagerFragment添加上
        SupportRequestManagerFragment fragment = null;
        FragmentManager supportFragmentManager = context.getSupportFragmentManager();
        fragment = (SupportRequestManagerFragment) supportFragmentManager.findFragmentByTag(FRG_TAG);
        if (fragment == null) {
            //如果pengding里面有，表示还在添加的过程中，所以不用重新添加了
            fragment = pendingSupports.get(supportFragmentManager);
            if (fragment == null) {
                //如果没有，则需要手动给添加上
                SupportRequestManagerFragment requestManagerFragment = new SupportRequestManagerFragment();
                pendingSupports.put(supportFragmentManager, requestManagerFragment);
                //将我们的Fragment添加上。因为这个是一个post事件。所以我们需要用一个中间变量来确定是否添加成功
                supportFragmentManager.beginTransaction().add(requestManagerFragment, FRG_TAG).commitAllowingStateLoss();
                //确保没有问题的情况下，把这个移除掉
                Message message = mMainHandler.obtainMessage(REMOTE_PENDING_FRAGMGENT);
                message.obj = supportFragmentManager;
            }
        }

        if (fragment == null) {
            throw new IllegalStateException("Can not create SupportRequestManagerFragmet!!!");
        }
        //将他传递给RequestMananger
        return new RequestManager(fragment.getActivityFragmentLifecycle());
    }

    private class MainHanderCallBack implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REMOTE_PENDING_FRAGMGENT:
                    SupportRequestManagerFragment obj = (SupportRequestManagerFragment) msg.obj;
                    pendingSupports.remove(obj);
                    break;
            }
            return false;
        }
    }
}

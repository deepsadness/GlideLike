package com.cry.glide_like.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.support.v4.app.Fragment;

/**
 * 由它来提供lifeCycle
 * 提供给RequestManager
 * <p>
 * v4的Fragment已经提供了Lifecycle了？！！
 */
public class SupportRequestManagerFragment extends Fragment {

    private ActivityFragmentLifecycle activityFragmentLifecycle = new ActivityFragmentLifecycle();

    public ActivityFragmentLifecycle getActivityFragmentLifecycle() {
        return activityFragmentLifecycle;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activityFragmentLifecycle != null) {
            activityFragmentLifecycle.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (activityFragmentLifecycle != null) {
            activityFragmentLifecycle.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if (activityFragmentLifecycle != null) {
            activityFragmentLifecycle.onDestroy();
        }
        super.onDestroy();

    }
}

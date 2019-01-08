package com.cry.glide_like.lifecycle;

import com.cry.glide_like.request.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 真正对Request生命周期进行管理
 */
public class RequestTracker {

    private Set<Request> requests =
            Collections.newSetFromMap(new WeakHashMap<Request, Boolean>());

    private ArrayList<Request> pendingRequests = new ArrayList<>();

    private boolean isPaused;

    //清除请求
    public void clearRequests() {
        ArrayList<Request> requestsList;
        synchronized (this) {
            requestsList = new ArrayList<>(requests.size());
            for (Request request : requests) {
                if (request != null) {
                    requestsList.add(request);
                }
            }
        }
        for (Request request : requestsList) {
            if (request != null) {
                requests.remove(request);
                request.clear();
                request.recycle();
            }
        }

        pendingRequests.clear();
    }

    //回复请求
    public void resumeRequests() {
        isPaused = false;
//        ArrayList<Request> requestsList;
//        synchronized (this) {
//            requestsList = new ArrayList<>(requests.size());
//            for (Request request : requests) {
//                if (request != null) {
//                    requestsList.add(request);
//                }
//            }
//        }
        for (Request request : pendingRequests) {
            if (request != null && !request.isCompleted() && !request.isCancel() && !request.isRunning()) {
                request.begin();
            }
        }
        pendingRequests.clear();
    }

    //暂停请求
    public void pauseRequests() {
        isPaused = true;
        ArrayList<Request> requestsList;
        synchronized (this) {
            requestsList = new ArrayList<>(requests.size());
            for (Request request : requests) {
                if (request != null) {
                    requestsList.add(request);
                }
            }
        }
        for (Request request : requestsList) {
            boolean isRunning = request.isRunning();
            if (isRunning) {
                request.pause();
                pendingRequests.add(request);
            }
        }
    }

    //开始请求
    public void runRequest(Request request) {
        //加入
        requests.add(request);

        if (isPaused) {
            pendingRequests.add(request);
        } else {
            request.begin();
        }
    }
}

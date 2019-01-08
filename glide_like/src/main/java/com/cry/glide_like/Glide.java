package com.cry.glide_like;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import com.cry.glide_like.cache.ActiveResource;
import com.cry.glide_like.cache.BitmapPool;
import com.cry.glide_like.cache.LruBitmapPool;
import com.cry.glide_like.cache.LruMemoryCache;
import com.cry.glide_like.cache.MemoryCache;
import com.cry.glide_like.request.RequestManager;
import com.cry.glide_like.request.RequestManagerRetriever;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Glide {

    private RequestManagerRetriever requestManagerRetriever = new RequestManagerRetriever();
    //作为全局的配置，将缓存池也放到这里
    ActiveResource activeResource;
    MemoryCache memoryCache;
    BitmapPool bitmapPool;


    public static class Builder {
        BitmapPool bitmapPool;
        MemoryCache memoryCache;
        ActiveResource activeResource;
        ExecutorService executorService;

        public Glide build(Context context) {
            ActivityManager systemService = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (systemService == null) {
                throw new IllegalStateException("Can not get ActivityManager");
            }
            int memoryBytes = systemService.getMemoryClass() * 1024 * 1024;
            //占用内存的40%
            memoryBytes = Math.round(memoryBytes * 0.4f);

            //先计算加载一个屏幕需要的内存大小
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;
            int screenSize = widthPixels * heightPixels * 4;

            float bitmapPoolSize = screenSize * 4.0f;
            float memoryCacheSize = screenSize * 2f;

            //如果在当前许可的内存范围内就直接使用
            int poolSize = 0;
            int memorySize = 0;
            if (bitmapPoolSize + memoryCacheSize <= memoryBytes) {
                poolSize = Math.round(bitmapPoolSize);
                memorySize = Math.round(memoryCacheSize);
            } else {
                //如果不够大了。就用可用的内存来进行分配
                //将其分为6分
                float v = memoryBytes / 6f;
                poolSize = (int) (v * 4);
                memorySize = (int) (v * 2);
            }

            if (bitmapPool == null) {
                bitmapPool = new LruBitmapPool(poolSize);
            }

            if (memoryCache == null) {
                memoryCache = new LruMemoryCache(memorySize);
            }

            if (executorService == null) {
                //获取当前可用的CPU合数。作为核心线程数
                int cpu = Runtime.getRuntime().availableProcessors();
                int threadCount = Math.min(4, cpu);
                //将加载任务作为计算型的任务，进行。所以核心的线程数为CPU数
                executorService = new ThreadPoolExecutor(
                        threadCount,
                        threadCount,
                        0,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingDeque<Runnable>(),
                        new ThreadFactory() {

                            int threadNum;

                            @Override
                            public Thread newThread(@NonNull Runnable r) {
                                Thread thread = new Thread(r, "glide-thread-" + threadNum);
                                threadNum++;
                                return thread;
                            }
                        });
            }
//            Engine engine = new Engine(bitmapPool, memoryCache, executorService);
            if (activeResource == null) {
//                activeResource = new ActiveResource(engine);
            }

            return new Glide(context, this);
        }

    }

    private Glide(Context context) {
        this(context, new Builder());
    }

    private Glide(Context context, Builder builder) {
         bitmapPool = builder.bitmapPool;
         memoryCache = builder.memoryCache;
         activeResource = builder.activeResource;

    }

    private static Glide INSTANCE;

    public static Glide get(Context context) {
        if (INSTANCE == null) {
            synchronized (Glide.class) {
                if (INSTANCE == null) {
                    init(context, new Builder());
                }
            }
        }
        return INSTANCE;
    }

    public static void init(Context context, Builder builder) {
        Context applicationContext = context.getApplicationContext();
        Glide build = builder.build(applicationContext);
        INSTANCE = build;
    }

    public RequestManager with(Context context) {
        if (context instanceof FragmentActivity) {
            return get(context).requestManagerRetriever.get((FragmentActivity) context);
        }
        return null;
    }
}

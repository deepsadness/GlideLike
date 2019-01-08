package com.cry.glide_like;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.cry.glide_like.load.ModelLoader;
import com.cry.glide_like.load.ModelLoaderRegistry;
import com.cry.glide_like.load.fetcher.DataFetcher;
import com.cry.glide_like.load.loader.FileUriLoader;
import com.cry.glide_like.load.loader.HttpUriLoader;
import com.cry.glide_like.load.loader.StringModelLoader;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoaderInstrumentedTest {

    private String model = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1546876683669&di=34762833c30cb2952f992e32f5ab6be4&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn20115%2F44%2Fw1000h644%2F20181221%2Fa94e-hqnkyps2618802.jpg";


    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.cry.glide_like.test", appContext.getPackageName());
    }

    @Test
    public void findLoader() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // 先进行注册
        ModelLoaderRegistry modelLoaderRegistry = new ModelLoaderRegistry();
        modelLoaderRegistry.add(String.class, InputStream.class, new StringModelLoader.Factory());
        modelLoaderRegistry.add(Uri.class, InputStream.class, new HttpUriLoader.Factory());
        modelLoaderRegistry.add(Uri.class, InputStream.class, new FileUriLoader.Factory(appContext.getContentResolver()));

        //进行查找。
        //查找的过程中，会将我们需要的ModelLoader创建出来
        List<ModelLoader<String, ?>> modelLoaders = modelLoaderRegistry.getModelLoaders(String.class);
        assertEquals(modelLoaders.size(), 0);

        final ModelLoader.LoaderData<?> loaderData = modelLoaders.get(0).buildLoaderData(model);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loaderData.fetcher.fetch(new DataFetcher.DataFetcherCallBack<InputStream>() {
                    @Override
                    public void onFetcherReady(InputStream inputStream) {
                        //得到InputStream
                    }

                    @Override
                    public void onLoadedFailed(Exception e) {

                    }
                });

            }
        }).start();
    }
}

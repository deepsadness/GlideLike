package com.cry.glide_like.load.loader;

import android.net.Uri;

import com.cry.glide_like.load.ModelLoader;
import com.cry.glide_like.load.ModelLoaderRegistry;

import java.io.File;
import java.io.InputStream;

/**
 * 处理String类型的modelloader
 */
public class StringModelLoader implements ModelLoader<String, InputStream> {

    private ModelLoader<Uri, InputStream> loader;

    public StringModelLoader(ModelLoader<Uri, InputStream> loader) {
        this.loader = loader;
    }

    @Override
    public boolean handle(String s) {
        if (s.startsWith("/")) {
            return true;
        } else if (s.startsWith("http") || s.startsWith("https")) {
            return true;
        }
        return false;
    }

    @Override
    public LoaderData<InputStream> buildLoaderData(String s) {
        Uri uri = null;
        if (s.startsWith("/")) {
            uri = Uri.fromFile(new File(s));
        } else {
            uri = Uri.parse(s);
        }
        return loader.buildLoaderData(uri);
    }

    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        @Override
        public ModelLoader<String, InputStream> build(ModelLoaderRegistry registry) {
            //因为StringModel 可能有多一个。或者单个。由registry来进行负责
            return new StringModelLoader(registry.build(Uri.class, InputStream.class));
        }
    }
}

package com.cry.glide_like.load.loader;

import android.net.Uri;

import com.cry.glide_like.cache.ObjectKey;
import com.cry.glide_like.load.ModelLoader;
import com.cry.glide_like.load.ModelLoaderRegistry;
import com.cry.glide_like.load.fetcher.HttpUriFetcher;

import java.io.InputStream;

public class HttpUriLoader implements ModelLoader<Uri, InputStream> {

    @Override
    public boolean handle(Uri uri) {
        String scheme = uri.getScheme();
        return scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https");
    }

    @Override
    public LoaderData<InputStream> buildLoaderData(Uri uri) {
        return new LoaderData<>(new ObjectKey(uri), new HttpUriFetcher(uri));
    }

    public static class Factory implements ModelLoader.ModelLoaderFactory<Uri, InputStream> {

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new HttpUriLoader();
        }
    }
}

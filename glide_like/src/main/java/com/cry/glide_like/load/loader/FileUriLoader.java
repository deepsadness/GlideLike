package com.cry.glide_like.load.loader;

import android.content.ContentResolver;
import android.net.Uri;

import com.cry.glide_like.cache.ObjectKey;
import com.cry.glide_like.load.ModelLoader;
import com.cry.glide_like.load.ModelLoaderRegistry;
import com.cry.glide_like.load.fetcher.FileUriFetcher;

import java.io.InputStream;

public class FileUriLoader implements ModelLoader<Uri, InputStream> {

    private ContentResolver contentResolver;

    public FileUriLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public boolean handle(Uri uri) {
        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    @Override
    public LoaderData<InputStream> buildLoaderData(Uri uri) {
        return new LoaderData<>(new ObjectKey(uri), new FileUriFetcher(contentResolver, uri));
    }

    public static class Factory implements ModelLoader.ModelLoaderFactory<Uri, InputStream> {
        private final ContentResolver contentResolver;

        public Factory(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new FileUriLoader(contentResolver);
        }
    }
}

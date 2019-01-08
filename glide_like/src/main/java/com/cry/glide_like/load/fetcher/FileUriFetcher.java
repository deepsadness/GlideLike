package com.cry.glide_like.load.fetcher;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUriFetcher implements DataFetcher<InputStream> {

    private final Uri uri;
    private final ContentResolver contentResolver;
    private boolean isCancel = false;

    public FileUriFetcher(ContentResolver contentResolver, Uri uri) {
        this.uri = uri;
        this.contentResolver = contentResolver;
    }

    @Override
    public void fetch(DataFetcherCallBack<? super InputStream> callBack) {

        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            if (isCancel) {
                return;
            }
            if (callBack != null) {
                callBack.onFetcherReady(inputStream);
            }
        } catch (FileNotFoundException e) {
            if (callBack != null) {
                callBack.onLoadedFailed(e);
            }
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void cancel() {
        isCancel = true;
    }

    @Override
    public Class<?> getDataClass() {
        return InputStream.class;
    }
}

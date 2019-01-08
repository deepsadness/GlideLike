package com.cry.glide_like.load.fetcher;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpUriFetcher implements DataFetcher<InputStream> {
    private final Uri uri;
    private boolean isCancel = false;

    public HttpUriFetcher(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void fetch(DataFetcherCallBack<? super InputStream> callBack) {
        URL url = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();
            int responseCode = connection.getResponseCode();

            //如果这里已经是取消了。就不管了
            if (isCancel) {
                return;
            }


            if (responseCode == HttpURLConnection.HTTP_OK) {
                //打开成功后，就讲准备好的数据返回回去
                callBack.onFetcherReady(inputStream);
            } else {
                callBack.onLoadedFailed(new RuntimeException(connection.getResponseMessage()));
            }
        } catch (Exception e) {
            callBack.onLoadedFailed(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
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

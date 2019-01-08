package com.cry.glide_like.load.fetcher;

public interface DataFetcher<Data> {

    void fetch(DataFetcherCallBack<? super Data> callBack);

    void cancel();

    Class<?> getDataClass();

    interface DataFetcherCallBack<Data> {
        void onFetcherReady(Data data);

        void onLoadedFailed(Exception e);
    }
}

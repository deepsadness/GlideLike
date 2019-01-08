package com.cry.glide_like.load;

import com.cry.glide_like.cache.Key;
import com.cry.glide_like.load.fetcher.DataFetcher;

/**
 * Glide中
 * 将加载的输入 抽象成 Loader,由它来组装完成 上流数据的获取和转换
 * <p>
 * 第一个范型，表示的是 输入的类型
 * 第二个范型，表示的是 上流数据最后转换成的类型
 * <p>
 * ModelLoader将Model(输入) 和 (Data)输出的类型，集合在一起.
 * 创建出LoaderData
 * LoaderData将model 和Key 和Fetcher 结合在一起
 * <p>
 * 最后提供一个抽象工厂，来完成ModelLoader的创建
 */
public interface ModelLoader<Model, Data> {

    //因为最后的创建的场合，会在ModelLoaderRegistry中的方法中，所以抽象成接口？？
    interface ModelLoaderFactory<Model, Data> {
        ModelLoader<Model, Data> build(ModelLoaderRegistry registry);
    }

    //创建一个类，将我们的Fetcher和Key结合在一起
    class LoaderData<Data> {
        public Key key;

        //最后负责拉取数据的类
        public DataFetcher fetcher;

        public LoaderData(Key key, DataFetcher fetcher) {
            this.key = key;
            this.fetcher = fetcher;
        }
    }


    //这种类型的转换是否支持
    boolean handle(Model model);

    //通过这个类来创建LoaderData. LoaderData中必须包含一个Fetcher,来loadData
    LoaderData<Data> buildLoaderData(Model model);
}

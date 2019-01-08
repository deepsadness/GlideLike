package com.cry.glide_like.load;

import com.cry.glide_like.load.loader.MultiModeLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局的配置类。
 * 将匹配的modelClass,dataClass和Factory注册，在合适的时候，通过它来创建
 *
 * 1. 根据输入的model来获取对应的ModelLoader
 * 2. 根据输入的model 和 输出的 Data 来匹配 ModelLoader
 * <p>
 * 使用entry来保存对应的配置
 */
public class ModelLoaderRegistry {
    //保存了好多entries
    List<Entry<?, ?>> entries = new ArrayList<>();

    //将ModelLoader注册进来
    public synchronized <Model, Data> void add(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {
        entries.add(new Entry<>(modelClass, dataClass, factory));
    }

    //build对应的modelLoader
    public <Model, Data> ModelLoader<Model, Data> build(Class<Model> modelClass, Class<Data> dataClass) {
        ArrayList<ModelLoader<Model, Data>> modelLoaders = new ArrayList<>(entries.size());
        for (Entry<?, ?> entry : entries) {
            if (entry.handles(modelClass, dataClass)) {
                modelLoaders.add((ModelLoader<Model, Data>) entry.factory.build(this));
            }
        }

        if (modelLoaders.size() > 1) {
            //如果是多个，就交给multiModelLoader来进行处理
            return new MultiModeLoader<>(modelLoaders);
        } else if (modelLoaders.size() == 1) {
            return modelLoaders.get(0);
        } else {

            throw new RuntimeException("No match!! model class:" + modelClass.getClass() + ",data class:" + dataClass.getClass());
        }

    }

    //得到能够匹配的ModelLoader
    public <Model> List<ModelLoader<Model, ?>> getModelLoaders(Class<Model> modelClass) {
        ArrayList<ModelLoader<Model, ?>> modelLoaders = new ArrayList<>(entries.size());
        for (Entry<?, ?> entry : entries) {
            boolean handles = entry.handles(modelClass);
            if (handles) {
                modelLoaders.add((ModelLoader<Model, ?>) entry.factory.build(this));
            }
        }
        return modelLoaders;
    }

    /*
    内部的Entry,才是真正的保存节点
    需要保存的就是刚刚的三点
    1. model
    2. data
    3. factory
    由这三个来组成创建的方式

     */
    public static class Entry<Model, Data> {
        Class<Model> modelClass;
        Class<Data> dataClass;
        ModelLoader.ModelLoaderFactory<Model, Data> factory;

        public Entry(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {
            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        //判断是否可以接受，只要是子类都可以接受
        // isAssignableFrom  是否是后面的类的爸爸
        boolean handles(Class<?> modelClass, Class<?> dataClass) {
            return this.modelClass.isAssignableFrom(modelClass) && this.dataClass.isAssignableFrom(dataClass);
        }

        //只判断输入
        boolean handles(Class<?> modelClass) {
            return this.modelClass.isAssignableFrom(modelClass);
        }
    }
}

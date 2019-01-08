package com.cry.glide_like.load.loader;

import com.cry.glide_like.load.ModelLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个特殊的ModelLoader 这个loader可以适配多一个条件。只要一个符合条件，就可以进行处理
 */
public class MultiModeLoader<Model, Data> implements ModelLoader<Model, Data> {

    private ArrayList<ModelLoader<Model, Data>> modelLoaders;

    public MultiModeLoader(ArrayList<ModelLoader<Model, Data>> modelLoaders) {
        this.modelLoaders = modelLoaders;
    }

    @Override
    public boolean handle(Model model) {
        for (int i = 0; i < modelLoaders.size(); i++) {
            ModelLoader<Model, Data> modelDataModelLoader = modelLoaders.get(i);
            if (modelDataModelLoader.handle(model)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LoaderData<Data> buildLoaderData(Model model) {
        //返回第一个符合条件的
        for (int i = 0; i < modelLoaders.size(); i++) {
            ModelLoader<Model, Data> modelDataModelLoader = modelLoaders.get(i);
            boolean handle = modelDataModelLoader.handle(model);
            if (handle) {
                return modelDataModelLoader.buildLoaderData(model);
            }
        }
        return null;
    }
}

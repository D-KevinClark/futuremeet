package com.kevin.futuremeet;

import android.app.Application;

import com.kevin.futuremeet.loader.GlideImageLoader;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * Created by carver on 2016/3/26.
 */
public class FMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeConfig theme = new ThemeConfig.Builder()
                .build();
        ImageLoader imageloader = new GlideImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageloader, theme)
                .build();
        GalleryFinal.init(coreConfig);
    }

}

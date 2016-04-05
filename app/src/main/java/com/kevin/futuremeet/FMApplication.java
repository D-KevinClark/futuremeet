package com.kevin.futuremeet;

import android.app.Application;
import android.os.Environment;

import com.avos.avoscloud.AVOSCloud;
import com.baidu.mapapi.SDKInitializer;
import com.kevin.futuremeet.loader.GlideImageLoader;

import java.io.File;

import cn.finalteam.galleryfinal.CoreConfig;
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

        //initial the Baidu SDK
        SDKInitializer.initialize(this);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "未遇");
        File photoCacheDir;
        if (!mediaStorageDir.exists())
            mediaStorageDir.mkdirs();

        photoCacheDir = new File(mediaStorageDir, "temp");
        if (!photoCacheDir.exists())
            photoCacheDir.mkdirs();

        ThemeConfig theme = new ThemeConfig.Builder()
                .setIconCamera(R.drawable.camera_icon)
                .build();

        ImageLoader imageloader = new GlideImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageloader, theme)
                .setTakePhotoFolder(mediaStorageDir)
                .setEditPhotoCacheFolder(photoCacheDir)
                .build();
        GalleryFinal.init(coreConfig);

        //initial the LeanCloud
        AVOSCloud.initialize(this,"Q0Uvxfqo7eW0EJCW0JPjTJID-gzGzoHsz","18gnqT4d6IAdD7vKxRBXOaFd");
    }

}

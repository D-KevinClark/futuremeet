package com.kevin.futuremeet;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.avos.avoscloud.AVOSCloud;
import com.baidu.mapapi.SDKInitializer;
import com.kevin.futuremeet.loader.GlideImageLoader;

import java.io.File;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import io.rong.imkit.RongIM;

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
        AVOSCloud.initialize(this, "Q0Uvxfqo7eW0EJCW0JPjTJID-gzGzoHsz", "18gnqT4d6IAdD7vKxRBXOaFd");
        AVOSCloud.setLastModifyEnabled(true);


        //IM =========================================
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
        }
    }


    /**
     * get the name of the current process
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

}

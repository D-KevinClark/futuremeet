package com.kevin.futuremeet.background;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.utility.Util;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class PublishMomentIntentService extends IntentService {


    private static final String EXTRA_CONTENT = "com.kevin.futuremeet.background.extra.content";
    private static final String EXTRA_IMAEGS = "com.kevin.futuremeet.background.extra.images";

    public static final String STATUS_REPORT_ACTION="com.kevin.futuremeet.background.action.status.report";
    public static final String EXTRA_STATUS="com.kevin.futuremeet.background.extra.status";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UPLOAD_SUCCESS,UPLOAD_FAIL})
    public @interface UploadStatus{}
    public static final int UPLOAD_SUCCESS = 100;
    public static final int UPLOAD_FAIL = 110;

    public PublishMomentIntentService() {
        super("PublishMomentIntentService");
    }


    public static void startPublishMoment(Context context, String content, HashMap<String, Bitmap.Config> imageInfoMap) {
        Intent intent = new Intent(context, PublishMomentIntentService.class);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_IMAEGS, imageInfoMap);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String content = intent.getStringExtra(EXTRA_CONTENT);
            final HashMap<String, Bitmap.Config> imageInfoMap = (HashMap<String, Bitmap.Config>) intent.getSerializableExtra(EXTRA_IMAEGS);
            handleMomentPublish(content, imageInfoMap);
        }
    }


    private void handleMomentPublish(String content, HashMap<String, Bitmap.Config> imageInfoMap) {
        Set<String> imagePathSet = imageInfoMap.keySet();
        Iterator iterator = imagePathSet.iterator();
        List<AVFile> fileList = new LinkedList<>();

        while (iterator.hasNext()) {
            String filePath= (String) iterator.next();
            //compress the original pic to smaller than 200KB so that it suit to upload to internet
            Bitmap bitmap = Util.decodeImageFileForUpload(filePath,
                    1024 * 200, imageInfoMap.get(filePath));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            AVFile avFile = new AVFile("MomentPics", bytes);
            try {
                avFile.save();
            } catch (AVException e) {
                e.printStackTrace();
                sendStatusReportBroadcast(UPLOAD_FAIL);
                return;
            }
            fileList.add(avFile);
        }
        AVObject avObject = new AVObject(MomentContract.CLASS_NAME);
        avObject.put(MomentContract.CONTENT, content);
        avObject.addAll(MomentContract.IMAGES, fileList);
        try {
            avObject.save();
        } catch (AVException e) {
            e.printStackTrace();
            sendStatusReportBroadcast(UPLOAD_FAIL);
            return;
        }
        sendStatusReportBroadcast(UPLOAD_SUCCESS);
    }

    private void sendStatusReportBroadcast(@UploadStatus int status) {
        Intent intent = new Intent(STATUS_REPORT_ACTION)
                .putExtra(EXTRA_STATUS, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

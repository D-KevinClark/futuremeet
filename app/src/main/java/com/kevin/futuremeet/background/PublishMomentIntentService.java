package com.kevin.futuremeet.background;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;


public class PublishMomentIntentService extends IntentService {


    private static final String EXTRA_CONTENT = "com.kevin.futuremeet.background.extra.content";
    private static final String EXTRA_IMAEGS = "com.kevin.futuremeet.background.extra.images";

    public static final String STATUS_REPORT_ACTION = "com.kevin.futuremeet.background.action.status.report";
    public static final String EXTRA_STATUS = "com.kevin.futuremeet.background.extra.status";

    private final List<AVFile> fileList = new LinkedList<>();


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UPLOAD_SUCCESS, UPLOAD_FAIL})
    public @interface UploadStatus {
    }

    public static final int UPLOAD_SUCCESS = 100;
    public static final int UPLOAD_FAIL = 110;


    //a flag indicate that if there is a failure when upload images
    private boolean mImageUploadFail = false;

    public PublishMomentIntentService() {
        super("PublishMomentIntentService");
    }


    public static void startPublishMoment(Context context, String content, HashMap<String, String> imageInfoMap) {
        Intent intent = new Intent(context, PublishMomentIntentService.class);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_IMAEGS, imageInfoMap);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String content = intent.getStringExtra(EXTRA_CONTENT);
            final HashMap<String, String> imageInfoMap = (HashMap<String, String>)
                    intent.getSerializableExtra(EXTRA_IMAEGS);
            handleMomentPublish(content, imageInfoMap);
        }
    }


    /**
     * handle the moment pulish ,upload image and content
     * @param content
     * @param imageInfoMap
     */
    private void handleMomentPublish(String content, HashMap<String, String> imageInfoMap) {
        //record tha handles to the image upload Thread so it can be interrupted
        ArrayList<Thread> imageUploadThreads = new ArrayList<>();

        Set<String> imagePathSet = imageInfoMap.keySet();
        Iterator iterator = imagePathSet.iterator();
        //used to preform the logic: upload the moment until all the image has been uploaded
        CountDownLatch latch = new CountDownLatch(imageInfoMap.size());

        while (iterator.hasNext()) {
            String filePath = (String) iterator.next();
            if (!mImageUploadFail) {
                new Thread(new ImageUploadRunnable(filePath,
                        imageUploadThreads, latch, Thread.currentThread())).start();
            } else {
                break;
            }
        }


        //wait until all the image is uploaded
        try {
            latch.await();
        } catch (InterruptedException e) {
            return;
        }

        //upload the moment content with all the images has been uploaded
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


    /**
     * a runnable class that decode and upload the image
     */
    private class ImageUploadRunnable implements Runnable {
        private final String mImagePath;
        private final ArrayList<Thread> threads;
        private final CountDownLatch latch;
        private final Thread mainThread;

        public ImageUploadRunnable(String filePath, ArrayList<Thread> threads, CountDownLatch latch, Thread mainThread) {
            this.mImagePath = filePath;
            this.threads = threads;
            this.latch = latch;
            this.mainThread = mainThread;
        }

        @Override
        public void run() {
            threads.add(Thread.currentThread());
            if (mImageUploadFail) {
                return;
            }
            if (Thread.interrupted()) {
                return;
            }
            ByteArrayOutputStream outputStream = Util.decodeImageFileForUpload(mImagePath, PublishMomentIntentService.this);

            if (Thread.interrupted()) {
                return;
            }

            byte[] bytes = outputStream.toByteArray();
            AVFile avFile = new AVFile("MomentPics", bytes);

            try {
                avFile.save();
            } catch (AVException e) {
                mImageUploadFail = true;//change the upload state flag
                interruptAllImageUploadThread(threads);//interrupt all the image upload thread
                sendStatusReportBroadcast(UPLOAD_FAIL);//send a report broadcast to MainActivity
                mainThread.interrupt();//no more wait
                return;
            }

            fileList.add(avFile);
            latch.countDown();
        }
    }

    /**
     * try to interrupt the threads that has been recorded in the list
     *
     * @param threads
     */
    private void interruptAllImageUploadThread(ArrayList<Thread> threads) {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }


    /**
     * send a broadcast to the {@link com.kevin.futuremeet.MainActivity} to indicate the status of the upload
     *
     * @param status
     */
    private void sendStatusReportBroadcast(@UploadStatus int status) {
        Intent intent = new Intent(STATUS_REPORT_ACTION)
                .putExtra(EXTRA_STATUS, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}

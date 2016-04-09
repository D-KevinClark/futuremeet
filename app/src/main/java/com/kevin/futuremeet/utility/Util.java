package com.kevin.futuremeet.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

/**
 * Created by carver on 2016/3/30.
 */
public class Util {
    /**
     * convert the dp unit to the px unit
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * dp + 0.5f);
    }

    /**
     * conver the px unit to the dp unit
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2dp(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }


    /**
     * get the scaled Bitmap according to the requested width and height
     * better not to call this method in the UI thread
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * Calculate the largest inSampleSize value that is a power of 2 and keeps both
     * height and width larger than the requested height and width.
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize=1;
        if (width > reqWidth || height > reqHeight) {
            final int halfWidth=width/2;
            final int halfHeight=height/2;
            while ((halfWidth/inSampleSize)>reqWidth
                    &&(halfHeight/inSampleSize)>reqHeight) {
                inSampleSize*=2;
            }
        }
        return inSampleSize;
    }


    /**
    * get a bitmap for upload use,the bitmap returned is decoded according the device screen width and height
     * @param filePath
     * @param context
     * @return
     */
    public static ByteArrayOutputStream decodeImageFileForUpload(String filePath,Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;

        options.inSampleSize = calculateInSampleSize(options, width, height);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int quality=100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        while (outputStream.toByteArray().length>200*1024){
            quality-=10;
            outputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        }
        bitmap.recycle();//recycle the space allocated for bitmap

        return outputStream;
    }


}

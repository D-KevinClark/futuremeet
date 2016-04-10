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
     *
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

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * Calculate the largest inSampleSize value that is a power of 2 and keeps both
     * height and width larger than the requested height and width.
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;
            while ((halfWidth / inSampleSize) > reqWidth
                    && (halfHeight / inSampleSize) > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;

    }


    /**
     * get a bitmap for upload use,the bitmap returned is decoded according the device screen width and height
     *
     * @param filePath
     * @param context
     * @return
     */
    public static ByteArrayOutputStream decodeImageFileForUpload(String filePath, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        final int width = 480;
        final int height = 800;

        options.inSampleSize = calculateInSampleSizeForUpload(options, width, height);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        bitmap.recycle();//recycle the space allocated for bitmap

        return outputStream;
    }

    /**
     * calculate InSampleSize For Upload use
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSizeForUpload(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width;
        final int height;
        //always make the width smaller than height , because the request width will be smaller than the height
        //this can make the compress get to the maximum level
        if (options.outWidth > options.outHeight) {
            width = options.outHeight;
            height = options.outWidth;
        } else {
            width = options.outWidth;
            height = options.outHeight;
        }


        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;
            while ((halfWidth / inSampleSize) > reqWidth
                    && (halfHeight / inSampleSize) > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;

    }


}

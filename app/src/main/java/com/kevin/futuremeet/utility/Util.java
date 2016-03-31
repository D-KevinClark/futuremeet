package com.kevin.futuremeet.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

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
     * decode a image File for upload
     * @param filePath the path of the file
     * @param maxByte the maximum byte for the file after compressed
     * @param config the bitmap config info for the corresponding image
     * @return
     */
    public static Bitmap decodeImageFileForUpload(String filePath,int maxByte,Bitmap.Config config) {
        File file = new File(filePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSizeForUploadImage(options, maxByte, config);
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * calculate the inSampleSize for the Bitmap for uploading , according to the minimum byte and the maximum byte
     * @param options
     * @param maxByte
     * @param config
     * @return
     */
    private static int calculateInSampleSizeForUploadImage(BitmapFactory.Options options,int maxByte,Bitmap.Config config) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSample=1;
        int size = width * height * getBytesPerPixel(config);
        while (size > maxByte) {
            inSample*=2;
            size/=4;
        }
        return inSample;
    }

    /**
     * get the bytes for per pixel according the bitmap config info
     * @param config
     * @return
     */
    private static int getBytesPerPixel(Bitmap.Config config) {
        switch (config) {
            case ARGB_8888:
                return 4;
            case ARGB_4444:
                return 2;
            case RGB_565:
                return 2;
            case ALPHA_8:
                return 1;
        }
        return 1;
    }
}

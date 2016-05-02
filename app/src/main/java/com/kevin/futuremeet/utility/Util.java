package com.kevin.futuremeet.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.kevin.futuremeet.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
     *
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


    /**
     * close The Soft Keyboard
     *
     * @param context
     */
    public static void closeTheSoftKeyboard(View currentView, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            //hide the keyboard
            inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

    /**
     * detect the network status
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailabel(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isOnlyDigitAndLetter(String pass) {
        for (int i = 0; i < pass.length(); i++) {
            if (!Character.isLetterOrDigit(pass.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * make the saved file can be seen in the gallery immediately
     *
     * @param context
     * @param filePath
     */
    public static final void scanImageFile(Context context, String filePath) {
        Uri uri = Uri.parse("file://" + filePath);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }


    /**
     * @param distanceInKilometer
     * @return
     */
    public static final String getProperDistanceFormat(Context context, double distanceInKilometer) {
        int distanceInMeter = (int) Math.floor(distanceInKilometer * 1000);
        if (distanceInMeter < 1000) {
            return distanceInMeter + context.getString(R.string.meter);
        } else {
            return String.valueOf(distanceInKilometer).substring(0, 4) + "km";
        }
    }

    /**
     * @param context
     * @param date
     * @return
     */
    public static String getProperPublishTimeFormate(Context context, Date date) {
        long nowTimeInMilliSecond = System.currentTimeMillis();
        long targetTimeInMilliSecond = date.getTime();

        Time time = new Time();
        time.setToNow();

        int mCurrentJulianDay = Time.getJulianDay(nowTimeInMilliSecond, time.gmtoff);
        int mTargetJulianDay = Time.getJulianDay(targetTimeInMilliSecond, time.gmtoff);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        String properFormat = null;

        if (mTargetJulianDay == mCurrentJulianDay) {
            int minuteOffset = (int) Math.abs((targetTimeInMilliSecond - nowTimeInMilliSecond) / (60 * 1000));
            if (minuteOffset == 0) {
                properFormat = context.getString(R.string.just_now);
            } else if (minuteOffset <= 59) {
                properFormat = minuteOffset + context.getString(R.string.minute_ago);
            } else {
                properFormat = context.getString(R.string.today) + simpleDateFormat.format(date);
            }
        } else if (mTargetJulianDay == mCurrentJulianDay - 1) {
            properFormat = context.getString(R.string.yesterday) + simpleDateFormat.format(date);
        } else if (mTargetJulianDay == mCurrentJulianDay - 2) {
            properFormat = context.getString(R.string.the_day_before_yesterday) + simpleDateFormat.format(date);
        }
        return properFormat;
    }





}

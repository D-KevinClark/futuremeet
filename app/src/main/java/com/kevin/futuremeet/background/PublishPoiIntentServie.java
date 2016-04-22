package com.kevin.futuremeet.background;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.kevin.futuremeet.beans.FuturePoiContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.database.FuturePoiDBContract;
import com.kevin.futuremeet.database.FuturePoiDBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PublishPoiIntentServie extends IntentService {

    private static final String ACTION = "com.kevin.futuremeet.background.action.publish.poi";

    public static final String ACTION_STATUS_REPORT = "com.kevin.futuremeet.background.action.publish.status.report";
    public static final String EXTRA_STATUS = "com.kevin.futuremeet.background.action.publish.status";
    public static final int PUBLISH_OK = 110;
    public static final int PUBLISH_FAILED = 111;


    // TODO: Rename parameters
    private static final String EXTRA_POI_NAME = "com.kevin.futuremeet.background.extra.poi.name";
    private static final String EXTRA_POI_ADDRESS = "com.kevin.futuremeet.background.extra.poi.address";
    private static final String EXTRA_POI_LNG = "com.kevin.futuremeet.background.extra.poi.lng";
    private static final String EXTRA_POI_LAT = "com.kevin.futuremeet.background.extra.poi.lat";
    private static final String EXTRA_POI_TIME = "com.kevin.futuremeet.background.extra.poi.time";


    public PublishPoiIntentServie() {
        super("PublishPoiIntentServie");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startPublishPoi(Context context, String poiName, String poiAddress, Double lng, Double lat, Date time) {
        Intent intent = new Intent(context, PublishPoiIntentServie.class);
        intent.setAction(ACTION);
        intent.putExtra(EXTRA_POI_NAME, poiName);
        intent.putExtra(EXTRA_POI_ADDRESS, poiAddress);
        intent.putExtra(EXTRA_POI_LNG, lng);
        intent.putExtra(EXTRA_POI_LAT, lat);
        intent.putExtra(EXTRA_POI_TIME, time);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION.equals(action)) {
                final String poiName = intent.getStringExtra(EXTRA_POI_NAME);
                final String poiAddress = intent.getStringExtra(EXTRA_POI_ADDRESS);
                final double lng = intent.getDoubleExtra(EXTRA_POI_LNG, 0);
                final double lat = intent.getDoubleExtra(EXTRA_POI_LAT, 0);
                final Date date = (Date) intent.getSerializableExtra(EXTRA_POI_TIME);
                handlePoiPublish(poiName, poiAddress, lng, lat, date);
            }
        }
    }

    private void handlePoiPublish(String poiName, String poiAddress, Double lng, Double lat, Date date) {
        AVUser user = AVUser.getCurrentUser();

        AVObject object = new AVObject(FuturePoiContract.CLASS_NAME);
        object.put(FuturePoiContract.POI_NAME, poiName);
        object.put(FuturePoiContract.POI_ADDRESS, poiAddress);
        AVGeoPoint geoPoint = new AVGeoPoint(lat, lng);
        object.put(FuturePoiContract.POI_LOCATION, geoPoint);
        object.put(FuturePoiContract.ARRIVE_TIME, date);
        object.put(FuturePoiContract.USER_DETAIL_INFO, user.get(UserContract.USER_DETAIL_INFO));


        AVACL avacl = new AVACL();
        avacl.setReadAccess(user, true);
        avacl.setWriteAccess(user, true);
        object.setACL(avacl);

        try {
            object.save();
            savePoiToDB(poiName,poiAddress,lng,lat,date);
            sendPublishStatusReport(PUBLISH_OK);
        } catch (AVException e) {
            sendPublishStatusReport(PUBLISH_FAILED);
        }
    }

    private void sendPublishStatusReport(int status) {
        Intent intent = new Intent(ACTION_STATUS_REPORT)
                .putExtra(EXTRA_STATUS, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private void savePoiToDB(String poiName, String poiAddress, Double lng, Double lat, Date date) {
        FuturePoiDBHelper dbHelper = new FuturePoiDBHelper(getApplicationContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        ContentValues contentValues = new ContentValues();
        contentValues.put(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_NAME,poiName);
        contentValues.put(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ADDRESS,poiAddress);
        contentValues.put(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LNG,lng);
        contentValues.put(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LAT,lat);
        contentValues.put(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ARRIVE_TIME,dateFormat.format(date));

        database.insert(FuturePoiDBContract.FuturePoiEntry.TABLE_NAME,null,contentValues);

        database.close();

    }


}

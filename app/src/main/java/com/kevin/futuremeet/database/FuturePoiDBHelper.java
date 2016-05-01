package com.kevin.futuremeet.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kevin.futuremeet.utility.Config;

/**
 * Created by carver on 2016/4/22.
 */
public class FuturePoiDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "futuremeet.db";
    private static final int DATABASE_VERSIION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FuturePoiDBContract.FuturePoiEntry.TABLE_NAME + " (" +
                    FuturePoiDBContract.FuturePoiEntry._ID + " INTEGER PRIMARY KEY," +
                    FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_NAME + TEXT_TYPE + COMMA_SEP +
                    FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LNG + TEXT_TYPE + COMMA_SEP +
                    FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LAT + TEXT_TYPE + COMMA_SEP +
                    FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ARRIVE_TIME + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FuturePoiDBContract.FuturePoiEntry.TABLE_NAME;


    public FuturePoiDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSIION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}

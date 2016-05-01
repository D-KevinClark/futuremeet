package com.kevin.futuremeet.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kevin.futuremeet.utility.Config;

/**
 * Created by carver on 2016/5/1.
 */
public class FollowerDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "follower.db";
    private static final int DATABASE_VERSIION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FollowerDBContract.FollowerEntry.TABLE_NAME + " (" +
                    FollowerDBContract.FollowerEntry._ID + " INTEGER PRIMARY KEY," +
                    FollowerDBContract.FollowerEntry.FOLLOWER_DETAIL_INFO_ID + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FollowerDBContract.FollowerEntry.TABLE_NAME;


    public FollowerDBHelper(Context context) {
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

package com.kevin.futuremeet.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.RelationShip;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.beans.UserDetailContract;
import com.kevin.futuremeet.database.FollowerDBContract;
import com.kevin.futuremeet.database.FollowerDBHelper;
import com.kevin.futuremeet.fragment.UserPreferInfoDialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// TODO: 2016/4/9 this is just a test activity which should be deleted eventually
public class TestActivity extends AppCompatActivity {

    public static final String TAG = TestActivity.class.getSimpleName();

    private Button mSendButton;
    private Button mShowButton;

    private EditText mLngEditText;
    private EditText mLatEditText;
    private EditText mOrderEditText;


    private TextView mShowTextview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        FollowerDBHelper helper = new FollowerDBHelper(getApplication());
        saveNewRelationToLocalDB();

        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(
                FollowerDBContract.FollowerEntry.TABLE_NAME,
                null,
                FollowerDBContract.FollowerEntry.FOLLOWER_DETAIL_INFO_ID + " = ? ",
                new String[]{"5719880c71cfe40057544800"},
                null,
                null,
                null
        );
        if (cursor.moveToNext()) {
            Toast.makeText(this, R.string.you_have_already_follow_this_person, Toast.LENGTH_SHORT).show();
        } else {
        }

        cursor.close();
        database.close();
    }


    private void saveNewRelationToLocalDB() {
        SQLiteOpenHelper helper = new FollowerDBHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FollowerDBContract.FollowerEntry.FOLLOWER_DETAIL_INFO_ID, "5719880c71cfe40057544800");
        database.insert(
                FollowerDBContract.FollowerEntry.TABLE_NAME,
                null,
                values
        );
        database.close();
    }
}

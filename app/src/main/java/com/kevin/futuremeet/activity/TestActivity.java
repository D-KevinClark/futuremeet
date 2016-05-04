package com.kevin.futuremeet.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.MomentLikeContrast;
import com.kevin.futuremeet.beans.RelationShipContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.NetUtils;
import com.kevin.futuremeet.utility.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

// TODO: 2016/4/9 this is just a test activity which should be deleted eventually
public class TestActivity extends AppCompatActivity {

    public static final String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                AVUser currUser = AVUser.getCurrentUser();
                String userid = currUser.getAVObject(UserContract.USER_BASIC_INFO).getObjectId();
                String username = currUser.getUsername();
                String avatar = currUser.getAVFile(UserContract.AVATAR).getThumbnailUrl(false, 50, 50, 100, "jsp");
                return NetUtils.getToken(userid, username, avatar);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.i(TAG, "onPostExecute: " + s);
            }
        }.execute();

    }

}

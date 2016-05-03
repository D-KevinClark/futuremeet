package com.kevin.futuremeet.activity;

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

        AVQuery<AVObject> query = new AVQuery<>(MomentLikeContrast.CLASS_NAME);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.include(MomentLikeContrast.MOMENT);
        query.include(MomentLikeContrast.TO_USER_BASIC_INFO);
        query.include(MomentLikeContrast.MOMENT + "." + MomentContract.IMAGES);


        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        query.whereLessThanOrEqualTo(RelationShipContract.ESTABLISH_TIME, date);

        AVObject cuurUserBasicInfoAVobj = AVUser.getCurrentUser().getAVObject(UserContract.USER_BASIC_INFO);
        query.whereEqualTo(MomentLikeContrast.FROM_USER_BASIC_INFO, cuurUserBasicInfoAVobj);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.i(TAG, "onCreate: " + list.size());
                } else {
                    Log.i(TAG, "onCreate: " + e.getMessage());
                }
            }
        });

    }


}

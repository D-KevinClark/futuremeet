package com.kevin.futuremeet.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.beans.UserDetailContract;
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

        AVUser user = AVUser.getCurrentUser();
        AVObject detailObj = (AVObject) user.get(UserContract.USER_DETAIL_INFO);
        String detailInfoId = detailObj.getObjectId();

        AVQuery<AVObject> query = new AVQuery<>(UserDetailContract.CLASS_NAME);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(10 * 60 * 1000);
        if (query.hasCachedResult()) {
            Log.i(TAG, "done: has cache");
        } else {
            Log.i(TAG, "done: no cache");
        }

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.i(TAG, "done: "+list.size());
                } else {
                    Log.i(TAG, "done: "+e.getMessage());
                }
            }
        });

//        AVQuery<AVObject> query1 = new AVQuery<>(MomentContract.CLASS_NAME);
//        query1.whereLessThanOrEqualTo(MomentContract.LIKE_COUNTER, 10);
//        query1.whereGreaterThanOrEqualTo(MomentContract.LIKE_COUNTER, 1);
//
//        AVQuery<AVObject> query2 = new AVQuery<>(MomentContract.CLASS_NAME);
//        query2.whereStartsWith(MomentContract.CONTENT, "push");
//
//        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(query1, query2));
//        AVGeoPoint geoPoint = new AVGeoPoint(30.2352988, 120.0512282);
//        query.whereWithinKilometers(MomentContract.LOCATION, geoPoint, 3);
//
//        query.orderByDescending(MomentContract.PUBLISH_TIME);
//
//        query.whereEqualTo(MomentContract.GEDER, 1);
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if (e == null) {
//                    Log.i("matag", "done: " + list.size());
//                    for (AVObject o : list) {
//                        Log.i("matag", "done: " + o.get(MomentContract.CONTENT));
//                    }
//                } else {
//                    Log.i("matag", "done: " +e.getMessage());
//                }
//
//            }
//        });

//        mLngEditText = (EditText) findViewById(R.id.lngitude);
//        mLatEditText = (EditText) findViewById(R.id.latitude);
//        mOrderEditText = (EditText) findViewById(R.id.order_number);
//
//        mSendButton = (Button) findViewById(R.id.send_button);
//        mShowButton = (Button) findViewById(R.id.show_button);
//
//        mShowTextview = (TextView) findViewById(R.id.result_textview);
//        final AVUser user = AVUser.getCurrentUser();
//        if (user != null) {
//        } else {
//            Log.i("mytag", "onCreate: null");
//        }
//
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                double lng = Double.valueOf(mLngEditText.getText().toString());
//                double lat = Double.valueOf(mLatEditText.getText().toString());
//                int order = Integer.valueOf(mOrderEditText.getText().toString());
//
//                AVGeoPoint geoPoint = new AVGeoPoint(lat, lng);
//                AVObject avObject = new AVObject("GeoPointText");
//                avObject.put("orderNumber", order);
//                avObject.put("location", geoPoint);
//                avObject.put("user", AVUser.getCurrentUser());
//
//                avObject.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if (e == null) {
//                            mShowTextview.setText("success");
//                        } else {
//                            e.printStackTrace();
//                            mShowTextview.setText(e.getCode() + "  " + e.getMessage());
//                        }
//                    }
//                });
//            }
//        });
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 13);
//        calendar.set(Calendar.MINUTE, 13);
//        final Date minDate = calendar.getTime();
//        calendar.set(Calendar.MINUTE, 30);
//        final Date maxDate = calendar.getTime();


//        mShowButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//                AVQuery<AVObject> query = new AVQuery<AVObject>("GeoPointText");
//                final AVGeoPoint avGeoPoint = new AVGeoPoint(30.229826, 120.04091);
//                query.whereWithinKilometers("location", avGeoPoint, 1)
//                        .whereExists("user")
//                        .include("user");
//
//
//                query.findInBackground(new FindCallback<AVObject>() {
//                    @Override
//                    public void done(List<AVObject> list, AVException e) {
//                        if (e == null) {
//                            for (AVObject object : list) {
//                                AVUser avUser = (AVUser) object.get("user");
//                                mShowTextview.append(AVUser.getCurrentUser().getSessionToken() + "  \n  ");
//                                mShowTextview.append(avUser.getObjectId() + "\n" + avUser.getMobilePhoneNumber() + "\n"
//                                        + avUser.getSessionToken() + "\n");
//                                AVGeoPoint geoPoint = object.getAVGeoPoint("location");
//                                mShowTextview.append(object.get("orderNumber") + "  " + geoPoint.getLongitude() + " " + geoPoint.getLatitude() + "\n");
//                            }
//                        } else {
//                            mShowTextview.setText(e.getCode() + "    " + e.getMessage());
//                        }
//                    }
//                });
    }

//        });
//    }
}

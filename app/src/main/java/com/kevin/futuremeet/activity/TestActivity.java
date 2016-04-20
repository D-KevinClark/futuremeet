package com.kevin.futuremeet.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.kevin.futuremeet.R;

import java.util.Calendar;
import java.util.Date;

// TODO: 2016/4/9 this is just a test activity which should be deleted eventually
public class TestActivity extends AppCompatActivity {

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

        mLngEditText = (EditText) findViewById(R.id.lngitude);
        mLatEditText = (EditText) findViewById(R.id.latitude);
        mOrderEditText = (EditText) findViewById(R.id.order_number);

        mSendButton = (Button) findViewById(R.id.send_button);
        mShowButton = (Button) findViewById(R.id.show_button);

        mShowTextview = (TextView) findViewById(R.id.result_textview);
        final AVUser user = AVUser.getCurrentUser();
        if (user != null) {
        } else {
            Log.i("mytag", "onCreate: null");
        }

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lng = Double.valueOf(mLngEditText.getText().toString());
                double lat = Double.valueOf(mLatEditText.getText().toString());
                int order = Integer.valueOf(mOrderEditText.getText().toString());

                AVGeoPoint geoPoint = new AVGeoPoint(lat, lng);
                AVObject avObject = new AVObject("GeoPointText");
                avObject.put("orderNumber", order);
                avObject.put("location", geoPoint);
                avObject.put("user", AVUser.getCurrentUser());

                avObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            mShowTextview.setText("success");
                        } else {
                            e.printStackTrace();
                            mShowTextview.setText(e.getCode() + "  " + e.getMessage());
                        }
                    }
                });
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 13);
        final Date minDate = calendar.getTime();
        calendar.set(Calendar.MINUTE, 30);
        final Date maxDate = calendar.getTime();


        mShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });
    }
}

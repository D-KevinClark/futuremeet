package com.kevin.futuremeet.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kevin.futuremeet.R;

// TODO: 2016/4/9 this is just a test activity which should be deleted eventually
public class TestActivity extends AppCompatActivity {

    public static final String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

    }



}

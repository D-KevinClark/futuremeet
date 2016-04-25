package com.kevin.futuremeet.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.kevin.futuremeet.MainActivity;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.UserContract;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        AVUser user = AVUser.getCurrentUser();
        Intent intent;
        if (user == null) {
            intent = new Intent(this, LoginActivity.class);
        } else {

            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}

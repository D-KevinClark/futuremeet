package com.kevin.futuremeet.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;

import java.util.Arrays;

public class UserNameEditActivity extends AppCompatActivity {

    private EditText userNameEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_edit);

        userNameEdit = (EditText) findViewById(R.id.username_edittext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setTitle(R.string.modify_username);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_complete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String username = userNameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            return true;
        }
        AVUser user = AVUser.getCurrentUser();
        AVObject userBasicInfo = user.getAVObject(UserContract.USER_BASIC_INFO);
        user.setUsername(username);
        userBasicInfo.put(UserBasicInfoContract.USERNAME, username);
        user.saveEventually();
        userBasicInfo.saveEventually();
        finish();
        return true;
    }
}

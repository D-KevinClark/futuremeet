package com.kevin.futuremeet.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.kevin.futuremeet.R;

public class PasswordChangeAcitivity extends AppCompatActivity {

    private EditText mOldPasswordEdit;
    private EditText mNewPasswordEdit;

    private Button mChangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change_acitivity);

        initViews();
        initEvents();

    }

    private void initEvents() {
        mChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mOldPassword = mOldPasswordEdit.getText().toString().trim();
                String mNewPassword = mNewPasswordEdit.getText().toString().trim();

                if (!TextUtils.isEmpty(mOldPassword) && !TextUtils.isEmpty(mNewPassword)) {
                    AVUser user = AVUser.getCurrentUser();
                    user.updatePasswordInBackground(mOldPassword, mNewPassword,new UpdatePasswordCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Toast.makeText(PasswordChangeAcitivity.this, R.string.change_password_success, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(PasswordChangeAcitivity.this, R.string.password_change_failed_please_try_later, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initViews() {
        mOldPasswordEdit = (EditText) findViewById(R.id.old_password_edittext);
        mNewPasswordEdit = (EditText) findViewById(R.id.new_password_edittext);
        mChangeButton = (Button) findViewById(R.id.change_password_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setTitle(R.string.change_password);
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
}

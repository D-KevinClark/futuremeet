package com.kevin.futuremeet.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.kevin.futuremeet.MainActivity;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.Util;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mPhoneInputLayout;
    private TextInputLayout mPasswordInputLayout;
    private TextView mPhoneTextview;
    private TextView mPasswordTextview;
    private Button mLoginButton;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initEvents();
    }

    private void initEvents() {
        mPhoneTextview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passowrd = mPasswordTextview.getText().toString();
                if (!TextUtils.isEmpty(passowrd) && s.length() > 0) {
                    mLoginButton.setEnabled(true);
                } else {
                    mLoginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPasswordTextview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mPhoneTextview.getText().toString();
                if (!TextUtils.isEmpty(phone) && s.length() > 0) {
                    mLoginButton.setEnabled(true);
                } else {
                    mLoginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        if (!Util.isNetworkAvailabel(this)) {
            Toast.makeText(LoginActivity.this, R.string.please_check_network, Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.is_logining));
        progressDialog.show();

        String phone = mPhoneTextview.getText().toString();
        String password = mPasswordTextview.getText().toString();


        AVUser.loginByMobilePhoneNumberInBackground(phone, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {


                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (e == null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(R.string.login_fail)
                            .setMessage(R.string.phone_ro_password_wrroy)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.login);
        setSupportActionBar(mToolbar);
        mPhoneInputLayout = (TextInputLayout) findViewById(R.id.phone_input_layout);
        mPasswordInputLayout = (TextInputLayout) findViewById(R.id.password_input_layout);
        mPhoneTextview = (TextView) findViewById(R.id.phone_edittext);
        mPasswordTextview = (TextView) findViewById(R.id.password_edittext);
        mLoginButton = (Button) findViewById(R.id.login_button);
    }


}

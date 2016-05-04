package com.kevin.futuremeet.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.kevin.futuremeet.FMApplication;
import com.kevin.futuremeet.MainActivity;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.NetUtils;
import com.kevin.futuremeet.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mPhoneInputLayout;
    private TextInputLayout mPasswordInputLayout;
    private TextView mPhoneTextview;
    private TextView mPasswordTextview;
    private Button mLoginButton;
    private Toolbar mToolbar;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initEvents();
    }

    private void initEvents() {

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
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
                    getRongIMToken();
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
        mRegisterButton = (Button) findViewById(R.id.go_register_button);
    }




    private void getRongIMToken() {
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
            protected void onPostExecute(String result) {
                try {
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("code");
                        String token = jsonObject.getString("token");
                        if (code == 200) {
                            connectToRongIM(token);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void connectToRongIM(String token) {

        if (getApplicationInfo().packageName.equals(FMApplication.getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {

                    Log.d("LoginActivity", "--onSuccess" + userid);

                    AVUser user = AVUser.getCurrentUser();
                    String username = user.getUsername();

                    AVFile avatar = user.getAVFile(UserContract.AVATAR);
                    String url = avatar.getThumbnailUrl(false, 50, 50, 100, "jpg");

                    if (RongIM.getInstance() != null) {
                        RongIM.getInstance().setCurrentUserInfo(new UserInfo(
                                userid, username, Uri.parse(url)
                        ));
                    }
                    RongIM.getInstance().setMessageAttachedUserInfo(true);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 *                  http://www.rongcloud.cn/docs/android.html#常见错误码
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }


}

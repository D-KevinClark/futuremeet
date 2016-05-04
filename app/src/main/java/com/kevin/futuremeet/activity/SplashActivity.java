package com.kevin.futuremeet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.kevin.futuremeet.FMApplication;
import com.kevin.futuremeet.MainActivity;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        AVUser user = AVUser.getCurrentUser();
        Intent startIntent;
        if (user == null) {
            startIntent = new Intent(this, LoginActivity.class);
            startActivity(startIntent);
        } else {
            getRongIMToken();
        }
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

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
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

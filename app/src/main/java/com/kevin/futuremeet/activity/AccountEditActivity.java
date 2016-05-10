package com.kevin.futuremeet.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.database.FollowerDBContract;
import com.kevin.futuremeet.database.FollowerDBHelper;
import com.kevin.futuremeet.utility.Config;
import com.kevin.futuremeet.utility.Util;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class AccountEditActivity extends AppCompatActivity {

    private View mChangeAvatarLayout;
    private View mChangeUsernameLayout;
    private View mChangePasswordLayout;
    private SwitchCompat mPushServiceSwitch;

    private ImageView mAvatarImageView;
    private Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);
        initviews();
        initEvent();

        showAvatar();
    }

    private void showAvatar() {
        AVUser user = AVUser.getCurrentUser();
        AVFile avatar = user.getAVFile(UserContract.AVATAR);
        int size = getResources().getDimensionPixelSize(R.dimen.setting_page_avatar_size);
        String url = avatar.getThumbnailUrl(false, size, size, 100, "jpg");
        Glide.with(this)
                .load(url)
                .asBitmap()
                .into(mAvatarImageView);
    }

    Handler avatarChangeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(AccountEditActivity.this, R.string.avatar_change_success, Toast.LENGTH_SHORT).show();
            showAvatar();
        }
    };

    private void initEvent() {
        mChangeAvatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionConfig config = new FunctionConfig.Builder()
                        .setMutiSelectMaxSize(1)
                        .setEnableCamera(true)
                        .build();
                GalleryFinal.openGalleryMuti(100,config,new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        if (reqeustCode == 100 && resultList != null) {
                            PhotoInfo photoInfo = resultList.get(0);
                            String photoPath = photoInfo.getPhotoPath();
                            ByteArrayOutputStream outputStream = Util.decodeImageFileForUpload(photoPath, AccountEditActivity.this);
                            final AVFile avatarFile = new AVFile(UserContract.AVATAR, outputStream.toByteArray());
                            avatarFile.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        AVUser user = AVUser.getCurrentUser();
                                        AVObject userBasicInfo = user.getAVObject(UserContract.USER_BASIC_INFO);
                                        user.put(UserContract.AVATAR, avatarFile);
                                        userBasicInfo.put(UserContract.AVATAR, avatarFile);
                                        AVObject.saveAllInBackground(Arrays.asList(user, userBasicInfo), new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    avatarChangeHandler.obtainMessage().sendToTarget();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AccountEditActivity.this, R.string.avatar_upload_failed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        Toast.makeText(AccountEditActivity.this, R.string.avatar_selection_failed_try_again, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser user = AVUser.getCurrentUser();
                user.logOut();
                romoveAllDataInRelationShipDB();
                Intent intent = new Intent(AccountEditActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }


        });

        mChangeUsernameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountEditActivity.this, UserNameEditActivity.class);
                startActivity(intent);
            }
        });

        mChangePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountEditActivity.this, PasswordChangeAcitivity.class);
                startActivity(intent);
            }
        });

        mPushServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccountEditActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    editor.putBoolean(Config.SETTING_PUSH_SERVICE_ENABLE, true);
                } else {
                    editor.putBoolean(Config.SETTING_PUSH_SERVICE_ENABLE, false);
                }
                editor.commit();
            }
        });
    }

    private void romoveAllDataInRelationShipDB() {
        FollowerDBHelper helper = new FollowerDBHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete(FollowerDBContract.FollowerEntry.TABLE_NAME, null, null);
    }

    private void initviews() {
        mChangeAvatarLayout = findViewById(R.id.change_avatar_layout);
        mChangeUsernameLayout = findViewById(R.id.change_username_layout);
        mChangePasswordLayout = findViewById(R.id.change_password_layout);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enablePushServiece=sharedPreferences.getBoolean(Config.SETTING_PUSH_SERVICE_ENABLE, true);
        mPushServiceSwitch = (SwitchCompat) findViewById(R.id.push_service_switch);
        if (enablePushServiece) {
            mPushServiceSwitch.setChecked(true);
        } else {
            mPushServiceSwitch.setChecked(false);
        }


        mAvatarImageView = (ImageView) findViewById(R.id.avatar);
        mLogoutButton = (Button) findViewById(R.id.logout_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setTitle(R.string.account_info_modify);
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

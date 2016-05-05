package com.kevin.futuremeet.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
    private View mChangeSettingLayout;

    private ImageView mAvatarImageView;

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
    }

    private void initviews() {
        mChangeAvatarLayout = findViewById(R.id.change_avatar_layout);
        mChangeUsernameLayout = findViewById(R.id.change_username_layout);
        mChangePasswordLayout = findViewById(R.id.change_password_layout);
        mChangeSettingLayout = findViewById(R.id.change_setting_layout);

        mAvatarImageView = (ImageView) findViewById(R.id.avatar);

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

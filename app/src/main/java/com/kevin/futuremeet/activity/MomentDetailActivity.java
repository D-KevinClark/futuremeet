package com.kevin.futuremeet.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.FMApplication;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.fragment.ComentsForCertainMomentFraqment;
import com.kevin.futuremeet.utility.Util;

import java.util.Date;
import java.util.List;

public class MomentDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOMENT_ID = "moment_id";


    private TextView contentText;
    private LinearLayout imagesContainer;
    private ImageView avatarImage;
    private TextView usernameText;
    private TextView arriveTimeDiffText;
    private TextView userAgeText;
    private TextView distanceText;
    private TextView publishTimeText;
    private ImageView commentImage;
    private ImageView likeImage;
    private TextView likeNumebrText;
    private TextView commentNumberText;
    private ImageView genderImage;
    private View likeLayoutView;
    private View commentLayoutView;
    private EditText commentEditText;
    private ImageButton commentSendButton;
    private View commentAreaView;

    private AVObject mMoment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_detail);
        initViews();
        initEvents();
        findMomentObj();
    }

    private void initEvents() {
        commentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/5/5  
            }
        });
    }

    private void findMomentObj() {
        AVQuery<AVObject> query = new AVQuery<>(MomentContract.CLASS_NAME);
        query.include(MomentContract.USER_BASIC_INFO);
        query.include(MomentContract.IMAGES);
        query.getInBackground(getIntent().getStringExtra(EXTRA_MOMENT_ID), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                mMoment = avObject;
                showMomentInfo();
            }
        });
    }


    private void showMomentInfo() {
        if (mMoment == null) {
            return;
        }

        final AVObject userBasicInfo = mMoment.getAVObject(MomentContract.USER_BASIC_INFO);

        String content = mMoment.getString(MomentContract.CONTENT);
        contentText.setText(content);

        final String username = userBasicInfo.getString(UserBasicInfoContract.USERNAME);
        usernameText.setText(username);

        int gender = mMoment.getInt(UserContract.GENDER);
        if (gender == 1) {
            genderImage.setImageResource(R.drawable.male_icon);
        } else {
            genderImage.setImageResource(R.drawable.female_icon);
        }

        int age = mMoment.getInt(UserContract.AGE);
        userAgeText.setText(age + "");

        FMApplication application = (FMApplication) getApplication();
        AVGeoPoint userCurrentAvGeoPoint = application.getUserCurrentAvGeoPoint();
        AVGeoPoint geoPoint = mMoment.getAVGeoPoint(MomentContract.LOCATION);
        String distance = Util.getProperDistanceFormat(this, geoPoint.distanceInKilometersTo(userCurrentAvGeoPoint));
        distanceText.setText(distance);

        Date date = mMoment.getDate(MomentContract.ARRIVE_TIME);
        arriveTimeDiffText.setText(Util.getProperTimeDiffFormat(this,date));

        Date publishDate = mMoment.getDate(MomentContract.PUBLISH_TIME);
        publishTimeText.setText(Util.getProperPublishTimeFormate(this, publishDate));

        likeNumebrText.setText(mMoment.get(MomentContract.LIKE_COUNTER) + "");


        int size = getResources().getDimensionPixelSize(R.dimen.moment_avatar_size_moment);
        AVFile avatar = userBasicInfo.getAVFile(UserBasicInfoContract.AVATAR);
        String url = avatar.getThumbnailUrl(false, size, size, 100, "jpg");
        Glide.with(this)
                .load(url)
                .asBitmap()
                .into(avatarImage);

        likeNumebrText.setText("" + mMoment.getInt(MomentContract.LIKE_COUNTER));
        commentNumberText.setText("" + mMoment.getInt(MomentContract.COMMENT_COUNTER));

        if (imagesContainer.getChildCount() != 0) {
            imagesContainer.removeAllViews();
        }
        List<AVFile> images = mMoment.getList(MomentContract.IMAGES);
        int imageSize = getResources().getDimensionPixelSize(R.dimen.moment_images_size);
        int imageViewMarginRight = getResources().getDimensionPixelSize(R.dimen.moment_images_margin_right);

        int imagesMarginTop = getResources().getDimensionPixelOffset(R.dimen.moment_images_margin_right);
        if (images == null) return;//if there is no image within this post moment just return
        for (int i = 0; i < images.size(); i++) {
            AVFile image = images.get(i);
            ImageView imageView = new ImageView(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
            layoutParams.setMargins(0, imagesMarginTop, imageViewMarginRight, 0);
            imageView.setLayoutParams(layoutParams);
            imagesContainer.addView(imageView);
            //down load a thumbnail to save the network traffic
            url = image.getThumbnailUrl(false, imageSize, imageSize, 100, "jpg");
            Glide.with(this)
                    .load(url)
                    .asBitmap()
                    .placeholder(R.color.greyShadow)
                    .into(imageView);
        }
    }


    private void initViews() {
        contentText = (TextView) findViewById(R.id.moment_content_textview);
        imagesContainer = (LinearLayout) findViewById(R.id.moment_images_container);
        avatarImage = (ImageView) findViewById(R.id.avatar_imageview);
        usernameText = (TextView) findViewById(R.id.username_text);
        arriveTimeDiffText = (TextView) findViewById(R.id.arrive_time_diff_textview);
        userAgeText = (TextView) findViewById(R.id.userage_textview);
        distanceText = (TextView) findViewById(R.id.poi_distance_textview);
        publishTimeText = (TextView) findViewById(R.id.publish_time_textview);
        commentImage = (ImageView) findViewById(R.id.comment_imageview);
        likeImage = (ImageView) findViewById(R.id.like_imageview);
        likeNumebrText = (TextView) findViewById(R.id.like_number_text);
        commentNumberText = (TextView) findViewById(R.id.comment_number_text);
        genderImage = (ImageView) findViewById(R.id.gender_imageview);
        likeLayoutView = findViewById(R.id.like_layout);
        commentLayoutView = findViewById(R.id.comment_layout);
        commentAreaView = findViewById(R.id.comment_area_view);
        commentEditText = (EditText) findViewById(R.id.comment_edittext);
        commentSendButton = (ImageButton) findViewById(R.id.send_comment_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.moment);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        ComentsForCertainMomentFraqment fraqment = ComentsForCertainMomentFraqment.newInstance(
                getIntent().getStringExtra(EXTRA_MOMENT_ID)
                , null
        );
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fraqment, null).commit();

    }
}

package com.kevin.futuremeet.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.RelationShipContract;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.beans.UserDetailContract;
import com.kevin.futuremeet.database.FollowerDBContract;
import com.kevin.futuremeet.database.FollowerDBHelper;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.rong.imkit.RongIM;

public class UserDetailInfoActivity extends AppCompatActivity {


    public static final String EXTRA_USER_BASIC_INFO_ID = "basic_info_id";
    private static final String TAG = UserDetailInfoActivity.class.getSimpleName();


    private TextView mOccupationText;
    private TextView mSchoolOrFirmText;
    private TextView mHometownText;
    private TextView mIdioGraghText;

    private FlowLayout mSelfLabelFlowLayout;
    private FlowLayout mMusicFlowLayout;
    private FlowLayout mFoodFlowLayout;
    private FlowLayout mSportFlowLayout;
    private FlowLayout mTVFlowLayout;
    private FlowLayout mLiteratureFlowLayout;

    private CoordinatorLayout mCoordinatorLayout;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mAppbarImage;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private String mUserBasiclInfoId;

    private AVObject mUserBasicInfoAvobject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_info);

        // TODO: 2016/5/1 check to see if has followed this person so show a different layout and function
        initViews();
        initEvent();

        prepereUserDetailInfo();

    }

    private void showUserInfo() {
        AVObject userDetailInfoObj = mUserBasicInfoAvobject.getAVObject(UserBasicInfoContract.DETAIL_INFO);
        ArrayList<String> selfLabelList = (ArrayList<String>)
                userDetailInfoObj.getList(UserDetailContract.USER_FREFER_SELFLABEL);
        ArrayList<String> musicList = (ArrayList<String>)
                userDetailInfoObj.getList(UserDetailContract.USER_FREFER_MUSIC);
        ArrayList<String> foodList = (ArrayList<String>)
                userDetailInfoObj.getList(UserDetailContract.USER_FREFER_FOOD);
        ArrayList<String> sportList = (ArrayList<String>)
                userDetailInfoObj.getList(UserDetailContract.USER_FREFERS_SPORT);
        ArrayList<String> tvList = (ArrayList<String>)
                userDetailInfoObj.getList(UserDetailContract.USER_FREFERS_TV);
        ArrayList<String> literatureList = (ArrayList<String>)
                userDetailInfoObj.getList(UserDetailContract.USER_FREFER_LITERATURE);

        String occupation = userDetailInfoObj.getString(UserDetailContract.OCCUPATION);
        String shoolOrfirm = userDetailInfoObj.getString(UserDetailContract.SCHOOL_OR_FIRM);
        String hometown = userDetailInfoObj.getString(UserDetailContract.HOMETOWN);
        String idiograph = userDetailInfoObj.getString(UserDetailContract.IDIOGRAPH);

        mOccupationText.setText(occupation);
        mSchoolOrFirmText.setText(shoolOrfirm);
        mHometownText.setText(hometown);
        mIdioGraghText.setText(idiograph);

        // TODO: 2016/4/30 change the ui later
        LayoutInflater inflater = LayoutInflater.from(this);

        if (selfLabelList != null)
            for (int i = 0; i < selfLabelList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(selfLabelList.get(i));
                mSelfLabelFlowLayout.addView(view);
            }

        if (musicList != null)
            for (int i = 0; i < musicList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(musicList.get(i));
                mMusicFlowLayout.addView(view);
            }

        if (foodList != null)
            for (int i = 0; i < foodList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(foodList.get(i));
                mFoodFlowLayout.addView(view);
            }

        if (sportList != null)
            for (int i = 0; i < sportList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(sportList.get(i));
                mSportFlowLayout.addView(view);
            }

        if (tvList != null)
            for (int i = 0; i < tvList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(tvList.get(i));
                mTVFlowLayout.addView(view);
            }

        if (literatureList != null)
            for (int i = 0; i < literatureList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(literatureList.get(i));
                mLiteratureFlowLayout.addView(view);
            }

        // TODO: 2016/5/1 change the loading policy here later
        AVFile avatar = mUserBasicInfoAvobject.getAVFile(UserBasicInfoContract.AVATAR);
        String url = avatar.getUrl();
        Glide.with(this)
                .load(url)
                .into(mAppbarImage);
    }

    private void initEvent() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser();
            }
        });
    }

    private void followUser() {
        FollowerDBHelper helper = new FollowerDBHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor=database.query(
                FollowerDBContract.FollowerEntry.TABLE_NAME,
                null,
                FollowerDBContract.FollowerEntry.FOLLOWER_BASIC_INFO_ID + "=? ",
                new String[]{mUserBasiclInfoId},
                null,
                null,
                null
        );
        if (cursor.moveToNext()) {
            Toast.makeText(UserDetailInfoActivity.this, R.string.you_have_already_follow_this_person, Toast.LENGTH_SHORT).show();
        } else {
            saveNewRelationToServer();
        }

        cursor.close();
        database.close();
    }

    private void saveNewRelationToServer() {
        final String currentUserBasicInfoID =  AVUser.getCurrentUser().getAVObject(UserContract.USER_BASIC_INFO).getObjectId();
        AVQuery<AVObject> query = new AVQuery<>(RelationShipContract.CLASS_NAME);

        final AVObject userBasicInfoAVobj = AVObject.createWithoutData(UserBasicInfoContract.CLASS_NAME,mUserBasiclInfoId);
        final AVObject currUserBasicInfoAVobj = AVObject.createWithoutData(UserBasicInfoContract.CLASS_NAME, currentUserBasicInfoID);
        query.whereEqualTo(RelationShipContract.FROM, userBasicInfoAVobj);
        query.whereEqualTo(RelationShipContract.TO, currUserBasicInfoAVobj);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        AVObject objNew = new AVObject(RelationShipContract.CLASS_NAME);
                        objNew.put(RelationShipContract.FROM, currUserBasicInfoAVobj);
                        objNew.put(RelationShipContract.TO, userBasicInfoAVobj);
                        objNew.put(RelationShipContract.TYPE, RelationShipContract.TYPE_SINGLE_SIDE);
                        objNew.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    saveNewRelationToLocalDB();
                                    Toast.makeText(UserDetailInfoActivity.this, R.string.follow_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserDetailInfoActivity.this, R.string.folllow_failed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        AVObject objOrig = list.get(0);
                        AVObject objNew = new AVObject(RelationShipContract.CLASS_NAME);
                        objNew.put(RelationShipContract.FROM, currUserBasicInfoAVobj);
                        objNew.put(RelationShipContract.TO, userBasicInfoAVobj);
                        objNew.put(RelationShipContract.TYPE, RelationShipContract.TYPE_BOTH_SIDE);
                        objOrig.put(RelationShipContract.TYPE, RelationShipContract.TYPE_BOTH_SIDE);
                        AVObject.saveAllInBackground(Arrays.asList(objNew, objOrig), new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    saveNewRelationToLocalDB();
                                    Toast.makeText(UserDetailInfoActivity.this, R.string.follow_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserDetailInfoActivity.this, R.string.folllow_failed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Log.i(TAG, "done: " + e.getMessage());
                }
            }
        });

    }

    private void saveNewRelationToLocalDB() {
        SQLiteOpenHelper helper = new FollowerDBHelper(this);
        SQLiteDatabase database=helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FollowerDBContract.FollowerEntry.FOLLOWER_BASIC_INFO_ID, mUserBasiclInfoId);
        database.insert(
                FollowerDBContract.FollowerEntry.TABLE_NAME,
                null,
                values
        );
        database.close();
    }

    private void initViews() {
        //app bar
        mAppbarImage = (ImageView) findViewById(R.id.appbar_image);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        //
        mFab = (FloatingActionButton) findViewById(R.id.fab);



        mOccupationText = (TextView)findViewById(R.id.occupation_text);
        mSchoolOrFirmText = (TextView)findViewById(R.id.school_or_firm_text);
        mHometownText = (TextView)findViewById(R.id.hometown_text);
        mIdioGraghText = (TextView)findViewById(R.id.idiograph_text);

        mSelfLabelFlowLayout = (FlowLayout)findViewById(R.id.selflable_container);
        mMusicFlowLayout = (FlowLayout)findViewById(R.id.music_container);
        mFoodFlowLayout = (FlowLayout) findViewById(R.id.food_container);
        mSportFlowLayout = (FlowLayout)findViewById(R.id.sport_container);
        mTVFlowLayout = (FlowLayout)findViewById(R.id.tv_container);
        mLiteratureFlowLayout = (FlowLayout)findViewById(R.id.literature_container);

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
    }

    public void prepereUserDetailInfo() {
        AVQuery<AVObject> query = new AVQuery<>(UserBasicInfoContract.CLASS_NAME);
        mUserBasiclInfoId = getIntent().getStringExtra(EXTRA_USER_BASIC_INFO_ID);

        query.include(UserBasicInfoContract.DETAIL_INFO);

        query.getInBackground(mUserBasiclInfoId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                mUserBasicInfoAvobject = object;
                showUserInfo();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_detail_info_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mUserBasicInfoAvobject != null && RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(
                    this,
                    mUserBasiclInfoId,
                    mUserBasicInfoAvobject.getString(UserBasicInfoContract.USERNAME)
            );
        }
        return true;
    }
}

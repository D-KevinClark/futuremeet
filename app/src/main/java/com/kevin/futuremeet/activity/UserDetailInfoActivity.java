package com.kevin.futuremeet.activity;

import android.content.ContentValues;
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
import com.kevin.futuremeet.beans.RelationShip;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.beans.UserDetailContract;
import com.kevin.futuremeet.database.FollowerDBContract;
import com.kevin.futuremeet.database.FollowerDBHelper;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDetailInfoActivity extends AppCompatActivity {


    public static final String EXTRA_USER_DETAIL_ID = "detail_id";
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

    private String mUserDetailInfoId;

    private AVObject mUserDetailInfoAvobject;


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
        ArrayList<String> selfLabelList = (ArrayList<String>)
                mUserDetailInfoAvobject.getList(UserDetailContract.USER_FREFER_SELFLABEL);
        ArrayList<String> musicList = (ArrayList<String>)
                mUserDetailInfoAvobject.getList(UserDetailContract.USER_FREFER_MUSIC);
        ArrayList<String> foodList = (ArrayList<String>)
                mUserDetailInfoAvobject.getList(UserDetailContract.USER_FREFER_FOOD);
        ArrayList<String> sportList = (ArrayList<String>)
                mUserDetailInfoAvobject.getList(UserDetailContract.USER_FREFERS_SPORT);
        ArrayList<String> tvList = (ArrayList<String>)
                mUserDetailInfoAvobject.getList(UserDetailContract.USER_FREFERS_TV);
        ArrayList<String> literatureList = (ArrayList<String>)
                mUserDetailInfoAvobject.getList(UserDetailContract.USER_FREFER_LITERATURE);

        String occupation = mUserDetailInfoAvobject.getString(UserDetailContract.OCCUPATION);
        String shoolOrfirm = mUserDetailInfoAvobject.getString(UserDetailContract.SCHOOL_OR_FIRM);
        String hometown = mUserDetailInfoAvobject.getString(UserDetailContract.HOMETOWN);
        String idiograph = mUserDetailInfoAvobject.getString(UserDetailContract.IDIOGRAPH);

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
        AVFile avatar = mUserDetailInfoAvobject.getAVFile(UserDetailContract.AVATAR);
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
                FollowerDBContract.FollowerEntry.FOLLOWER_DETAIL_INFO_ID + "=? ",
                new String[]{mUserDetailInfoId},
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
        final String currentUserDetailInfoID =  AVUser.getCurrentUser().getAVObject(UserContract.USER_DETAIL_INFO).getObjectId();
        AVQuery<AVObject> query = new AVQuery<>(RelationShip.CLASS_NAME);

        final AVObject userDetailInfoAVobj = AVObject.createWithoutData(UserDetailContract.CLASS_NAME,mUserDetailInfoId);
        final AVObject currUserDetailInfoAVobj = AVObject.createWithoutData(UserDetailContract.CLASS_NAME, currentUserDetailInfoID);
        query.whereEqualTo(RelationShip.FROM, userDetailInfoAVobj);
        query.whereEqualTo(RelationShip.TO, currUserDetailInfoAVobj);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        AVObject objNew = new AVObject(RelationShip.CLASS_NAME);
                        objNew.put(RelationShip.FROM, currUserDetailInfoAVobj);
                        objNew.put(RelationShip.TO, userDetailInfoAVobj);
                        objNew.put(RelationShip.TYPE, RelationShip.TYPE_SINGLE_SIDE);
                        objNew.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Toast.makeText(UserDetailInfoActivity.this, R.string.follow_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserDetailInfoActivity.this, R.string.folllow_failed, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        AVObject objOrig = list.get(0);
                        AVObject objNew = new AVObject(RelationShip.CLASS_NAME);
                        objNew.put(RelationShip.FROM, currUserDetailInfoAVobj);
                        objNew.put(RelationShip.TO, userDetailInfoAVobj);
                        objNew.put(RelationShip.TYPE, RelationShip.TYPE_BOTH_SIDE);
                        objOrig.put(RelationShip.TYPE, RelationShip.TYPE_BOTH_SIDE);
                        AVObject.saveAllInBackground(Arrays.asList(objNew, objOrig), new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
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
        saveNewRelationToLocalDB();
    }

    private void saveNewRelationToLocalDB() {
        SQLiteOpenHelper helper = new FollowerDBHelper(this);
        SQLiteDatabase database=helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FollowerDBContract.FollowerEntry.FOLLOWER_DETAIL_INFO_ID, mUserDetailInfoId);
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
        AVQuery<AVObject> query = new AVQuery<>(UserDetailContract.CLASS_NAME);
        mUserDetailInfoId = getIntent().getStringExtra(EXTRA_USER_DETAIL_ID);
        query.getInBackground(mUserDetailInfoId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                mUserDetailInfoAvobject = object;
                showUserInfo();
            }
        });
    }
}

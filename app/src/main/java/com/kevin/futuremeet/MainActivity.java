package com.kevin.futuremeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.futuremeet.background.PublishMomentIntentService;
import com.kevin.futuremeet.fragment.FriendsFragment;
import com.kevin.futuremeet.fragment.FutureMeetFragment;
import com.kevin.futuremeet.fragment.MeFragment;
import com.kevin.futuremeet.fragment.NewsFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FrameLayout mFragmentContainer;

    LinearLayout mFriendsLayout;
    LinearLayout mFutureLayout;
    LinearLayout mNewsLayout;
    LinearLayout mMeLayout;

    ImageView mFriendsImage;
    ImageView mFutureImage;
    ImageView mNewsImage;
    ImageView mMeImage;

    TextView mFriendsText;
    TextView mFutureText;
    TextView mNewsText;
    TextView mUserText;


    FriendsFragment mFriendsFragment;
    FutureMeetFragment mFutureMeetFragment;
    NewsFragment mNewsFragment;
    MeFragment mMeFragment;

    private static final String TAG_FRAGMENT_ME = "tag_me_fragment";
    private static final String TAG_FRAGMENT_NEARBY = "tag_nearby_fragment";
    private static final String TAG_FRAGMENT_NEWS = "tag_news_fragment";
    private static final String TAG_FRAGMENT_FUTUREMEET = "tag_futuremeet_fragmetn";

    private static final String KEY_BUNDLE_SELECTED_TAG_LAYOTU_ID = "selected_tag_layout_id";

    //record the id of the selected tab layout id
    private int mSelectedTabLayoutID;

    FragmentManager mFragmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();

        //these code is prepared for the case that activity is killed by the system for resource
        //if so, the UI instance may still be in the memory but we lost the reference to them,
        //then by the logic in my code , the fragment will be recreate, in this way, there may be
        //fragment overlapping to each other
        if (savedInstanceState != null) {
            mSelectedTabLayoutID = savedInstanceState.getInt(KEY_BUNDLE_SELECTED_TAG_LAYOTU_ID);

            mFutureMeetFragment = (FutureMeetFragment) mFragmentManager.findFragmentByTag(TAG_FRAGMENT_FUTUREMEET);
            mFriendsFragment = (FriendsFragment) mFragmentManager.findFragmentByTag(TAG_FRAGMENT_NEARBY);
            mNewsFragment = (NewsFragment) mFragmentManager.findFragmentByTag(TAG_FRAGMENT_NEWS);
            mMeFragment = (MeFragment) mFragmentManager.findFragmentByTag(TAG_FRAGMENT_ME);
            //update the status of the bottom tab and the fragments show-hide status
            updataTabAndFragStatus();

        } else {
            //init MainActivity so when use first come in, the nearby Fragment is selected
            mFutureMeetFragment = FutureMeetFragment.newInstance(null, null);
            mFragmentManager.beginTransaction().
                    add(R.id.fragment_container, mFutureMeetFragment, TAG_FRAGMENT_FUTUREMEET).commit();
            mSelectedTabLayoutID = R.id.futuremeet_tab_layout;
            mFutureImage.setImageResource(R.drawable.futuremeet_selected);
            mFutureText.setTextColor(Color.parseColor(getString(R.string.accentColor)));
        }

        //get the broadcast that indicate the status of the moments just published(if it's successful)
        IntentFilter intentFilter = new IntentFilter(PublishMomentIntentService.STATUS_REPORT_ACTION);
        MomentUploadStatusReportReceiver reportReceiver = new MomentUploadStatusReportReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(reportReceiver, intentFilter);

    }



    private class MomentUploadStatusReportReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int status = intent.getIntExtra(PublishMomentIntentService.EXTRA_STATUS, 0);
                if (status == PublishMomentIntentService.UPLOAD_SUCCESS) {
                    Toast.makeText(MainActivity.this, R.string.moment_publish_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.moment_publish_fail, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initEvents() {
        mFriendsLayout.setOnClickListener(this);
        mFutureLayout.setOnClickListener(this);
        mNewsLayout.setOnClickListener(this);
        mMeLayout.setOnClickListener(this);
    }

    /**
     * init all the view will be used through findViewById() method
     */
    private void initViews() {
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        mFriendsImage = (ImageView) findViewById(R.id.friends_image);
        mFutureImage = (ImageView) findViewById(R.id.futuremeet_image);
        mNewsImage = (ImageView) findViewById(R.id.news_image);
        mMeImage = (ImageView) findViewById(R.id.me_image);

        mFriendsLayout = (LinearLayout) findViewById(R.id.friends_tab_layout);
        mFutureLayout = (LinearLayout) findViewById(R.id.futuremeet_tab_layout);
        mNewsLayout = (LinearLayout) findViewById(R.id.news_tab_layout);
        mMeLayout = (LinearLayout) findViewById(R.id.me_layout);

        mFriendsText = (TextView) findViewById(R.id.friends_text);
        mFutureText = (TextView) findViewById(R.id.futuremeet_text);
        mNewsText = (TextView) findViewById(R.id.news_text);
        mUserText = (TextView) findViewById(R.id.me_text);
    }


    /**
     * settle the status of the bottom tab and fragment show-or-hide status according the selected
     * bottom tab  image id
     */
    private void updataTabAndFragStatus() {
        resetTabImageState();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        //first hide all the fragment
        if (mFriendsFragment != null) fragmentTransaction.hide(mFriendsFragment);
        if (mNewsFragment != null) fragmentTransaction.hide(mNewsFragment);
        if (mMeFragment != null) fragmentTransaction.hide(mMeFragment);
        if (mFutureMeetFragment != null) fragmentTransaction.hide(mFutureMeetFragment);

        switch (mSelectedTabLayoutID) {
            case R.id.friends_tab_layout:
                mSelectedTabLayoutID = R.id.friends_tab_layout;
                if (mFriendsFragment == null) {
                    mFriendsFragment = FriendsFragment.newInstance(null, null);
                    fragmentTransaction.add(R.id.fragment_container, mFriendsFragment, TAG_FRAGMENT_NEARBY);
                } else {
                    fragmentTransaction.show(mFriendsFragment);
                }
                mFriendsImage.setImageResource(R.drawable.contact_icon_select);
                mFriendsText.setTextColor(Color.parseColor(getString(R.string.accentColor)));
                break;
            case R.id.futuremeet_tab_layout:
                mSelectedTabLayoutID = R.id.futuremeet_tab_layout;
                if (mFutureMeetFragment == null) {
                    mFutureMeetFragment = FutureMeetFragment.newInstance(null, null);
                    fragmentTransaction.add(R.id.fragment_container, mFutureMeetFragment, TAG_FRAGMENT_FUTUREMEET);
                } else {
                    fragmentTransaction.show(mFutureMeetFragment);
                }
                mFutureImage.setImageResource(R.drawable.futuremeet_selected);
                mFutureText.setTextColor(Color.parseColor(getString(R.string.accentColor)));
                break;
            case R.id.news_tab_layout:
                mSelectedTabLayoutID = R.id.news_tab_layout;
                if (mNewsFragment == null) {
                    mNewsFragment = NewsFragment.newInstance(null, null);
                    fragmentTransaction.add(R.id.fragment_container, mNewsFragment, TAG_FRAGMENT_NEWS);
                } else {
                    fragmentTransaction.show(mNewsFragment);
                }
                mNewsImage.setImageResource(R.drawable.news_selected);
                mNewsText.setTextColor(Color.parseColor(getString(R.string.accentColor)));
                break;
            case R.id.me_layout:
                mSelectedTabLayoutID = R.id.me_layout;
                if (mMeFragment == null) {
                    mMeFragment = MeFragment.newInstance(null, null);
                    fragmentTransaction.add(R.id.fragment_container, mMeFragment, TAG_FRAGMENT_ME);
                } else {
                    fragmentTransaction.show(mMeFragment);
                }
                mMeImage.setImageResource(R.drawable.user_selected);
                mUserText.setTextColor(Color.parseColor(getString(R.string.accentColor)));
                break;
        }
        fragmentTransaction.commit();
    }


    @Override
    public void onClick(View v) {
        if (mFragmentManager == null) return;
        int id = v.getId();
        if (mSelectedTabLayoutID == id)
            return;//user click the tab that they are now in so do nothing
        mSelectedTabLayoutID = id;
        updataTabAndFragStatus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_BUNDLE_SELECTED_TAG_LAYOTU_ID, mSelectedTabLayoutID);
    }

    /**
     * set the current corresponding tab image and text to normal, prepare for the change is about
     * to coming
     */
    private void resetTabImageState() {
        mFriendsText.setTextColor(0xffcccccc);
        mFriendsImage.setImageResource(R.drawable.contact_icon);
        mFutureText.setTextColor(0xffcccccc);
        mFutureImage.setImageResource(R.drawable.futuremeet);
        mNewsText.setTextColor(0xffcccccc);
        mNewsImage.setImageResource(R.drawable.news);
        mUserText.setTextColor(0xffcccccc);
        mMeImage.setImageResource(R.drawable.user);
    }

//
//    /**
//     * set the future poi info to shared preference
//     *
//     * @param poiName
//     * @param poiAdress
//     * @param poiLat
//     * @param poiLng
//     * @param showFutureMeetFragment
//     */
//    private void setFuturePoiInfoToPrefs(String poiName, String poiAdress,
//                                         String poiLat, String poiLng,
//                                         Long arriveTime, String detailLabel, boolean showFutureMeetFragment) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        sharedPreferences.edit()
//                .putString(Config.PREF_FUTURE_POI_ADRESS, poiAdress)
//                .putString(Config.PREF_FUTURE_POI_NAME, poiName)
//                .putString(Config.PREF_FUTURE_POI_LAT, poiLat)
//                .putString(Config.PREF_FUTURE_POI_LNG, poiLng)
//                .putLong(Config.PREF_FUTURE_POI_ARRIVE_TIME, arriveTime)
//                .putString(Config.PREF_FUTURE_POI_DETAIL_LABEL, detailLabel)
//                .putBoolean(Config.PREF_SHOW_FUTUREMEET_FRAGMENT, showFutureMeetFragment)
//                .apply();
//    }


//    /**
//     * when the future destination is confirmed in the {@link DestChooseFragment},this method will be called
//     *
//     * @param poiInfo
//     */
//    @Override
//    public void onFuturePOIandTimeConfirmed(Bundle poiInfo) {
//        String poiAdr = poiInfo.getString(DestChooseFragment.POI_ADDRESS);
//        String poiName = poiInfo.getString(DestChooseFragment.POI_NAME);
//        String poiLat = poiInfo.getString(DestChooseFragment.POI_LAT);
//        String poiLng = poiInfo.getString(DestChooseFragment.POI_LNG);
//        Long arriTime = poiInfo.getLong(DestChooseFragment.POI_ARRIVE_TIME);
//        String label = poiInfo.getString(DestChooseFragment.POI_DETAIL_LABEL);
//
//        setFuturePoiInfoToPrefs(poiName, poiAdr, poiLat, poiLng, arriTime, label, true);
//
//        mFutureMeetFragment = FutureMeetFragment.newInstance(poiInfo);
//        mFragmentManager.beginTransaction().remove(mDestChooseFragment)
//                .add(R.id.fragment_container, mFutureMeetFragment)
//                .commit();
//    }
}

package com.kevin.futuremeet;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kevin.futuremeet.fragment.FutureMeetFragment;
import com.kevin.futuremeet.fragment.MeFragment;
import com.kevin.futuremeet.fragment.NearbyFragment;
import com.kevin.futuremeet.fragment.NewsFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FrameLayout mFragmentContainer;

    LinearLayout mNearbyLayout;
    LinearLayout mFutureLayout;
    LinearLayout mNewsLayout;
    LinearLayout mMeLayout;

    ImageView mNearbyImage;
    ImageView mFutureImage;
    ImageView mNewsImage;
    ImageView mMeImage;

    TextView mNearbyText;
    TextView mFutureText;
    TextView mNewsText;
    TextView mUserText;


    NearbyFragment mNearbyFragment;
    FutureMeetFragment mFutureMeetFragment;
    NewsFragment mNewsFragment;
    MeFragment mMeFragment;

    //record the id of the selected tab image
    private int mSelectedTabImageID;

    FragmentManager mFragmentManager=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvents();



        //init MainActivity so when use first come in, the nearby Fragment is selected
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().
                add(R.id.fragment_container,mNearbyFragment.newInstance(null,null)).commit();
        mSelectedTabImageID=R.id.nearby_image;
        mNearbyImage.setImageResource(R.drawable.nearby_selected);
        mNearbyText.setTextColor(0xFFFF4081);

    }

    private void initEvents() {
        mNearbyLayout.setOnClickListener(this);
        mFutureLayout.setOnClickListener(this);
        mNewsLayout.setOnClickListener(this);
        mMeLayout.setOnClickListener(this);
    }

    /**
     * init all the view will be used through findViewById() method
     */
    private void initViews() {
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        mNearbyImage = (ImageView) findViewById(R.id.nearby_image);
        mFutureImage = (ImageView) findViewById(R.id.futuremeet_image);
        mNewsImage = (ImageView) findViewById(R.id.news_image);
        mMeImage = (ImageView) findViewById(R.id.me_image);

        mNearbyLayout = (LinearLayout) findViewById(R.id.nearby_tab_layout);
        mFutureLayout = (LinearLayout) findViewById(R.id.futuremeet_tab_layout);
        mNewsLayout = (LinearLayout) findViewById(R.id.news_tab_layout);
        mMeLayout = (LinearLayout) findViewById(R.id.me_layout);

        mNearbyText = (TextView) findViewById(R.id.nearby_text);
        mFutureText = (TextView) findViewById(R.id.futuremeet_text);
        mNewsText = (TextView) findViewById(R.id.news_text);
        mUserText = (TextView) findViewById(R.id.me_text);
    }

    @Override
    public void onClick(View v) {
        if (mFragmentManager == null) return;
        int id = v.getId();
        if (mSelectedTabImageID==id)return;//user click the tab that they are now in so do nothing
        resetTabImageState(mSelectedTabImageID);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (id) {
            case R.id.nearby_tab_layout:
                mSelectedTabImageID=R.id.nearby_image;
                mNearbyFragment=NearbyFragment.newInstance(null,null);
                fragmentTransaction.replace(R.id.fragment_container, mNearbyFragment);
                ((ImageView)findViewById(mSelectedTabImageID)).setImageResource(R.drawable.nearby_selected);
                mNearbyText.setTextColor(0xFFFF4081);
                break;
            case R.id.futuremeet_tab_layout:
                mSelectedTabImageID=R.id.futuremeet_image;
                mFutureMeetFragment=FutureMeetFragment.newInstance(null,null);
                fragmentTransaction.replace(R.id.fragment_container,mFutureMeetFragment);
                ((ImageView)findViewById(mSelectedTabImageID)).setImageResource(R.drawable.futuremeet_selected);
                mFutureText.setTextColor(0xFFFF4081);
                break;
            case R.id.news_tab_layout:
                mSelectedTabImageID=R.id.news_image;
                mNewsFragment = NewsFragment.newInstance(null, null);
                fragmentTransaction.replace(R.id.fragment_container, mNewsFragment);
                ((ImageView)findViewById(mSelectedTabImageID)).setImageResource(R.drawable.news_selected);
                mNewsText.setTextColor(0xFFFF4081);
                break;
            case R.id.me_layout:
                mSelectedTabImageID=R.id.me_image;
                mMeFragment = MeFragment.newInstance(null, null);
                fragmentTransaction.replace(R.id.fragment_container, mMeFragment);
                ((ImageView)findViewById(mSelectedTabImageID)).setImageResource(R.drawable.user_selected);
                mUserText.setTextColor(0xFFFF4081);
                break;
        }
        fragmentTransaction.commit();
    }

    /**
     * @param mSelectedTabImageID
     */
    private void resetTabImageState(int mSelectedTabImageID) {

        switch (mSelectedTabImageID) {
            case R.id.nearby_image:
                mNearbyText.setTextColor(0xffcccccc);
                ((ImageView)findViewById(mSelectedTabImageID)).setImageResource(R.drawable.nearby);
                break;
            case R.id.futuremeet_image:
                mFutureText.setTextColor(0xffcccccc);
                ((ImageView)findViewById(mSelectedTabImageID)).setImageResource(R.drawable.futuremeet);
                break;
            case R.id.news_image:
                mNewsText.setTextColor(0xffcccccc);
                ((ImageView)findViewById(mSelectedTabImageID)).setImageResource(R.drawable.news);
                break;
            case R.id.me_image:
                mUserText.setTextColor(0xffcccccc);
                ((ImageView)findViewById(mSelectedTabImageID)).setImageResource(R.drawable.user);
                break;
        }
    }
}

package com.kevin.futuremeet.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.fragment.FullImageDisplayFragment;

import java.util.ArrayList;

public class ImageDispalyActivity extends AppCompatActivity {

    public static final String EXTEA_IMAGES_URL = "images_url";

    private ViewPager mViewPager;

    private ArrayList<String> mImageUrlList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_dispaly);

        //hide the status bar
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        if (getIntent() != null) {
            mImageUrlList = getIntent().getStringArrayListExtra(EXTEA_IMAGES_URL);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);


        MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);


    }



    class MyViewPagerAdapter extends FragmentStatePagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FullImageDisplayFragment.newInstance(mImageUrlList.get(position));
        }

        @Override
        public int getCount() {
            return mImageUrlList.size();
        }
    }


}

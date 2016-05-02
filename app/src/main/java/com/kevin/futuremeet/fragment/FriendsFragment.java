package com.kevin.futuremeet.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevin.futuremeet.R;

public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        initViews(root);
        mViewPager = (ViewPager) root.findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) root.findViewById(R.id.tab_layout);

        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        return root;
    }

    private void initViews(View root) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        appCompatActivity.setSupportActionBar(mToolbar);

    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int FRAGMENT_NUM = 3;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = GoodFriendsFragment.newInstance(null, null);
                break;
                case 1:
                    fragment = FolloweeFragment.newInstance(null, null);
                break;
                case 2:
                    fragment = FollowerFragment.newInstance(null, null);
                break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return FRAGMENT_NUM;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = getResources().getString(R.string.good_friends);
                    break;
                case 1:
                    title = getResources().getString(R.string.followee);
                    break;
                case 2:
                    title = getResources().getString(R.string.follower);
                    break;
            }
            return title;
        }
    }

}

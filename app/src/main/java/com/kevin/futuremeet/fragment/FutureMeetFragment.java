package com.kevin.futuremeet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.MomentEditorActivity;


public class FutureMeetFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    public FutureMeetFragment() {
        // Required empty public constructor
    }


    /**
     * get a instance of this fragment
     *
     * @param param1
     * @param param2
     * @return
     */
    public static FutureMeetFragment newInstance(String param1, String param2) {
        FutureMeetFragment fragment = new FutureMeetFragment();
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
        View view = inflater.inflate(R.layout.fragment_future_meet, container, false);
        initToolbar(view);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    private void initToolbar(View view) {
        setHasOptionsMenu(true);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        appCompatActivity.setSupportActionBar(mToolbar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.nearby_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.moment_editor:
                Intent intent = new Intent(getActivity(), MomentEditorActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int FRAGMENT_NUM = 2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                MomentFragment momentFragment = MomentFragment.newInstance(null, null);
                return momentFragment;
            } else {
                PeopleFragment peopleFragment = PeopleFragment.newInstance(null, null);
                return peopleFragment;
            }
        }

        @Override
        public int getCount() {
            return FRAGMENT_NUM;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.moment_fragment_page_title);
            } else {
                return getResources().getString(R.string.people_fragment_page_title);
            }
        }
    }
}

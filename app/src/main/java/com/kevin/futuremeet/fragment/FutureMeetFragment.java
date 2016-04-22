package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.avos.avoscloud.AVGeoPoint;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.DestChooseActivity;
import com.kevin.futuremeet.activity.MomentEditorActivity;
import com.kevin.futuremeet.adapter.PoiPageFilterAdapter;
import com.kevin.futuremeet.beans.FuturePoiBean;
import com.kevin.futuremeet.database.FuturePoiDBContract;
import com.kevin.futuremeet.database.FuturePoiDBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FutureMeetFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mPageFilterImageView;

    private ListView mPageFilterListView;
    private PoiPageFilterAdapter mPageFilterAdapter;
    private PopupWindow mPageFilterPopupWindow;
    private ArrayList<FuturePoiBean> mFuturePoiList = new ArrayList<>();

    private PopupWindow mPublishChoicePopupWindow;

    private CoordinatorLayout mCoordinatorLayout;


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
        initViews(view);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);

        preparePagerFilter();
        preparePublishChoosePopWindow();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkIfPoiOutOfDate();
    }

    public void preparePublishChoosePopWindow() {
        String[] choice = {getString(R.string.publish_moment), getString(R.string.publish_future_poi)};

        View publishChoicePopWindowContent = getActivity().getLayoutInflater()
                .inflate(R.layout.publish_moment_or_poi_menu_pop_window_layout, null);
        View publishMomentLayout = publishChoicePopWindowContent.findViewById(R.id.publish_moment_layout);
        View publishPoiLayout = publishChoicePopWindowContent.findViewById(R.id.publish_poi_layout);

        publishMomentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublishChoicePopupWindow.dismiss();
                tryToPublishMoment();
            }
        });

        publishPoiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublishChoicePopupWindow.dismiss();
                tryToPublishPoi();
            }
        });

        mPublishChoicePopupWindow = new PopupWindow(publishChoicePopWindowContent, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPublishChoicePopupWindow.setTouchable(true);
        mPublishChoicePopupWindow.setOutsideTouchable(true);
        mPublishChoicePopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
    }

    private void tryToPublishPoi() {
        if (mFuturePoiList.size() >= 2) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.two_future_poi_at_most,
                    Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            Intent intent = new Intent(getContext(), DestChooseActivity.class);
            startActivity(intent);
        }
    }

    private void tryToPublishMoment() {
        if (mFuturePoiList.size() == 0) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout,
                    getString(R.string.publish_poi_first_please), Snackbar.LENGTH_SHORT);
            snackbar.setAction(getString(R.string.publish_now), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DestChooseActivity.class);
                    startActivity(intent);
                }
            });
            snackbar.show();
        } else {
            Intent intent = new Intent(getContext(), MomentEditorActivity.class);
            startActivity(intent);
        }
    }

    /**
     * prepare the page filter panel
     */
    private void preparePagerFilter() {
        preparePoiData();
        checkIfPoiOutOfDate();
        View pagerFilterPopWindowContent = getActivity().getLayoutInflater().inflate(R.layout.future_meet_page_filter, null);
        mPageFilterListView = (ListView) pagerFilterPopWindowContent.findViewById(R.id.page_filter_listview);

        View header = getActivity().getLayoutInflater().inflate(R.layout.poi_page_filter_listview_header, null);
        mPageFilterListView.addHeaderView(header);


        mPageFilterAdapter = new PoiPageFilterAdapter(getActivity(), mFuturePoiList);
        mPageFilterListView.setAdapter(mPageFilterAdapter);


        mPageFilterPopupWindow = new PopupWindow(pagerFilterPopWindowContent, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPageFilterPopupWindow.setTouchable(true);
        mPageFilterPopupWindow.setOutsideTouchable(true);
        mPageFilterPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
    }

    /**
     * read future poi data form database and set them to a list that back up the page filter listview adapter
     */
    private void preparePoiData() {
        FuturePoiDBHelper helper = new FuturePoiDBHelper(getContext());
        SQLiteDatabase database = helper.getReadableDatabase();
        String[] projection = {
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_NAME,
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ADDRESS,
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LNG,
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LAT,
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ARRIVE_TIME
        };

        String sortOrder =
                FuturePoiDBContract.FuturePoiEntry._ID + " DESC";

        Cursor c = database.query(
                FuturePoiDBContract.FuturePoiEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        while (c.moveToNext()) {
            FuturePoiBean poiBean = new FuturePoiBean();
            String poiName = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_NAME));
            String poiAdress = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ADDRESS));
            String poiLng = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LNG));
            String poiLat = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LAT));
            String poiArriveTime = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ARRIVE_TIME));

            AVGeoPoint avGeoPoint = new AVGeoPoint(Double.valueOf(poiLat), Double.valueOf(poiLng));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            Date date = null;
            try {
                date = format.parse(poiArriveTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            poiBean.setPoiName(poiName);
            poiBean.setPoiAddress(poiAdress);
            poiBean.setAvGeoPoint(avGeoPoint);
            poiBean.setArriveTime(date);

            mFuturePoiList.add(poiBean);
        }
    }

    /**
     * check to see if the future poi has out of date
     */
    private void checkIfPoiOutOfDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        FuturePoiDBHelper helper = new FuturePoiDBHelper(getContext());
        SQLiteDatabase database = helper.getWritableDatabase();
        for (int i = mFuturePoiList.size() - 1; i >= 0; i--) {
            FuturePoiBean poiBean = mFuturePoiList.get(i);
            if (poiBean.getArriveTime().before(date)) {
                mFuturePoiList.remove(poiBean);
                String selection = FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_NAME + " LIKE ?";
                String[] selectionArgs = {poiBean.getPoiName()};
                database.delete(FuturePoiDBContract.FuturePoiEntry.TABLE_NAME, selection, selectionArgs);
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.future_poi_out_of_date_reminder)
                        .setMessage(poiBean.getPoiName() + getString(R.string.out_of_date_future_poi_have_cancled_for_u))
                        .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
        database.close();
    }


    private void initViews(View view) {
        mPageFilterImageView = (ImageView) view.findViewById(R.id.page_filter_image);
        mPageFilterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfPoiOutOfDate();
                mPageFilterPopupWindow.showAtLocation(getView(), Gravity.TOP | Gravity.LEFT, 0, 50);
            }
        });
        mCoordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout);
    }


    private void initToolbar(View view) {
        setHasOptionsMenu(true);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        appCompatActivity.setSupportActionBar(mToolbar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.futuremeet_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPublishChoicePopupWindow.showAtLocation(getView(), Gravity.TOP | Gravity.RIGHT, 0,50);
        return true;
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

    /**
     * update the poi page filter
     */
    public void updatePoiPageFilter() {
        new UpdataPoiPageFilterTask().execute();
    }

    private class UpdataPoiPageFilterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mFuturePoiList.clear();
            preparePagerFilter();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mPageFilterAdapter.notifyDataSetChanged();
        }
    }
}

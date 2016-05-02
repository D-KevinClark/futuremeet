package com.kevin.futuremeet.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.adapter.MomentsRecyclerViewAdapter;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.custom.EndlessRecyclerViewScrollListener;
import com.kevin.futuremeet.utility.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MomentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = MomentContract.class.getSimpleName();


    //    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    private List<AVObject> mMomentList = new ArrayList<>();

    private MomentsRecyclerViewAdapter mMomentsAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    // TODO: 2016/4/11 this number may need to change eventually
    private static final int MOMENT_SEARCH_PAGE_SIZE = 10;

    private AVGeoPoint mCurrentSearchCenterGeoPoint = null;

    private double mCurrentSearchDistanceRange;


    //make it a private field , every time a new query is required a new instance will be created,
    //but when search more page with a same query , it should not be newed
    private AVQuery<AVObject> mMomentSearchQuery = null;

    private Date mCurrentTargetDate = null;


    public MomentFragment() {
        // Required empty public constructor
    }

    public static MomentFragment newInstance(String param1, String param2) {
        MomentFragment fragment = new MomentFragment();
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
        View view = inflater.inflate(R.layout.fragment_moments, container, false);
        initViews(view);
        initEvents();
        return view;
    }


    private void initQueryBasic() {
        mMomentSearchQuery = new AVQuery<>(MomentContract.CLASS_NAME);
        mMomentSearchQuery.setLimit(MOMENT_SEARCH_PAGE_SIZE);
        mMomentSearchQuery.orderByDescending(MomentContract.PUBLISH_TIME);
        mMomentSearchQuery.include(MomentContract.IMAGES);
        mMomentSearchQuery.include(MomentContract.USER_BASIC_INFO);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        mMomentSearchQuery.whereLessThanOrEqualTo(MomentContract.PUBLISH_TIME, date);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int gender = sharedPreferences.getInt(Config.SEARCH_CONDITION_GENDER, 0);
        if (gender != 0) {
            mMomentSearchQuery.whereEqualTo(MomentContract.GEDER, gender);
        }

        int ageRange = sharedPreferences.getInt(Config.SEARCH_CONDITION_AGE_RANGE, 1000);
        int cuurentYear = calendar.get(Calendar.YEAR);
        int userAge = cuurentYear - 1900 - AVUser.getCurrentUser().getDate(UserContract.AGE).getYear();
        mMomentSearchQuery.whereGreaterThanOrEqualTo(MomentContract.AGE, userAge - ageRange);
        mMomentSearchQuery.whereLessThanOrEqualTo(MomentContract.AGE, userAge + ageRange);

        int arriveTimeRange = sharedPreferences.getInt(Config.SEARCH_CONDITION_TIME_RANGE, 120);
        calendar.setTime(mCurrentTargetDate);
        calendar.add(Calendar.MINUTE, arriveTimeRange);
        Date maxDate = calendar.getTime();

        calendar.setTime(mCurrentTargetDate);
        calendar.add(Calendar.MINUTE, -arriveTimeRange);
        Date minDate = calendar.getTime();

        mMomentSearchQuery.whereGreaterThanOrEqualTo(MomentContract.ARRIVE_TIME, minDate);
        mMomentSearchQuery.whereLessThanOrEqualTo(MomentContract.ARRIVE_TIME, maxDate);
        // TODO: 2016/4/10 maybe use LeanCloud cache strategy, Think this later....
    }

    private void performNewQuery() {
        mMomentSearchQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mMomentList = list;
                    mMomentsAdapter = new MomentsRecyclerViewAdapter(getContext(), mMomentList);
                    mMomentsAdapter.setOnMoreDataWantedListener(new MomentsRecyclerViewAdapter.OnMoreDataWantedListener() {
                        @Override
                        public void onMoreDataWanted() {
                            increaseSearchRange();
                        }
                    });

                    //set this to calculate the data for each item
                    mMomentsAdapter.setCurrentGeoPoint(mCurrentSearchCenterGeoPoint);
                    mMomentsAdapter.setCurrentTargetDate(mCurrentTargetDate);

                    mRecyclerView.setAdapter(mMomentsAdapter);


                    if (list.size() < MOMENT_SEARCH_PAGE_SIZE) {
                        mMomentsAdapter.showAllDataLoadedFooter();
                    }
                } else {
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(getContext(), R.string.search_failed_please_check_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void performSearch(AVGeoPoint avGeoPoint, Date targetDate) {
        mCurrentSearchDistanceRange = Config.QUERY_STEP_RANGE;
        mCurrentSearchCenterGeoPoint = avGeoPoint;
        mCurrentTargetDate = targetDate;
        mSwipeRefreshLayout.setRefreshing(true);

        initQueryBasic();
        mMomentSearchQuery.whereWithinKilometers(MomentContract.LOCATION,
                mCurrentSearchCenterGeoPoint, mCurrentSearchDistanceRange);

        performNewQuery();
    }

    private void initEvents() {
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore() {
                    paginationQueryOfMoments(false);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FutureMeetFragment fragment = (FutureMeetFragment) getParentFragment();
                if (fragment != null) {
                    fragment.onMomentRefresh();
                }
            }
        });
    }


    private void initViews(View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent,
                R.color.colorPrimary, R.color.colorAccentLight);
    }



    /**
     * a query of more moment for the pagination mechanism
     * @param isSearchRangeIncreased if is increasing the search range
     */
    private void paginationQueryOfMoments(boolean isSearchRangeIncreased) {

        if (mMomentSearchQuery == null) {
            return;
        }

        final int currItemsNum = mMomentsAdapter.getDataItemCount();

        if (isSearchRangeIncreased) {
            double minDis = mCurrentSearchDistanceRange + 0.001;
            double maxDis = mCurrentSearchDistanceRange + Config.QUERY_STEP_RANGE;
            mCurrentSearchDistanceRange = maxDis;
            mMomentSearchQuery.whereWithinKilometers(MomentContract.LOCATION,
                    mCurrentSearchCenterGeoPoint,
                    maxDis,
                    minDis);
        } else {
            mMomentSearchQuery.skip(currItemsNum);
        }

        mMomentSearchQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() < MOMENT_SEARCH_PAGE_SIZE) {
                        mMomentsAdapter.showAllDataLoadedFooter();
                    }
                    mMomentList.addAll(list);
                    mMomentsAdapter.setDatasList(mMomentList);
                    mMomentsAdapter.notifyItemRangeInserted(currItemsNum, list.size());
                } else {
                    Toast.makeText(getContext(), R.string.search_failed_please_check_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void increaseSearchRange() {
        paginationQueryOfMoments(true);
    }
}

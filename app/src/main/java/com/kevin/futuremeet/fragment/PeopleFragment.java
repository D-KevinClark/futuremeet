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
import com.kevin.futuremeet.adapter.PeopleRecyclerViewAdapter;
import com.kevin.futuremeet.beans.FuturePoiBean;
import com.kevin.futuremeet.beans.FuturePoiContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.custom.EndlessRecyclerViewScrollListener;
import com.kevin.futuremeet.utility.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PeopleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = PeopleFragment.class.getName();

    //    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    private List<AVObject> mPeopleList = new ArrayList<>();

    private PeopleRecyclerViewAdapter mPeoplesAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    // TODO: 2016/4/11 this number may need to change eventually
    private static final int PEOPLE_SEARCH_PAGE_SIZE = 10;

    private AVGeoPoint mCurrentSearchCenterGeoPoint = null;

    private double mCurrentSearchDistanceRange;


    //make it a private field , every time a new query is required a new instance will be created,
    //but when search more page with a same query , it should not be newed
    private AVQuery<AVObject> mFuturePoiSearchQuery = null;

    private Date mCurrentTargetDate = null;

//    private OnFragmentInteractionListener mListener;

    public PeopleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeopleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeopleFragment newInstance(String param1, String param2) {
        PeopleFragment fragment = new PeopleFragment();
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
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        initViews(view);
        initEvents();
        Log.i(TAG, "onCreateView: ");
        return view;
    }




    private void initQueryBasic() {
        mFuturePoiSearchQuery = new AVQuery<>(FuturePoiContract.CLASS_NAME);
        mFuturePoiSearchQuery.setLimit(PEOPLE_SEARCH_PAGE_SIZE);
        mFuturePoiSearchQuery.include(FuturePoiContract.USER_BASIC_INFO);
        
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        mFuturePoiSearchQuery.whereLessThanOrEqualTo(FuturePoiContract.PUBLISH_TIME, date);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int gender = sharedPreferences.getInt(Config.SEARCH_CONDITION_GENDER, 0);
        if (gender != 0) {
            mFuturePoiSearchQuery.whereEqualTo(FuturePoiContract.USER_GENDER, gender);
        }

        int ageRange = sharedPreferences.getInt(Config.SEARCH_CONDITION_AGE_RANGE, 1000);
        int cuurentYear = calendar.get(Calendar.YEAR);
        int userAge = cuurentYear - 1900 - AVUser.getCurrentUser().getDate(UserContract.AGE).getYear();
        mFuturePoiSearchQuery.whereGreaterThanOrEqualTo(FuturePoiContract.USER_AGE, userAge - ageRange);
        mFuturePoiSearchQuery.whereLessThanOrEqualTo(FuturePoiContract.USER_AGE, userAge + ageRange);

        int arriveTimeRange = sharedPreferences.getInt(Config.SEARCH_CONDITION_TIME_RANGE, 120);
        calendar.setTime(mCurrentTargetDate);
        calendar.add(Calendar.MINUTE, arriveTimeRange);
        Date maxDate = calendar.getTime();

        calendar.setTime(mCurrentTargetDate);
        calendar.add(Calendar.MINUTE, -arriveTimeRange);
        Date minDate = calendar.getTime();

        mFuturePoiSearchQuery.whereGreaterThanOrEqualTo(FuturePoiContract.ARRIVE_TIME, minDate);
        mFuturePoiSearchQuery.whereLessThanOrEqualTo(FuturePoiContract.ARRIVE_TIME, maxDate);
        // TODO: 2016/4/10 maybe use LeanCloud cache strategy, Think this later....
    }

    private void performNewQuery() {
        mFuturePoiSearchQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mPeopleList = list;
                    mPeoplesAdapter = new PeopleRecyclerViewAdapter(getContext(), mPeopleList);
                    mPeoplesAdapter.setOnMoreDataWantedListener(new PeopleRecyclerViewAdapter.OnMoreDataWantedListener() {
                        @Override
                        public void onMoreDataWanted() {
                            increaseSearchRange();
                        }
                    });

                    //set this to calculate the data for each item
                    mPeoplesAdapter.setCurrentGeoPoint(mCurrentSearchCenterGeoPoint);
                    mPeoplesAdapter.setCurrentTargetDate(mCurrentTargetDate);

                    mRecyclerView.setAdapter(mPeoplesAdapter);


                    if (list.size() < PEOPLE_SEARCH_PAGE_SIZE) {
                        mPeoplesAdapter.showAllDataLoadedFooter();
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
        mFuturePoiSearchQuery.whereWithinKilometers(FuturePoiContract.POI_LOCATION,
                mCurrentSearchCenterGeoPoint, mCurrentSearchDistanceRange);

        performNewQuery();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }
    private void initEvents() {
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore() {
                paginationQueryOfPeoples(false);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FutureMeetFragment fragment = (FutureMeetFragment) getParentFragment();
                if (fragment != null) {
                    fragment.onPeopleRefresh();
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
     * a query of more people for the pagination mechanism
     * @param isSearchRangeIncreased if is increasing the search range
     */
    private void paginationQueryOfPeoples(boolean isSearchRangeIncreased) {

        if (mFuturePoiSearchQuery == null) {
            return;
        }

        final int currItemsNum = mPeoplesAdapter.getDataItemCount();

        if (isSearchRangeIncreased) {
            double minDis = mCurrentSearchDistanceRange + 0.001;
            double maxDis = mCurrentSearchDistanceRange + Config.QUERY_STEP_RANGE;
            mCurrentSearchDistanceRange = maxDis;
            mFuturePoiSearchQuery.whereWithinKilometers(FuturePoiContract.POI_LOCATION,
                    mCurrentSearchCenterGeoPoint,
                    maxDis,
                    minDis);
        } else {
            mFuturePoiSearchQuery.skip(currItemsNum);
        }

        mFuturePoiSearchQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() < PEOPLE_SEARCH_PAGE_SIZE) {
                        mPeoplesAdapter.showAllDataLoadedFooter();
                    }
                    mPeopleList.addAll(list);
                    mPeoplesAdapter.setDatasList(mPeopleList);
                    mPeoplesAdapter.notifyItemRangeInserted(currItemsNum, list.size());
                } else {
                    Toast.makeText(getContext(), R.string.search_failed_please_check_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void increaseSearchRange() {
        paginationQueryOfPeoples(true);
    }
}

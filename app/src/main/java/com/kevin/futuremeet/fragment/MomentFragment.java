package com.kevin.futuremeet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.adapter.MomentsRecyclerViewAdapter;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.custom.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;


public class MomentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    private List<AVObject> mMomentList = new ArrayList<>();
    private MomentsRecyclerViewAdapter mMomentsAdater;
    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // TODO: 2016/4/11 this number may need to change eventually
    private static final int MOMENT_SEARCH_PAGE_SIZE = 10;


    //make it a private field , every time a new query is required a new instance will be created,
    //but when search more page with a same query , it should not be newed
    private AVQuery<AVObject> mMomentSearchQuery;


    public MomentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MomentFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        View view = inflater.inflate(R.layout.fragment_near_by_moment, container, false);
        initViews(view);
        initEvents();
        newQueryOfMoments();
        return view;
    }

    private void initEvents() {
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore() {
                paginationQueryOfMoments();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newQueryOfMoments();
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
     * a new search, used when this activity first is launched or a refresh is needed
     */
    private void newQueryOfMoments() {
        mMomentSearchQuery = new AVQuery<>(MomentContract.CLASS_NAME);
        // TODO: 2016/4/10 maybe use LeanCloud cache strategy, Think this later....
        mMomentSearchQuery.setLimit(MOMENT_SEARCH_PAGE_SIZE);
        mMomentSearchQuery.orderByDescending(MomentContract.PUBLISH_TIME);
        mMomentSearchQuery.include(MomentContract.IMAGES);
        mMomentSearchQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null) {
                        mMomentsAdater.showAllMomentsLoadedFooter();
                        return;//no need to add items just return
                    }
                    if (list.size() < MOMENT_SEARCH_PAGE_SIZE) {
                        mMomentsAdater.showAllMomentsLoadedFooter();
                    }
                    mMomentList = list;
                    mMomentsAdater = new MomentsRecyclerViewAdapter(getContext(), mMomentList);
                    mRecyclerView.setAdapter(mMomentsAdater);
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
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


    /**
     * a query of more moment for the pagination mechanism
     */
    private void paginationQueryOfMoments() {
        if (mMomentSearchQuery == null) {
            return;
        }
        final int currItemsNum = mMomentsAdater.getDataItemCount();
        mMomentSearchQuery.skip(currItemsNum);
        mMomentSearchQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null) {
                        mMomentsAdater.showAllMomentsLoadedFooter();
                        return;//no need to add items just return
                    }
                    if (list.size() < MOMENT_SEARCH_PAGE_SIZE) {
                        mMomentsAdater.showAllMomentsLoadedFooter();
                    }
                    mMomentList.addAll(list);
                    mMomentsAdater.setMomentsList(mMomentList);
                    mMomentsAdater.notifyItemRangeInserted(currItemsNum, list.size());
                } else {
                    Toast.makeText(getContext(), R.string.search_failed_please_check_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

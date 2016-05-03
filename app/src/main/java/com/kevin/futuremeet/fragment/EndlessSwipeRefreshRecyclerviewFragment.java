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
import com.kevin.futuremeet.custom.EndlessRecyclerViewScrollListener;

import java.util.List;

/**
 * Created by carver on 2016/5/2.
 */
public abstract class EndlessSwipeRefreshRecyclerviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int pageNumber;

    private List<AVObject> mDataList;
    private AVQuery<AVObject> query;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ViewHolderAdapter mViewholderAdapter;


    abstract RecyclerView.ViewHolder getNormalItemViewHolder(ViewGroup parent);

    abstract void onBindNormalItemViewHolder(RecyclerView.ViewHolder holder, List<AVObject> dataList, int position);

    abstract int getPageNumber();

    protected abstract AVQuery<AVObject> initQueryBasic();


    public EndlessSwipeRefreshRecyclerviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.endless_scroll_swiperefresh_recycleview_layout, container, false);

        initViews(root);
        initEvents();
        pageNumber = getPageNumber();
        query = initQueryBasic();
        performNewQuery();

        return root;
    }


    private void initEvents() {
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore() {
                paginationQuery();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initQueryBasic();
                performNewQuery();
            }
        });
    }


    public void initViews(View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent,
                R.color.colorPrimary, R.color.colorAccentLight);
    }

    private void performNewQuery() {
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mDataList = list;
                    mViewholderAdapter = new ViewHolderAdapter();

                    mRecyclerView.setAdapter(mViewholderAdapter);


                    if (list.size() < pageNumber) {
                        mViewholderAdapter.showAllDataLoadedFooter();
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


    private void paginationQuery() {
        final int currItemsNum = mDataList.size();
        query.skip(currItemsNum);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() < pageNumber) {
                        mViewholderAdapter.showAllDataLoadedFooter();
                    }
                    mDataList.addAll(list);
                    mViewholderAdapter.notifyItemRangeInserted(currItemsNum, list.size());
                } else {
                    Toast.makeText(getContext(), R.string.search_failed_please_check_network, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public class ViewHolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        protected LayoutInflater mLayoutInflater;

        protected FooterViewHolder mFooterViewHolder;


        protected boolean mIsAllDataLoaded = false;


        protected static final int FOOTER_ITEM_TYPE = 100;
        protected static final int NORMAL_ITEM_TYPE = 101;


        protected ViewHolderAdapter() {
            mLayoutInflater = LayoutInflater.from(getContext());
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == NORMAL_ITEM_TYPE) {
                return getNormalItemViewHolder(parent);
            } else {
                View view = mLayoutInflater.inflate(R.layout.moments_footer, parent, false);
                final FooterViewHolder footerViewHolder = new FooterViewHolder(view);
                return footerViewHolder;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return FOOTER_ITEM_TYPE;
            } else {
                return NORMAL_ITEM_TYPE;
            }
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == getItemCount() - 1) {//this is footer
                if (mIsAllDataLoaded) {
                    FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                    mFooterViewHolder = footerViewHolder;
                    footerViewHolder.isLoadingView.setVisibility(View.GONE);
                    footerViewHolder.allLoadedView.setVisibility(View.VISIBLE);
                }
                return;
            } else {
                onBindNormalItemViewHolder(holder,mDataList, position);
            }
        }


        @Override
        public int getItemCount() {
            return mDataList.size() + 1;
        }

        /**
         * set the flag, so when the footer show, it shows in the proper way ,see the method "onBindViewHolder"
         */
        public void showAllDataLoadedFooter() {
            mIsAllDataLoaded = true;
            if (mFooterViewHolder != null) {
                mFooterViewHolder.allLoadedView.setVisibility(View.VISIBLE);
                mFooterViewHolder.isLoadingView.setVisibility(View.GONE);
            }
        }


        private class FooterViewHolder extends RecyclerView.ViewHolder {
            public final View isLoadingView;
            public final View allLoadedView;

            public FooterViewHolder(View itemView) {
                super(itemView);
                isLoadingView = itemView.findViewById(R.id.is_loading);
                allLoadedView = itemView.findViewById(R.id.all_result_showed);
            }
        }
    }
}

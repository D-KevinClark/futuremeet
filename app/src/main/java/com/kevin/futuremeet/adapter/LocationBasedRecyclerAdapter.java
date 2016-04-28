package com.kevin.futuremeet.adapter;

import android.app.ListActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.kevin.futuremeet.R;

import java.util.Date;
import java.util.List;

/**
 * Created by carver on 2016/4/27.
 */
public abstract class LocationBasedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected List<AVObject> mAvobjectList;

    protected FooterViewHolder mFooterViewHolder;


    protected AVGeoPoint mCurrentGeoPoint;


    protected Date mCurrentTargetDate;


    protected boolean mIsAllDataLoaded = false;


    protected static final int FOOTER_ITEM_TYPE = 100;
    protected static final int NORMAL_ITEM_TYPE = 101;


    protected LocationBasedRecyclerAdapter(Context context, List<AVObject> objects) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mAvobjectList = objects;
    }

    protected void setCurrentGeoPoint(AVGeoPoint mCurrentGeoPoint) {
        this.mCurrentGeoPoint = mCurrentGeoPoint;
    }

    protected void setCurrentTargetDate(Date mCurrentTargetDate) {
        this.mCurrentTargetDate = mCurrentTargetDate;
    }

    abstract RecyclerView.ViewHolder getNormalItemViewHolder(ViewGroup parent);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL_ITEM_TYPE) {
            return getNormalItemViewHolder(parent);
        } else {
            View view = mLayoutInflater.inflate(R.layout.moments_footer, parent, false);
            final FooterViewHolder footerViewHolder = new FooterViewHolder(view);
            mFooterViewHolder = footerViewHolder;
            View allDataLoadedView = footerViewHolder.allLoadedView;
            allDataLoadedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMoreDataWantedListener != null) {
                        footerViewHolder.isLoadingView.setVisibility(View.VISIBLE);
                        footerViewHolder.allLoadedView.setVisibility(View.GONE);
                        mMoreDataWantedListener.onMoreDataWanted();
                    }
                }
            });
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

    /**
     * get the real data item (exclude the footer) ,just a convenient method
     *
     * @return
     */
    public int getDataItemCount() {
        return mAvobjectList.size();
    }

    abstract void onBindNormalItemViewHolder(RecyclerView.ViewHolder holder, int position);

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
            onBindNormalItemViewHolder(holder,position);
        }
    }


    /**
     * @param date
     * @return
     */
    protected String getProperTimeDiffFormat(Date date) {
        long nowTimeInMilliSecond = System.currentTimeMillis();
        long targetTimeInMilliSecond = date.getTime();
        String earlyOrLate = null;

        long minuteOffset = (targetTimeInMilliSecond - nowTimeInMilliSecond) / (60 * 1000);
        if (minuteOffset == 0) {
            return mContext.getString(R.string.arrive_at_same_time);
        } else if (minuteOffset < 0) {
            earlyOrLate = mContext.getString(R.string.early_arrive);
            minuteOffset = (-minuteOffset);
        } else {
            earlyOrLate = mContext.getString(R.string.late_arrive);
        }

        if (minuteOffset <= 59) {
            return earlyOrLate + minuteOffset + mContext.getString(R.string.minute);
        } else {
            int hour = (int) (minuteOffset / 60);
            int minute = (int) (minuteOffset % 60);
            return earlyOrLate + hour + mContext.getString(R.string.hour)
                    + minute + mContext.getString(R.string.minute);
        }
    }

    public void setDatasList(List<AVObject> datasList) {
        mAvobjectList = datasList;
    }

    @Override
    public int getItemCount() {
        return mAvobjectList.size() + 1;
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


    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public final View isLoadingView;
        public final View allLoadedView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            isLoadingView = itemView.findViewById(R.id.is_loading);
            allLoadedView = itemView.findViewById(R.id.all_result_showed);
        }
    }


    public OnMoreDataWantedListener mMoreDataWantedListener;

    public interface OnMoreDataWantedListener {
        void onMoreDataWanted();
    }

    public void setOnMoreDataWantedListener(OnMoreDataWantedListener listener) {
        mMoreDataWantedListener = listener;
    }

}

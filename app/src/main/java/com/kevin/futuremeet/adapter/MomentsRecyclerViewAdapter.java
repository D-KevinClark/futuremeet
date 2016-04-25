package com.kevin.futuremeet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.UserContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by carver on 2016/4/9.
 */
public class MomentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<AVObject> mMomentsList;

    private FooterViewHolder mFooterViewHolder;


    private AVGeoPoint mCurrentGeoPoint;


    private Date mCurrentTargetDate;


    private boolean mIsAllDataLoaded = false;


    private static final int FOOTER_ITEM_TYPE = 100;
    private static final int NORMAL_ITEM_TYPE = 101;


    public MomentsRecyclerViewAdapter(Context context, List<AVObject> moments) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMomentsList = moments;
    }

    public void setCurrentGeoPoint(AVGeoPoint mCurrentGeoPoint) {
        this.mCurrentGeoPoint = mCurrentGeoPoint;
    }

    public void setCurrentTargetDate(Date mCurrentTargetDate) {
        this.mCurrentTargetDate = mCurrentTargetDate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL_ITEM_TYPE) {
            View view = mLayoutInflater.inflate(R.layout.moment_item_layout, parent, false);
            MomentViewHolder viewHodler = new MomentViewHolder(view);
            return viewHodler;
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
        return mMomentsList.size();
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

            MomentViewHolder momentHolder = (MomentViewHolder) holder;
            AVObject moment = mMomentsList.get(position);

            String content = moment.getString(MomentContract.CONTENT);
            momentHolder.contentText.setText(content);

            String username = moment.getString(MomentContract.USER_NAME);
            momentHolder.usernameText.setText(username);

            int gender = moment.getInt(UserContract.GENDER);
            if (gender == 1) {
                momentHolder.genderImage.setImageResource(R.drawable.male_icon);
            } else {
                momentHolder.genderImage.setImageResource(R.drawable.female_icon);
            }

            int age = moment.getInt(UserContract.AGE);
            momentHolder.userAgeText.setText(age + "");

            AVGeoPoint geoPoint = moment.getAVGeoPoint(MomentContract.LOCATION);
            String distance=getProperDistanceFormat(geoPoint.distanceInKilometersTo(mCurrentGeoPoint));
            momentHolder.distanceText.setText(distance);

            Date date = moment.getDate(MomentContract.ARRIVE_TIME);
            momentHolder.arriveTimeDiffText.setText(getProperTimeDiffFormat(date));

            Date publishDate = moment.getDate(MomentContract.PUBLISH_TIME);
            momentHolder.publishTimeText.setText(getProperPublishTimeFormate(publishDate));

            if (momentHolder.imagesContainer.getChildCount() != 0) {
                momentHolder.imagesContainer.removeAllViews();
            }
            List<AVFile> images = mMomentsList.get(position).getList(MomentContract.IMAGES);
            int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.moment_images_size);
            int imageViewMarginRight = mContext.getResources().getDimensionPixelSize(R.dimen.moment_images_margin_right);

            int imagesMarginTop = mContext.getResources().getDimensionPixelOffset(R.dimen.moment_images_margin_right);
            if (images == null) return;//if there is no image within this post moment just return
            for (int i = 0; i < images.size(); i++) {
                AVFile image = images.get(i);
                ImageView imageView = new ImageView(mContext);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
                layoutParams.setMargins(0,imagesMarginTop, imageViewMarginRight, 0);
                imageView.setLayoutParams(layoutParams);
                momentHolder.imagesContainer.addView(imageView);
                //down load a thumbnail to save the network traffic
                String url = image.getThumbnailUrl(false, imageSize, imageSize, 100, "jpg");
                Glide.with(mContext)
                        .load(url)
                        .asBitmap()
                        .placeholder(R.color.greyShadow)
                        .into(imageView);
            }
        }
    }

    private String getProperDistanceFormat(double distanceInKilometer) {
        int distanceInMeter= (int) Math.floor(distanceInKilometer + 1000);
        if (distanceInMeter < 1000) {
            return distanceInMeter + mContext.getString(R.string.meter);
        } else {
            return String.valueOf(distanceInKilometer).substring(0, 4)+"km";
        }
    }

    private String getProperPublishTimeFormate(Date date) {
        long nowTimeInMilliSecond = System.currentTimeMillis();
        long targetTimeInMilliSecond = date.getTime();

        Time time = new Time();
        time.setToNow();

        int mCurrentJulianDay = Time.getJulianDay(nowTimeInMilliSecond, time.gmtoff);
        int mTargetJulianDay = Time.getJulianDay(targetTimeInMilliSecond, time.gmtoff);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        String properFormat = null;

        if (mTargetJulianDay == mCurrentJulianDay) {
            int minuteOffset = (int) Math.abs((targetTimeInMilliSecond - nowTimeInMilliSecond) /(60*1000));
            if (minuteOffset <= 59) {
                properFormat = minuteOffset + mContext.getString(R.string.minute_ago);
            } else {
                properFormat = mContext.getString(R.string.today) + simpleDateFormat.format(date);
            }
        } else if (mTargetJulianDay == mCurrentJulianDay - 1) {
            properFormat = mContext.getString(R.string.yesterday) + simpleDateFormat.format(date);
        } else if (mTargetJulianDay == mCurrentJulianDay - 2) {
            properFormat = mContext.getString(R.string.the_day_before_yesterday) + simpleDateFormat.format(date);
        }
        return properFormat;
    }

    private String getProperTimeDiffFormat(Date date) {
        long nowTimeInMilliSecond = System.currentTimeMillis();
        long targetTimeInMilliSecond = date.getTime();
        String earlyOrLate = null;

        long minuteOffset = (targetTimeInMilliSecond - nowTimeInMilliSecond) / (60*1000);
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
            return earlyOrLate + hour + mContext.getString(R.string.hour)
                    + minuteOffset + mContext.getString(R.string.minute);
        }
    }

    public void setMomentsList(List<AVObject> moments) {
        mMomentsList = moments;

    }

    @Override
    public int getItemCount() {
        return mMomentsList.size() + 1;
    }

    /**
     * set the flag, so when the footer show, it shows in the proper way ,see the method "onBindViewHolder"
     */
    public void showAllMomentsLoadedFooter() {
        mIsAllDataLoaded = true;
        if (mFooterViewHolder != null) {
            mFooterViewHolder.allLoadedView.setVisibility(View.VISIBLE);
            mFooterViewHolder.isLoadingView.setVisibility(View.GONE);
        }
    }

    public static class MomentViewHolder extends RecyclerView.ViewHolder {

        public final TextView contentText;
        public final LinearLayout imagesContainer;
        public final ImageView avatarImage;
        public final TextView usernameText;
        public final TextView arriveTimeDiffText;
        public final TextView userAgeText;
        public final TextView distanceText;
        public final TextView publishTimeText;
        public final ImageView commentImage;
        public final ImageView likeImage;
        public final TextView likeNumebrText;
        public final TextView commentNumberText;
        public final ImageView genderImage;


        public MomentViewHolder(View itemView) {
            super(itemView);
            contentText = (TextView) itemView.findViewById(R.id.moment_content_textview);
            imagesContainer = (LinearLayout) itemView.findViewById(R.id.moment_images_container);
            avatarImage = (ImageView) itemView.findViewById(R.id.avatar_imageview);
            usernameText = (TextView) itemView.findViewById(R.id.username_text);
            arriveTimeDiffText = (TextView) itemView.findViewById(R.id.arrive_time_diff_textview);
            userAgeText = (TextView) itemView.findViewById(R.id.userage_textview);
            distanceText = (TextView) itemView.findViewById(R.id.poi_distance_textview);
            publishTimeText = (TextView) itemView.findViewById(R.id.publish_time_textview);
            commentImage = (ImageView) itemView.findViewById(R.id.comment_imageview);
            likeImage = (ImageView) itemView.findViewById(R.id.like_imageview);
            likeNumebrText = (TextView) itemView.findViewById(R.id.like_number_text);
            commentNumberText = (TextView) itemView.findViewById(R.id.comment_number_text);
            genderImage = (ImageView) itemView.findViewById(R.id.gender_imageview);
        }
    }


    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public final View isLoadingView;
        public final View allLoadedView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            isLoadingView = itemView.findViewById(R.id.is_loading);
            allLoadedView = itemView.findViewById(R.id.all_result_showed);
        }
    }

    private OnMoreDataWantedListener mMoreDataWantedListener;

    public void setOnMoreDataWantedListener(OnMoreDataWantedListener listener) {
        mMoreDataWantedListener = listener;
    }

    public interface OnMoreDataWantedListener {
        void onMoreDataWanted();
    }
}

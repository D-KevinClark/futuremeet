package com.kevin.futuremeet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.FuturePoiContract;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.Util;

import java.util.Date;
import java.util.List;

/**
 * Created by carver on 2016/4/28.
 */
public class PeopleRecyclerViewAdapter extends LocationBasedRecyclerAdapter {

    public PeopleRecyclerViewAdapter(Context context, List<AVObject> peoples) {
        super(context,peoples);
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mAvobjectList = peoples;
    }

    public void setCurrentGeoPoint(AVGeoPoint mCurrentGeoPoint) {
        this.mCurrentGeoPoint = mCurrentGeoPoint;
    }

    public void setCurrentTargetDate(Date mCurrentTargetDate) {
        this.mCurrentTargetDate = mCurrentTargetDate;
    }

    @Override
    RecyclerView.ViewHolder getNormalItemViewHolder(ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.people_item_layout, parent, false);
        PeopleViewHolder viewHodler = new PeopleViewHolder(view);
        return viewHodler;
    }


    @Override
    void onBindNormalItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PeopleViewHolder peopleViewHolder = (PeopleViewHolder) holder;
        final AVObject people = mAvobjectList.get(position);
        final AVObject userBasicInfo = people.getAVObject(FuturePoiContract.USER_BASIC_INFO);


        final String username = userBasicInfo.getString(UserBasicInfoContract.USERNAME);
        peopleViewHolder.usernameText.setText(username);

        int gender = people.getInt(UserContract.GENDER);
        if (gender == 1) {
            peopleViewHolder.genderImage.setImageResource(R.drawable.male_icon);
        } else {
            peopleViewHolder.genderImage.setImageResource(R.drawable.female_icon);
        }

        int age = people.getInt(UserContract.AGE);
        peopleViewHolder.userAgeText.setText(age + "");

        AVGeoPoint geoPoint = people.getAVGeoPoint(FuturePoiContract.POI_LOCATION);
        String distance = Util.getProperDistanceFormat(mContext, geoPoint.distanceInKilometersTo(mCurrentGeoPoint));
        peopleViewHolder.distanceText.setText(distance);

        Date date = people.getDate(FuturePoiContract.ARRIVE_TIME);
        peopleViewHolder.arriveTimeDiffText.setText(getProperTimeDiffFormat(date));

        int size=mContext.getResources().getDimensionPixelSize(R.dimen.people_avatar_size);
        AVFile avatar = userBasicInfo.getAVFile(UserBasicInfoContract.AVATAR);
        String url = avatar.getThumbnailUrl(false, size, size, 100, "jpg");
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .into(peopleViewHolder.avatarImage);
    }


    public static class PeopleViewHolder extends RecyclerView.ViewHolder {

        public final ImageView avatarImage;
        public final TextView usernameText;
        public final TextView arriveTimeDiffText;
        public final TextView userAgeText;
        public final TextView distanceText;
        public final ImageView genderImage;


        public PeopleViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.avatar_imageview);
            usernameText = (TextView) itemView.findViewById(R.id.username_text);
            arriveTimeDiffText = (TextView) itemView.findViewById(R.id.arrive_time_diff_textview);
            userAgeText = (TextView) itemView.findViewById(R.id.userage_textview);
            distanceText = (TextView) itemView.findViewById(R.id.poi_distance_textview);
            genderImage = (ImageView) itemView.findViewById(R.id.gender_imageview);
        }
    }
}

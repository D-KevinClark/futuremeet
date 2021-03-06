package com.kevin.futuremeet.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.MomentDetailActivity;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.MomentLikeContrast;
import com.kevin.futuremeet.beans.RelationShipContract;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyLikedMomentFragment extends EndlessSwipeRefreshRecyclerviewFragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MyLikedMomentFragment() {
        // Required empty public constructor
    }

    public static MyLikedMomentFragment newInstance(String param1, String param2) {
        MyLikedMomentFragment fragment = new MyLikedMomentFragment();
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
    RecyclerView.ViewHolder getNormalItemViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.my_liked_moment_item_layout, parent, false);
        return new MyLikedMomentFragment.LikeStatusViewHolder(view);
    }


    @Override
    String getAllDateLoadText() {
        return getString(R.string.all_has_been_showed);
    }

    @Override
    void onBindNormalItemViewHolder(RecyclerView.ViewHolder holder,List<AVObject> dataList, int position) {

        AVObject likeObj = dataList.get(position);
        final AVObject momentObj = likeObj.getAVObject(MomentLikeContrast.MOMENT);
        AVObject fromUserInfo = likeObj.getAVObject(MomentLikeContrast.FROM_USER_BASIC_INFO);

        LikeStatusViewHolder likeStatusViewHolder = (LikeStatusViewHolder) holder;



        int avatarsize = getResources().getDimensionPixelSize(R.dimen.like_item_avatar_size);
        String avatarUrl = fromUserInfo.getAVFile(UserBasicInfoContract.AVATAR).getThumbnailUrl(false, avatarsize, avatarsize, 100, "jpg");
        Glide.with(getContext())
                .load(avatarUrl)
                .asBitmap()
                .into(likeStatusViewHolder.avatarImage);

        int momentImageSize = getResources().getDimensionPixelSize(R.dimen.like_item_image_size);
        List<AVFile> images = momentObj.getList(MomentContract.IMAGES);
        AVFile image = images.get(0);
        String momentImageUrl = image.getThumbnailUrl(false, momentImageSize, momentImageSize, 100, "jpg");
        Glide.with(getContext())
                .load(momentImageUrl)
                .asBitmap()
                .into(likeStatusViewHolder.momentImage);


        String name = fromUserInfo.getString(UserBasicInfoContract.USERNAME);
        likeStatusViewHolder.usernameText.setText(name);

        Date likeTime = likeObj.getCreatedAt();
        String properFormatTime = Util.getProperPublishTimeFormate(getContext(), likeTime);
        likeStatusViewHolder.timeText.setText(properFormatTime);

        likeStatusViewHolder.wholeLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String momentObjectId = momentObj.getObjectId();
                Intent intent = new Intent(getContext(), MomentDetailActivity.class);
                intent.putExtra(MomentDetailActivity.EXTRA_MOMENT_ID, momentObjectId);
                getContext().startActivity(intent);
            }
        });

    }

    @Override
    int getPageNumber() {
        return 10;
    }

    @Override
    protected AVQuery<AVObject> initQueryBasic() {
        AVQuery<AVObject> query = new AVQuery<>(MomentLikeContrast.CLASS_NAME);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.include(MomentLikeContrast.MOMENT);
        query.include(MomentLikeContrast.FROM_USER_BASIC_INFO);
        query.include(MomentLikeContrast.MOMENT + "." + MomentContract.IMAGES);
        query.orderByDescending(MomentLikeContrast.HAPPEN_TIME);


        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        query.whereLessThanOrEqualTo(RelationShipContract.ESTABLISH_TIME, date);

        AVObject cuurUserBasicInfoAVobj = AVUser.getCurrentUser().getAVObject(UserContract.USER_BASIC_INFO);
        query.whereEqualTo(MomentLikeContrast.TO_USER_BASIC_INFO, cuurUserBasicInfoAVobj);

        return query;
    }

    public static class LikeStatusViewHolder extends RecyclerView.ViewHolder {

        public final ImageView avatarImage;
        public final TextView usernameText;
        public final ImageView momentImage;
        public final TextView timeText;
        public View wholeLayoutItem;


        public LikeStatusViewHolder(View itemView) {
            super(itemView);
            wholeLayoutItem = itemView;
            avatarImage = (ImageView) itemView.findViewById(R.id.avatar);
            usernameText = (TextView) itemView.findViewById(R.id.username_text);
            timeText = (TextView) itemView.findViewById(R.id.time_textview);
            momentImage = (ImageView) itemView.findViewById(R.id.moment_image);
        }
    }

}

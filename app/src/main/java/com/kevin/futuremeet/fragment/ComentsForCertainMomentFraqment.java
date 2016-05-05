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
import com.kevin.futuremeet.beans.MomentCommentContract;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComentsForCertainMomentFraqment extends EndlessSwipeRefreshRecyclerviewFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MOMENT_ID = "moment_id";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mMomentID;
    private String mParam2;


    public ComentsForCertainMomentFraqment() {
        // Required empty public constructor
    }


    public static ComentsForCertainMomentFraqment newInstance(String momenID, String param2) {
        ComentsForCertainMomentFraqment fragment = new ComentsForCertainMomentFraqment();
        Bundle args = new Bundle();
        args.putString(ARG_MOMENT_ID, momenID);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMomentID = getArguments().getString(ARG_MOMENT_ID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    RecyclerView.ViewHolder getNormalItemViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.comment_for_a_certain_moment_item_layout, parent, false);
        return new CommentingViewHolder(view);
    }

    @Override
    void onBindNormalItemViewHolder(RecyclerView.ViewHolder holder, List<AVObject> dataList, int position) {
        AVObject comment = dataList.get(position);
        final AVObject moment = comment.getAVObject(MomentCommentContract.MOMENT);
        AVObject fromUserBasicInfo = comment.getAVObject(MomentCommentContract.FROM_USER_BASIC_INFO);
        AVObject toUserBasicInfo = comment.getAVObject(MomentCommentContract.TO_USER_BASIC_INFO);
        int type = comment.getInt(MomentCommentContract.TYPE);

        CommentingViewHolder viewHolder = (CommentingViewHolder) holder;

        int size = getResources().getDimensionPixelSize(R.dimen.like_item_image_size);
        AVFile toUserAvatarImage = fromUserBasicInfo.getAVFile(UserBasicInfoContract.AVATAR);
        String url = toUserAvatarImage.getThumbnailUrl(false, size, size, 100, "jpg");
        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .into(viewHolder.avatarImage);


        viewHolder.fromUsernameText.setText(fromUserBasicInfo.getString(UserBasicInfoContract.USERNAME));
        viewHolder.toUsernameText.setText(toUserBasicInfo.getString(UserBasicInfoContract.USERNAME));

        if (type == MomentCommentContract.TYPE_COMMENT) {
            viewHolder.commentTypeText.setText(R.string.comment);
        } else {
            viewHolder.commentTypeText.setText(R.string.reply);
        }

        viewHolder.contentText.setText(comment.getString(MomentCommentContract.CONTENT));

        String time = Util.getProperPublishTimeFormate(getContext(), Calendar.getInstance().getTime());
        viewHolder.timeText.setText(time);
    }

    @Override
    int getPageNumber() {
        return 10;
    }

    @Override
    protected AVQuery<AVObject> initQueryBasic() {
        AVQuery<AVObject> query = new AVQuery<>(MomentCommentContract.CALSS_NAME);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        query.whereLessThanOrEqualTo(MomentCommentContract.HAPPEN_TIME, date);

        query.include(MomentCommentContract.FROM_USER_BASIC_INFO);
        query.include(MomentCommentContract.TO_USER_BASIC_INFO);

        query.orderByDescending(MomentCommentContract.HAPPEN_TIME);

        AVObject moment = AVObject.createWithoutData(MomentContract.CLASS_NAME, mMomentID);
        query.whereEqualTo(MomentCommentContract.MOMENT, moment);
        return query;
    }

    private static class CommentingViewHolder extends RecyclerView.ViewHolder {

        public final ImageView avatarImage;
        public final TextView fromUsernameText;
        public final TextView toUsernameText;
        public final TextView contentText;
        public final TextView timeText;
        public final TextView commentTypeText;

        public CommentingViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.avatar);
            fromUsernameText = (TextView) itemView.findViewById(R.id.from_username_text);
            toUsernameText = (TextView) itemView.findViewById(R.id.to_user_name_text);
            contentText = (TextView) itemView.findViewById(R.id.comment_content_text);
            timeText = (TextView) itemView.findViewById(R.id.time_textview);
            commentTypeText = (TextView) itemView.findViewById(R.id.comment_type_text);
        }
    }

    @Override
    String getAllDateLoadText() {
        return getString(R.string.no_more_comment_concern_your_two);
    }

}

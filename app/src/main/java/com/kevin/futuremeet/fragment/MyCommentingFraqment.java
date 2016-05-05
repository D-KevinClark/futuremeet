package com.kevin.futuremeet.fragment;


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
import com.kevin.futuremeet.beans.MomentCommentContract;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyCommentingFraqment extends EndlessSwipeRefreshRecyclerviewFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MyCommentingFraqment() {
        // Required empty public constructor
    }


    public static MyCommentingFraqment newInstance(String param1, String param2) {
        MyCommentingFraqment fragment = new MyCommentingFraqment();
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
        View view = inflater.inflate(R.layout.my_commenting_moment_item_layout, parent, false);
        return new CommentingViewHolder(view);
    }

    @Override
    void onBindNormalItemViewHolder(RecyclerView.ViewHolder holder, List<AVObject> dataList, int position) {
        AVObject comment = dataList.get(position);
        AVObject moment = comment.getAVObject(MomentCommentContract.MOMENT);
        AVObject toUserBasicInfo = comment.getAVObject(MomentCommentContract.TO_USER_BASIC_INFO);
        AVUser currUser = AVUser.getCurrentUser();

        CommentingViewHolder viewHolder = (CommentingViewHolder) holder;

        int size = getResources().getDimensionPixelSize(R.dimen.like_item_image_size);
        AVFile toUserAvatarImage = currUser.getAVFile(UserBasicInfoContract.AVATAR);
        String url = toUserAvatarImage.getThumbnailUrl(false, size, size, 100, "jpg");
        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .into(viewHolder.avatarImage);

        List<AVFile> momentImage = moment.getList(MomentContract.IMAGES);
        AVFile image = momentImage.get(0);
        String imageUri = image.getThumbnailUrl(false, size, size, 100, "jpg");
        Glide.with(getContext())
                .load(imageUri)
                .asBitmap()
                .into(viewHolder.momentImage);

        viewHolder.usernameText.setText(toUserBasicInfo.getString(UserBasicInfoContract.USERNAME));

        viewHolder.contentText.setText(comment.getString(MomentCommentContract.CONTENT));

        String time = Util.getProperPublishTimeFormate(getContext(), Calendar.getInstance().getTime());
        viewHolder.timeText.setText(time);

        int type = comment.getInt(MomentCommentContract.TYPE);
        String typeStr;
        if (type == MomentCommentContract.TYPE_COMMENT) {
            typeStr = getString(R.string.comment);
        } else {
            typeStr = getString(R.string.reply);
        }
        viewHolder.commentTypeText.setText(typeStr);

    }

    @Override
    int getPageNumber() {
        return 10;
    }

    @Override
    protected AVQuery<AVObject> initQueryBasic() {
        AVQuery<AVObject> query = new AVQuery<>(MomentCommentContract.CALSS_NAME);

        AVObject currUserBasicInfo = AVUser.getCurrentUser().getAVObject(UserContract.USER_BASIC_INFO);
        query.whereEqualTo(MomentCommentContract.FROM_USER_BASIC_INFO, currUserBasicInfo);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        query.whereLessThanOrEqualTo(MomentCommentContract.HAPPEN_TIME, date);

        query.include(MomentCommentContract.TO_USER_BASIC_INFO);
        query.include(MomentCommentContract.MOMENT + "." + MomentContract.IMAGES);

        query.orderByDescending(MomentCommentContract.HAPPEN_TIME);

        return query;
    }

    private static class CommentingViewHolder extends RecyclerView.ViewHolder {

        public final ImageView avatarImage;
        public final TextView usernameText;
        public final TextView contentText;
        public final TextView timeText;
        public final ImageView momentImage;
        public final TextView commentTypeText;

        public CommentingViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.avatar);
            usernameText = (TextView) itemView.findViewById(R.id.username_text);
            contentText = (TextView) itemView.findViewById(R.id.comment_content_text);
            timeText = (TextView) itemView.findViewById(R.id.time_textview);
            momentImage = (ImageView) itemView.findViewById(R.id.moment_image);
            commentTypeText = (TextView) itemView.findViewById(R.id.comment_type_text);
        }
    }

}

package com.kevin.futuremeet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.UserDetailInfoActivity;
import com.kevin.futuremeet.beans.MomentCommentContract;
import com.kevin.futuremeet.beans.MomentContract;
import com.kevin.futuremeet.beans.MomentLikeContrast;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.Util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by carver on 2016/4/9.
 */
public class MomentsRecyclerViewAdapter extends LocationBasedRecyclerAdapter {

    private static final String TAG = MomentsRecyclerViewAdapter.class.getSimpleName();


    public MomentsRecyclerViewAdapter(Context context, List<AVObject> moments) {
        super(context, moments);
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mAvobjectList = moments;
    }

    public void setCurrentGeoPoint(AVGeoPoint mCurrentGeoPoint) {
        this.mCurrentGeoPoint = mCurrentGeoPoint;
    }

    public void setCurrentTargetDate(Date mCurrentTargetDate) {
        this.mCurrentTargetDate = mCurrentTargetDate;
    }

    @Override
    RecyclerView.ViewHolder getNormalItemViewHolder(ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.moment_item_layout, parent, false);
        MomentViewHolder viewHodler = new MomentViewHolder(view);
        return viewHodler;
    }


    @Override
    void onBindNormalItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MomentViewHolder momentHolder = (MomentViewHolder) holder;
        final AVObject moment = mAvobjectList.get(position);
        final AVObject userBasicInfo = moment.getAVObject(MomentContract.USER_BASIC_INFO);
        final AVObject currUserBasicInfo = AVUser.getCurrentUser().getAVObject(UserContract.USER_BASIC_INFO);

        String content = moment.getString(MomentContract.CONTENT);
        momentHolder.contentText.setText(content);

        final String username = userBasicInfo.getString(UserBasicInfoContract.USERNAME);
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
        String distance = Util.getProperDistanceFormat(mContext, geoPoint.distanceInKilometersTo(mCurrentGeoPoint));
        momentHolder.distanceText.setText(distance);

        Date date = moment.getDate(MomentContract.ARRIVE_TIME);
        momentHolder.arriveTimeDiffText.setText(getProperTimeDiffFormat(date));

        Date publishDate = moment.getDate(MomentContract.PUBLISH_TIME);
        momentHolder.publishTimeText.setText(Util.getProperPublishTimeFormate(mContext, publishDate));

        momentHolder.likeNumebrText.setText(moment.get(MomentContract.LIKE_COUNTER) + "");


        int size = mContext.getResources().getDimensionPixelSize(R.dimen.moment_avatar_size_moment);
        AVFile avatar = userBasicInfo.getAVFile(UserBasicInfoContract.AVATAR);
        String url = avatar.getThumbnailUrl(false, size, size, 100, "jpg");
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .into(momentHolder.avatarImage);
        momentHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/5/1 add check to see if is "me"
                String userBasicInfoId = userBasicInfo.getObjectId();
                Intent intent = new Intent(mContext, UserDetailInfoActivity.class);
                intent.putExtra(UserDetailInfoActivity.EXTRA_USER_BASIC_INFO_ID, userBasicInfoId);
                mContext.startActivity(intent);
            }
        });


        momentHolder.likeNumebrText.setText("" + moment.getInt(MomentContract.LIKE_COUNTER));

        momentHolder.likeLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AVObject like = new AVObject(MomentLikeContrast.CLASS_NAME);
                like.put(MomentLikeContrast.FROM_USER_BASIC_INFO,
                        currUserBasicInfo);
                like.put(MomentLikeContrast.TO_USER_BASIC_INFO, userBasicInfo);
                like.put(MomentLikeContrast.MOMENT, moment);

                like.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            moment.setFetchWhenSave(true);
                            moment.increment(MomentContract.LIKE_COUNTER);
                            moment.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        momentHolder.likeNumebrText.setText("" + moment.get(MomentContract.LIKE_COUNTER));
                                    } else {
                                        // TODO: 2016/5/3 change the fail logic
                                        Log.i(TAG, "done: " + e.getMessage());
                                    }
                                }
                            });
                        } else {
                            // TODO: 2016/5/3 add fail logic
                            Log.i(TAG, "done: " + e.getMessage());
                        }
                    }
                });
            }
        });

        momentHolder.commentNumberText.setText("" + moment.getInt(MomentContract.COMMENT_COUNTER));

        momentHolder.commentLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (momentHolder.commentAreaView.getVisibility() == View.GONE) {
                    momentHolder.commentAreaView.setVisibility(View.VISIBLE);
                    if (momentHolder.commentEditText.requestFocus()) {
                        Util.openTheSoftKeyBoard(mContext, momentHolder.commentEditText);
                    }
                } else {
                    momentHolder.commentAreaView.setVisibility(View.GONE);
                }
            }
        });
        if (momentHolder.commentAreaView.getVisibility() == View.VISIBLE) {
            momentHolder.commentAreaView.setVisibility(View.GONE);
        }

        //clear the comment previously input
        momentHolder.commentEditText.setText("");

        momentHolder.commentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String commentStr = momentHolder.commentEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(commentStr)) {
                    final AVObject comment = new AVObject(MomentCommentContract.CALSS_NAME);
                    comment.put(MomentCommentContract.FROM_USER_BASIC_INFO, currUserBasicInfo);
                    comment.put(MomentCommentContract.TO_USER_BASIC_INFO, userBasicInfo);
                    comment.put(MomentCommentContract.CONTENT, commentStr);
                    comment.put(MomentCommentContract.MOMENT, moment);

                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                moment.setFetchWhenSave(true);
                                moment.increment(MomentContract.COMMENT_COUNTER);
                                moment.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            int counter=moment.getInt(MomentContract.COMMENT_COUNTER);
                                            momentHolder.commentNumberText.setText("" + counter);
                                            momentHolder.commentEditText.setText("");
                                            momentHolder.commentAreaView.setVisibility(View.GONE);
                                        } else {
                                            // TODO: 2016/5/3 add the failed logic
                                            Toast.makeText(mContext, mContext.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, "done: " + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                // TODO: 2016/5/3 add fail logic
                                Log.i(TAG, "done: " + e.getMessage());
                                Toast.makeText(mContext, mContext.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        if (momentHolder.imagesContainer.getChildCount() != 0) {
            momentHolder.imagesContainer.removeAllViews();
        }

        List<AVFile> images = mAvobjectList.get(position).getList(MomentContract.IMAGES);
        int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.moment_images_size);
        int imageViewMarginRight = mContext.getResources().getDimensionPixelSize(R.dimen.moment_images_margin_right);

        int imagesMarginTop = mContext.getResources().getDimensionPixelOffset(R.dimen.moment_images_margin_right);
        if (images == null) return;//if there is no image within this post moment just return
        for (int i = 0; i < images.size(); i++) {
            AVFile image = images.get(i);
            ImageView imageView = new ImageView(mContext);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
            layoutParams.setMargins(0, imagesMarginTop, imageViewMarginRight, 0);
            imageView.setLayoutParams(layoutParams);
            momentHolder.imagesContainer.addView(imageView);
            //down load a thumbnail to save the network traffic
            url = image.getThumbnailUrl(false, imageSize, imageSize, 100, "jpg");
            Glide.with(mContext)
                    .load(url)
                    .asBitmap()
                    .placeholder(R.color.greyShadow)
                    .into(imageView);
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
        public final View likeLayoutView;
        public final View commentLayoutView;
        public final EditText commentEditText;
        public final Button commentSendButton;
        public final View commentAreaView;


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
            likeLayoutView = itemView.findViewById(R.id.like_layout);
            commentLayoutView = itemView.findViewById(R.id.comment_layout);
            commentAreaView = itemView.findViewById(R.id.comment_area_view);
            commentEditText = (EditText) itemView.findViewById(R.id.comment_edittext);
            commentSendButton = (Button) itemView.findViewById(R.id.send_comment_button);
        }
    }

}

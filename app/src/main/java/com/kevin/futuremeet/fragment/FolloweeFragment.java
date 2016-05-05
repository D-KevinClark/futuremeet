package com.kevin.futuremeet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.UserDetailInfoActivity;
import com.kevin.futuremeet.beans.RelationShipContract;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.beans.UserDetailContract;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by carver on 2016/5/2.
 */
public class FolloweeFragment extends EndlessSwipeRefreshRecyclerviewFragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FolloweeFragment() {
        // Required empty public constructor
    }

    public static FolloweeFragment newInstance(String param1, String param2) {
        FolloweeFragment fragment = new FolloweeFragment();
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
        View view = inflater.inflate(R.layout.friends_item_layout, parent, false);
        FriendViewHolder friendViewHolder = new FriendViewHolder(view);
        return friendViewHolder;
    }

    @Override
    void onBindNormalItemViewHolder(RecyclerView.ViewHolder holder,List<AVObject> dataList, int position) {
        AVObject fromUserObj = dataList.get(position);
        final AVObject friendObj = fromUserObj.getAVObject(RelationShipContract.TO);
        FriendViewHolder friendViewHolder = (FriendViewHolder) holder;

        int size = getResources().getDimensionPixelSize(R.dimen.friends_avatar_size);
        String url = friendObj.getAVFile(UserBasicInfoContract.AVATAR).getThumbnailUrl(false, size, size, 100, "jpg");
        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .into(friendViewHolder.avatarImage);

        friendViewHolder.wholeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userBasicInfoId = friendObj.getObjectId();
                Intent intent = new Intent(getContext(), UserDetailInfoActivity.class);
                intent.putExtra(UserDetailInfoActivity.EXTRA_USER_BASIC_INFO_ID, userBasicInfoId);
                getContext().startActivity(intent);
            }
        });

        String name = friendObj.getString(UserBasicInfoContract.USERNAME);
        friendViewHolder.usernameText.setText(name);

        Date birthDate = friendObj.getDate(UserBasicInfoContract.AGE);
        Calendar calendar = Calendar.getInstance();
        int  currYear = calendar.get(Calendar.YEAR);
        int age = currYear - 1900 - birthDate.getYear();
        friendViewHolder.userAgeText.setText(age + "");

        int gender = friendObj.getInt(UserBasicInfoContract.GENDER);
        if (gender == 1) {
            friendViewHolder.genderImage.setImageResource(R.drawable.male_icon);
        } else {
            friendViewHolder.genderImage.setImageResource(R.drawable.female_icon);
        }
    }

    @Override
    int getPageNumber() {
        return 10;
    }

    @Override
    protected AVQuery<AVObject> initQueryBasic() {
        AVQuery<AVObject> query = new AVQuery<>(RelationShipContract.CLASS_NAME);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.include(RelationShipContract.TO);


        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        query.whereLessThanOrEqualTo(RelationShipContract.ESTABLISH_TIME, date);

        String currUserBasicInfoID = AVUser.getCurrentUser().getAVObject(UserContract.USER_BASIC_INFO).getObjectId();
        AVObject cuurUserBasicInfoAVobj = AVObject.createWithoutData(UserDetailContract.CLASS_NAME, currUserBasicInfoID);
        query.whereEqualTo(RelationShipContract.FROM, cuurUserBasicInfoAVobj);

        return query;
    }

    @Override
    String getAllDateLoadText() {
        return getString(R.string.all_has_been_showed);
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        public final ImageView avatarImage;
        public final TextView usernameText;
        public final ImageView genderImage;
        public final TextView userAgeText;
        public final View wholeLayout;


        public FriendViewHolder(View itemView) {
            super(itemView);
            wholeLayout = itemView;
            avatarImage = (ImageView) itemView.findViewById(R.id.avatar_imageview);
            usernameText = (TextView) itemView.findViewById(R.id.username_text);
            userAgeText = (TextView) itemView.findViewById(R.id.user_age_textview);
            genderImage = (ImageView) itemView.findViewById(R.id.gender_imageview);
        }
    }
}

package com.kevin.futuremeet.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.beans.UserDetailContract;
import com.kevin.futuremeet.utility.Config;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MeFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = MeFragment.class.getSimpleName();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private View mOccupationLayout;
    private View mSchoolOrFirmLayout;
    private View mHometownLayout;
    private View mIdioGraghLayout;
    private View mSelfLabelLayout;


    private View mMusicLayout;
    private View mFoodLayout;
    private View mSportLayout;
    private View mTVlayout;
    private View mLiteratureLayout;


    private TextView mOccupationText;
    private TextView mSchoolOrFirmText;
    private TextView mHometownText;
    private TextView mIdioGraghText;

    private FlowLayout mSelfLabelFlowLayout;
    private FlowLayout mMusicFlowLayout;
    private FlowLayout mFoodFlowLayout;
    private FlowLayout mSportFlowLayout;
    private FlowLayout mTVFlowLayout;
    private FlowLayout mLiteratureFlowLayout;

    private CoordinatorLayout mCoordinatorLayout;

    private JSONObject mCategoryJsonObject;
    private AVObject mUserDetialAVobject;


    public static final String EDIT_COMPLETED = "edit_completed";
    public static final String PREFERS_TO_CHANGE = "prefers_to_change";
    public static final String ID_OF_VIEWS_TO_CHANGE = "id_of_views_to_change";




    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mAppbarImage;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;




    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
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
        View root=inflater.inflate(R.layout.fragment_me, container, false);
        initViews(root);
        initEvents();
        new Thread(parseJsonRunnable).start();
        getCurrentUserDetailInfo();
        return root;
    }




    private void initViews(View root) {
        //app bar
        AppCompatActivity appCompatActivity= (AppCompatActivity) getActivity();
        mAppbarImage = (ImageView) root.findViewById(R.id.appbar_image);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar);
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        appCompatActivity.setSupportActionBar(mToolbar);
        String username = AVUser.getCurrentUser().getUsername();
        mCollapsingToolbarLayout.setTitle(username);

        //
        mFab = (FloatingActionButton) root.findViewById(R.id.fab);


        mOccupationLayout = root.findViewById(R.id.occupation_layout);
        mSchoolOrFirmLayout = root.findViewById(R.id.school_or_firm_layout);
        mHometownLayout = root.findViewById(R.id.hometown_layout);
        mIdioGraghLayout = root.findViewById(R.id.idiograph_layout);
        mSelfLabelLayout = root.findViewById(R.id.selflable_layout);

        mMusicLayout = root.findViewById(R.id.music_layout);
        mFoodLayout = root.findViewById(R.id.food_layout);
        mSportLayout = root.findViewById(R.id.sport_layout);
        mTVlayout = root.findViewById(R.id.tv_layout);
        mLiteratureLayout = root.findViewById(R.id.literature_layout);

        mOccupationText = (TextView) root.findViewById(R.id.occupation_text);
        mSchoolOrFirmText = (TextView) root.findViewById(R.id.school_or_firm_text);
        mHometownText = (TextView) root.findViewById(R.id.hometown_text);
        mIdioGraghText = (TextView) root.findViewById(R.id.idiograph_text);

        mSelfLabelFlowLayout = (FlowLayout) root.findViewById(R.id.selflable_container);
        mMusicFlowLayout = (FlowLayout) root.findViewById(R.id.music_container);
        mFoodFlowLayout = (FlowLayout) root.findViewById(R.id.food_container);
        mSportFlowLayout = (FlowLayout) root.findViewById(R.id.sport_container);
        mTVFlowLayout = (FlowLayout) root.findViewById(R.id.tv_container);
        mLiteratureFlowLayout = (FlowLayout) root.findViewById(R.id.literature_container);

        mCoordinatorLayout = (CoordinatorLayout) root.findViewById(R.id.coordinator_layout);
    }

    public void getCurrentUserDetailInfo() {
        AVUser user = AVUser.getCurrentUser();
        AVObject detailObj = (AVObject) user.get(UserContract.USER_DETAIL_INFO);
        String detailInfoId = detailObj.getObjectId();

        AVQuery<AVObject> query = new AVQuery<>(UserDetailContract.CLASS_NAME);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
//        query.setMaxCacheAge(1 * 60 * 1000);
        query.whereEqualTo(UserDetailContract.OBJECT_ID, detailInfoId);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mUserDetialAVobject = list.get(0);
                    showUserDetailInfo();
                } else {
                    Snackbar.make(mCoordinatorLayout, R.string.please_check_network, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void showUserDetailInfo() {

        ArrayList<String> selfLabelList = (ArrayList<String>)
                mUserDetialAVobject.getList(UserDetailContract.USER_FREFER_SELFLABEL);
        ArrayList<String> musicList = (ArrayList<String>)
                mUserDetialAVobject.getList(UserDetailContract.USER_FREFER_MUSIC);
        ArrayList<String> foodList = (ArrayList<String>)
                mUserDetialAVobject.getList(UserDetailContract.USER_FREFER_FOOD);
        ArrayList<String> sportList = (ArrayList<String>)
                mUserDetialAVobject.getList(UserDetailContract.USER_FREFERS_SPORT);
        ArrayList<String> tvList = (ArrayList<String>)
                mUserDetialAVobject.getList(UserDetailContract.USER_FREFERS_TV);
        ArrayList<String> literatureList = (ArrayList<String>)
                mUserDetialAVobject.getList(UserDetailContract.USER_FREFER_LITERATURE);

        String occupation = mUserDetialAVobject.getString(UserDetailContract.OCCUPATION);
        String shoolOrfirm = mUserDetialAVobject.getString(UserDetailContract.SCHOOL_OR_FIRM);
        String hometown = mUserDetialAVobject.getString(UserDetailContract.HOMETOWN);
        String idiograph = mUserDetialAVobject.getString(UserDetailContract.IDIOGRAPH);

        mOccupationText.setText(occupation);
        mSchoolOrFirmText.setText(shoolOrfirm);
        mHometownText.setText(hometown);
        mIdioGraghText.setText(idiograph);

        // TODO: 2016/4/30 change the ui later
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (selfLabelList != null)
            for (int i = 0; i < selfLabelList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(selfLabelList.get(i));
                mSelfLabelFlowLayout.addView(view);
            }

        if (musicList != null)
            for (int i = 0; i < musicList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(musicList.get(i));
                mMusicFlowLayout.addView(view);
            }

        if (foodList != null)
            for (int i = 0; i < foodList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(foodList.get(i));
                mFoodFlowLayout.addView(view);
            }

        if (sportList != null)
            for (int i = 0; i < sportList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(sportList.get(i));
                mSportFlowLayout.addView(view);
            }

        if (tvList != null)
            for (int i = 0; i < tvList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(tvList.get(i));
                mTVFlowLayout.addView(view);
            }

        if (literatureList != null)
            for (int i = 0; i < literatureList.size(); i++) {
                View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
                TextView preferText = (TextView) view.findViewById(R.id.prefers_textview);
                preferText.setText(literatureList.get(i));
                mLiteratureFlowLayout.addView(view);
            }

    }


    private Runnable parseJsonRunnable = new Runnable() {
        @Override
        public void run() {
            InputStream inputStream = getResources().openRawResource(R.raw.category_suggestions);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int ctr;
            try {
                ctr = inputStream.read();
                while (ctr != -1) {
                    byteArrayOutputStream.write(ctr);
                    ctr = inputStream.read();
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                // Parse the data into jsonobject to get original data in form of json.
                mCategoryJsonObject = new JSONObject(
                        byteArrayOutputStream.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    

    private void initEvents() {
        mOccupationLayout.setOnClickListener(this);
        mSchoolOrFirmLayout.setOnClickListener(this);
        mHometownLayout.setOnClickListener(this);
        mIdioGraghLayout.setOnClickListener(this);
        mSelfLabelLayout.setOnClickListener(this);

        mMusicLayout.setOnClickListener(this);
        mFoodLayout.setOnClickListener(this);
        mSportLayout.setOnClickListener(this);
        mTVlayout.setOnClickListener(this);
        mLiteratureLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mUserDetialAVobject == null || mCategoryJsonObject == null) {
            return;
        }
        JSONArray categoryJsa = null;
        ArrayList<String> userPreferList = null;
        UserPreferInfoDialog dialog = null;
        try {
            Log.i(TAG, "onClick: ");
            switch (v.getId()) {

                case R.id.occupation_layout:
                    categoryJsa = mCategoryJsonObject.getJSONArray(Config.CATEGORY_JSON_KEY_OCCUPATION);
                    UserOccupationSelectDialog occupationDialog =
                            UserOccupationSelectDialog.newInstance(categoryJsa, getString(R.string.occupation), R.id.occupation_layout);
                    occupationDialog.show(getChildFragmentManager(), null);
                    break;
                case R.id.school_or_firm_layout:
                    UserInfoEditTextDialog schoolEditDialog =
                            UserInfoEditTextDialog.newInstance(
                                    null,
                                    getString(R.string.school_or_firm),
                                    getString(R.string.input_school_or_company_info),
                                    R.id.school_or_firm_layout);
                    schoolEditDialog.show(getChildFragmentManager(), null);
                    break;

                case R.id.hometown_layout:
                    JSONArray countrysJsa = mCategoryJsonObject.getJSONArray(Config.CATEGORY_JSON_KEY_COUNTRY);
                    JSONObject provinceJsobj = mCategoryJsonObject.getJSONObject(Config.CATEGORY_JSON_KEY_PROVINCE);
                    UserCountrySelectDialog countrySelectDialog = UserCountrySelectDialog
                            .newInstance(countrysJsa, provinceJsobj, getString(R.string.area), R.id.hometown_layout);
                    countrySelectDialog.show(getChildFragmentManager(), null);
                    break;

                case R.id.idiograph_layout:
                    UserInfoEditTextDialog idiographEditDialog =
                            UserInfoEditTextDialog.newInstance(
                                    null,
                                    getString(R.string.idiograph),
                                    getString(R.string.input_idiograph)
                                    , R.id.idiograph_layout);
                    idiographEditDialog.show(getChildFragmentManager(), null);
                    break;

                case R.id.selflable_layout:
                    categoryJsa = mCategoryJsonObject.getJSONArray(Config.CATEGORY_JSON_KEY_SELELABEL);
                    userPreferList = (ArrayList<String>)
                            mUserDetialAVobject.getList(UserDetailContract.USER_FREFER_SELFLABEL);
                    dialog = UserPreferInfoDialog.newInstance(
                            userPreferList,
                            categoryJsa,
                            getString(R.string.selflabel), R.id.selflable_layout);
                    dialog.show(getChildFragmentManager(), null);
                    break;

                case R.id.music_layout:
                    categoryJsa = mCategoryJsonObject.getJSONArray(Config.CATEGORY_JSON_KEY_MUSIC);
                    userPreferList = (ArrayList<String>)
                            mUserDetialAVobject.getList(UserDetailContract.USER_FREFER_MUSIC);
                    dialog = UserPreferInfoDialog.newInstance(userPreferList, categoryJsa, getString(R.string.music),R.id.music_layout);
                    dialog.show(getChildFragmentManager(), null);
                    break;

                case R.id.food_layout:
                    categoryJsa = mCategoryJsonObject.getJSONArray(Config.CATEGORY_JSON_KEY_FOOD);
                    userPreferList = (ArrayList<String>) mUserDetialAVobject.getList(UserDetailContract.USER_FREFER_FOOD);
                    dialog = UserPreferInfoDialog.newInstance(userPreferList, categoryJsa, getString(R.string.food),R.id.food_layout);
                    dialog.show(getChildFragmentManager(), null);
                    break;

                case R.id.sport_layout:
                    categoryJsa = mCategoryJsonObject.getJSONArray(Config.CATEGORY_JSON_KEY_SPORT);
                    userPreferList = (ArrayList<String>)
                            mUserDetialAVobject.getList(UserDetailContract.USER_FREFERS_SPORT);
                    dialog = UserPreferInfoDialog.newInstance(userPreferList, categoryJsa, getString(R.string.sport),R.id.sport_layout);
                    dialog.show(getChildFragmentManager(), null);
                    break;

                case R.id.tv_layout:
                    categoryJsa = mCategoryJsonObject.getJSONArray(Config.CATEGORY_JSON_KEY_TV);
                    userPreferList = (ArrayList<String>)
                            mUserDetialAVobject.getList(UserDetailContract.USER_FREFERS_TV);
                    dialog = UserPreferInfoDialog.newInstance(userPreferList, categoryJsa, getString(R.string.tv),R.id.tv_layout);
                    dialog.show(getChildFragmentManager(), null);
                    break;

                case R.id.literature_layout:
                    categoryJsa = mCategoryJsonObject.getJSONArray(Config.CATEGORY_JSON_KEY_LITERATURE);
                    userPreferList = (ArrayList<String>) mUserDetialAVobject.getList(UserDetailContract.USER_FREFER_LITERATURE);
                    dialog = UserPreferInfoDialog.newInstance(userPreferList, categoryJsa, getString(R.string.literature),R.id.literature_layout);
                    dialog.show(getChildFragmentManager(), null);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        rigesterEditBroadcast();
    }

    @Override
    public void onStop() {
        super.onStop();
        unrigesterEditBroadcast();
    }

    private void rigesterEditBroadcast() {
        IntentFilter filter = new IntentFilter(EDIT_COMPLETED);
        EditCompleteBroadcastReceiver receiver = new EditCompleteBroadcastReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }

    private void unrigesterEditBroadcast() {
        EditCompleteBroadcastReceiver receiver = new EditCompleteBroadcastReceiver();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    private class EditCompleteBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(EDIT_COMPLETED)) {
                ArrayList<String> prefersToChangeList = intent.getStringArrayListExtra(PREFERS_TO_CHANGE);
                int id = intent.getIntExtra(ID_OF_VIEWS_TO_CHANGE, 0);

                Log.i(TAG, "onReceive: " + prefersToChangeList.get(0).toString());

                switch (id) {
                    case R.id.occupation_layout:
                        Log.i(TAG, "onReceive: occupation_layout");
                        String occupation = prefersToChangeList.get(0);
                        mUserDetialAVobject.put(UserDetailContract.OCCUPATION, occupation);
                        mOccupationText.setText(occupation);
                        break;

                    case R.id.school_or_firm_layout:
                        Log.i(TAG, "onReceive: school_or_firm_layout");
                        String schoolOrFirm = prefersToChangeList.get(0);
                        mUserDetialAVobject.put(UserDetailContract.SCHOOL_OR_FIRM, schoolOrFirm);
                        mSchoolOrFirmText.setText(schoolOrFirm);
                        break;

                    case R.id.hometown_layout:
                        Log.i(TAG, "onReceive: hometown_layout");
                        String hometown = prefersToChangeList.get(0);
                        mUserDetialAVobject.put(UserDetailContract.HOMETOWN, hometown);
                        mHometownText.setText(hometown);
                        break;

                    case R.id.idiograph_layout:
                        Log.i(TAG, "onReceive: idiograph_layout");
                        String idiograph = prefersToChangeList.get(0);
                        mUserDetialAVobject.put(UserDetailContract.IDIOGRAPH, idiograph);
                        mIdioGraghText.setText(idiograph);
                        break;

                    case R.id.selflable_layout:
                        Log.i(TAG, "onReceive: selflable_layout");
                        mUserDetialAVobject.put(UserDetailContract.USER_FREFER_SELFLABEL, prefersToChangeList);
                        updateUserPreferContainer(mSelfLabelFlowLayout, prefersToChangeList);
                        break;

                    case R.id.music_layout:
                        Log.i(TAG, "onReceive: music_layout");
                        mUserDetialAVobject.put(UserDetailContract.USER_FREFER_MUSIC, prefersToChangeList);
                        updateUserPreferContainer(mMusicFlowLayout, prefersToChangeList);
                        break;

                    case R.id.food_layout:
                        Log.i(TAG, "onReceive: food_layout");
                        mUserDetialAVobject.put(UserDetailContract.USER_FREFER_FOOD, prefersToChangeList);
                        updateUserPreferContainer(mFoodFlowLayout, prefersToChangeList);
                        break;

                    case R.id.sport_layout:
                        Log.i(TAG, "onReceive: sport_layout");
                        mUserDetialAVobject.put(UserDetailContract.USER_FREFERS_SPORT, prefersToChangeList);
                        updateUserPreferContainer(mSportFlowLayout, prefersToChangeList);
                        break;

                    case R.id.tv_layout:
                        Log.i(TAG, "onReceive: tv_layout");
                        mUserDetialAVobject.put(UserDetailContract.USER_FREFERS_TV, prefersToChangeList);
                        updateUserPreferContainer(mTVFlowLayout, prefersToChangeList);
                        break;

                    case R.id.literature_layout:
                        Log.i(TAG, "onReceive: literature_layout");
                        mUserDetialAVobject.put(UserDetailContract.USER_FREFER_LITERATURE, prefersToChangeList);
                        updateUserPreferContainer(mLiteratureFlowLayout, prefersToChangeList);
                        break;
                }
            }
            saveAndUpdateCache();
        }
    }

    /**
     * save the update to server and query once to do nothing just for update the local cache
     */
    private void saveAndUpdateCache() {
        mUserDetialAVobject.saveEventually(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVUser user = AVUser.getCurrentUser();
                AVObject detailObj = (AVObject) user.get(UserContract.USER_DETAIL_INFO);
                String detailInfoId = detailObj.getObjectId();

                AVQuery<AVObject> query = new AVQuery<>(UserDetailContract.CLASS_NAME);
                query.whereEqualTo(UserDetailContract.OBJECT_ID, detailInfoId);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        //do nothing , this is just for update the cache
                    }
                });
            }
        });
    }

    /**
     * update the views that contains user prefers (eg. self-label sport food etc.)
     *
     * @param mLiteratureFlowLayout
     * @param prefers
     */
    private void updateUserPreferContainer(FlowLayout mLiteratureFlowLayout, ArrayList<String> prefers) {
        mLiteratureFlowLayout.removeAllViews();
         LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < prefers.size(); i++) {
            View view = inflater.inflate(R.layout.user_prefers_textview_layout, null);
            TextView text = (TextView) view.findViewById(R.id.prefers_textview);
            text.setText(prefers.get(i));
            mLiteratureFlowLayout.addView(view);
        }
    }

}

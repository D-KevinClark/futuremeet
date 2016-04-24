package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.utility.Config;

import java.util.Date;

/**
 * Created by carver on 2016/4/23.
 */
public class SearchConditionSelectionDialog extends DialogFragment implements View.OnClickListener {

    private TextView mAllGenderText;
    private TextView mFemaleText;
    private TextView mMaleText;
    private TextView mTwoYearText;
    private TextView mFiveYearText;
    private TextView mTenYearText;
    private TextView mNoLimitYear;
    private TextView mHalfHourText;
    private TextView mOneHourText;
    private TextView mTwoHourText;
    private TextView mFiveHourText;

    private int mGender;
    private int mAgeRange;
    private int mArriveTimeRange;

    private void setTextsStateAccordingSharePref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mGender = sharedPreferences.getInt(Config.SEARCH_CONDITION_GENDER, 0);
        mAgeRange = sharedPreferences.getInt(Config.SEARCH_CONDITION_AGE_RANGE, 1000);
        mArriveTimeRange = sharedPreferences.getInt(Config.SEARCH_CONDITION_TIME_RANGE, 120);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.search_condition_selection_dialog, null);
        setTextsStateAccordingSharePref();
        initViews(root);
        initEvents();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(root)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveChangesToPref();
                        if (mListener != null) {
                            mListener.onSearchConditionChange();
                        }
                    }
                });
        return builder.create();
    }

    private void saveChangesToPref() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Config.SEARCH_CONDITION_AGE_RANGE, mAgeRange);
        editor.putInt(Config.SEARCH_CONDITION_GENDER, mGender);
        editor.putInt(Config.SEARCH_CONDITION_TIME_RANGE, mArriveTimeRange);
        editor.commit();
    }

    private OnSearchConditionChangeListener mListener = null;


    public void setOnSearchConditionChangeListner(OnSearchConditionChangeListener listner) {
        mListener = listner;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSearchConditionChangeListener {
        void onSearchConditionChange();
    }


    private void initEvents() {
        mAllGenderText.setOnClickListener(this);
        mFemaleText.setOnClickListener(this);
        mMaleText.setOnClickListener(this);
        mTwoYearText.setOnClickListener(this);
        mFiveYearText.setOnClickListener(this);
        mTenYearText.setOnClickListener(this);
        mNoLimitYear.setOnClickListener(this);
        mHalfHourText.setOnClickListener(this);
        mOneHourText.setOnClickListener(this);
        mTwoHourText.setOnClickListener(this);
        mFiveHourText.setOnClickListener(this);
    }

    private void initViews(View root) {
        mAllGenderText = (TextView) root.findViewById(R.id.all_gender_text);
        mFemaleText = (TextView) root.findViewById(R.id.female_gender_text);
        mMaleText = (TextView) root.findViewById(R.id.male_gender_text);
        mTwoYearText = (TextView) root.findViewById(R.id.two_birth_year_range_text);
        mFiveYearText = (TextView) root.findViewById(R.id.five_birth_year_range_text);
        mTenYearText = (TextView) root.findViewById(R.id.ten_birth_year_range_text);
        mNoLimitYear = (TextView) root.findViewById(R.id.no_limit_birth_year_range_text);
        mHalfHourText = (TextView) root.findViewById(R.id.half_hour_range_text);
        mOneHourText = (TextView) root.findViewById(R.id.one_hour_range_text);
        mTwoHourText = (TextView) root.findViewById(R.id.two_hour_range_text);
        mFiveHourText = (TextView) root.findViewById(R.id.five_hour_range_text);

        switch (mGender) {
            case 0:
                mAllGenderText.setSelected(true);
                break;
            case 1:
                mMaleText.setSelected(true);
                break;
            case 2:
                mFemaleText.setSelected(true);
                break;
        }

        switch (mArriveTimeRange) {
            case 30:
                mHalfHourText.setSelected(true);
                break;
            case 60:
                mOneHourText.setSelected(true);
                break;
            case 120:
                mTwoHourText.setSelected(true);
                break;
            case 300:
                mFiveHourText.setSelected(true);
                break;
        }

        switch (mAgeRange) {
            case 2:
                mTwoYearText.setSelected(true);
                break;
            case 5:
                mFiveHourText.setSelected(true);
                break;
            case 10:
                mTenYearText.setSelected(true);
                break;
            case 1000:
                mNoLimitYear.setSelected(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_gender_text:
            case R.id.female_gender_text:
            case R.id.male_gender_text:
                mAllGenderText.setSelected(false);
                mFemaleText.setSelected(false);
                mMaleText.setSelected(false);
                switch (v.getId()) {
                    case R.id.all_gender_text:
                        mGender = 0;
                        mAllGenderText.setSelected(true);
                        break;
                    case R.id.female_gender_text:
                        mGender = 2;
                        mFemaleText.setSelected(true);
                        break;
                    case R.id.male_gender_text:
                        mGender = 1;
                        mMaleText.setSelected(true);
                        break;
                }
                break;
            case R.id.two_birth_year_range_text:
            case R.id.five_birth_year_range_text:
            case R.id.ten_birth_year_range_text:
            case R.id.no_limit_birth_year_range_text:
                mTwoYearText.setSelected(false);
                mFiveYearText.setSelected(false);
                mTenYearText.setSelected(false);
                mNoLimitYear.setSelected(false);
                switch (v.getId()) {
                    case R.id.two_birth_year_range_text:
                        mAgeRange = 2;
                        mTwoYearText.setSelected(true);
                        break;
                    case R.id.five_birth_year_range_text:
                        mAgeRange = 5;
                        mFiveYearText.setSelected(true);
                        break;
                    case R.id.ten_birth_year_range_text:
                        mAgeRange = 10;
                        mTenYearText.setSelected(true);
                        break;
                    case R.id.no_limit_birth_year_range_text:
                        mAgeRange = 1000;
                        mNoLimitYear.setSelected(true);
                        break;
                }
                break;
            case R.id.half_hour_range_text:
            case R.id.one_hour_range_text:
            case R.id.two_hour_range_text:
            case R.id.five_hour_range_text:
                mHalfHourText.setSelected(false);
                mOneHourText.setSelected(false);
                mTwoHourText.setSelected(false);
                mFiveHourText.setSelected(false);
                switch (v.getId()) {
                    case R.id.half_hour_range_text:
                        mArriveTimeRange = 30;
                        mHalfHourText.setSelected(true);
                        break;
                    case R.id.one_hour_range_text:
                        mArriveTimeRange = 60;
                        mOneHourText.setSelected(true);
                        break;
                    case R.id.two_hour_range_text:
                        mArriveTimeRange = 120;
                        mTwoHourText.setSelected(true);
                        break;
                    case R.id.five_hour_range_text:
                        mArriveTimeRange = 300;
                        mFiveHourText.setSelected(true);
                        break;
                }
        }
    }


}

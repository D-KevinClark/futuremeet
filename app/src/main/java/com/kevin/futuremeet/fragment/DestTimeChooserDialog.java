package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.DestChooseActivity;
import com.kevin.futuremeet.utility.Util;

import java.util.Calendar;
import java.util.Date;

public class DestTimeChooserDialog extends DialogFragment {

    private OnTimePickerListener mListener;
    private TextView mDestText;
    private TextView mHourText;
    private TextView mMinuteText;
    private NumberPicker mHourPicker;
    private NumberPicker mMinutePicker;
    private TextView mTodayOrTomorrowText;
    private int mBeginDay;
    private Calendar mCalendar = Calendar.getInstance();

    private String mPoiName;


    //the min time distance from now on the user can set for their arrive time
    private static final int MIN_TIME_DIS_FROM_NOW = 20;

    public DestTimeChooserDialog() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mPoiName = getArguments().getString(DestChooseActivity.POI_NAME);
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dest_time_chooser_dialog, null);
        initViews(view);
        initEvents();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Util.isNetworkAvailabel(getContext())) {
                            Date date = mCalendar.getTime();
                            mListener.OnTimerPicked(date);
                        } else {
                            Toast.makeText(getContext(), R.string.please_check_network, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return builder.create();
    }

    private void initEvents() {
        mHourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal == 0 && newVal != 0) {
                    mMinutePicker.setMinValue(0);
                }
                if (oldVal != 0 && newVal == 0) {
                    mMinutePicker.setMinValue(MIN_TIME_DIS_FROM_NOW);
                }
                setTimeBoardProperly(newVal - oldVal, 0);
            }
        });

        mMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setTimeBoardProperly(0, newVal - oldVal);
            }
        });

    }

    private void initViews(View view) {
        mDestText = (TextView) view.findViewById(R.id.dest_textview);
        mHourText = (TextView) view.findViewById(R.id.hour_textview);
        mMinuteText = (TextView) view.findViewById(R.id.minute_textview);
        mTodayOrTomorrowText = (TextView) view.findViewById(R.id.today_or_tomorrow_text);
        mHourPicker = (NumberPicker) view.findViewById(R.id.hour_picker);
        mMinutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);

        mDestText.setText(mPoiName);

        mBeginDay = mCalendar.get(Calendar.DAY_OF_YEAR);
        setTimeBoardProperly(0, MIN_TIME_DIS_FROM_NOW);

        mHourPicker.setMinValue(0);
        mHourPicker.setMaxValue(23);
        mMinutePicker.setMinValue(MIN_TIME_DIS_FROM_NOW);
        mMinutePicker.setMaxValue(59);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimePickerListener) {
            mListener = (OnTimePickerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTimePickerListener {
        void OnTimerPicked(Date date);
    }


    private void setTimeBoardProperly(int hourDelta, int minuteDelta) {
        if (hourDelta != 0)
            mCalendar.add(Calendar.HOUR_OF_DAY, hourDelta);
        if (minuteDelta != 0)
            mCalendar.add(Calendar.MINUTE, minuteDelta);
        //set day board(today or tomorrow)
        if (mCalendar.get(Calendar.DAY_OF_YEAR) == mBeginDay) {
            mTodayOrTomorrowText.setText(R.string.today);
        } else {
            mTodayOrTomorrowText.setText(R.string.tomorrow);
        }
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        mHourText.setText(hour > 9 ? (hour + "") : ("0" + hour));
        mMinuteText.setText(minute > 9 ? (minute + "") : ("0" + minute));
    }
}

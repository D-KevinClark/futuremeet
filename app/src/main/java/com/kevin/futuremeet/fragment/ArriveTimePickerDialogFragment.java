package com.kevin.futuremeet.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.utility.Config;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by carver on 2016/3/24.
 */
public class ArriveTimePickerDialogFragment extends DialogFragment {

    private NumberPicker mMinutePicker;
    private TextView mArriHour;
    private TextView mArriMinute;
    private Calendar mCalendar;
    private int mBeginDay;
    private TextView mTodayOrTomoText;
    private EditText mLabelText;
    private String mPoiName;
    private ArriveTimePicerDialogListener mListener;
    private Bundle mPoiInfoBundle;
    private Long mInitTime;

    public interface ArriveTimePicerDialogListener {
        void onFuturePOIandTimeConfirmed(Bundle poiInfo);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ArriveTimePicerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View DialogView = inflater.inflate(R.layout.arrive_time_picker, null);
        mCalendar = new GregorianCalendar();
        mInitTime = mCalendar.getTimeInMillis();


        if (getArguments() != null) {
            mPoiInfoBundle = getArguments();
            mPoiName = mPoiInfoBundle.getString(DestChooseFragment.POI_NAME);
        }

        initViews(DialogView);
        builder.setView(DialogView)
                .setPositiveButton(getActivity().getString(R.string.positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPoiInfoBundle.putLong(DestChooseFragment.POI_ARRIVE_TIME, mCalendar.getTimeInMillis());
                        mPoiInfoBundle.putString(DestChooseFragment.POI_DETAIL_LABEL, mLabelText.getText().toString());
                        mListener.onFuturePOIandTimeConfirmed(mPoiInfoBundle);
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArriveTimePickerDialogFragment.this.getDialog().dismiss();
                    }
                });
        return builder.create();
    }

    private void initViews(View root) {
        mBeginDay = mCalendar.get(Calendar.DAY_OF_YEAR);

        mLabelText = (EditText) root.findViewById(R.id.label_edittext);
        NumberPicker mHourPicker = (NumberPicker) root.findViewById(R.id.hour_number_picker);
        mMinutePicker = (NumberPicker) root.findViewById(R.id.minute_number_picker);
        mArriHour = (TextView) root.findViewById(R.id.hour_text);
        mArriMinute = (TextView) root.findViewById(R.id.minute_text);
        mTodayOrTomoText = (TextView) root.findViewById(R.id.today_or_tomorrow_text);
        TextView mPoiNameText = (TextView) root.findViewById(R.id.poi_name_textview);
        mPoiNameText.setEllipsize(TextUtils.TruncateAt.END);
        mPoiNameText.setText(mPoiName);

        setTimeBoardProperly(0, Config.MINIMUM_FUTURE_ARRIVE_TIME_FROM_NOW);

        mHourPicker.setMinValue(0);
        mHourPicker.setMaxValue(23);
        mMinutePicker.setMinValue(Config.MINIMUM_FUTURE_ARRIVE_TIME_FROM_NOW);
        mMinutePicker.setMaxValue(59);


        mHourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal == 0 && newVal != 0) {
                    mMinutePicker.setMinValue(0);
                }
                if (oldVal != 0 && newVal == 0) {
                    mMinutePicker.setMinValue(Config.MINIMUM_FUTURE_ARRIVE_TIME_FROM_NOW);
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

    /**
     * set the time board properly according to the detal of the
     *
     * @param hourDelta
     * @param minuteDelta
     */
    private void setTimeBoardProperly(int hourDelta, int minuteDelta) {

        if (hourDelta != 0)
            mCalendar.add(Calendar.HOUR_OF_DAY, hourDelta);
        if (minuteDelta != 0)
            mCalendar.add(Calendar.MINUTE, minuteDelta);
        //set day board(today or tomorrow)
        if (mCalendar.get(Calendar.DAY_OF_YEAR) == mBeginDay) {
            mTodayOrTomoText.setText(R.string.toady);
        } else {
            mTodayOrTomoText.setText(R.string.tomorrow);
        }
        mArriHour.setText(mCalendar.get(Calendar.HOUR_OF_DAY) + "");
        mArriMinute.setText(mCalendar.get(Calendar.MINUTE) + "");
    }


}

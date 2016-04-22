package com.kevin.futuremeet.adapter;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.FuturePoiBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by carver on 2016/4/22.
 */
public class PoiPageFilterAdapter extends BaseAdapter {
    private List<FuturePoiBean> mFuturePoiList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;


    public PoiPageFilterAdapter(Context context, List<FuturePoiBean> poiBeanList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mFuturePoiList = poiBeanList;
    }

    @Override

    public int getCount() {
        return mFuturePoiList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFuturePoiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        Calendar calendar = Calendar.getInstance();
        int nowDay = calendar.get(Calendar.DAY_OF_YEAR);
        FuturePoiBean futurerPoi = mFuturePoiList.get(position);
        Date date = futurerPoi.getArriveTime();
        calendar.setTime(date);
        int futureDay = calendar.get(Calendar.DAY_OF_YEAR);
        String firstLine;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String hourStr = hour > 9 ? hour + "" : "0" + hour;
        int minute = calendar.get(Calendar.MINUTE);
        String minuteStr = minute > 9 ? minute + "" : "0" + minute;

        if (futureDay == nowDay) {
            firstLine = mContext.getString(R.string.today) + " " + hourStr + ":" + minuteStr;
        } else {
            firstLine = mContext.getString(R.string.tomorrow) + " " + hourStr + ":" + minuteStr;
        }
        String secondline = futurerPoi.getPoiName();
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
        text1.setText(firstLine);
        text2.setText(secondline);
        return view;
    }
}

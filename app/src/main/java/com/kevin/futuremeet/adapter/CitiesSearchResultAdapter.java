package com.kevin.futuremeet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.City;

import java.util.List;

/**
 * Created by carver on 2016/3/20.
 */
public class CitiesSearchResultAdapter extends BaseAdapter {

    private List<City> mResultCitiesList;
    private LayoutInflater mLayoutInflater;

    public CitiesSearchResultAdapter(Context context,List<City> cities){
        mLayoutInflater=LayoutInflater.from(context);
        mResultCitiesList=cities;
    }


    @Override
    public int getCount() {
        return mResultCitiesList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            convertView=mLayoutInflater.inflate(R.layout.cities_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.cityName = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.cityName.setText(mResultCitiesList.get(position).getName());
        return convertView;
    }

    private class ViewHolder{
        TextView cityName;
    }
}

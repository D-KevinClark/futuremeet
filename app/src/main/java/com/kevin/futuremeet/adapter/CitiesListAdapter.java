package com.kevin.futuremeet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.City;
import com.kevin.futuremeet.utility.PinYinUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by carver on 2016/3/19.
 */
public class CitiesListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<City> cityList;//all the cities

    public HashMap<String, Integer> getInitialIndexer() {
        return initialIndexer;
    }

    public HashMap<String,Integer> initialIndexer;
    
    final int VIEW_TYPE = 3;

    /**
     * the adapter of all the cities and the current location city
     * NOTICE: the cityList passed in here must be sorted by the initial of the PinYin
     * @param context
     * @param cityList
     */
    public CitiesListAdapter(Context context, List<City> cityList) {
        this.inflater = LayoutInflater.from(context);
        this.cityList = cityList;
        this.context = context;
        initialIndexer = new HashMap<String, Integer>();


        for (int i = 0; i < cityList.size(); i++) {
            // get the initial of the current string(PinYin)
            String currentInitial = PinYinUtil.getAlpha(cityList.get(i).getPinyin());
            // get the initial of the last string(PinYin)
            String previousInitial = (i - 1) >= 0 ? PinYinUtil.getAlpha(cityList.get(i - 1)
                    .getPinyin()) : " ";
            //the cities list show up here should already sorted, so here record the first position of
            //the whole section in which all the cities has the same initial
            if (!previousInitial.equals(currentInitial)) {
                initialIndexer.put(currentInitial, i);
            }
        }
    }



    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        return position < 2 ? position : 2;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView city;
        int viewType = getItemViewType(position);
        if (viewType == 0) { // location
            convertView = inflater.inflate(R.layout.location_item_in_cities_list, null);
        } else if (viewType==1){//the TextView : "全部城市"
            convertView=inflater.inflate(R.layout.allcity_list_item,null);
        } else {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.cities_list_item, null);
                holder = new ViewHolder();
                holder.initial = (TextView) convertView
                        .findViewById(R.id.initial);
                holder.name = (TextView) convertView
                        .findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position >= 1) {
                holder.name.setText(cityList.get(position).getName());
                //check to see if this is the first city of a new group of the same initial
                //and if it is , show the initial
                String currentInitial = PinYinUtil.getAlpha(cityList.get(position).getPinyin());
                String previousInitial = (position - 1) >= 0 ? PinYinUtil.getAlpha(cityList
                        .get(position - 1).getPinyin()) : " ";
                if (!previousInitial.equals(currentInitial)) {
                    holder.initial.setVisibility(View.VISIBLE);
                    holder.initial.setText(currentInitial);
                } else {
                    holder.initial.setVisibility(View.GONE);
                }
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView initial; // first letter of the city
        TextView name; // name of the city
    }
}

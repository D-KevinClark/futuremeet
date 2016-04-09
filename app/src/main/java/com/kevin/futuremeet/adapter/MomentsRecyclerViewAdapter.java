package com.kevin.futuremeet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevin.futuremeet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carver on 2016/4/9.
 */
public class MomentsRecyclerViewAdapter extends RecyclerView.Adapter<MomentsRecyclerViewAdapter.MyViewHodler> {

    public Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> datas = new ArrayList<>();

    public MomentsRecyclerViewAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        for (int i = 0; i < 60; i++) {
            datas.add(i + " " + i + " " + i + " " + i + " ");
        }
    }

    @Override
    public MyViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.moment_recyclerview_item, parent, false);
        MyViewHodler viewHodler = new MyViewHodler(view);
        return viewHodler;
    }
    @Override
    public void onBindViewHolder(MyViewHodler holder, int position) {
//        holder.contentTextView.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class MyViewHodler extends RecyclerView.ViewHolder {

        public final TextView contentTextView;
        public MyViewHodler(View itemView) {
            super(itemView);
            contentTextView = (TextView) itemView.findViewById(R.id.content_textview);
        }
    }
}

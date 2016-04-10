package com.kevin.futuremeet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.beans.MomentContract;

import java.util.List;

/**
 * Created by carver on 2016/4/9.
 */
public class MomentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<AVObject> mMomentsList;

    private boolean mIsAllDataLoaded=false;

    private static final int FOOTER_ITEM_TYPE = 100;
    private static final int NORMAL_ITEM_TYPE = 101;


    public MomentsRecyclerViewAdapter(Context context, List<AVObject> moments) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMomentsList = moments;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL_ITEM_TYPE) {
            View view = mLayoutInflater.inflate(R.layout.moment_recyclerview_item, parent, false);
            MomentViewHolder viewHodler = new MomentViewHolder(view);
            return viewHodler;
        } else {
            View view = mLayoutInflater.inflate(R.layout.moments_footer, parent, false);
            FooterViewHolder footerViewHolder = new FooterViewHolder(view);
            return footerViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) {
            return FOOTER_ITEM_TYPE;
        } else {
            return NORMAL_ITEM_TYPE;
        }
    }

    /**
     * get the real data item (exclude the footer) ,just a convenient method
     * @return
     */
    public int getDataItemCount() {
        return mMomentsList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount()-1) {//this is footer
            if (mIsAllDataLoaded) {
                FooterViewHolder footerViewHolder= (FooterViewHolder) holder;
                footerViewHolder.isLoadingView.setVisibility(View.GONE);
                footerViewHolder.allLoadedView.setVisibility(View.VISIBLE);
            }
            return;
        } else {
            MomentViewHolder moementHolder = (MomentViewHolder) holder;
            if (moementHolder.imagesContainer.getChildCount() != 0) {
                moementHolder.imagesContainer.removeAllViews();
            }
            String content = mMomentsList.get(position).getString(MomentContract.CONTENT);
            List<AVFile> images = mMomentsList.get(position).getList(MomentContract.IMAGES);
            int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.moment_pics_size);
            int imageViewMarginRight = mContext.getResources().getDimensionPixelSize(R.dimen.moment_images_margin_right);
            moementHolder.contentTextView.setText(content);

            if (images == null) return;//if there is no image within this post moment just return
            for (int i = 0; i < images.size(); i++) {
                AVFile image = images.get(i);
                ImageView imageView = new ImageView(mContext);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
                layoutParams.setMargins(0, 0, imageViewMarginRight, 0);
                imageView.setLayoutParams(layoutParams);
                moementHolder.imagesContainer.addView(imageView);

                //down load a thumbnail to save the network traffic
                String url = image.getThumbnailUrl(false, imageSize, imageSize, 100, "jpg");
                Glide.with(mContext)
                        .load(url)
                        .asBitmap()
                        .placeholder(R.color.greyShadow)
                        .into(imageView);
            }
        }

    }

    public void setMomentsList(List<AVObject> moments) {
        mMomentsList = moments;
    }

    @Override
    public int getItemCount() {
        return mMomentsList.size() + 1;
    }

    /**
     * set the flag, so when the footer show, it shows in the proper way ,see the method "onBindViewHolder"
     */
    public void showAllMomentsLoadedFooter(){
        mIsAllDataLoaded=true;
    }

    public static class MomentViewHolder extends RecyclerView.ViewHolder {

        public final TextView contentTextView;
        public final LinearLayout imagesContainer;

        public MomentViewHolder(View itemView) {
            super(itemView);
            contentTextView = (TextView) itemView.findViewById(R.id.content_textview);
            imagesContainer = (LinearLayout) itemView.findViewById(R.id.images_container);
        }
    }


    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public final View isLoadingView;
        public final View allLoadedView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            isLoadingView = itemView.findViewById(R.id.is_loading);
            allLoadedView = itemView.findViewById(R.id.all_result_showed);
        }
    }
}

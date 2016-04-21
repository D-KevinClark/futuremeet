package com.kevin.futuremeet.custom;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ActionProvider;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.DestChooseActivity;
import com.kevin.futuremeet.activity.MomentEditorActivity;

/**
 * Created by carver on 2016/4/20.
 */
public class PublishActionProvider extends ActionProvider {
    private Context mContext;

    public PublishActionProvider(Context context) {
        super(context);
        mContext=context;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        subMenu.add(R.string.publish_moment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(mContext, MomentEditorActivity.class);
                mContext.startActivity(intent);
                return true;
            }
        });

        subMenu.add(R.string.add_future_poi).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(mContext, DestChooseActivity.class);
                mContext.startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }
}

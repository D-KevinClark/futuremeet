package com.kevin.futuremeet.database;

import android.provider.BaseColumns;

/**
 * Created by carver on 2016/5/1.
 */
public class FollowerDBContract {
    public FollowerDBContract() {

    }

    public static class FollowerEntry implements BaseColumns {
        public static final String TABLE_NAME = "follower";
        public static final String FOLLOWER_BASIC_INFO_ID = "follower_basic_info_id";
    }
}

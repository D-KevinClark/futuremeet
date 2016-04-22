package com.kevin.futuremeet.database;

import android.provider.BaseColumns;

/**
 * Created by carver on 2016/4/21.
 */
public class FuturePoiDBContract {
    FuturePoiDBContract() {

    }

    public static class FuturePoiEntry implements BaseColumns {
        public static final String TABLE_NAME = "futurepoi";
        public static final String COLUMN_NAME_POI_NAME = "poiname";
        public static final String COLUMN_NAME_POI_ADDRESS = "poiadress";
        public static final String COLUMN_NAME_POI_LNG = "lng";
        public static final String COLUMN_NAME_POI_LAT = "lat";
        public static final String COLUMN_NAME_POI_ARRIVE_TIME = "arrivetime";
    }
}

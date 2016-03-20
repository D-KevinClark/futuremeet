package com.kevin.futuremeet.database;

import android.provider.BaseColumns;

/**
 * Created by carver on 2016/3/19.
 */
public class CitiesContract {

    /**
     * this is where the system will save the database at by default
     */
    public static final String DB_PATH = "/data/data/com.futuremeet/databases/";

    public static final String DB_NAME = "cities.db";
    public static final String DB_ASSETS_NAME = "cities.db";

    public static final class CityEntry implements BaseColumns {
        public static final String COLUMN__CITY_NAME = "name";
        public static final String COLUMN_CITY_PINYIN = "pinyin";

    }

}

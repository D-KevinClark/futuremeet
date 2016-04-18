package com.kevin.futuremeet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.adapter.CitiesSearchResultAdapter;
import com.kevin.futuremeet.beans.City;
import com.kevin.futuremeet.custom.LetterListView;
import com.kevin.futuremeet.database.CitiesDBHelper;
import com.kevin.futuremeet.utility.PinYinUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityChooseActivity extends AppCompatActivity implements
        AbsListView.OnScrollListener, LetterListView.OnTouchingLetterChangedListener {

    private List<City> mCityList;
    private CitiesDBHelper mCityDBHelper;
    private ListView mAllcityListView;
    private TextView mLetterOverLay;

    private boolean mIsLetterOverlayReady = false;

    private Handler mOverlayHandler;

    private OverlayDismissThread mOverlayDismissThread;

    private Map<String, Integer> mInitialIndexer;

    private CitiesListAdapter mCitiesListAdapter;

    private LetterListView mLetterListView;
    private EditText mSearchCityEditText;
    private ListView mCitySearchResultListView;
    private TextView mCityNoFoundTextView;
    private TextView mCurrCityTextView;
    private ProgressBar mLocationProgBar;

    private List<City> mSearchResultCityList;

    private Toolbar toolbar;

    private CitiesSearchResultAdapter mCitiesSearchResultAdapter;
    private LocationClient mLocationClient;
    private boolean isScroll;// indicate if the Cities ListView is scrolling because of direct touch or fling

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_city_choose);
        mAllcityListView = (ListView) findViewById(R.id.all_city_listview);
        mAllcityListView.setOnScrollListener(this);
        mAllcityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 1) {
                    City city = mCityList.get(position);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(DestChooseActivity.CURR_CITY_NAME, city.getName());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
        mLetterListView = (LetterListView) findViewById(R.id.letters_sidebar_listview);
        mLetterListView.setOnTouchingLetterChangedListener(this);
        mSearchCityEditText = (EditText) findViewById(R.id.search_city_edittext);
        mCitySearchResultListView = (ListView) findViewById(R.id.search_city_result_listview);
        mCitySearchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = mSearchResultCityList.get(position);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(DestChooseActivity.CURR_CITY_NAME, city.getName());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        mCityNoFoundTextView = (TextView) findViewById(R.id.search_city_no_result_textview);
        mSearchCityEditText = (EditText) findViewById(R.id.search_city_edittext);
        initToolBar();


        mSearchCityEditText.addTextChangedListener(mSearchCityTextWhatcher);

        mCityDBHelper = new CitiesDBHelper(this);
        mOverlayDismissThread = new OverlayDismissThread();
        mOverlayHandler = new Handler();
        mSearchResultCityList = new ArrayList<>();

        initAllCitise();
        initLetterOverLay();


        mCitiesListAdapter = new CitiesListAdapter(this, mCityList);
        mAllcityListView.setAdapter(mCitiesListAdapter);
        //init the sidebar letter ListView user the adapter which contains all the cities
        mInitialIndexer = mCitiesListAdapter.getInitialIndexer();
        mCitiesSearchResultAdapter = new CitiesSearchResultAdapter(this, mSearchResultCityList);
        mCitySearchResultListView.setAdapter(mCitiesSearchResultAdapter);

        mLocationClient=new LocationClient(getApplicationContext());
        initLocationOption();
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();
    }


    /**
     * A listener for the location result,deal the Location Info here
     */
    private BDLocationListener myLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                String currCity = bdLocation.getCity();
                if (mCurrCityTextView!=null){
                    mCurrCityTextView.setClickable(true);
                    mCurrCityTextView.setText(currCity.substring(0, currCity.length() - 1));
                    if (mLocationProgBar!=null)mLocationProgBar.setVisibility(View.GONE);
                }
                mLocationClient.stop();
            }else{
                Toast.makeText(CityChooseActivity.this, R.string.location_failure, Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * set some option to the LocationClient,like Mode,or Address needed
     */
    private void initLocationOption() {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        locationClientOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(locationClientOption);
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.city_choose_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.choose_city);
    }

    private void getResultCityList(String keyword) {
        SQLiteDatabase db = mCityDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from city where name like \"%" + keyword
                        + "%\" or pinyin like \"%" + keyword + "%\"", null);
        City city;
        while (cursor.moveToNext()) {
            city = new City(cursor.getString(1), cursor.getString(2));
            mSearchResultCityList.add(city);
        }
        cursor.close();
        db.close();

        Collections.sort(mSearchResultCityList, comparator);
    }


    /**
     * a text watcher for the city search edittext
     */
    private final TextWatcher mSearchCityTextWhatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString() == null || "".equals(s.toString())) {
                mLetterListView.setVisibility(View.VISIBLE);
                mAllcityListView.setVisibility(View.VISIBLE);
                mCitySearchResultListView.setVisibility(View.GONE);
                mCityNoFoundTextView.setVisibility(View.GONE);
            } else {
                if (mSearchResultCityList != null) mSearchResultCityList.clear();
                mLetterListView.setVisibility(View.GONE);
                mAllcityListView.setVisibility(View.GONE);
                //get the result cities
                getResultCityList(s.toString());

                if (mSearchResultCityList.size() <= 0) {
                    mCityNoFoundTextView.setVisibility(View.VISIBLE);
                    mCitySearchResultListView.setVisibility(View.GONE);
                } else {
                    mCityNoFoundTextView.setVisibility(View.GONE);
                    mCitySearchResultListView.setVisibility(View.VISIBLE);
                    mCitiesSearchResultAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * get all the city data into the ArrayList for adapter to show them
     */
    private void initAllCitise() {
        List<City> allCities = new ArrayList<>();
        //add these two is only for the "当前定位城市" label showing
        // and the "全部城市" label showing, no actual meaning
        allCities.add(new City("定位", "0"));
        allCities.add(new City("全部", "1"));
        allCities.addAll(getCityList());
        mCityList = allCities;
    }


    /**
     * get the cities which have been sorted by the initial of the PinYin
     *
     * @return
     */
    private ArrayList<City> getCityList() {
        ArrayList<City> list = new ArrayList<City>();
        SQLiteDatabase db = mCityDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from city", null);
        City city;
        while (cursor.moveToNext()) {
            city = new City(cursor.getString(1), cursor.getString(2));
            list.add(city);
        }
        cursor.close();
        db.close();
        //IMPORTANT: here sort the cities
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * use this to sort the city by the initial of PinYin
     */
    @SuppressWarnings("rawtypes")
    Comparator comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            int flag = a.compareTo(b);
            return flag;
        }
    };

    /**
     * init the overlay for showing the current initial
     */
    private void initLetterOverLay() {
        mIsLetterOverlayReady = true;
        LayoutInflater inflater = LayoutInflater.from(this);
        mLetterOverLay = (TextView) inflater.inflate(R.layout.letter_overlay, null);
        mLetterOverLay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(mLetterOverLay, lp);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL
                || scrollState == SCROLL_STATE_FLING) {
            isScroll = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!isScroll) return;
        if (!mIsLetterOverlayReady) return;
        String text;
        String pinyin = mCityList.get(firstVisibleItem).getPinyin();
        if (firstVisibleItem < 2) {
            return;// if it's in the current location or the
        } else {
            text = PinYinUtil.converterToFirstSpell(pinyin)
                    .substring(0, 1).toUpperCase();
        }
        mLetterOverLay.setText(text);
        mLetterOverLay.setVisibility(View.VISIBLE);
        mOverlayHandler.removeCallbacks(mOverlayDismissThread);
        // delay the execution for 1 second ,then make the overlay disappeared
        mOverlayHandler.postDelayed(mOverlayDismissThread, 1000);
    }

    /**
     * invoked when the letter list is been touched
     *
     * @param s
     */
    @Override
    public void onTouchingLetterChanged(String s) {
        isScroll = false;
        if (mInitialIndexer == null) return;
        if (mInitialIndexer.get(s) == null) return;
        int position = mInitialIndexer.get(s);
        //the follow method is to make the scroll animation stop immediately
        mAllcityListView.dispatchTouchEvent(
                MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_CANCEL,
                        0,
                        0,
                        0)
        );
        mAllcityListView.setSelection(position);
        mLetterOverLay.setText(s);
        mLetterOverLay.setVisibility(View.VISIBLE);
        mOverlayHandler.removeCallbacks(mOverlayDismissThread);
        // 延迟一秒后执行，让overlay为不可见
        mOverlayHandler.postDelayed(mOverlayDismissThread, 1000);

    }

    /**
     * set the letter-showing  overlay to be gone
     */
    private class OverlayDismissThread implements Runnable {
        @Override
        public void run() {
            mLetterOverLay.setVisibility(View.GONE);
        }
    }

    /**
     * Created by carver on 2016/3/19.
     */
    public  class CitiesListAdapter extends BaseAdapter {
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
                mCurrCityTextView= (TextView) convertView.findViewById(R.id.current_city_textview);
                mLocationProgBar= (ProgressBar) convertView.findViewById(R.id.location_progressbar);
                mCurrCityTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(DestChooseActivity.CURR_CITY_NAME,
                                mCurrCityTextView.getText().toString());
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
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
}

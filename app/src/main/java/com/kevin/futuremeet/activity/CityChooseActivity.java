package com.kevin.futuremeet.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.adapter.CitiesListAdapter;
import com.kevin.futuremeet.adapter.CitiesSearchResultAdapter;
import com.kevin.futuremeet.beans.City;
import com.kevin.futuremeet.customview.LetterListView;
import com.kevin.futuremeet.database.CitiesDBHelper;
import com.kevin.futuremeet.utility.PinYinUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private List<City> mSearchResultCityList;

    private Toolbar toolbar;

    private CitiesSearchResultAdapter mCitiesSearchResultAdapter;
    private boolean isScroll;// indicate if the Cities ListView is scrolling because of direct touch or fling

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_choose);
        mAllcityListView = (ListView) findViewById(R.id.all_city_listview);
        mAllcityListView.setOnScrollListener(this);
        mLetterListView = (LetterListView) findViewById(R.id.letters_sidebar_listview);
        mLetterListView.setOnTouchingLetterChangedListener(this);
        mSearchCityEditText = (EditText) findViewById(R.id.search_city_edittext);
        mCitySearchResultListView = (ListView) findViewById(R.id.search_city_result_listview);
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

    }

    private void initToolBar() {
        toolbar= (Toolbar) findViewById(R.id.city_choose_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
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

                Log.i("searchresult", String.valueOf(mSearchResultCityList.size()));
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
}

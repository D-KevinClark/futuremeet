package com.kevin.futuremeet.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.adapter.CitiesListAdapter;
import com.kevin.futuremeet.beans.City;
import com.kevin.futuremeet.database.CitiesDBHelper;
import com.kevin.futuremeet.utility.PinYinUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CityChooseActivity extends AppCompatActivity implements AbsListView.OnScrollListener{

    private List<City> mCityList;
    private CitiesDBHelper   mCityDBHelper;
    private ListView mAllcityListView;

    private TextView mLetterOverLay;
    
    private boolean mIsLetterOverlayReady=false;

    private Handler mOverlayHandler;

    private OverlayDismissThread mOverlayDismissThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_choose);
        mAllcityListView = (ListView) findViewById(R.id.all_city_listview);
        mAllcityListView.setOnScrollListener(this);
        mCityDBHelper = new CitiesDBHelper(this);
        mOverlayDismissThread=new OverlayDismissThread();
        mOverlayHandler=new Handler();

        initAllCitise();
        initLetterOverLay();


        mAllcityListView.setAdapter(new CitiesListAdapter(this,mCityList));
    }

    /**
     * get all the city data into the ArrayList for adapter to show them
     */
    private void initAllCitise() {
        List<City> allCities=new ArrayList<>();
        //add these two is only for the "当前定位城市" label showing
        // and the "全部城市" label showing, no actual meaning
        allCities.add(new City("定位","0"));
        allCities.add(new City("全部", "0"));
        allCities.addAll(getCityList());
        mCityList=allCities;
    }


    /**
     * get the cities which have been sorted by the initial of the PinYin
     * @return
     */
    private ArrayList<City> getCityList() {
        ArrayList<City> list = new ArrayList<City>();
        SQLiteDatabase db = mCityDBHelper.getWritableDatabase();
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
    private void initLetterOverLay(){
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
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(mLetterOverLay, lp);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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
     * set the letter-showing  overlay to be gone
     */
    private class OverlayDismissThread implements Runnable {
        @Override
        public void run() {
            mLetterOverLay.setVisibility(View.GONE);
        }
    }
}

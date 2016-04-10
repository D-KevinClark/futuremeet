package com.kevin.futuremeet.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.CityChooseActivity;
import com.kevin.futuremeet.beans.CurrentLocation;
import com.kevin.futuremeet.utility.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DestChooseFragment extends Fragment implements OnGetPoiSearchResultListener,
        OnGetSuggestionResultListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int CITY_REQUEST_CODE = 100;

    private String mParam1;
    private String mParam2;

    public static final String CURR_CITY_NAME = "curr_city_name";



    private LocationClient mLocationClient;
    private PoiSearch mPoiSearch;
    private SuggestionSearch mSugSearch;


    private Toolbar mToolbar;

    private ListView mPoiListView;
    private ArrayList<Map<String, String>> mPoiList;
    private SimpleAdapter mPoiListAdapter;

    private AutoCompleteTextView mDestSearchView;
    private ArrayAdapter<String> mAutoTextAdapter;
    private List<String> mSugList;

    private Button mPoiSearchButton;

    private TextView mEmptyView;//show this when there are no search result for the poi search
    private TextView mTellWhereTextView;//show this when user input nothing in the poi search view

    private ProgressDialog mProgressDialog;

    private View root;//this is the root view for this fragment

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({POI_NAME,POI_ADDRESS,POI_LNG,POI_LAT,POI_ARRIVE_TIME})
    public @interface PoiAtrr{}

    public static final String POI_NAME = "poi_name";
    public static final String POI_ADDRESS = "poi_address";
    public static final String POI_LNG = "poi_lng";
    public static final String POI_LAT = "poi_lat";
    public static final String POI_ARRIVE_TIME = "poi_arrive_time";
    public static final String POI_DETAIL_LABEL="poi_detail_label";


    private static final int POI_SEARCH_PAGESIEZ = 20;//set the page size of the poi search result
    private int mPoiCuttPageNum = 0;//page number index  start at 0
    private int mTotalPoiPageNum = 0;//total page number start at 1
    //click the search button to preform a poi search do not need the old poi data but click listview footer need
    //so here this boolean is to indicate that
    private boolean mIsPreviousPoiDataNeeded;


    private View mAllPoiShowedFooter;
    private View mIsloadingPoiFooter;
    private View mLoadMorePoiFooter;

    private View mDeleteAllTextLayout;

    private String mCurrentPoiKeywords = null;

    private String mCurrentCity = null;


//    private OnFragmentInteractionListener mListener;

    public DestChooseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DestChooseFragment.
     */
    public static DestChooseFragment newInstance(String param1, String param2) {
        DestChooseFragment fragment = new DestChooseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dest_choose, container, false);
        initToolbar();
        initLocationFunc();
        initSuggestionFunc();
        mLocationClient.start();

        initPoiSearch();
        initPoiListView();
        initAutoTextView();
        initPoiSearchButton();
        initDeleteAllTextView();
        return root;
    }

    private void initDeleteAllTextView() {
        mDeleteAllTextLayout = root.findViewById(R.id.delete_all_text_layout);
        mDeleteAllTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDestSearchView.setText("");
            }
        });
    }


    private void initSuggestionFunc() {
        mSugSearch = SuggestionSearch.newInstance();
        mSugSearch.setOnGetSuggestionResultListener(this);
    }

    @Override

    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }

        mSugList = new ArrayList<String>();
        for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
            if (info.key != null) {
                mSugList.add(info.key);
            }
        }
        mAutoTextAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, mSugList);
        mDestSearchView.setAdapter(mAutoTextAdapter);
        mAutoTextAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initAutoTextView() {
        mDestSearchView = (AutoCompleteTextView) root.findViewById(R.id.dest_place_searchview);
        mDestSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mPoiSearchButton.setEnabled(true);
                } else {//when the searchview is empty the listview should be empty too
                    mPoiSearchButton.setEnabled(false);
                    mPoiListView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    mTellWhereTextView.setVisibility(View.VISIBLE);
                }

                if (mCurrentCity == null)  return;

                mSugSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(s.toString()).city(mCurrentCity));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initPoiSearchButton() {
        mPoiSearchButton = (Button) root.findViewById(R.id.poi_search_button);
        mPoiSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentCity == null) {
                    Toast.makeText(getContext(), R.string.please_choose_city, Toast.LENGTH_SHORT).show();
                    return;
                }
                String keyword = mDestSearchView.getText().toString();
                mCurrentPoiKeywords = keyword;
                mPoiCuttPageNum = 0;//reset the current poi page number

                mIsPreviousPoiDataNeeded = false;
                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setMessage(getString(R.string.is_searching_relevant_place));
                mProgressDialog.show();
                mTellWhereTextView.setVisibility(View.GONE);
                searchPois(keyword, mCurrentCity, 0);
            }
        });
    }


    private void setCurrentCityToPrefs(String city) {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit()
                .putString(CurrentLocation.CURRENT_LOCAITON, city)
                .apply();
    }

    private String getCurrentCityfromPrefs() {
        String city = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(CurrentLocation.CURRENT_LOCAITON, null);
        return city;
    }


    private void searchPois(String keywords, String city, int pagenumber) {
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(city)
                .keyword(keywords)
                .pageCapacity(POI_SEARCH_PAGESIEZ)
                .pageNum(pagenumber));
    }

    /**
     * i the list view for show pois by searching,instantiation for listview,arraylist,adapter,and
     * set it to the listview
     */
    private void initPoiListView() {
        //since these two view is associate with the list view , so find it here
        mEmptyView = (TextView) root.findViewById(R.id.empty);
        mTellWhereTextView = (TextView) root.findViewById(R.id.tell_me_where_textview);
        mPoiListView = (ListView) root.findViewById(R.id.dest_search_listview);
        //init the footer of the listview
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View footerLayout = inflater.inflate(R.layout.poi_list_footer_layout, null);
        mAllPoiShowedFooter = footerLayout.findViewById(R.id.all_result_showed);
        mIsloadingPoiFooter = footerLayout.findViewById(R.id.is_loading);
        mLoadMorePoiFooter = footerLayout.findViewById(R.id.load_moro);
        mLoadMorePoiFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = getCurrentCityfromPrefs();
                if (city == null || mCurrentPoiKeywords == null) return;
                searchPois(mCurrentPoiKeywords, city, ++mPoiCuttPageNum);
                updataListFooterState(IS_LOADING_MORE);
                mIsPreviousPoiDataNeeded = true;
            }
        });

        mPoiListView.setVisibility(View.GONE);
        mPoiListView.addFooterView(footerLayout);
        mPoiList = new ArrayList<>();
        mPoiListAdapter = new SimpleAdapter(getContext(),
                mPoiList,
                R.layout.search_poi_list_item,
                new String[]{POI_NAME, POI_ADDRESS},
                new int[]{R.id.search_poi_name, R.id.search_poi_address}
        );
        mPoiListView.setAdapter(mPoiListAdapter);
        mPoiListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Util.closeTheSoftKeyboard(v, getContext());
                return false;
            }
        });
        mPoiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> map = mPoiList.get(position);
                String poiName = map.get(POI_NAME);
                String poiAdress = map.get(POI_ADDRESS);
                String poiLng = map.get(POI_LNG);
                String poiLat = map.get(POI_LAT);

                ArriveTimePickerDialogFragment pickerDialogFragment = new ArriveTimePickerDialogFragment();
                Bundle bundle = new Bundle();

                bundle.putString(POI_ADDRESS, poiAdress);
                bundle.putString(POI_NAME, poiName);
                bundle.putString(POI_LAT, poiLat);
                bundle.putString(POI_LNG, poiLng);

                pickerDialogFragment.setArguments(bundle);
                pickerDialogFragment.show(getActivity().getSupportFragmentManager(), "dialogFragment");
            }
        });
    }

    private static final int CLICK_TO_LOAD_MORE = 1;
    private static final int IS_LOADING_MORE = 2;
    private static final int All_POI_SHOWED = 3;


    private void updataListFooterState(int state) {
        mIsloadingPoiFooter.setVisibility(View.GONE);
        mAllPoiShowedFooter.setVisibility(View.GONE);
        mLoadMorePoiFooter.setVisibility(View.GONE);
        switch (state) {
            case CLICK_TO_LOAD_MORE:
                mLoadMorePoiFooter.setVisibility(View.VISIBLE);
                break;
            case IS_LOADING_MORE:
                mIsloadingPoiFooter.setVisibility(View.VISIBLE);
                break;
            case All_POI_SHOWED:
                mAllPoiShowedFooter.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * init the PoiSearch, instantiation and set the listener
     */
    private void initPoiSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    /**
     * A listener for the location result,deal the Location Info here
     */
    private BDLocationListener myLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                String cityFullName = bdLocation.getCity();
                String city = cityFullName.substring(0, cityFullName.length() - 1);
                mCurrentCity = city;
                setCurrentCityToPrefs(city);
                if (mToolbar != null) mToolbar.setTitle(city);
                mLocationClient.stop();
            } else {
                Toast.makeText(getContext(), R.string.location_failure, Toast.LENGTH_SHORT).show();
                mCurrentCity = getCurrentCityfromPrefs();
                if (mCurrentCity != null && mToolbar != null) {
                    mToolbar.setTitle(mCurrentCity);
                    Toast.makeText(getContext(), R.string.location_failure, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     * set some option to the LocationClient,like Mode,or Address needed,after this function you can
     * call LocationClient.start() immediately
     */
    private void initLocationFunc() {
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        locationClientOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(locationClientOption);
        mLocationClient.registerLocationListener(myLocationListener);
    }

    /**
     * set the title of the title bar
     *
     * @param cityName
     */
    private void setToolBarTitle(String cityName) {
        if (mToolbar != null) {
            mToolbar.setTitle(cityName);
        }
    }

    private void initToolbar() {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        mToolbar = (Toolbar) root.findViewById(R.id.dest_choose_toolbar);
        appCompatActivity.setSupportActionBar(mToolbar);
        String currCity = getCurrentCityfromPrefs();
        if (currCity != null) {
            mToolbar.setTitle(currCity);
        } else {
            mToolbar.setTitle(R.string.is_locating);
        }
        mToolbar.setNavigationIcon(R.drawable.list);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CityChooseActivity.class);
                startActivityForResult(intent, CITY_REQUEST_CODE);
            }
        });
    }

    /**
     * handle the cities that has been chosen from the {@link CityChooseActivity}
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String cityName = data.getStringExtra(DestChooseFragment.CURR_CITY_NAME);
            if (cityName != null && !cityName.equals("")) {
                setToolBarTitle(cityName);
            }
        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.getAllPoi() == null) {
            if (mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }
            mEmptyView.setVisibility(View.VISIBLE);
            mPoiListView.setVisibility(View.GONE);
            return;
        }
        mPoiCuttPageNum = poiResult.getCurrentPageNum();
        mTotalPoiPageNum = poiResult.getTotalPageNum();
        if (mPoiList == null||!mIsPreviousPoiDataNeeded) mPoiList = new ArrayList<>();

        new PreparePoiDataTask().execute(poiResult.getAllPoi());
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    private class PreparePoiDataTask extends AsyncTask<List<PoiInfo>, Void, Void> {

        @Override
        protected Void doInBackground(List<PoiInfo>... params) {
            List<PoiInfo> pois = params[0];
            for (PoiInfo info : pois) {
                //we don't need the bus line poi and the subway line poi
                if (info.type == PoiInfo.POITYPE.BUS_LINE || info.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                    continue;
                }
                Map<String, String> map = new HashMap<>();
                if (info.name != null && info.address != null) {
                    map.put(POI_NAME, info.name);
                    map.put(POI_ADDRESS, info.address);
                    map.put(POI_LAT, String.valueOf(info.location.latitude));
                    map.put(POI_LNG, String.valueOf(info.location.longitude));
                }
                mPoiList.add(map);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            int poiSize = mPoiList.size();
            if (poiSize == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
                mPoiListView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mPoiListView.setVisibility(View.VISIBLE);
            }
            if (!mIsPreviousPoiDataNeeded) {
                mPoiListAdapter = new SimpleAdapter(getContext(),
                        mPoiList,
                        R.layout.search_poi_list_item,
                        new String[]{POI_NAME, POI_ADDRESS},
                        new int[]{R.id.search_poi_name, R.id.search_poi_address}
                );
                mPoiListView.setAdapter(mPoiListAdapter);
            }
            mPoiListAdapter.notifyDataSetChanged();

            if (mPoiCuttPageNum == mTotalPoiPageNum - 1) {
                updataListFooterState(All_POI_SHOWED);
            } else {
                updataListFooterState(CLICK_TO_LOAD_MORE);
            }
        }
    }
}

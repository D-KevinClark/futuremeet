package com.kevin.futuremeet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.CityChooseActivity;
import com.kevin.futuremeet.beans.CurrentLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DestChooseFragment extends Fragment implements OnGetPoiSearchResultListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int CITY_REQUEST_CODE = 100;

    private String mParam1;
    private String mParam2;

    public static final String CURR_CITY_NAME = "curr_city_name";

    private String mCurrCity;

    private Toolbar mToolbar;

    private LocationClient mLocationClient;
    private SharedPreferences mSharedPref;
    private PoiSearch mPoiSearch;
    private ListView mSearchResListView;
    private ArrayList<Map<String,String>> mPoiList;
    private SimpleAdapter mPoiListAdapter;
    private SearchView mDestSearchView;
    private Button mPoiSearchButton;
    private TextView mEmptyView;//show this when there are no search result for the poi search
    private TextView mTellWhereTextView;//show this when user input nothing in the poi search view
    private LinearLayout mSearchingIndicator;//show this when performing the poi searching


    private static final String POI_NAME="poi_name";
    private static final String POI_ADDRESS="poi_address";




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
        View root = inflater.inflate(R.layout.fragment_dest_choose, container, false);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initToolbar(root);
        initLocationFunc();
        mLocationClient.start();
        initPoiSearch();
        initPoiListView(root);
        initPoiSearchView(root);
        initPoiSearchButton(root);

        return root;
    }

    private void initPoiSearchView(View root) {
        mDestSearchView = (SearchView) root.findViewById(R.id.dest_place_searchview);
        mDestSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length()>0){
                    mPoiSearchButton.setEnabled(true);
                }else{//when the searchview is empty the listview should be empty too
                    mPoiSearchButton.setEnabled(false);
                    mSearchResListView.setVisibility(View.GONE);
                    mSearchingIndicator.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    mTellWhereTextView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    private void initPoiSearchButton(View root){
        mPoiSearchButton = (Button) root.findViewById(R.id.poi_search_button);
        mPoiSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword=mDestSearchView.getQuery().toString();
                String city = mToolbar.getTitle().toString();
                //is the location has failed, there no need and no way to proceed an poi search
                if (city.equals(getString(R.string.is_locating))) {
                    Toast.makeText(getContext(), R.string.location_failure, Toast.LENGTH_SHORT).show();
                    return;
                }
                mSearchingIndicator.setVisibility(View.VISIBLE);
                mTellWhereTextView.setVisibility(View.GONE);
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(city)
                        .keyword(keyword)
                        .pageCapacity(10));
            }
        });
    }

    /**
     * i the list view for show pois by searching,instantiation for listview,arraylist,adapter,and
     * set it to the listview
     * @param root
     */
    private void initPoiListView(View root) {
        //since these two view is associate with the list view , so find it here
        mEmptyView= (TextView) root.findViewById(R.id.empty);
        mTellWhereTextView = (TextView) root.findViewById(R.id.tell_me_where_textview);
        mSearchingIndicator = (LinearLayout) root.findViewById(R.id.searching_indicator);

        mSearchResListView = (ListView) root.findViewById(R.id.dest_search_listview);
        mPoiList = new ArrayList<>();
        mPoiListAdapter=new SimpleAdapter(getContext(),
                mPoiList,
                R.layout.search_poi_list_item,
                new String[]{POI_NAME,POI_ADDRESS},
                new int[]{R.id.search_poi_name,R.id.search_poi_address}
                );
        mSearchResListView.setAdapter(mPoiListAdapter);
    }



    /**
     * init the PoiSearch, instantiation and set the listener
     */
    private void initPoiSearch(){
        SDKInitializer.initialize(getActivity().getApplicationContext());
        mPoiSearch=PoiSearch.newInstance();
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
                mCurrCity = cityFullName.substring(0, cityFullName.length() - 1);
                if (mSharedPref.getString(CurrentLocation.CURRENT_LOCAITON, null) == null) {
                    mSharedPref.edit().putString(CurrentLocation.CURRENT_LOCAITON, mCurrCity).apply();
                    if (mToolbar != null) mToolbar.setTitle(mCurrCity);
                } else {
                    if (mSharedPref.getString(CurrentLocation.CURRENT_LOCAITON, null).equals(mCurrCity)) {
                        return;
                    } else {
                        mSharedPref.edit().putString(CurrentLocation.CURRENT_LOCAITON, mCurrCity).apply();
                        if (mToolbar != null) mToolbar.setTitle(mCurrCity);
                    }
                }
                mLocationClient.stop();
            } else {
                Toast.makeText(getContext(), R.string.location_failure, Toast.LENGTH_SHORT).show();
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

    private void initToolbar(View root) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        mToolbar = (Toolbar) root.findViewById(R.id.dest_choose_toolbar);
        appCompatActivity.setSupportActionBar(mToolbar);
        if (mSharedPref.getString(CurrentLocation.CURRENT_LOCAITON, null) != null) {
            String currCity = mSharedPref.getString(CurrentLocation.CURRENT_LOCAITON, null);
            mCurrCity = currCity;
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
            mSearchingIndicator.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        }
        if (mPoiList == null) mPoiList = new ArrayList<>();
        mPoiList.clear();
        new PreparePoiDataTask().execute(poiResult.getAllPoi());
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    private class PreparePoiDataTask extends AsyncTask<List<PoiInfo>,Void,Void>{

        @Override
        protected Void doInBackground(List<PoiInfo>... params) {
            List<PoiInfo> pois=params[0];
            for (PoiInfo info: pois) {
                if (info.type== PoiInfo.POITYPE.BUS_LINE||info.type==PoiInfo.POITYPE.SUBWAY_LINE){
                    continue;
                }
                Map<String, String> map = new HashMap<>();
                if (info.name != null&&info.address!=null) {
                    map.put(POI_NAME,info.name);
                    map.put(POI_ADDRESS, info.address);
                }
                mPoiList.add(map);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mSearchingIndicator.setVisibility(View.GONE);
            if (mPoiList.size()==0){
                mEmptyView.setVisibility(View.VISIBLE);
                mSearchResListView.setVisibility(View.GONE);
            }else{
                mEmptyView.setVisibility(View.GONE);
                mSearchResListView.setVisibility(View.VISIBLE);
            }
            mPoiListAdapter.notifyDataSetChanged();
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}

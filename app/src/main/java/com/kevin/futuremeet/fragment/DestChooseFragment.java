package com.kevin.futuremeet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.CityChooseActivity;
import com.kevin.futuremeet.beans.CurrentLocation;


public class DestChooseFragment extends Fragment {

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
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        initLocationOption();
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();
        return root;
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
     * set some option to the LocationClient,like Mode,or Address needed
     */
    private void initLocationOption() {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        locationClientOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(locationClientOption);
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

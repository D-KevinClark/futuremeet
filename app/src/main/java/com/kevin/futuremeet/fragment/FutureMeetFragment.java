package com.kevin.futuremeet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.utility.Config;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class FutureMeetFragment extends Fragment {
    private String mPoiName;
    private String mPoiAdrress;
    private double mPoiLat;
    private double mPoiLng;
    private Long mPoiTime;
    private Calendar mCalendar;

    TextView textView;


//    private OnFragmentInteractionListener mListener;

    public FutureMeetFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param poiInfo
     * @return
     */
    public static FutureMeetFragment newInstance(Bundle poiInfo) {
        FutureMeetFragment fragment = new FutureMeetFragment();
        fragment.setArguments(poiInfo);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCalendar = new GregorianCalendar();
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mPoiAdrress = bundle.getString(Config.BUNDLE_POI_ADDRESS);
            mPoiName = bundle.getString(Config.BUNDLE_POI_NAME);
            mPoiLat = Double.parseDouble(bundle.getString(Config.BUNDLE_POI_LAT));
            mPoiLng = Double.parseDouble(bundle.getString(Config.BUNDLE_POI_LNG));
            mPoiTime = bundle.getLong(Config.BUNDLE_POI_ARRIVE_TIME);
            mCalendar.setTimeInMillis(mPoiTime);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_future_meet, container, false);
        textView = (TextView) root.findViewById(R.id.location_text);
        textView.setText(mPoiAdrress+" "+mPoiName+" "+mPoiLat+" "+mPoiLng+" "+mPoiTime+" "
        +"date: "+mCalendar.get(Calendar.DAY_OF_MONTH)+"hout:min "+mCalendar.get(Calendar.HOUR_OF_DAY)+":"
        +mCalendar.get(Calendar.MINUTE));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
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
//
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

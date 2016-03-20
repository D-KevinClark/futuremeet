package com.kevin.futuremeet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.kevin.futuremeet.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FutureMeetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FutureMeetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FutureMeetFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LocationClient mLocationClient=null;
    TextView textView;


    /**
     * A listener for the location result,deal the Location Info here
     */
    private BDLocationListener myLocationListener=new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {

            Log.i("BaiduLocationApiDem", bdLocation.getCity());
                textView.setText(bdLocation.getCity());
            }else{
                Toast.makeText(getContext(), R.string.location_failure, Toast.LENGTH_SHORT).show();
            }
        }
    };

//    private OnFragmentInteractionListener mListener;

    public FutureMeetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FutureMeetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FutureMeetFragment newInstance(String param1, String param2) {
        FutureMeetFragment fragment = new FutureMeetFragment();
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
        mLocationClient = new LocationClient(getContext().getApplicationContext());
        mLocationClient.registerLocationListener(myLocationListener);
        initLocationOption();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_future_meet, container, false);
        textView = (TextView) root.findViewById(R.id.location_text);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.start();
    }

    /**
     * set some option to the LocationClient,like Mode,or Address needed
     */
    private void initLocationOption() {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        locationClientOption.setIsNeedAddress(true);
        mLocationClient.setLocOption(locationClientOption);
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

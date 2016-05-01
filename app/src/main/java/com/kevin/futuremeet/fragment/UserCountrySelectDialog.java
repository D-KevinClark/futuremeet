package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kevin.futuremeet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by carver on 2016/4/30.
 */
public class UserCountrySelectDialog extends DialogFragment {


    public static final String EXTRA_COUNTRY = "country";
    public static final String EXTRA_PROVINCE = "province";
    public static final String DIALOG_TITLE = "title";


    public static final String EXTRA_CLICKED_VIEWS_ID = "clicked_views_id";
    private int mClickedViewID;

    private View mCustomLayout;
    private ListView mListView;
    private Button mCancleButton;
    private TextView mTitleText;

    private ArrayList<String> mCountrysList = new ArrayList<>();
    private JSONArray mCountryJsa;
    private JSONObject mProvinceJsObj;
    private String mTitle;

    public UserCountrySelectDialog() {
    }


    public static UserCountrySelectDialog newInstance(JSONArray countryArray,
                                                      JSONObject provinceObj,String title,int clickedViewId) {
        UserCountrySelectDialog dialog = new UserCountrySelectDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_COUNTRY, countryArray.toString());
        bundle.putString(EXTRA_PROVINCE, provinceObj.toString());
        bundle.putInt(EXTRA_CLICKED_VIEWS_ID, clickedViewId);
        bundle.putString(DIALOG_TITLE, title);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String countryJsa = getArguments().getString(EXTRA_COUNTRY);
            String provinceJsobj = getArguments().getString(EXTRA_PROVINCE);
            mTitle = getArguments().getString(DIALOG_TITLE);
            mClickedViewID = getArguments().getInt(EXTRA_CLICKED_VIEWS_ID);
            try {
                mCountryJsa = new JSONArray(countryJsa);
                mProvinceJsObj = new JSONObject(provinceJsobj);
                for (int i = 0; i < mCountryJsa.length(); i++) {
                    mCountrysList.add(mCountryJsa.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View root = LayoutInflater.from(getContext()).inflate(R.layout.user_hometown_edit_dialog_layout, null);
        initViews(root);
        initEvent();

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, mCountrysList);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String country = mCountrysList.get(position);
                    JSONArray provins = mProvinceJsObj.getJSONArray(country);
                    if (provins.length() != 0) {
                        UserProvinceSelectDialog dialog =
                                UserProvinceSelectDialog.newInstance(provins, getString(R.string.area),mClickedViewID);
                        dialog.show(getActivity().getSupportFragmentManager(), null);
                        dismiss();
                    } else {
                        UserInfoEditTextDialog dialog = UserInfoEditTextDialog.newInstance(country, mTitle
                                , getString(R.string.input_area_info),mClickedViewID);
                        dialog.show(getActivity().getSupportFragmentManager(), null);
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(root);
        return builder.create();
    }

    private void initEvent() {
        mCancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mCustomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoEditTextDialog dialog = UserInfoEditTextDialog.newInstance(null, mTitle
                        , getString(R.string.input_area_info),mClickedViewID);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                dismiss();
            }
        });
    }

    private void initViews(View root) {
        mCustomLayout = root.findViewById(R.id.custom_layout);
        mListView = (ListView) root.findViewById(R.id.listview);
        mCancleButton = (Button) root.findViewById(R.id.cancel_button);
        mTitleText = (TextView) root.findViewById(R.id.title_textview);
        mTitleText.setText(mTitle);

    }

}

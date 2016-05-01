package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
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

import java.util.ArrayList;

/**
 * Created by carver on 2016/4/30.
 */
public class UserProvinceSelectDialog extends DialogFragment {

    public static final String EXTRA_PROVINCE = "province";

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CLICKED_VIEW_ID = "clicked_view_id";


    private View mCustomLayout;

    private ListView mListView;
    private Button mCancleButton;


    private String mTitle;
    private int mClickViewID;
    private ArrayList<String> mProvinceList = new ArrayList<>();
    private JSONArray mProvinceJsa;

    public UserProvinceSelectDialog() {
    }


    public static UserProvinceSelectDialog newInstance(JSONArray provinceObj,String dialogTitle,int clickeViewID) {
        UserProvinceSelectDialog dialog = new UserProvinceSelectDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PROVINCE, provinceObj.toString());
        bundle.putString(EXTRA_TITLE, dialogTitle);
        bundle.putInt(EXTRA_CLICKED_VIEW_ID, clickeViewID);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(EXTRA_TITLE);
            mClickViewID = getArguments().getInt(EXTRA_CLICKED_VIEW_ID);
            String provinceJsobj = getArguments().getString(EXTRA_PROVINCE);
            try {
                mProvinceJsa = new JSONArray(provinceJsobj);
                for (int i = 0; i < mProvinceJsa.length(); i++) {
                    mProvinceList.add(mProvinceJsa.getString(i));
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

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, mProvinceList);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String province = mProvinceList.get(position);
                dismiss();
                sendBroadcastBack(province);
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
                UserInfoEditTextDialog dialog = UserInfoEditTextDialog.newInstance(null, mTitle,
                        getString(R.string.input_area_info),mClickViewID);
                dialog.show(getActivity().getSupportFragmentManager(), null);
            }
        });
    }

    private void initViews(View root) {
        mCustomLayout = root.findViewById(R.id.custom_layout);
        mListView = (ListView) root.findViewById(R.id.listview);
        mCancleButton = (Button) root.findViewById(R.id.cancel_button);

        TextView titleText = (TextView) root.findViewById(R.id.title_textview);
        titleText.setText(mTitle);
    }

    private void sendBroadcastBack(String content) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(content);
        Intent intent = new Intent(MeFragment.EDIT_COMPLETED);
        intent.putExtra(MeFragment.PREFERS_TO_CHANGE, strings);
        intent.putExtra(MeFragment.ID_OF_VIEWS_TO_CHANGE, mClickViewID);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }
}

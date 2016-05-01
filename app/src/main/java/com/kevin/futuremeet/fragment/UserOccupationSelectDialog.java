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
public class UserOccupationSelectDialog extends DialogFragment {

    public static final String EXTRA_OCCUPATIONS= "occuptions";
    public static final String TITLE_TEXT = "title";
    public static final String EXTRA_CLICKED_VIEW_ID = "clickedViewId";




    private ListView mListView;
    private Button mCancleButton;
    private View mCustomView;
    private String mTitle;
    private TextView mTitleTextView;
    private int mClickedViewid;

    private ArrayList<String> mOccupationsList = new ArrayList<>();
    private JSONArray mOccupationsJsa;

    public UserOccupationSelectDialog() {
    }


    public static UserOccupationSelectDialog newInstance(JSONArray countryArray,
                                                         String dialogTitle, int clickedViewId) {

        UserOccupationSelectDialog dialog = new UserOccupationSelectDialog();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_OCCUPATIONS, countryArray.toString());
        bundle.putString(TITLE_TEXT, dialogTitle);
        bundle.putInt(EXTRA_CLICKED_VIEW_ID, clickedViewId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String countryJsa = getArguments().getString(EXTRA_OCCUPATIONS);
            mTitle = getArguments().getString(TITLE_TEXT);
            mClickedViewid = getArguments().getInt(EXTRA_CLICKED_VIEW_ID);
            try {
                mOccupationsJsa = new JSONArray(countryJsa);
                for (int i = 0; i < mOccupationsJsa.length(); i++) {
                    mOccupationsList.add(mOccupationsJsa.getString(i));
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

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, mOccupationsList);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String occupation = mOccupationsList.get(position);
                sendBroadcastBack(occupation);
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(root);
        return builder.create();
    }

    private void sendBroadcastBack(String content) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(content);
        Intent intent = new Intent(MeFragment.EDIT_COMPLETED);
        intent.putExtra(MeFragment.PREFERS_TO_CHANGE, strings);
        intent.putExtra(MeFragment.ID_OF_VIEWS_TO_CHANGE, mClickedViewid);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void initEvent() {
        mCancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoEditTextDialog dialog = UserInfoEditTextDialog.newInstance(null, mTitle,
                        getString(R.string.input_occupation_info),mClickedViewid);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                dismiss();
            }
        });
    }

    private void initViews(View root) {
        mListView = (ListView) root.findViewById(R.id.listview);
        mCancleButton = (Button) root.findViewById(R.id.cancel_button);
        mCustomView = root.findViewById(R.id.custom_layout);

        mTitleTextView = (TextView) root.findViewById(R.id.title_textview);
        mTitleTextView.setText(mTitle);

    }
}

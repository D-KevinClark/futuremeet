package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.kevin.futuremeet.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by carver on 2016/4/29.
 */
public class UserPreferInfoDialog extends DialogFragment {

    private static final String EXTRA_CURRENT_USER_PREFER_IN_THIS_CATEGORY = "user_prefer_in_this_category";
    private static final String EXTRA_STRING_JSON_ARRAY_TO_DISPLAY = "json_array_to_dispaly";
    private static final String EXTRA_CLICKED_VIEW_ID = "clickedViewID";

    private static final String TITLE_TEXT = "title";
    private int mClickedViewId;


    private String mTitle;


    private ArrayList<String> mPreferOptions = new ArrayList<>();
    private ArrayList<String> mUserSelectedPrefer;


    private View mCustomLayout;
    private ListView mListView;
    private Button mCancleButton;
    private Button mConfirmButton;
    private TextView mTitleTextView;

    public UserPreferInfoDialog() {
    }

    /**
     * @param selectedPerfers the options has been selected in the according scope
     * @param jsonArray       the total options that can be selected
     * @return
     */
    public static UserPreferInfoDialog newInstance(ArrayList<String> selectedPerfers
            , JSONArray jsonArray, String dialogTitle,int clickedViewID) {
        UserPreferInfoDialog dialog = new UserPreferInfoDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(EXTRA_CURRENT_USER_PREFER_IN_THIS_CATEGORY, selectedPerfers);
        bundle.putString(EXTRA_STRING_JSON_ARRAY_TO_DISPLAY, jsonArray.toString());
        bundle.putInt(EXTRA_CLICKED_VIEW_ID, clickedViewID);
        bundle.putString(TITLE_TEXT, dialogTitle);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE_TEXT);
            mClickedViewId = getArguments().getInt(EXTRA_CLICKED_VIEW_ID);
            ArrayList<String> userPrefersList = getArguments().getStringArrayList(EXTRA_CURRENT_USER_PREFER_IN_THIS_CATEGORY);
            if (userPrefersList != null) {
                mUserSelectedPrefer = new ArrayList<>(userPrefersList);
            } else {
                mUserSelectedPrefer = new ArrayList<>();
            }

            String prefersJsaString = getArguments().getString(EXTRA_STRING_JSON_ARRAY_TO_DISPLAY);
            try {
                JSONArray prefersJsa = new JSONArray(prefersJsaString);
                for (int i = 0; i < prefersJsa.length(); i++) {
                    mPreferOptions.add(prefersJsa.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View root = LayoutInflater.from(getContext()).inflate(R.layout.user_prefer_detail_info_dialog_layout, null);
        initViews(root);
        initEvents();

        //make the selected item always at the begin
        for (String s : mUserSelectedPrefer) {
            if (mPreferOptions.contains(s)) {
                mPreferOptions.remove(s);
            }
        }
        mPreferOptions.addAll(0, mUserSelectedPrefer);

        PrefersListAdapter adapter = new PrefersListAdapter(getContext(),
                R.layout.user_prefer_detail_info_list_item, mPreferOptions);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String option = mPreferOptions.get(position);
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
                if (mUserSelectedPrefer.contains(option)) {
                    mUserSelectedPrefer.remove(option);
                    if (cb.isChecked()) {
                        cb.setChecked(false);
                    }
                } else {
                    mUserSelectedPrefer.add(option);
                    if (!cb.isChecked()) {
                        cb.setChecked(true);
                    }
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(root);
        return builder.create();
    }

    private void initViews(View root) {
        mCustomLayout = root.findViewById(R.id.custom_layout);
        mListView = (ListView) root.findViewById(R.id.listview);
        mCancleButton = (Button) root.findViewById(R.id.cancel_button);
        mConfirmButton = (Button) root.findViewById(R.id.confirm_button);
        mTitleTextView = (TextView) root.findViewById(R.id.title_textview);
        mTitleTextView.setText(mTitle);

    }

    private void initEvents() {
        mCancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCompleteBroadcast();
                dismiss();
            }
        });

        mCustomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoEditTextDialog dialog = UserInfoEditTextDialog.newInstance(null, mTitle,
                        "",mClickedViewId);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                dismiss();
            }
        });
    }

    private void sendCompleteBroadcast() {
        Intent intent = new Intent(MeFragment.EDIT_COMPLETED);
        intent.putExtra(MeFragment.PREFERS_TO_CHANGE, mUserSelectedPrefer);
        intent.putExtra(MeFragment.ID_OF_VIEWS_TO_CHANGE, mClickedViewId);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    class PrefersListAdapter extends ArrayAdapter<String> {

        private ArrayList<String> mPerfersList;
        private Context mContext;

        public PrefersListAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            mPerfersList = objects;
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = LayoutInflater.from(mContext);

            if (view == null) {
                view = inflater.inflate(R.layout.user_prefer_detail_info_list_item, null);
            }

            String content = mPerfersList.get(position);
            if (content != null) {
                TextView textView = (TextView) view.findViewById(R.id.prefer_item_text);
                textView.setText(content);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                if (mUserSelectedPrefer.contains(content)) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
            }

            return view;
        }
    }

}

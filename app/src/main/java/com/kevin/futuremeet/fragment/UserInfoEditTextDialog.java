package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kevin.futuremeet.R;

import java.util.ArrayList;

/**
 * Created by carver on 2016/4/30.
 */
public class UserInfoEditTextDialog extends DialogFragment {

    public static final String EXTRA_APPEND_TEXT = "appended_text";
    public static final String EXTRA_CLICKED_VIEWS_ID = "clicked_views_id";
    public static final String DIALOG_TITLE = "title";
    public static final String TEXT_HINT = "hint";

    private String mTitle;
    private String mHint;
    private int mClickedViewID;

    private String mAppendedText;

    public UserInfoEditTextDialog() {

    }

    public static UserInfoEditTextDialog newInstance(
            String appendText, @NonNull String title, @NonNull String hint, @NonNull int clickedViewsID) {

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_APPEND_TEXT, appendText);
        bundle.putString(DIALOG_TITLE, title);
        bundle.putString(TEXT_HINT, hint);
        bundle.putInt(EXTRA_CLICKED_VIEWS_ID, clickedViewsID);
        UserInfoEditTextDialog dialog = new UserInfoEditTextDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAppendedText = getArguments().getString(EXTRA_APPEND_TEXT);
            mTitle = getArguments().getString(DIALOG_TITLE);
            mHint = getArguments().getString(TEXT_HINT);
            mClickedViewID = getArguments().getInt(EXTRA_CLICKED_VIEWS_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.user_info_edittext_dialog_layout, null);

        final EditText editText = (EditText) root.findViewById(R.id.edittext);
        Button confirmBtn = (Button) root.findViewById(R.id.confirm_button);
        Button cancleBtn = (Button) root.findViewById(R.id.cancel_button);
        TextView titleTextView = (TextView) root.findViewById(R.id.title_textview);

        editText.setHint(mHint);
        titleTextView.setText(mTitle);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resultStr;
                String content = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(mAppendedText)) {
                    resultStr = content + "  " + mAppendedText;
                } else {
                    resultStr = content;
                }
                sendBroadcastBack(resultStr);
                dismiss();
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        intent.putExtra(MeFragment.ID_OF_VIEWS_TO_CHANGE, mClickedViewID);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }
}

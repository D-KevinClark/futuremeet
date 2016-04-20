package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.kevin.futuremeet.MainActivity;
import com.kevin.futuremeet.R;

public class VerifyCodeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText mVerifyCodeText;
    private Button mResendBotton;
    private Button mConfirmButton;

    private int mTimeCounter;

    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            mTimerHandler.sendEmptyMessage(0);
        }
    };

    private Handler mTimerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mTimeCounter++;
            if (mTimeCounter < 60) {
                mResendBotton.setText(getString(R.string.resend) + "(" + (mTimeCounter < 10 ? "0" + mTimeCounter : mTimeCounter) + ")");
                mTimerHandler.postDelayed(mTimerRunnable, 1000);
            } else {
                mResendBotton.setText(getString(R.string.resend));
                mResendBotton.setEnabled(true);
            }

        }
    };


    public VerifyCodeFragment() {
        // Required empty public constructor
    }


    public static VerifyCodeFragment newInstance(String param1, String param2) {
        VerifyCodeFragment fragment = new VerifyCodeFragment();
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

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vrify_code, container, false);
        mResendBotton = (Button) view.findViewById(R.id.resend_button);
        mVerifyCodeText = (EditText) view.findViewById(R.id.verify_code_edittext);
        mConfirmButton = (Button) view.findViewById(R.id.confirm_button);

        mVerifyCodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mConfirmButton.setEnabled(false);
                } else {
                    mConfirmButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.waiting));
                progressDialog.show();

                String verifyCode = mVerifyCodeText.getText().toString();

                AVUser.verifyMobilePhoneInBackground(verifyCode, new AVMobilePhoneVerifyCallback() {
                    @Override
                    public void done(AVException e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (e == null) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            mTimerHandler.removeCallbacks(mTimerRunnable);
                            mResendBotton.setEnabled(true);
                            mResendBotton.setText(getString(R.string.resend));
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.verification_failed)
                                    .setMessage(R.string.please_check_and_try_again)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
            }
        });

        mResendBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.waiting));
                progressDialog.show();

                AVUser.requestMobilePhoneVerifyInBackground(
                        AVUser.getCurrentUser().getMobilePhoneNumber(),
                        new RequestMobileCodeCallback() {
                            @Override
                            public void done(AVException e) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                if (e == null) {
                                    beginTimer();
                                    mResendBotton.setEnabled(false);
                                } else {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle(R.string.send_failed)
                                            .setMessage(R.string.please_try_later)
                                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }
                        }
                );
            }
        });


        beginTimer();
        return view;
    }

    private void beginTimer() {
        mTimeCounter = 0;
        mTimerHandler.sendEmptyMessage(0);
    }

}

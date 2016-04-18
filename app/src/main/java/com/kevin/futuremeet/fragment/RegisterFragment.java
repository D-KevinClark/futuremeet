package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.ClipImageActivity;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.utility.Util;

import java.io.ByteArrayOutputStream;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG_REGISTER_FRAGMENT = RegisterFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int SELECT_AVATAR_REQUEST_CODE = 100;

//    private OnFragmentInteractionListener mListener;

    private FrameLayout mAvatarLayout;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mPhoneNumEditText;
    private Button mRegisterButton;

    private String mAvatarPath = null;

    public RegisterFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        initView(root);
        initEvent();
        return root;
    }

    /**
     * init all the view needed
     *
     * @param view
     */
    private void initView(View view) {
        mAvatarLayout = (FrameLayout) view.findViewById(R.id.avatar_layout);
        mUsernameEditText = (EditText) view.findViewById(R.id.nickname_edittext);
        mPasswordEditText = (EditText) view.findViewById(R.id.password_edittext);
        mPhoneNumEditText = (EditText) view.findViewById(R.id.phonenumber_edittext);
        mRegisterButton = (Button) view.findViewById(R.id.register_button);


    }


    /**
     * add event handler to the Views
     */
    private void initEvent() {
        mAvatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAvatarIamge();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIsAllInfoSetProperly()) {
                    String userName = mUsernameEditText.getText().toString();
                    String passWord = mPasswordEditText.getText().toString();
                    String phone = mPhoneNumEditText.getText().toString();
                    new UserRegisterTask(getContext()).execute(mAvatarPath,userName,passWord,phone);
                }
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeRegisterButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mUsernameEditText.addTextChangedListener(watcher);
        mPasswordEditText.addTextChangedListener(watcher);
        mPhoneNumEditText.addTextChangedListener(watcher);
    }


    /**
     * change the Register Button's state according to weather the EditText are all set
     */
    private void changeRegisterButtonState() {
        String userName = mUsernameEditText.getText().toString();
        String passWord = mPasswordEditText.getText().toString();
        String phone = mPhoneNumEditText.getText().toString();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord) || TextUtils.isEmpty(phone)) {
            mRegisterButton.setEnabled(false);
        } else {
            mRegisterButton.setEnabled(true);
        }
    }


    /**
     * Execute the Register action
     */
    class UserRegisterTask extends AsyncTask<String, Void, Integer> {
        private ProgressDialog dialog;
        private Context mContext;
        private static final int REGISTER_FAILED = 0;
        private static final int REGISTER_SUCCESS = 1;

        UserRegisterTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.please_wait));
        }

        @Override
        protected Integer doInBackground(String... params) {
            String imagePath = params[0];
            ByteArrayOutputStream outputStream = Util.decodeImageFileForUpload(imagePath, mContext);
            final AVFile avFile = new AVFile(UserContract.AVATAR, outputStream.toByteArray());
            try {
                avFile.save();
            } catch (AVException e) {
                return REGISTER_FAILED;
            }
            AVObject user = new AVObject(UserContract.CLASS_NAME);
            user.put(UserContract.USERNAME, params[1]);
            user.put(UserContract.PASSWORD, params[2]);
            user.put(UserContract.PHONE_NUMEBR, params[3]);
            user.put(UserContract.AVATAR, avFile);
            try {
                user.save();
            } catch (AVException e) {
                return REGISTER_FAILED;
            }
            return REGISTER_SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer status) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (status == REGISTER_FAILED) {
                Toast.makeText(getContext(), R.string.register_fail_please_check_network, Toast.LENGTH_SHORT).show();
            } else {
                // TODO: 2016/4/13 lunch the verify code fragment
                Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * check to see if all the user info is set properly
     *
     * @return
     */
    private boolean checkIsAllInfoSetProperly() {
        String username = mUsernameEditText.getText().toString();
        String phoneNum = mPhoneNumEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String errorMsg = null;
        if (mAvatarPath == null) {
            errorMsg = getString(R.string.please_set_avatar);
        } else if (TextUtils.isEmpty(username)) {
            errorMsg = getString(R.string.please_input_nicknane);
        } else if (username.contains(" ") || username.contains("@")) {
            errorMsg = getString(R.string.username_format_error_msg);
        } else if (TextUtils.isEmpty(phoneNum)) {
            errorMsg = getString(R.string.please_input_phonenumber);
        } else if (!TextUtils.isDigitsOnly(phoneNum)) {
            errorMsg = getString(R.string.please_check_phonenumber_format);
        } else if (TextUtils.isEmpty(password)) {
            errorMsg = getString(R.string.please_input_phone_number);
        } else if (!Util.isOnlyDigitAndLetter(password) || password.length() < 5) {
            errorMsg = getString(R.string.the_password_format_statement);
        }

        if (errorMsg != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.register_fail)
                    .setMessage(errorMsg)
                    .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return false;
        }
        return true;
    }


    /**
     * launch the selector to chose a avator iamge
     */
    private void selectAvatarIamge() {

        FunctionConfig config = new FunctionConfig.Builder()
                .setMutiSelectMaxSize(1)
                .setEnableCamera(true)
                .build();
        GalleryFinal.openGalleryMuti(SELECT_AVATAR_REQUEST_CODE, config, new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                String path = resultList.get(0).getPhotoPath();
                openAvatarCropper(path);
            }

            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                Toast.makeText(getContext(), R.string.phote_select_fail_please_retry, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * open a editor to crop the image you passed
     *
     * @param originalPath
     */
    private void openAvatarCropper(String originalPath) {
        Intent intent = new Intent(getContext(), ClipImageActivity.class);
        intent.putExtra(ClipImageActivity.ORIGIN_IMAGE_PATH, originalPath);
        startActivityForResult(intent, ClipImageActivity.REQUEST_CLIP_IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ClipImageActivity.REQUEST_CLIP_IMAGE_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            mAvatarPath = data.getStringExtra(ClipImageActivity.CLIP_IMAGE_PATH);
            if (mAvatarPath != null) {
                int size = getResources().getDimensionPixelSize(R.dimen.avatar_select_image_size);
                Bitmap bitmap = Util.decodeSampledBitmapFromFile(mAvatarPath, size, size);
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                imageView.setImageBitmap(bitmap);
                mAvatarLayout.removeAllViews();
                mAvatarLayout.addView(imageView);
            } else {
                Toast.makeText(getContext(), R.string.avatar_selection_failed, Toast.LENGTH_SHORT).show();
            }
        }
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

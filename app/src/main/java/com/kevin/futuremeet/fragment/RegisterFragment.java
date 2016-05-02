package com.kevin.futuremeet.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.ClipImageActivity;
import com.kevin.futuremeet.beans.UserBasicInfoContract;
import com.kevin.futuremeet.beans.UserContract;
import com.kevin.futuremeet.beans.UserDetailContract;
import com.kevin.futuremeet.utility.Util;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class RegisterFragment extends Fragment implements View.OnClickListener {
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
    private RadioButton mMaleRadio;
    private RadioButton mFemaleRadio;
    private View mBirthdayLayout;
    private TextView mBirthDayTextView;

    private String mAvatarPath = null;
    private int mGender = 0;//1---male  2----female
    private Date mBirthday = null;

    private OnRegisteListener mListener;


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

        mFemaleRadio = (RadioButton) view.findViewById(R.id.female_radio);
        mMaleRadio = (RadioButton) view.findViewById(R.id.male_radio);

        mBirthdayLayout = view.findViewById(R.id.birthday_layout);
        mBirthDayTextView = (TextView) view.findViewById(R.id.birthday_textview);
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
                    new UserRegisterTask(getContext()).execute(
                            mAvatarPath,
                            userName,
                            passWord,
                            phone
                    );
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


        mFemaleRadio.setOnClickListener(this);
        mMaleRadio.setOnClickListener(this);

        mBirthdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                int currentYear = calendar.get(Calendar.YEAR);
                                if (currentYear - year < 12 || currentYear - year > 100) {
                                    Toast.makeText(getContext(), R.string.birthyear_restriction, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                calendar.set(year, monthOfYear, dayOfMonth);
                                mBirthday = calendar.getTime();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                mBirthDayTextView.setText(dateFormat.format(mBirthday));
                            }
                        }, 1990, 0, 1).show();
            }
        });
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

    @Override
    public void onClick(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        if (checked)
            switch (v.getId()) {
                case R.id.male_radio:
                    mGender = 1;
                    break;
                case R.id.female_radio:
                    mGender = 2;
                    break;
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
        private static final int PHONENUMBER_INVALID = 2;
        private static final int PHONENUMBER_ALREADY_TAKEN = 3;
        private static final int USERNAME_ALERADY_TAKKEN = 4;

        UserRegisterTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.please_wait));
        }

        @Override
        protected Integer doInBackground(String... params) {

            AVUser user;
            // if case the follow image and detail info upload failed and user click the register button again
            if (AVUser.getCurrentUser() == null) {
                //save the user account
                user = new AVUser();
                user.setUsername(params[1]);
                user.setPassword(params[2]);
                user.setMobilePhoneNumber(params[3]);
                try {
                    user.save();
                } catch (AVException e) {
                    if (e.getCode() == AVException.INVALID_PHONE_NUMBER) {
                        return PHONENUMBER_INVALID;
                    } else if (e.getCode() == AVException.USER_MOBILE_PHONENUMBER_TAKEN) {
                        return PHONENUMBER_ALREADY_TAKEN;
                    } else if (e.getCode() == AVException.USERNAME_TAKEN) {
                        return USERNAME_ALERADY_TAKKEN;
                    } else {
                        return REGISTER_FAILED;
                    }
                }
            } else {
                user = AVUser.getCurrentUser();
            }


            //save the avatar
            String imagePath = params[0];
            ByteArrayOutputStream outputStream = Util.decodeImageFileForUpload(imagePath, mContext);
            final AVFile avatarFile = new AVFile(UserContract.AVATAR, outputStream.toByteArray());

            try {
                avatarFile.save();
            } catch (AVException e) {
                return REGISTER_FAILED;
            }

            //save the user detail info
            AVObject basicInfo = new AVObject(UserBasicInfoContract.CLASS_NAME);
            basicInfo.put(UserBasicInfoContract.USERNAME, user.getUsername());
            basicInfo.put(UserBasicInfoContract.GENDER, mGender);
            basicInfo.put(UserBasicInfoContract.AGE, mBirthday);
            basicInfo.put(UserBasicInfoContract.AVATAR, avatarFile);

            AVObject detailInfo = AVObject.create(UserDetailContract.CLASS_NAME);
            basicInfo.put(UserBasicInfoContract.DETAIL_INFO, detailInfo);

            //only the this user hava the write access to this user detail info record
            AVACL avacl = new AVACL();
            avacl.setWriteAccess(user, true);
            avacl.setPublicReadAccess(true);
            detailInfo.setACL(avacl);
            basicInfo.setACL(avacl);

            try {
                basicInfo.save();
            } catch (AVException e) {
                return REGISTER_FAILED;
            }

            user.put(UserContract.USER_BASIC_INFO, basicInfo);
            user.put(UserContract.USER_DETAIL_INFO, detailInfo);
            user.put(UserContract.AVATAR, avatarFile);
            user.put(UserContract.AGE, mBirthday);
            user.put(UserContract.GENDER, mGender);

            try {
                user.save();
            } catch (AVException e) {
                return REGISTER_FAILED;
            }
            return REGISTER_SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer status) {
            String errorMsg = null;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (status == PHONENUMBER_ALREADY_TAKEN) {
                errorMsg = getString(R.string.phonenumber_has_been_taken);
            } else if (status == PHONENUMBER_INVALID) {
                errorMsg = getString(R.string.invalid_phonenumber);
            } else if (status == USERNAME_ALERADY_TAKKEN) {
                errorMsg = getString(R.string.username_taken);
            } else if (status == REGISTER_FAILED) {
                errorMsg = getString(R.string.please_check_network);
            }

            if (errorMsg != null) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.register_failed))
                        .setMessage(errorMsg)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else if (mListener != null) {
                mListener.onResisteSuccess();
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
        } else if (mGender == 0) {
            errorMsg = getString(R.string.please_input_gender);
        } else if (mBirthday == null) {
            errorMsg = getString(R.string.please_input_birthday);
        }

        if (errorMsg != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.register_failed)
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisteListener) {
            mListener = (OnRegisteListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnRegisteListener {
        void onResisteSuccess();
    }
}

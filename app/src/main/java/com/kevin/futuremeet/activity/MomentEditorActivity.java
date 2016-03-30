package com.kevin.futuremeet.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.utility.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class MomentEditorActivity extends AppCompatActivity {

    private EditText mWordsEdit;
    private TextView mWordsCount;
    private View mAddPicView;
    private LinearLayout mPicContainerLayout;

    private ArrayList<String> mImages = new ArrayList<>();

    private static final int GALLERY_OPEN_REQUEST_CODE = 100;
    private static final int GALLERY_MITI_PIC_MAX_SIZE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_editor);
        initView();
        initEvent();
    }

    private void initEvent() {
        //add a text watcher for the moment words edittext , to show the current count of the words
        mWordsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mWordsCount.setText(s.length() + "/100");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAddPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionConfig config = new FunctionConfig.Builder()
                        .setMutiSelectMaxSize(GALLERY_MITI_PIC_MAX_SIZE)
                        .setEnableCamera(true)
                        .setEnablePreview(true)
                        .build();
                GalleryFinal.openGalleryMuti(GALLERY_OPEN_REQUEST_CODE, config, mOnGalleryResultCallback);
            }
        });
    }

    private GalleryFinal.OnHanlderResultCallback mOnGalleryResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            //get the size of the imageview
            int width = getResources().getDimensionPixelSize(R.dimen.moment_pic_layout_size);
            int height = getResources().getDimensionPixelSize(R.dimen.moment_pic_layout_size);
            LayoutInflater inflater = LayoutInflater.from(MomentEditorActivity.this);
            int tag=0;//the tag used to find the pic layout when user want to delete it
            for (PhotoInfo info : resultList) {
                final File file = new File(info.getPhotoPath());
                if (file.exists()) {
                    String imagePath=file.getAbsolutePath();
                    mImages.add(imagePath);
                    Bitmap bitmap = Util.decodeSampledBitmapFromFile(imagePath, width, height);
                    final RelativeLayout view = (RelativeLayout)
                            inflater.inflate(R.layout.moment_editor_pic_item, null, false);
                    final int finalTag = tag;
                    view.setTag(tag++);
                    ImageView picImage = (ImageView) view.findViewById(R.id.pic_image);
                    final ImageView deleteImage = (ImageView) view.findViewById(R.id.moment_pic_delete_image);
                    deleteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View deletePic=view.findViewWithTag(finalTag);
                            deletePic.setVisibility(View.GONE);
                            mImages.remove(finalTag);
                        }
                    });
                    picImage.setImageBitmap(bitmap);
                    mPicContainerLayout.addView(view, width, height);
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    /**
     * init all the needed view
     */
    private void initView() {
        mWordsEdit = (EditText) findViewById(R.id.moment_words_edit);
        mWordsCount = (TextView) findViewById(R.id.words_count_text);
        mAddPicView = findViewById(R.id.add_pic_layout);
        mPicContainerLayout = (LinearLayout) findViewById(R.id.pics_container);
    }
}

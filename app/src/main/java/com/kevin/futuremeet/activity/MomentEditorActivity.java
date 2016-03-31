package com.kevin.futuremeet.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.background.PublishMomentIntentService;
import com.kevin.futuremeet.utility.Config;
import com.kevin.futuremeet.utility.Util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class MomentEditorActivity extends AppCompatActivity {

    private EditText mWordsEdit;
    private TextView mWordsCount;
    private View mAddPicView;
    private LinearLayout mPicContainerLayout;

    private Toolbar mToolBar;


    private HashMap<String, Bitmap.Config> mSelectedImageConfigInfo = new HashMap<>();


    private static final int GALLERY_OPEN_REQUEST_CODE = 100;
    private static final int GALLERY_MITI_PIC_MAX_SIZE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_editor);

        IntentFilter intentFilter = new IntentFilter(PublishMomentIntentService.STATUS_REPORT_ACTION);
        MomentUploadStatusReportReceiver reportReceiver = new MomentUploadStatusReportReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(reportReceiver, intentFilter);

        initView();
        initEvent();
    }

    private class MomentUploadStatusReportReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int status = intent.getIntExtra(PublishMomentIntentService.EXTRA_STATUS,0);
                if (status == PublishMomentIntentService.UPLOAD_SUCCESS) {
                    Toast.makeText(MomentEditorActivity.this, R.string.moment_publish_success, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MomentEditorActivity.this, R.string.moment_publish_fail, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.moment_editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.publish:
                String content = mWordsEdit.getText().toString();
                if (TextUtils.isEmpty(content) && mSelectedImageConfigInfo.size() == 0) {
                    Toast.makeText(MomentEditorActivity.this, R.string.please_edit_content_first, Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }
                PublishMomentIntentService.startPublishMoment(this,
                        mWordsEdit.getText().toString(), mSelectedImageConfigInfo);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                int mitiPicNum=GALLERY_MITI_PIC_MAX_SIZE-mSelectedImageConfigInfo.size();
                FunctionConfig config = new FunctionConfig.Builder()
                        .setMutiSelectMaxSize(mitiPicNum)
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
            if (resultList.size()+mSelectedImageConfigInfo.size() == GALLERY_MITI_PIC_MAX_SIZE) {
                mAddPicView.setVisibility(View.GONE);
            }
            //get the size of the imageview
            final int width = getResources().getDimensionPixelSize(R.dimen.moment_pic_layout_size);
            final int height = getResources().getDimensionPixelSize(R.dimen.moment_pic_layout_size);
            LayoutInflater inflater = LayoutInflater.from(MomentEditorActivity.this);
            for (PhotoInfo info : resultList) {
                final File file = new File(info.getPhotoPath());
                if (file.exists()) {
                    String imagePath = file.getAbsolutePath();
                    //find the relevant views
                    final RelativeLayout view = (RelativeLayout)
                            inflater.inflate(R.layout.moment_editor_pic_item, null, false);
                    view.setTag(imagePath);
                    ImageView picImage = (ImageView) view.findViewById(R.id.pic_image);
                    final ImageView deleteImage = (ImageView) view.findViewById(R.id.moment_pic_delete_image);
                    //set the tag and the click event handler for the case that user delete the selected pics
                    deleteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View deletePic = (View) v.getParent();
                            String keyString = (String) deletePic.getTag();
                            mSelectedImageConfigInfo.remove(keyString);
                            mPicContainerLayout.removeView(deletePic);
                            if (mAddPicView.getVisibility() == View.GONE) {
                                mAddPicView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    //load the scaled bitmap to the imageview
                    BitmapWorkTask bitmapWorkTask = new BitmapWorkTask(picImage);
                    bitmapWorkTask.execute(imagePath);
                    //add the imageview to the parent layout to show them
                    mPicContainerLayout.addView(view, width, height);

                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    class BitmapWorkTask extends AsyncTask<String, Void, Bitmap> {
        private WeakReference<ImageView> imageViewWeakReference;
        private String data;

        public BitmapWorkTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }


        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            final int width = getResources().getDimensionPixelSize(R.dimen.moment_pic_layout_size);
            final int height = getResources().getDimensionPixelSize(R.dimen.moment_pic_layout_size);
            return Util.decodeSampledBitmapFromFile(data, width, height);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && imageViewWeakReference != null) {
                final ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    mSelectedImageConfigInfo.put(data, bitmap.getConfig());
                }
            }
        }
    }

    /**
     * init all the needed view
     */
    private void initView() {
        mWordsEdit = (EditText) findViewById(R.id.moment_words_edit);
        mWordsCount = (TextView) findViewById(R.id.words_count_text);
        mAddPicView = findViewById(R.id.add_pic_layout);
        mPicContainerLayout = (LinearLayout) findViewById(R.id.pics_container);
        //set up the toolbar
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.publish_moment);
    }
}

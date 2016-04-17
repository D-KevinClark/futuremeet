package com.kevin.futuremeet.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.custom.ClipImageLayout;
import com.kevin.futuremeet.utility.Config;
import com.kevin.futuremeet.utility.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClipImageActivity extends AppCompatActivity {

    private ClipImageLayout mClipImageLayout;
    private Button mOkButton;
    private Button mCancelButton;

    public static final String ORIGIN_IMAGE_PATH = "original_image_path";
    public static final String CLIP_IMAGE_PATH = "clip_image_path";
    public static final int REQUEST_CLIP_IMAGE_CODE = 110;


    private String mOrigImagePath;
    private String mClippeddImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_image);

        initViews();
        initEvent();

        //get the path of the image
        mOrigImagePath = getIntent().getStringExtra(ORIGIN_IMAGE_PATH);
        //get the size of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        //get a bitmap effectively according to the screen size
        Bitmap bitmap = Util.decodeSampledBitmapFromFile(mOrigImagePath, widthPixels, heightPixels);
        //set the image to the layout to show it for clip operation
        mClipImageLayout.setImageBitmap(bitmap);

    }

    private void initEvent() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipBitmap();
            }
        });
    }

    /**
     * clip the bitmap and save it
     */
    private void clipBitmap() {
        Bitmap bitmap = mClipImageLayout.clip();
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "未遇");
        mClippeddImagePath = mediaStorageDir.getAbsolutePath()+File.separator+ Config.AVATAR_IMAGE_LOCAL_STORAGE_NAEM;
        saveImage(bitmap, mClippeddImagePath);
        Intent intent = new Intent();
        intent.putExtra(CLIP_IMAGE_PATH, mClippeddImagePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * save the clipped image to local
     *
     * @param bitmap
     * @param clippeddImagePath
     */
    private void saveImage(Bitmap bitmap, String clippeddImagePath) {
        File file = new File(clippeddImagePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Util.scanImageFile(this, clippeddImagePath);
    }

    private void initViews() {
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.clip_image_layout);
        mOkButton = (Button) findViewById(R.id.ok_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
    }
}

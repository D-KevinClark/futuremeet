package com.kevin.futuremeet.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;

// TODO: 2016/4/9 this is just a test activity which should be deleted eventually
public class TestActivity extends AppCompatActivity {

    private Button mButton;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mButton = (Button) findViewById(R.id.download_bt);
        mImageView = (ImageView) findViewById(R.id.imageview);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(TestActivity.this)
                        .load("http://ac-q0uvxfqo.clouddn.com/lCopIltOoQMV3Z57KUyDtIZpdwBGHm1QBxACRS0f")
                        .asBitmap()
                        .into(mImageView);
            }
        });
    }
}

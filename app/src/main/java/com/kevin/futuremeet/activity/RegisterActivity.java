package com.kevin.futuremeet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.fragment.RegisterFragment;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToobar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToobar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToobar);
        getSupportActionBar().setTitle(R.string.register);

        if (savedInstanceState == null) {
            RegisterFragment registerFragment = RegisterFragment.newInstance(null, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container,registerFragment, RegisterFragment.TAG_REGISTER_FRAGMENT)
                    .commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package com.kevin.futuremeet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.fragment.RegisterFragment;
import com.kevin.futuremeet.fragment.VerifyCodeFragment;

public class RegisterActivity extends AppCompatActivity implements RegisterFragment.OnRegisterListener {

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
                    .add(R.id.fragment_container, registerFragment, RegisterFragment.TAG_REGISTER_FRAGMENT)
                    .commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * register succeed, show the verify code confirm fragment
     */
    @Override
    public void onRegisterSuccess() {
        VerifyCodeFragment verifyCodeFragment = VerifyCodeFragment.newInstance(null, null);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, verifyCodeFragment).commit();
    }
}

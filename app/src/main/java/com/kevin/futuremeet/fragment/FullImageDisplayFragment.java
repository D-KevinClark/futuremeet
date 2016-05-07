package com.kevin.futuremeet.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kevin.futuremeet.R;

public class FullImageDisplayFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "image_url";


    private String mImageUrl;

    private ImageView mImageView;

    public FullImageDisplayFragment() {
        // Required empty public constructor
    }

    public static FullImageDisplayFragment newInstance(String imageUrl) {
        FullImageDisplayFragment fragment = new FullImageDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUrl = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_full_image_display, container, false);
        mImageView = (ImageView) rootViewGroup.findViewById(R.id.image);
        Glide.with(this)
                .load(mImageUrl)
                .asBitmap()
                .into(mImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return rootViewGroup;
    }

}

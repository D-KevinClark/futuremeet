package com.kevin.futuremeet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.MyCommentedMomentActivity;
import com.kevin.futuremeet.activity.MyCommentingMomentActivity;
import com.kevin.futuremeet.activity.MyLikedMomentActivity;
import com.kevin.futuremeet.activity.MyLikingMomentActivity;

import io.rong.imkit.RongIM;

public class NewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mLikeLayout;
    private View mCommentLayout;
    private View mMyLikeLayout;
    private View mMyCommentLayout;
    private View mConversationLayout;


    private ListView mListView;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
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
        View root=inflater.inflate(R.layout.fragment_news, container, false);
        initViews(root);
        initEvents();
        return root;
    }

    private void initEvents() {
        mMyLikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyLikingMomentActivity.class);
                startActivity(intent);
            }
        });
        mLikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyLikedMomentActivity.class);
                startActivity(intent);
            }
        });

        mMyCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyCommentingMomentActivity.class);
                startActivity(intent);
            }
        });

        mCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyCommentedMomentActivity.class);
                startActivity(intent);
            }
        });

        mConversationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startConversationList(getContext());
                }
            }
        });
    }

    private void initViews(View root) {
        mLikeLayout = root.findViewById(R.id.like_layout);
        mCommentLayout = root.findViewById(R.id.comment_layout);
        mMyLikeLayout = root.findViewById(R.id.my_like_layout);
        mMyCommentLayout = root.findViewById(R.id.my_comment_layout);
        mConversationLayout = root.findViewById(R.id.conversation_layout);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.message);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);

    }

}

package com.kevin.futuremeet.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVGeoPoint;
import com.baidu.location.Poi;
import com.kevin.futuremeet.R;
import com.kevin.futuremeet.adapter.PoiPageFilterAdapter;
import com.kevin.futuremeet.background.PublishMomentIntentService;
import com.kevin.futuremeet.beans.FuturePoiBean;
import com.kevin.futuremeet.database.FuturePoiDBContract;
import com.kevin.futuremeet.database.FuturePoiDBHelper;
import com.kevin.futuremeet.utility.Util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class MomentEditorActivity extends AppCompatActivity {

    private EditText mWordsEdit;
    private TextView mWordsCount;
    private View mAddPicView;
    private LinearLayout mPicContainerLayout;
    private ArrayList<AVGeoPoint> mSelectedFuturePois = new ArrayList<>();


    private ListView mFuturePoiListView;
    private ArrayList<FuturePoiBean> mFuturePoiList = new ArrayList<>();


    private Toolbar mToolBar;


    //a map to record the selected pic info , for the delete operation,
    // the key and value is the same which will be the image file path
    private HashMap<String, String> mSelectedImageConfigInfo = new HashMap<>();


    private static final int GALLERY_OPEN_REQUEST_CODE = 100;
    public static final int GALLERY_MITI_PIC_MAX_SIZE = 3;

    private LinearLayout mRootLayout;
    private int mStatusBarHeight;
    private boolean mIsKeyBoardShowed;


    // 获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            // 应用可以显示的区域。此处包括应用占用的区域，
            // 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
            Rect r = new Rect();
            mRootLayout.getWindowVisibleDisplayFrame(r);

            // 屏幕高度。这个高度不含虚拟按键的高度
            int screenHeight = mRootLayout.getRootView().getHeight();

            int heightDiff = screenHeight - (r.bottom - r.top);

            // 在不显示软键盘时，heightDiff等于状态栏的高度
            // 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
            // 所以heightDiff大于状态栏高度时表示软键盘出现了，
            // 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
//            if(keyboardHeight == 0 && heightDiff > statusBarHeight){
//                keyboardHeight = heightDiff - statusBarHeight;
//            }

            if (mIsKeyBoardShowed) {
                // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
                // 说明这时软键盘已经收起
                if (heightDiff <= mStatusBarHeight) {
                    mIsKeyBoardShowed = false;
                    onHideKeyboard();
                }
            } else {
                // 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
                // 说明这时软键盘已经弹出
                if (heightDiff > mStatusBarHeight) {
                    mIsKeyBoardShowed = true;
                    onShowKeyboard();
                }
            }
        }
    };

    private void onShowKeyboard() {
    }

    private void onHideKeyboard() {
        restoreSelectionState();
    }


    /**
     * read future poi data form database and set them to a list that back up the page filter listview adapter
     */
    private void preparePoiData() {
        FuturePoiDBHelper helper = new FuturePoiDBHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        String[] projection = {
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_NAME,
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ADDRESS,
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LNG,
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LAT,
                FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ARRIVE_TIME
        };

        String sortOrder =
                FuturePoiDBContract.FuturePoiEntry._ID + " DESC";

        Cursor c = database.query(
                FuturePoiDBContract.FuturePoiEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        while (c.moveToNext()) {
            FuturePoiBean poiBean = new FuturePoiBean();
            String poiName = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_NAME));
            String poiAdress = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ADDRESS));
            String poiLng = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LNG));
            String poiLat = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_LAT));
            String poiArriveTime = c.getString(c.getColumnIndex(FuturePoiDBContract.FuturePoiEntry.COLUMN_NAME_POI_ARRIVE_TIME));

            AVGeoPoint avGeoPoint = new AVGeoPoint(Double.valueOf(poiLat), Double.valueOf(poiLng));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            Date date = null;
            try {
                date = format.parse(poiArriveTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            poiBean.setPoiName(poiName);
            poiBean.setPoiAddress(poiAdress);
            poiBean.setAvGeoPoint(avGeoPoint);
            poiBean.setArriveTime(date);

            mFuturePoiList.add(poiBean);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_editor);
        preparePoiData();
        initView();
        initEvent();
        mStatusBarHeight = getStatusBarHeight(getApplicationContext());
        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mRootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
        } else {
            mRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
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
                Log.i("mytag", mSelectedFuturePois.size() + " ");

                if (TextUtils.isEmpty(content) && mSelectedImageConfigInfo.size() == 0) {
                    Toast.makeText(MomentEditorActivity.this, R.string.please_edit_content_first, Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }
                if (mSelectedFuturePois.size() == 0) {
                    Toast.makeText(MomentEditorActivity.this, R.string.please_choose_future_poi, Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }
                if (!Util.isNetworkAvailabel(MomentEditorActivity.this)) {
                    Toast.makeText(MomentEditorActivity.this, R.string.please_check_network, Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }

                PublishMomentIntentService.startPublishMoment(this,
                        mWordsEdit.getText().toString(), mSelectedImageConfigInfo, mSelectedFuturePois);
                Util.closeTheSoftKeyboard(this.getCurrentFocus(), MomentEditorActivity.this);
                finish();
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
                Util.closeTheSoftKeyboard(v, MomentEditorActivity.this);
                int mitiPicNum = GALLERY_MITI_PIC_MAX_SIZE - mSelectedImageConfigInfo.size();
                FunctionConfig config = new FunctionConfig.Builder()
                        .setMutiSelectMaxSize(mitiPicNum)
                        .setEnableCamera(true)
                        .setEnablePreview(true)
                        .build();
                GalleryFinal.openGalleryMuti(GALLERY_OPEN_REQUEST_CODE, config, mOnGalleryResultCallback);
            }
        });


        FuturePoiListAdapter adapter = new FuturePoiListAdapter();
        mFuturePoiListView.setAdapter(adapter);

        mFuturePoiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView tickView = (ImageView) view.findViewById(R.id.tick_symbol);
                if (tickView.getVisibility() != View.VISIBLE) {
                    tickView.setVisibility(View.VISIBLE);
                    mSelectedFuturePois.add(mFuturePoiList.get(position).getAvGeoPoint());
                    mSelectedPosition.add(position);
                } else {
                    tickView.setVisibility(View.INVISIBLE);
                    mSelectedFuturePois.remove(mFuturePoiList.get(position).getAvGeoPoint());
                    mSelectedPosition.remove(position);
                }
            }
        });

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Util.closeTheSoftKeyboard(v,MomentEditorActivity.this);
                return false;
            }
        });
    }


    /**
     * the code below is because when the soft keyboard show out, it hide the future poi listview,
     * and when is hide ,my listview's item's selected state lost, which I don't know why
     */
    private Set<Integer> mSelectedPosition = new HashSet<>();

    private void restoreSelectionState() {
        int size = mFuturePoiList.size();
        for (int i = 0; i < size; i++) {
            if (mSelectedPosition.contains(i)) {
                View view = mFuturePoiListView.getChildAt(i);
                ImageView tickView = (ImageView) view.findViewById(R.id.tick_symbol);
                tickView.setVisibility(View.VISIBLE);
            }
        }
    }


    private int mSelectedPicNum;//record the number of the pic that user has been selected
    private ProgressDialog mProgressDialog;//show this before the pic that be selected has been processed


    private GalleryFinal.OnHanlderResultCallback mOnGalleryResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            //if it the max number of pics that user can pic, dismiss the picker launcher imageview
            if (resultList.size() + mSelectedImageConfigInfo.size() == GALLERY_MITI_PIC_MAX_SIZE) {
                mAddPicView.setVisibility(View.GONE);
            }

            mSelectedPicNum = resultList.size();
            //get the size of the imageview
            final int width = getResources().getDimensionPixelSize(R.dimen.moment_pic_layout_size);
            final int height = getResources().getDimensionPixelSize(R.dimen.moment_pic_layout_size);
            LayoutInflater inflater = LayoutInflater.from(MomentEditorActivity.this);
            for (PhotoInfo info : resultList) {
                final File file = new File(info.getPhotoPath());
                if (file.exists()) {
                    final String imagePath = file.getAbsolutePath();
                    //find the relevant views
                    final RelativeLayout view = (RelativeLayout)
                            inflater.inflate(R.layout.moment_editor_pic_item, null, false);
                    view.setTag(imagePath);
                    final ImageView picImage = (ImageView) view.findViewById(R.id.pic_image);
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
                    mPicContainerLayout.addView(view, width, height);

                }
            }
            mProgressDialog = new ProgressDialog(MomentEditorActivity.this);
            mProgressDialog.setMessage("正在处理图片...");
            mProgressDialog.show();

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
                    mSelectedImageConfigInfo.put(data, data);
                }
            }
            mSelectedPicNum--;
            if (mSelectedPicNum == 0 && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
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
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.closeTheSoftKeyboard(v, MomentEditorActivity.this);
                finish();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.publish_moment);

        mFuturePoiListView = (ListView) findViewById(R.id.future_poi_listview);


        mRootLayout = (LinearLayout) findViewById(R.id.root_layout);

    }


    /**
     * adapter to show the future poi
     */
    class FuturePoiListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFuturePoiList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(MomentEditorActivity.this);
            View view = inflater.inflate(R.layout.moment_editor_poi_list_item, null);
            Calendar calendar = Calendar.getInstance();
            int nowDay = calendar.get(Calendar.DAY_OF_YEAR);
            FuturePoiBean futurerPoi = mFuturePoiList.get(position);
            Date date = futurerPoi.getArriveTime();
            calendar.setTime(date);
            int futureDay = calendar.get(Calendar.DAY_OF_YEAR);
            String firstLine;
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            String hourStr = hour > 9 ? hour + "" : "0" + hour;
            int minute = calendar.get(Calendar.MINUTE);
            String minuteStr = minute > 9 ? minute + "" : "0" + minute;

            if (futureDay == nowDay) {
                firstLine = getString(R.string.today) + " " + hourStr + ":" + minuteStr;
            } else {
                firstLine = getString(R.string.tomorrow) + " " + hourStr + ":" + minuteStr;
            }
            String secondline = futurerPoi.getPoiName();
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            text1.setText(firstLine);
            text2.setText(secondline);
            return view;
        }
    }
}

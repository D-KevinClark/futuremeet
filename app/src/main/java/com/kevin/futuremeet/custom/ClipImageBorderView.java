package com.kevin.futuremeet.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by carver on 2016/4/14.
 */
public class ClipImageBorderView extends View {

    private int mHorizontalPadding;

    private int mVerticalPadding;

    //the width of the Clip Bord
    private int mWidth;

    //the color of the Clip Bord
    private int mBorderColor = Color.parseColor("#FFFFFF");
    // the Width of the border of the Clip bord
    private int mBorderWidth = 1;

    private Paint mPaint;

    public ClipImageBorderView(Context context) {
        this(context, null);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mBorderWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
                        .getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //calculate of the width of the clip bord
        mWidth = getWidth() - 2 * mHorizontalPadding;
        // calculate the vertical padding of  of the clip bord
        mVerticalPadding = (getHeight() - mWidth) / 2;
        mPaint.setColor(Color.parseColor("#aa000000"));
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);

        canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(),
                getHeight(), mPaint);

        canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding,
                mVerticalPadding, mPaint);

        canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding,
                getWidth() - mHorizontalPadding, getHeight(), mPaint);

        //draw the border of the clip bord
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()
                - mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);

    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }
}

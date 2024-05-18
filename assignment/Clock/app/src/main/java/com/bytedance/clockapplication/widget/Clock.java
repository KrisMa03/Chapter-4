package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;


import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.Date;

public class Clock extends View {

    private static final String TAG = Clock.class.getSimpleName();
    private static final int FULL_ANGLE = 360;
    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;
    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;
    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;
    private static final int AM = 0;
    private static final int RIGHT_ANGLE = 90;

    private float panelRadius = 200.0f;
    private float hourPointerLength;
    private float minutePointerLength;
    private float secondPointerLength;
    private float unitDegree = (float) (6 * Math.PI / 180);

    private int mWidth, mCenterX, mCenterY, mRadius;
    private int degreesColor;
    private Paint mNeedlePaint;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            mHandler.postDelayed(this, 1000);
        }
    };

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.degreesColor = DEFAULT_PRIMARY_COLOR;
        mNeedlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNeedlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mNeedlePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mWidth = Math.min(getHeight(), getWidth());
        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;
        panelRadius = mRadius;
        hourPointerLength = panelRadius - 400;
        minutePointerLength = panelRadius - 250;
        secondPointerLength = panelRadius - 150;

        drawDegrees(canvas);
        drawHoursValues(canvas);
        drawNeedles(canvas);

        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);
    }

    private void drawDegrees(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6) {
            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0) {
                paint.setAlpha(CUSTOM_ALPHA);
            } else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));
            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    private void drawHoursValues(Canvas canvas) {
        // Default Color: hoursValuesColor
    }

    private void drawNeedles(final Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        int nowHours = now.getHours();
        int nowMinutes = now.getMinutes();
        int nowSeconds = now.getSeconds();

        drawPointer(canvas, 2, nowSeconds);
        drawPointer(canvas, 1, nowMinutes);
        int part = nowMinutes / 12;
        drawPointer(canvas, 0, 5 * nowHours + part);
    }

    private void drawPointer(Canvas canvas, int pointerType, int value) {
        float degree;
        float[] pointerHeadXY = new float[2];
        mNeedlePaint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);

        switch (pointerType) {
            case 0:
                degree = value * unitDegree;
                mNeedlePaint.setColor(Color.WHITE);
                pointerHeadXY = getPointerHeadXY(hourPointerLength, degree);
                break;
            case 1:
                degree = value * unitDegree;
                mNeedlePaint.setColor(Color.BLUE);
                pointerHeadXY = getPointerHeadXY(minutePointerLength, degree);
                break;
            case 2:
                degree = value * unitDegree;
                mNeedlePaint.setColor(Color.BLACK);
                pointerHeadXY = getPointerHeadXY(secondPointerLength, degree);
                break;
        }

        canvas.drawLine(mCenterX, mCenterY, pointerHeadXY[0], pointerHeadXY[1], mNeedlePaint);
    }

    private float[] getPointerHeadXY(float pointerLength, float degree) {
        float[] xy = new float[2];
        xy[0] = (float) (mCenterX + pointerLength * Math.sin(degree));
        xy[1] = (float) (mCenterY - pointerLength * Math.cos(degree));
        return xy;
    }
}
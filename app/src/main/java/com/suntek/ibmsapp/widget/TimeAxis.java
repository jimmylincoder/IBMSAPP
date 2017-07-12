package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeAxis extends View
{
    private static final float INTERVAL_LENGTH = 0.08f;// 一小格的长度，单位dp
    private static final int BIG_TIME_INTERVAL = 1800;// 30分钟一大格
    private static final int SMALL_TIME_INTERVAL = 1;// 1秒钟一小格 ,小刻度线不画
    private static final int TICK_MARK_HEIGHT = 50;// 大刻度线高度
    private static final int TEXT_SIZE = 12;// 文字大小

    private float mDensity;// 屏幕密度 dp*mDensity=px
    private TimeAlgorithm mValue;// 当前刻度指示时间
    private TimeAlgorithm mMaxValue;// 刻度最大阀值
    private TimeAlgorithm mMinValue;// 刻度最小阀值
    private float mLastX, mMove;
    private float mWidth, mHeight;
    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private OnValueChangeListener mListener;
    private SQLiteDatabase db = null;
    private long st;
    private long et;
    private String mDate;
    private boolean isEnabled;
    private ViewPager vg;

    public interface OnValueChangeListener
    {
        public void onValueChange(TimeAlgorithm _value);

        public void onStartValueChange(TimeAlgorithm _value);

        public void onStopValueChange(TimeAlgorithm _value);
    }

    public TimeAxis(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        if (isInEditMode())
            return;
        mScroller = new Scroller(context);
        mDensity = context.getResources().getDisplayMetrics().density;
        mMinVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
        mMaxValue = new TimeAlgorithm("23:59:59");
        mMinValue = new TimeAlgorithm("00:00:00");
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        mDate = sdFormatter.format(nowTime);
        sdFormatter = new SimpleDateFormat("HH:mm:ss");
        mValue = new TimeAlgorithm(sdFormatter.format(nowTime));
        isEnabled = true;
        setBackgroundColor(Color.parseColor("#88424242"));
    }

    public void setVg(ViewPager _vg)
    {
        vg = _vg;
    }

    public void setEnabled(boolean arg)
    {
        isEnabled = arg;
    }

    public boolean is_Enabled()
    {
        return isEnabled;
    }

    public void setDate(String _date)
    {
        mDate = _date;
    }

    public void setDataBase(SQLiteDatabase _db)
    {
        db = _db;
        postInvalidate();
    }

    public void drawVideo(Canvas canvas)
    {
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1);
        Cursor cursor;
        RectF r;
        if (st < et)
        {
            cursor = db.rawQuery("select * from VideoList where minValue < "
                    + et + " and maxValue > " + st, null);
            while (cursor.moveToNext())
            {
                linePaint.setColor(Color.parseColor("#66FF4040"));
                r = new RectF((cursor.getLong(1) - st) * INTERVAL_LENGTH
                        * mDensity, 0, (cursor.getLong(2) - st)
                        * INTERVAL_LENGTH * mDensity, this.getHeight());
                canvas.drawRoundRect(r, 0, 0, linePaint);
                linePaint.setColor(Color.parseColor("#88424242"));
                canvas.drawLine((cursor.getLong(1) - st) * INTERVAL_LENGTH
                        * mDensity, 0, (cursor.getLong(1) - st)
                        * INTERVAL_LENGTH * mDensity, getHeight(), linePaint);
            }
        }
        else
        {
            cursor = db.rawQuery(
                    "select * from VideoList where (minValue < "
                            + mMaxValue.getSec(mDate) + " and maxValue > " + st
                            + ")" + " or (minValue <" + et + " and maxValue>"
                            + mMinValue.getSec(mDate) + ")", null);
            while (cursor.moveToNext())
            {
                if (cursor.getLong(2) >= st)
                {
                    linePaint.setColor(Color.parseColor("#66FF4040"));
                    r = new RectF((cursor.getLong(1) - st) * INTERVAL_LENGTH
                            * mDensity, 0, (cursor.getLong(2) - st)
                            * INTERVAL_LENGTH * mDensity, this.getHeight());
                    canvas.drawRoundRect(r, 0, 0, linePaint);
                    linePaint.setColor(Color.parseColor("#88424242"));
                    canvas.drawLine((cursor.getLong(1) - st) * INTERVAL_LENGTH
                                    * mDensity, 0, (cursor.getLong(1) - st)
                                    * INTERVAL_LENGTH * mDensity, getHeight(),
                            linePaint);
                }
                else
                {
                    linePaint.setColor(Color.parseColor("#66FF4040"));
                    Long value = cursor.getLong(1) - mMinValue.getSec(mDate);
                    value = value > 0 ? value : 0;
                    r = new RectF(
                            (mMaxValue.getSec(mDate) - st + value)
                                    * INTERVAL_LENGTH * mDensity,
                            0,
                            ((mMaxValue.getSec(mDate) - st) + cursor.getLong(2) - mMinValue
                                    .getSec(mDate))
                                    * INTERVAL_LENGTH
                                    * mDensity, getHeight());
                    canvas.drawRoundRect(r, 0, 0, linePaint);
                    linePaint.setColor(Color.parseColor("#88424242"));
                    canvas.drawLine((mMaxValue.getSec(mDate) - st + value)
                                    * INTERVAL_LENGTH * mDensity, 0,
                            (mMaxValue.getSec(mDate) - st + value)
                                    * INTERVAL_LENGTH * mDensity, getHeight(),
                            linePaint);
                }
            }
        }
        cursor.close();
        cursor = null;
    }

    public void setOnValueChangeListener(OnValueChangeListener listener)
    {
        mListener = listener;
    }

    public TimeAlgorithm getValue()
    {
        return mValue;
    }

    public Long getSec()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        Long sec = 0l;
        try
        {
            date = sdf.parse(mDate + " " + mValue.getData());
            Calendar calendarObj = Calendar.getInstance();
            calendarObj.setTime(date);
            sec = calendarObj.getTimeInMillis() / 1000;
            return sec;
        } catch (ParseException e)
        {
            return sec;
        }
    }

    public void setValue(TimeAlgorithm _value)
    {
        mValue = _value;
        int sec = Math.round(mWidth / (2 * INTERVAL_LENGTH * mDensity));
        st = mValue.addOrSub(-sec).getSec(mDate);
        et = mValue.addOrSub(sec).getSec(mDate);
        postInvalidate();
    }

    public void setValue(Long miliSec)
    {
        Date nowTime = new Date(miliSec);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("HH:mm:ss");
        mValue = new TimeAlgorithm(sdFormatter.format(nowTime));
        int sec = Math.round(mWidth / (2 * INTERVAL_LENGTH * mDensity));
        st = mValue.addOrSub(-sec).getSec(mDate);
        et = mValue.addOrSub(sec).getSec(mDate);
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom)
    {
        mWidth = getWidth();
        mHeight = getHeight();
        int sec = Math.round(mWidth / (2 * INTERVAL_LENGTH * mDensity));
        st = mValue.addOrSub(-sec).getSec(mDate);
        et = mValue.addOrSub(sec).getSec(mDate);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        try
        {
            if (db != null)
            {
                drawVideo(canvas);
            }
            drawScaleLine(canvas);
            drawMiddleLine(canvas);
        } catch (ParseException e)
        {

        }
    }

    private void drawScaleLine(Canvas canvas) throws ParseException
    {
        canvas.save();
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.rgb(255, 0, 0));
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.rgb(255, 255, 0));
        textPaint.setTextSize(TEXT_SIZE * mDensity * 0.7f);
        float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);
        int numSize = String.valueOf("00:00:00").length();
        int mod = mValue.mod(BIG_TIME_INTERVAL);
        if (mod < BIG_TIME_INTERVAL / 2)
            canvas.drawText(String.valueOf(mValue.getData()), mWidth / 2 + 6,
                    getHeight() / 2, textPaint);
        else
            canvas.drawText(String.valueOf(mValue.getData()), mWidth / 2
                    - numSize * textWidth, getHeight() / 2, textPaint);
        textPaint.setTextSize(TEXT_SIZE * mDensity);
        textPaint.setColor(Color.rgb(255, 0, 0));
        float drawCount = 0;
        for (int i = 0; drawCount < mWidth; i++)
        {

            xPosition = (mWidth / 2 - mMove) + ((1800 - mod) + 1800 * i)
                    * INTERVAL_LENGTH * mDensity;
            if (xPosition + getPaddingRight() < mWidth)
            {
                canvas.drawLine(xPosition, getPaddingTop(), xPosition, mDensity
                        * TICK_MARK_HEIGHT, linePaint);

                canvas.drawText(
                        String.valueOf(mValue.addOrSub(
                                SMALL_TIME_INTERVAL * i * 1800 + 1800 - mod)
                                .getData()), xPosition - (textWidth * numSize)
                                / 2, getHeight() - textWidth, textPaint);

            }
            xPosition = (mWidth / 2 - mMove) - (mod + 1800 * i)
                    * INTERVAL_LENGTH * mDensity;
            if (xPosition > getPaddingLeft())
            {
                canvas.drawLine(xPosition, getPaddingTop(), xPosition, mDensity
                        * TICK_MARK_HEIGHT, linePaint);

                canvas.drawText(String.valueOf(mValue.addOrSub(
                        -SMALL_TIME_INTERVAL * 1800 * i - mod).getData()),
                        xPosition - (textWidth * numSize) / 2, getHeight()
                                - textWidth, textPaint);

            }
            drawCount += 2 * INTERVAL_LENGTH * mDensity * 1800;
        }
        canvas.restore();
    }

    private void drawMiddleLine(Canvas canvas)
    {
        // int gap = 12, shadow = 6;
        // String color = "#66999999";
        // int indexWidth = 2, indexTitleWidth = 24, indexTitleHight = 10;

        canvas.save();
        Paint redPaint = new Paint();
        redPaint.setStrokeWidth(2);
        redPaint.setColor(Color.rgb(255, 255, 0));
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, redPaint);
        Paint ovalPaint = new Paint();
        ovalPaint.setColor(Color.rgb(255, 255, 0));
        ovalPaint.setStrokeWidth(24);
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, 10, ovalPaint);
        canvas.drawLine(mWidth / 2, mHeight - 10, mWidth / 2, mHeight,
                ovalPaint);
        // Paint shadowPaint = new Paint();
        // shadowPaint.setStrokeWidth(shadow);
        // shadowPaint.setColor(Color.parseColor(color));
        // canvas.drawLine(mWidth / 2 + gap, 0, mWidth / 2 + gap, mHeight,
        // shadowPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        float xPosition = event.getX();
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();

        mVelocityTracker.addMovement(event);

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                Log.e("()()", "ti onTouchEvent down*****************");
              //  vg.requestDisallowInterceptTouchEvent(true);
                mListener.onStartValueChange(mValue);
                mScroller.forceFinished(true);
                mLastX = xPosition;
                mMove = 0;

                return true;
            case MotionEvent.ACTION_MOVE:
                Log.e("()()", "ti onTouchEvent move*****************");
               // vg.requestDisallowInterceptTouchEvent(true);
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
                Log.e("()()", "ti onTouchEvent up*****************");
            case MotionEvent.ACTION_CANCEL:
                Log.e("()()", "ti onTouchEvent cancle*****************");
              //  vg.requestDisallowInterceptTouchEvent(false);
                countVelocityTracker(event);
                countMoveEnd();
                break;
//   return false;
            default:
                break;
        }
        mLastX = xPosition;
        return super.onTouchEvent(event);
    }

    // 松开手控件滑动起来,fling()需要postInvalidate()
    private void countVelocityTracker(MotionEvent event)
    {
        mVelocityTracker.computeCurrentVelocity(1000, 1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity)
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
        else if (isEnabled)
            mListener.onStopValueChange(mValue);
    }

    private void changeMoveAndValue()
    {
        float tValue = mMove / (INTERVAL_LENGTH * mDensity);
        if (Math.abs(tValue) > 0)
        {
            mValue = mValue.addOrSub(Math.round(tValue * SMALL_TIME_INTERVAL));
            int sec = Math.round(mWidth / (2 * INTERVAL_LENGTH * mDensity));
            st = mValue.addOrSub(-sec).getSec(mDate);
            et = mValue.addOrSub(sec).getSec(mDate);
            mMove -= tValue * INTERVAL_LENGTH * mDensity;
            notifyValueChange();
        }
        postInvalidate();
    }

    private void notifyValueChange()
    {
        if (null != mListener)
            if (mScroller.isFinished())
                mListener.onValueChange(mValue);
    }

    private void countMoveEnd()
    {
        float roundMove = mMove / (INTERVAL_LENGTH * mDensity);
        if (Math.abs(roundMove) > 0)
        {
            mValue = mValue.addOrSub(Math
                    .round(roundMove * SMALL_TIME_INTERVAL));
            int sec = Math.round(mWidth / (2 * INTERVAL_LENGTH * mDensity));
            st = mValue.addOrSub(-sec).getSec(mDate);
            et = mValue.addOrSub(sec).getSec(mDate);
        }
        mLastX = 0;
        mMove = 0;
        notifyValueChange();
        postInvalidate();
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (mScroller.computeScrollOffset())
        {
            if (mScroller.getCurrX() == mScroller.getFinalX())
            {
                countMoveEnd();
                if (isEnabled)
                    mListener.onStopValueChange(mValue);
            }
            else
            {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }
}



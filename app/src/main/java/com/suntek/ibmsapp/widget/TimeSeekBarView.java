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
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.suntek.ibmsapp.model.RecordItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * SeekBar
 * <p>
 * 执行流程
 * <p>
 * 1.首先实例化对象调用构造函数，初始化需要的实例
 * <p>
 * 2.onMeasure()测量控件大小(本类无)---->onLayout()重新放置控制-->onDraw()进行控件界面绘制
 * <p>
 * 3.onDraw()中执行步骤
 * <p>
 * a.绘制视频已有录像背景
 * b.绘制刻度
 * c.绘制seekbar的中间线
 *
 * @author jimmy
 */
public class TimeSeekBarView extends View
{
    // 一小格的长度，单位dp
    private static final float INTERVAL_LENGTH = 0.08f;
    // 30分钟一大格
    private static final int BIG_TIME_INTERVAL = 360;
    // 1秒钟一小格 ,小刻度线不画
    private static final int SMALL_TIME_INTERVAL = 1;
    // 大刻度线高度
    private static final int TICK_MARK_HEIGHT = 60;
    // 文字大小
    private static final int TEXT_SIZE = 12;
    // 屏幕密度 dp*screenDensity=px
    private float screenDensity;
    // 当前刻度指示时间
    private TimeAlgorithm nowTimeValue;
    // 刻度最大阀值
    private TimeAlgorithm maxValue;
    // 刻度最小阀值
    private TimeAlgorithm minValue;
    // 刚按下X坐标和移动距离
    private float beginPointX, moveDistance;

    //View的宽高
    private float viewWidth, viewHeight;
    //最小速度值
    private int minVelocity;
    //滚动
    private Scroller scroller;
    private VelocityTracker velocityTracker;
    //滑动监听器
    private OnValueChangeListener listener;
    private SQLiteDatabase db = null;
    //开始时间
    private long startTime;
    //结束时间
    private long endTime;
    //当前时间字符串格式
    private String nowDate;
    //是否可滑动
    private boolean isEnabled;

    //录像时间段
    private List<RecordItem> recordList;

    /**
     * 滑动时时间变化监听器
     */
    public interface OnValueChangeListener
    {
        void onValueChange(Date date);

        void onStartValueChange(Date date);

        void onStopValueChange(Date date);
    }

    public TimeSeekBarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        //是否可编辑
        if (isInEditMode())
            return;

        //用于横向滚动
        scroller = new Scroller(context);

        //获取屏幕密度
        screenDensity = context.getResources().getDisplayMetrics().density;

        //获得允许执行一个fling手势动作的最小速度值
        minVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        //刻度最大值
        maxValue = new TimeAlgorithm("23:59:59");
        //刻度最小值
        minValue = new TimeAlgorithm("00:00:00");

        //当前时间
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        nowDate = sdFormatter.format(nowTime);
        sdFormatter = new SimpleDateFormat("HH:mm:ss");
        nowTimeValue = new TimeAlgorithm(sdFormatter.format(nowTime));

        //是否可滑动
        isEnabled = true;

        //设置背景色
        setBackgroundColor(Color.parseColor("#302922"));
    }

    public void setEnabled(boolean arg)
    {
        isEnabled = arg;
    }

    public boolean is_Enabled()
    {
        return isEnabled;
    }

    public void setNowDate(String _date)
    {
        nowDate = _date;
    }

    public void setDataBase(SQLiteDatabase _db)
    {
        db = _db;
        postInvalidate();
    }

    /**
     * 绘制录像时间段
     *
     * @param canvas
     */
    public void drawVideo(Canvas canvas)
    {
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1);
        RectF r;
        if (startTime < endTime)
        {
            for (RecordItem record : recordList)
            {
                long recordBeginTime = record.getStartTime();
                long recordEndTime = record.getEndTime();
                linePaint.setColor(Color.parseColor("#673320"));
                r = new RectF((recordBeginTime - startTime) * INTERVAL_LENGTH
                        * screenDensity, 0, (recordEndTime - startTime)
                        * INTERVAL_LENGTH * screenDensity, this.getHeight());
                canvas.drawRoundRect(r, 0, 0, linePaint);
                linePaint.setColor(Color.parseColor("#88424242"));
                canvas.drawLine((recordBeginTime - startTime) * INTERVAL_LENGTH
                        * screenDensity, 0, (recordBeginTime - startTime)
                        * INTERVAL_LENGTH * screenDensity, getHeight(), linePaint);

            }
        }
        else
        {
            for (RecordItem record : recordList)
            {
                long recordBeginTime = record.getStartTime();
                long recordEndTime = record.getEndTime();
                if (recordEndTime >= startTime)
                {
                    linePaint.setColor(Color.parseColor("#673320"));
                    r = new RectF((recordBeginTime - startTime) * INTERVAL_LENGTH
                            * screenDensity, 0, (recordEndTime - startTime)
                            * INTERVAL_LENGTH * screenDensity, this.getHeight());
                    canvas.drawRoundRect(r, 0, 0, linePaint);
                    linePaint.setColor(Color.parseColor("#88424242"));
                    canvas.drawLine((recordBeginTime - startTime) * INTERVAL_LENGTH
                                    * screenDensity, 0, (recordBeginTime - startTime)
                                    * INTERVAL_LENGTH * screenDensity, getHeight(),
                            linePaint);
                }
                else
                {
                    linePaint.setColor(Color.parseColor("#673320"));
                    Long value = recordBeginTime - minValue.getSec(nowDate);
                    value = value > 0 ? value : 0;
                    r = new RectF(
                            (maxValue.getSec(nowDate) - startTime + value)
                                    * INTERVAL_LENGTH * screenDensity,
                            0,
                            ((maxValue.getSec(nowDate) - startTime) + recordEndTime - minValue
                                    .getSec(nowDate))
                                    * INTERVAL_LENGTH
                                    * screenDensity, getHeight());
                    canvas.drawRoundRect(r, 0, 0, linePaint);
                    linePaint.setColor(Color.parseColor("#88424242"));
                    canvas.drawLine((maxValue.getSec(nowDate) - startTime + value)
                                    * INTERVAL_LENGTH * screenDensity, 0,
                            (maxValue.getSec(nowDate) - startTime + value)
                                    * INTERVAL_LENGTH * screenDensity, getHeight(),
                            linePaint);
                }
            }
        }
    }

    public void setOnValueChangeListener(OnValueChangeListener listener)
    {
        this.listener = listener;
    }

    public TimeAlgorithm getNowTimeValue()
    {
        return nowTimeValue;
    }

    public Long getSec()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        Long sec = 0l;
        try
        {
            date = sdf.parse(this.nowDate + " " + nowTimeValue.getData());
            Calendar calendarObj = Calendar.getInstance();
            calendarObj.setTime(date);
            sec = calendarObj.getTimeInMillis() / 1000;
            return sec;
        } catch (ParseException e)
        {
            return sec;
        }
    }

    /**
     * 设置当前时间点
     *
     * @param _value
     */
    public void setNowTimeValue(TimeAlgorithm _value)
    {
        nowTimeValue = _value;
        int sec = Math.round(viewWidth / (2 * INTERVAL_LENGTH * screenDensity));
        startTime = nowTimeValue.addOrSub(-sec).getSec(nowDate);
        endTime = nowTimeValue.addOrSub(sec).getSec(nowDate);
        postInvalidate();
    }

    public void setValue(Long miliSec)
    {
        //获取当前时间值
        Date nowTime = new Date(miliSec);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        nowTimeValue = new TimeAlgorithm(sdFormatter.format(nowTime));
        nowDate = dateFormat.format(new Date(miliSec));
        //当前界面的时间长度
        int sec = Math.round(viewWidth / (2 * INTERVAL_LENGTH * screenDensity));

        //界面开始的时间点
        startTime = nowTimeValue.addOrSub(-sec).getSec(nowDate);
        //界面结束的时间点
        endTime = nowTimeValue.addOrSub(sec).getSec(nowDate);
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom)
    {
        //获得当前view的宽度
        viewWidth = getWidth();
        //获得当前view的高度
        viewHeight = getHeight();

        //获得秒数
        int sec = Math.round(viewWidth / (2 * INTERVAL_LENGTH * screenDensity));

        //获得开始时间
        startTime = nowTimeValue.addOrSub(-sec).getSec(nowDate);
        //获得结束时间
        endTime = nowTimeValue.addOrSub(sec).getSec(nowDate);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        try
        {
            if (recordList != null)
            {
                drawVideo(canvas);
            }
            drawScaleLine(canvas);
            drawMiddleLine(canvas);
        } catch (ParseException e)
        {

        }
    }

    /**
     * 绘制刻度线
     *
     * @param canvas
     * @throws ParseException
     */
    private void drawScaleLine(Canvas canvas) throws ParseException
    {
        canvas.save();
        //绘制刻度画笔
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(4);
        //红色
        linePaint.setColor(Color.rgb(62, 54, 47));

        //绘制时间画笔
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        //黄色
        textPaint.setColor(Color.rgb(150, 150, 150));
        textPaint.setTextSize(TEXT_SIZE * screenDensity * 0.7f);

        //x位置，字体宽度
        float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);
        //字数大小
        int numSize = String.valueOf("00:00:00").length();
        //算出刻度数(余数在1-1799)  (分钟 * 60 + 秒数）
        int mod = nowTimeValue.mod(BIG_TIME_INTERVAL);

        //如果大于刻度间中间值绘制于左侧，小于则画右侧
        if (mod < BIG_TIME_INTERVAL / 2)
            canvas.drawText(String.valueOf(nowTimeValue.getData()), viewWidth / 2 + 6,
                    getHeight() / 2, textPaint);
        else
            canvas.drawText(String.valueOf(nowTimeValue.getData()), viewWidth / 2
                    - numSize * textWidth, getHeight() / 2, textPaint);

        //绘制刻度线和时间
        //设置字体大小
        textPaint.setTextSize(TEXT_SIZE * screenDensity);
        //设置字体黄色
        textPaint.setColor(Color.rgb(150, 150, 150));
        //画的次数
        float drawCount = 0;
        for (int i = 0; drawCount < viewWidth; i++)
        {
            //(view宽度 / 2 - 移动距离) +        * (每一秒宽度dp * 密度)px
            xPosition = (viewWidth / 2 - moveDistance) + ((BIG_TIME_INTERVAL - mod) + BIG_TIME_INTERVAL * i)
                    * INTERVAL_LENGTH * screenDensity;
            if (xPosition + getPaddingRight() < viewWidth)
            {
                TimeAlgorithm timeAlgorithm = nowTimeValue.addOrSub(
                        SMALL_TIME_INTERVAL * i * BIG_TIME_INTERVAL + BIG_TIME_INTERVAL - mod);
                int lineHeight = 0;
                //画时间值
                if (timeAlgorithm.mod(1800) == 0)
                {
                    canvas.drawText(String.valueOf(timeAlgorithm.getData()), xPosition - (textWidth * numSize)
                            / 2, getHeight() - textWidth, textPaint);
                    lineHeight = TICK_MARK_HEIGHT;
                }
                else
                {
                    lineHeight = 50;
                }


                //画刻度线
                canvas.drawLine(xPosition, getPaddingTop() + (viewHeight - screenDensity * lineHeight) / 2, xPosition, screenDensity
                        * lineHeight, linePaint);
            }


            xPosition = (viewWidth / 2 - moveDistance) - (mod + BIG_TIME_INTERVAL * i)
                    * INTERVAL_LENGTH * screenDensity;
            if (xPosition > getPaddingLeft())
            {

                TimeAlgorithm timeAlgorithm = nowTimeValue.addOrSub(
                        -SMALL_TIME_INTERVAL * BIG_TIME_INTERVAL * i - mod);

                int lineHeight = 0;
                if (timeAlgorithm.mod(1800) == 0)
                {
                    canvas.drawText(String.valueOf(timeAlgorithm.getData()),
                            xPosition - (textWidth * numSize) / 2, getHeight()
                                    - textWidth, textPaint);
                    lineHeight = TICK_MARK_HEIGHT;

                }
                else
                {
                    lineHeight = 50;

                }

                canvas.drawLine(xPosition, getPaddingTop() + (viewHeight - screenDensity * lineHeight) / 2, xPosition, screenDensity
                        * lineHeight, linePaint);

            }
            drawCount += 2 * INTERVAL_LENGTH * screenDensity * BIG_TIME_INTERVAL;
        }
        canvas.restore();
    }

    /**
     * 绘制标记线（中间线)
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas)
    {
        canvas.save();
        Paint redPaint = new Paint();
        redPaint.setStrokeWidth(4);
        redPaint.setColor(Color.rgb(255, 255, 255));
        canvas.drawLine(viewWidth / 2, 0, viewWidth / 2, viewHeight, redPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //获取当前动作
        int action = event.getAction();
        //事件触发的x坐标
        float xPosition = event.getX();

        if (velocityTracker == null)
            velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);

        switch (action)
        {
            //点击动作,必定触发
            case MotionEvent.ACTION_DOWN:
                //  vg.requestDisallowInterceptTouchEvent(true);
                //通知监听器当前值
                listener.onStartValueChange(getNowDate());
                scroller.forceFinished(true);
                //记录起点X坐标
                beginPointX = xPosition;
                //移动距离为0
                moveDistance = 0;
                return true;

            //移动动作
            case MotionEvent.ACTION_MOVE:
                // vg.requestDisallowInterceptTouchEvent(true);
                //算出移动距离
                moveDistance += (beginPointX - xPosition);
                //更新界面变化和值变化通知
                changeMoveAndValue();
                break;

            //抬起动作
            case MotionEvent.ACTION_UP:


                //动作取消
            case MotionEvent.ACTION_CANCEL:
                //  vg.requestDisallowInterceptTouchEvent(false);
                countVelocityTracker(event);
                countMoveEnd();
                break;

            default:
                break;
        }
        beginPointX = xPosition;
        return super.onTouchEvent(event);
    }

    // 松开手控件滑动起来,fling()需要postInvalidate()
    private void countVelocityTracker(MotionEvent event)
    {
        velocityTracker.computeCurrentVelocity(1000, 1000);
        float xVelocity = velocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > minVelocity)
            scroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
        else if (isEnabled)
            listener.onStopValueChange(getNowDate());
    }

    /**
     * 通知值变化和重绘界面
     */
    private void changeMoveAndValue()
    {
        float tValue = moveDistance / (INTERVAL_LENGTH * screenDensity);
        if (Math.abs(tValue) > 0)
        {
            nowTimeValue = nowTimeValue.addOrSub(Math.round(tValue * SMALL_TIME_INTERVAL));
            int sec = Math.round(viewWidth / (2 * INTERVAL_LENGTH * screenDensity));
            startTime = nowTimeValue.addOrSub(-sec).getSec(nowDate);
            endTime = nowTimeValue.addOrSub(sec).getSec(nowDate);
            moveDistance -= tValue * INTERVAL_LENGTH * screenDensity;
            notifyValueChange();
        }
        postInvalidate();
    }

    /**
     * 通知监听器时间变化
     */
    private void notifyValueChange()
    {
        if (null != listener)
            if (scroller.isFinished())
                listener.onValueChange(getNowDate());
    }

    /**
     * 重新初始化值，通知值变化和重绘界面
     */
    private void countMoveEnd()
    {
        float roundMove = moveDistance / (INTERVAL_LENGTH * screenDensity);
        if (Math.abs(roundMove) > 0)
        {
            nowTimeValue = nowTimeValue.addOrSub(Math
                    .round(roundMove * SMALL_TIME_INTERVAL));
            int sec = Math.round(viewWidth / (2 * INTERVAL_LENGTH * screenDensity));
            startTime = nowTimeValue.addOrSub(-sec).getSec(nowDate);
            endTime = nowTimeValue.addOrSub(sec).getSec(nowDate);
        }
        beginPointX = 0;
        moveDistance = 0;
        notifyValueChange();
        postInvalidate();
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if (scroller.computeScrollOffset())
        {
            if (scroller.getCurrX() == scroller.getFinalX())
            {
                countMoveEnd();
                if (isEnabled)
                    listener.onStopValueChange(getNowDate());
            }
            else
            {
                int xPosition = scroller.getCurrX();
                moveDistance += (beginPointX - xPosition);
                changeMoveAndValue();
                beginPointX = xPosition;
            }
        }
    }


    /**
     * 设置录像视频段
     *
     * @param recordList
     */
    public void setRecordList(List<RecordItem> recordList)
    {
        this.recordList = recordList;
        postInvalidate();
    }


    /**
     * 获取当前时间
     *
     * @return
     */
    private Date getNowDate()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try
        {
            date = format.parse(nowDate + " " + nowTimeValue.getData());
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 更新时间
     */
    public void updateTime(int sec)
    {
        long setTime = (nowTimeValue.getSec(nowDate) + sec) * 1000;
        //如果不在录像时间则跳至下个录像时间段
        if (!isInRecord(new Date(setTime)) && setTime < new Date().getTime() - 1 * 1000
                && nowTimeValue.getSec(nowDate) != 0)
        {
            //         setTime = getLastRecordTime(new Date(setTime)).getTime();
        }
        setValue(setTime);
        postInvalidate();
    }

    private boolean isInRecord(Date date)
    {
        boolean isContain = false;
        for (RecordItem map : recordList)
        {
            long beginTime = map.getStartTime();
            long endTime = map.getEndTime();
            long nowTime = date.getTime() / 1000;
            if (nowTime > beginTime && nowTime < endTime)
            {
                isContain = true;
                break;
            }
        }
        return isContain;
    }

    private Date getLastRecordTime(Date date)
    {
        List<RecordItem> lastNowTime = new ArrayList<>();
        long nowTime = date.getTime() / 1000;
        long theLastedTime = 0;
        long theLast = 0;
        for (RecordItem map : recordList)
        {
            long beginTime = map.getStartTime();
            if (nowTime < beginTime)
            {
                lastNowTime.add(map);
            }
        }

        for (RecordItem map : lastNowTime)
        {
            long endTime = map.getEndTime();
            long beginTime = map.getStartTime();
            if (theLast != 0)
            {
                long temp = beginTime - nowTime;
                if (temp < theLast)
                {
                    theLast = temp;
                    theLastedTime = beginTime;
                }
            }
            else
            {
                theLast = beginTime - nowTime;
                theLastedTime = beginTime;
            }
        }

        return new Date(theLastedTime * 1000);
    }

}



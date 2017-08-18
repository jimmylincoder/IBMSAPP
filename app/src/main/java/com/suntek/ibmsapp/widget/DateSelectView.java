package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义日期选择器View
 *
 * 绘制流程
 * 1.首先绘制中点
 *
 * 2.绘制每个日期位置
 *
 * 3.处理位置移动事件
 *
 * @author jimmy
 */
public class DateSelectView extends View
{
    //日期列表
    private List<String> dateList;

    //view宽度
    private float viewWidth;

    //view高度
    private float viewHeight;

    //日期的间距
    private static final int SPACING = 80;

    // 屏幕密度 dp*screenDensity=px
    private float screenDensity;

    //最小速度值
    private int minVelocity;

    //滚动
    private Scroller scroller;
    private VelocityTracker velocityTracker;

    //滑动监听器
    private TimeSeekBarView.OnValueChangeListener listener;

    // 刚按下X坐标和移动距离
    private float beginPointX, moveDistance;
    /**
     * 滑动时时间变化监听器
     */
    public interface OnValueChangeListener
    {
        void onValueChange(String date);

        void onStartValueChange(String date);

        void onStopValueChange(String date);
    }


    public DateSelectView(Context context)
    {
        super(context);
    }

    public DateSelectView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        //初始化日期列表
        dateList = new ArrayList<>();
        dateList.add("07.01");
        dateList.add("07.02");
        dateList.add("07.03");

        //获取屏幕密度
        screenDensity = context.getResources().getDisplayMetrics().density;
        scroller = new Scroller(context);

        setBackgroundResource(android.R.color.holo_red_light);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        //控件高度
        viewHeight = getHeight();
        //控制宽度
        viewWidth = getWidth();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawCenterPoint(canvas);
        if(dateList.size() > 0)
            drawDate(canvas);
    }

    /**
     * 绘制中点标记点
     *
     * @param canvas
     */
    private void drawCenterPoint(Canvas canvas)
    {
        canvas.save();
        TextPaint textPaint = new TextPaint();
        textPaint.setStrokeWidth(4);
        textPaint.setColor(Color.rgb(255, 255, 255));

        //x位置，字体宽度
        float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);
        //字数大小
        int numSize = String.valueOf("00:00:00").length();

        canvas.drawLine(viewWidth / 2, 0, viewWidth / 2, viewHeight, textPaint);
        canvas.restore();
    }

    /**
     * 绘制日期
     *
     * @param canvas
     */
    private void drawDate(Canvas canvas)
    {
        canvas.save();
      //  float dateWidth = viewWidth / (SPACING * screenDensity);
        //画笔
        Paint strPaint = new Paint();
        strPaint.setStrokeWidth(4);
        strPaint.setTextSize(15 * screenDensity);
        //红色
        strPaint.setColor(Color.rgb(0, 0, 0));

        int i = 0;
        for(String s : dateList)
        {
            canvas.drawText(s,SPACING * screenDensity * i ,viewHeight / 2,strPaint);
            i++;
        }
        canvas.restore();
    }

    // 松开手控件滑动起来,fling()需要postInvalidate()
    private void countVelocityTracker(MotionEvent event)
    {
        velocityTracker.computeCurrentVelocity(1000, 1000);
        float xVelocity = velocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > minVelocity)
            scroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
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
                scroller.forceFinished(true);
                //记录起点X坐标
                beginPointX = xPosition;
                //移动距离为0
                moveDistance = 0;
                return true;

            //移动动作
            case MotionEvent.ACTION_MOVE:
                //算出移动距离
                moveDistance += (beginPointX - xPosition);
                break;

            //抬起动作
            case MotionEvent.ACTION_UP:

            //动作取消
            case MotionEvent.ACTION_CANCEL:
                countVelocityTracker(event);
                break;

            default:
                break;
        }
        beginPointX = xPosition;
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll()
    {
        super.computeScroll();
        if(scroller.computeScrollOffset())
        {
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            invalidate();
        }
    }
}

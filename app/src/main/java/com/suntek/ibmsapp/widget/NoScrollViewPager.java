package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 设置不可滚动viewpager
 *
 * @author jimmy
 */
public class NoScrollViewPager extends ViewPager
{
    private boolean noScroll = false;

    public NoScrollViewPager(Context context)
    {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event)
    {
        if (noScroll)
            return false;
        else
            return super.onInterceptHoverEvent(event);
    }

    public boolean isNoScroll()
    {
        return noScroll;
    }

    public void setNoScroll(boolean noScroll)
    {
        this.noScroll = noScroll;
    }
}

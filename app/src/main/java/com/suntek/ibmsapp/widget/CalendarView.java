package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 自定义日历控件
 *
 * @author jimmy
 */
public class CalendarView extends LinearLayout
{
    private Context context;

    public CalendarView(Context context)
    {
        super(context);
        init(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
    }
}

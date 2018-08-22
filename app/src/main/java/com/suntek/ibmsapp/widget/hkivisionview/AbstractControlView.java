package com.suntek.ibmsapp.widget.hkivisionview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 视频顶层控制view
 *
 * @author jimmy
 */
public abstract class AbstractControlView extends LinearLayout
{
    public AbstractControlView(Context context)
    {
        super(context);
    }

    public AbstractControlView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public AbstractControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}

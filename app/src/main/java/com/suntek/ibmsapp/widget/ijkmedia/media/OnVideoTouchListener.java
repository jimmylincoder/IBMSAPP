package com.suntek.ibmsapp.widget.ijkmedia.media;

import android.view.MotionEvent;

/**
 * 视频界面点击监听
 *
 * @author jimmy
 */
public interface OnVideoTouchListener
{
    void onSingleTouchEvent(MotionEvent ev);

    void onDoubleTouchEvent(MotionEvent ev);
}

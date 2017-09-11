package com.suntek.ibmsapp.widget.GestureImageView;

/**
 * 放大缩小的自定义控件监听接口
 *
 * @author Devin
 */
public interface GestureImageViewListener
{

    public void onTouch(float x, float y);

    public void onScale(float scale);

    public void onPosition(float x, float y);

}

package com.suntek.ibmsapp.widget.GestureImageView;

/**
 * 上升动画 监听接口
 *
 * @author Devin
 */
public interface ZoomAnimationListener
{
    public void onZoom(float scale, float x, float y);

    public void onComplete();
}

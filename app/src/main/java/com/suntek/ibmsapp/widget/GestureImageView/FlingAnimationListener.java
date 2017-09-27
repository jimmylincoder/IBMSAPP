package com.suntek.ibmsapp.widget.GestureImageView;


/**
 * 放大缩小图片的动画监听接口
 *
 * @author Devin
 */
public interface FlingAnimationListener
{

    public void onMove(float x, float y);

    public void onComplete();

}

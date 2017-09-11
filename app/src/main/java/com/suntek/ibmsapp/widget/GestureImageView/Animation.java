package com.suntek.ibmsapp.widget.GestureImageView;

/**
 * 放大缩小图片的动画接口
 *
 * @author Devin
 */
public interface Animation
{

    /**
     * Transforms the view.
     *
     * @param view
     * @return true if this animation should remain active.  False otherwise.
     */
    public boolean update(GestureImageView view, long time);

}

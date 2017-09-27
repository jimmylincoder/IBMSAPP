package com.suntek.ibmsapp.widget;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.suntek.ibmsapp.util.BitmapUtil;

/**
 * 正方形图片显示
 */
public class SquareBitmapDisplay implements BitmapDisplayer
{
    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
    {
        bitmap = BitmapUtil.centerSquareScaleBitmap(bitmap, 100);
        imageAware.setImageBitmap(bitmap);
    }
}

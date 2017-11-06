package com.suntek.ibmsapp.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.suntek.ibmsapp.util.BitmapUtil;

/**
 * 长方形截图
 *
 * @author jimmy
 */
public class RectangularDisplay implements BitmapDisplayer
{

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
    {
        Bitmap bitmap1 = BitmapUtil.rectangularBitmap(bitmap, 16, 9);
        imageAware.setImageBitmap(bitmap1);
    }
}

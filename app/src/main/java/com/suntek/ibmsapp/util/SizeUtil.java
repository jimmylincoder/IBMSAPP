package com.suntek.ibmsapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * 安卓dp
 */
public class SizeUtil
{
    public static float getRawSize(Context context, int unit, float value) {
        Resources res = context.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }
}

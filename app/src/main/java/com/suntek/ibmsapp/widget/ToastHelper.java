package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast操作类
 *
 * @author Devin
 */
public class ToastHelper
{
    private static ToastHelper model;
    private Context context;
    private Toast toast;
    private boolean isToastShow = false;

    public ToastHelper(Context context)
    {
        this.context = context;
    }

    /**
     * 单例组件
     *
     * @param context
     * @return
     */
    public static ToastHelper getInstance(Context context)
    {
        if (model == null)
        {
            model = new ToastHelper(context);
        }
        return model;
    }

    /**
     * 短提示信息
     *
     * @param message 内容
     */
    public void shortShowMessage(String message)
    {
        if (isToastShow)
        {
            toast.cancel();
            isToastShow = false;
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
        isToastShow = true;
    }

    /**
     * 长提示信息
     *
     * @param message 内容
     */
    public void longShowMessage(String message)
    {
        if (isToastShow)
        {
            toast.cancel();
            isToastShow = false;
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
        isToastShow = true;
    }
}

package com.suntek.ibmsapp.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.widget.gif.GifView;


/**
 * 加载窗口
 *
 * @author deVlin
 */
public class LoadingDialog implements DialogInterface.OnKeyListener
{
    //上下文
    private Context context;

    //单例
    private static LoadingDialog model;

    //Dialog
    public AlertDialog alert;

    //窗口操作类
    private Window w;

    //Gif加载
    private GifView mGifView;

    //提示信息
    private TextView tvContent;

    //结果图标
    private ImageView ivState;

    //消息处理机制
    private Handler handler;

    //是否可以按返回键退出
    private boolean isExit;


    private LoadingDialog(Context context)
    {
        this.context = context.getApplicationContext();
        handler = new Handler();
    }

    public static LoadingDialog getInstance(Context context)
    {
        if (model == null)
        {
            model = new LoadingDialog(context);
        }
        //如果不是当前窗口，重新New，避免出现activity实效
        else
        {
            if (context != model.context)
            {
                model = new LoadingDialog(context);
            }
        }
        return model;
    }

    /**
     * 显示loading
     *
     * @param string 提示内容
     */
    public void showLoading(String string)
    {
        isExit=false;
        if (alert == null)
        {
            alert = new AlertDialog.Builder(context).create();
            alert.setOnKeyListener(this);                     //设置返回键无效
            ((Dialog) alert).setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);            //设置dialog无标题

            w = alert.getWindow();
            w.setBackgroundDrawableResource(android.R.color.transparent);   //设置窗口的背景颜色
            w.setBackgroundDrawable(new BitmapDrawable());

        }
        try
        {
            if (!alert.isShowing())
            {
                alert.show();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            // TODO: handle exception
        }
        w.setContentView(R.layout.view_loading_dialog);

        mGifView = (GifView) w.findViewById(R.id.loading_gif);
        tvContent = (TextView) w.findViewById(R.id.content);
        ivState = (ImageView) w.findViewById(R.id.iv_state);
        if (!string.equals(""))
        {
            tvContent.setText(string);
        }
        ivState.setVisibility(View.GONE);
        mGifView.setGifImage(R.mipmap.loading_gif);
    }

    /**
     * 请求状态显示
     *
     * @param content 显示的内容
     * @param state   请求状态，成功或失败
     * @param seconds 毫秒
     */
    public void showRequestStatus(String content, boolean state, int seconds)
    {
        if(mGifView==null)
        {
            return;
        }
        mGifView.setRun(false);
        mGifView.setVisibility(View.GONE);
        ivState.setVisibility(View.VISIBLE);
        tvContent.setText(content);
        if (state)
        {
            ivState.setBackgroundResource(R.mipmap.ic_loading_success);
            tvContent.setTextColor(Color.parseColor("#1baa0d"));
        }
        else
        {
            ivState.setBackgroundResource(R.mipmap.ic_loading_error);
            tvContent.setTextColor(Color.parseColor("#e63434"));
            if(content==null || content.equals(""))
            {
                tvContent.setText("未知错误");
            }
        }

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, seconds);
    }

    /**
     * 更改提示内容
     *
     * @param status    三种状态，1转动圈，2成功，3失败
     * @param content   提示
     */
    public void showContent(int status,String content)
    {
        switch(status)
        {
            case 1:
                mGifView.setVisibility(View.VISIBLE);
                if (!alert.isShowing())
                {
                    alert.show();
                }
                break;
            case 2:
            case 3:
                mGifView.setRun(false);
                mGifView.setVisibility(View.GONE);
                ivState.setVisibility(View.VISIBLE);
                if (status==2)
                {
                    ivState.setBackgroundResource(R.mipmap.ic_loading_success);
                    tvContent.setTextColor(Color.parseColor("#1baa0d"));
                }
                else
                {
                    ivState.setBackgroundResource(R.mipmap.ic_loading_error);
                    tvContent.setTextColor(Color.parseColor("#e63434"));
                }
                break;
        }
        tvContent.setText(content);
    }

    /**
     * 关闭加载
     */
    public void loadingDiss()
    {

        try
        {
            alert.dismiss();
            alert = null;
            w.closeAllPanels();
            mGifView.setRun(false);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

    }


    public boolean isExit()
    {
        return isExit;
    }

    public void setIsExit(boolean isExit)
    {
        this.isExit = isExit;
    }

    /**
     * 定时线程，用于关闭加载框
     */
    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            loadingDiss();
        }
    };


    /**
     * 屏蔽返回键
     *
     * @param dialog
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(isExit)
            {
                if(alert!=null && alert.isShowing())
                {
                    alert.dismiss();
                }
            }
            return true;
        }
        return false;
    }

}
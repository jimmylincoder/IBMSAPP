package com.suntek.ibmsapp.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.sun.jna.Platform;
import com.suntek.ibmsapp.component.CrashHandler;
import com.suntek.ibmsapp.component.base.BaseApplication;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * 应用程序启动类
 *
 * @author jimmy
 */
public class IBMSApp extends BaseApplication
{
    public static IBMSApp mInstance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());
        initShare();
        //init();
    }

    private void initShare()
    {
        Config.DEBUG = true;
        UMShareAPI.get(this);
    }

    {
        PlatformConfig.setWeixin("wxa038574f9c6fb1d5","ab05eea80ed5dc818f769fc2663a2535");
        PlatformConfig.setQQZone("1106525692", "0pNBWO0rSIooNQ1U");
    }

    private void init()
    {
        //初始化Leak内存泄露检测工具
        LeakCanary.install(this);
    }

    public static IBMSApp getInstance()
    {
        return mInstance;
    }

}

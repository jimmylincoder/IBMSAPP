package com.suntek.ibmsapp.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.suntek.ibmsapp.component.base.BaseApplication;

/**
 *
 *  应用程序启动类
 *
 *  @author jimmy
 */
public class IBMSApp extends BaseApplication
{
    public static IBMSApp mInstance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
     //   init();
    }

    private void init() {
        //初始化Leak内存泄露检测工具
        LeakCanary.install(this);
    }

    public static IBMSApp getInstance() {
        return mInstance;
    }

}

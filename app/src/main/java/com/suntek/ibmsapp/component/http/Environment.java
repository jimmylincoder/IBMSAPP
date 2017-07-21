package com.suntek.ibmsapp.component.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.suntek.ibmsapp.component.base.BaseApplication;
import com.suntek.ibmsapp.component.core.BaseComponent;


/**
 * Environment
 *
 * @author kutzhang@gmail.com
 */
public class Environment extends BaseComponent
{
    /**
     * 获取操作系统名
     *
     * @return 操作系统名
     */
    public String getOs()
    {
        return "Android";
    }

    /**
     * 获取操作系统版本
     *
     * @return 操作系统版本
     */
    public String getOsVersion()
    {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取应用名
     *
     * @return 应用名
     */
    public String getAppName()
    {
        return getAppInfo().packageName;
    }

    /**
     * 获取应用版本号
     *
     * @return 应用版本号
     */
    public String getAppVersion()
    {
        return getAppInfo().versionName;
    }

    /**
     * 获取设备号
     *
     * @return 设备号
     */
    public String getUdid()
    {
        BaseApplication application = BaseApplication.getApplication(BaseApplication.class);
        TelephonyManager telephonyManager =
                (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取应用信息
     *
     * @return 应用信息
     */
    private PackageInfo getAppInfo()
    {
        BaseApplication application = BaseApplication.getApplication(BaseApplication.class);

        PackageManager manager = application.getPackageManager();
        PackageInfo info;
        try
        {
            info = manager.getPackageInfo(application.getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException(e);
        }

        return info;
    }

    @Override
    public boolean cleanable()
    {
        return false;
    }
}

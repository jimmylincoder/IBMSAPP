package com.suntek.ibmsapp.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 获取app版本
 *
 * @author jimmy
 */
public class VersionUtil
{
    public static String getAppInfo(Context context)
    {
        String pkName = context.getPackageName();
        try
        {
            String versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e)
        {
            return null;
        }
    }
}

package com.suntek.ibmsapp.util;

import android.content.SharedPreferences;

import com.suntek.ibmsapp.app.IBMSApp;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.base.BaseApplication;
import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.core.Config;

/**
 * 保存数据到内存
 *
 * @author jimmy
 */
public class SaveDataWithSharedHelper extends BaseComponent
{
    private SharedPreferences sharedPreferences;

    //保存的缓存名
    @Config("share_preferences.data_file_name")
    private String dataFileName;

    /**
     * 构造方法
     */
    public SaveDataWithSharedHelper()
    {
        sharedPreferences = IBMSApp.mInstance.getSharedPreferences(dataFileName, 0);
    }

    /**
     * 保存int数据
     *
     * @param key
     * @param value
     */
    public void save(String key, int value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 保存boolean类型的数据
     *
     * @param key
     * @param value
     */
    public void save(String key, Boolean value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 保存String类型的数据
     *
     * @param key
     * @param value
     */
    public void save(String key, String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);

        editor.commit();
    }

    /**
     * 保存long类型的数据
     *
     * @param key
     * @param value
     */
    public void save(String key, Long value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(key, value);

        editor.commit();
    }

    /**
     * 获取long类型的数据
     *
     * @param key
     * @return Long
     */
    public Long getLong(String key)
    {
        return sharedPreferences.getLong(key, 0);
    }


    /**
     * 获取int类型的数据
     *
     * @param key
     * @return int
     */
    public int getInt(String key)
    {
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * 获取Boolean类型的数据
     *
     * @param key
     * @return Boolean
     */
    public Boolean getBoolean(String key, boolean isDefault)
    {
        return sharedPreferences.getBoolean(key, isDefault);
    }

    /**
     * 获取String类型的数据
     *
     * @param key
     * @return String
     */
    public String getString(String key)
    {
        return sharedPreferences.getString(key, "");
    }

    /**
     * 清除数据
     */
    public void clear()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}

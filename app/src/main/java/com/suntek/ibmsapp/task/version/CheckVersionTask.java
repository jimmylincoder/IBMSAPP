package com.suntek.ibmsapp.task.version;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.VersionManager;
import com.suntek.ibmsapp.model.Version;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 检测更新
 *
 * @author jimmy
 */
public class CheckVersionTask extends BaseTask
{
    private VersionManager versionManager;

    private String versionNum;

    public CheckVersionTask(Context context, String versionName)
    {
        super(context);
        this.versionNum = versionName;
        versionManager = (VersionManager) ComponentEngine.getInstance(VersionManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            Version version = versionManager.checkUpdate(versionNum);
            return new TaskResult(version, null);
        } catch (FHttpException e)
        {
            return new TaskResult(null, e);
        }
    }
}

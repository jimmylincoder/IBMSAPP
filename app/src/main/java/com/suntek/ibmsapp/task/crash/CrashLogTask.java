package com.suntek.ibmsapp.task.crash;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CrashManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 崩溃日志记录
 *
 * @author jimmy
 */
public class CrashLogTask extends BaseTask
{
    private CrashManager crashManager;

    private String message;

    public CrashLogTask(Context context,String message)
    {
        super(context);

        this.message = message;
        crashManager = (CrashManager) ComponentEngine.getInstance(CrashManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            crashManager.logCrash(message);
            return new TaskResult("",null);
        }
        catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

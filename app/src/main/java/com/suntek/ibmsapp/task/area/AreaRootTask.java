package com.suntek.ibmsapp.task.area;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.AreaManager;
import com.suntek.ibmsapp.model.Area;
import com.suntek.ibmsapp.task.base.BaseTask;


/**
 * 获取根节点数据
 *
 * @author jimmy
 */

public class AreaRootTask extends BaseTask
{
    private AreaManager areaManager;

    public AreaRootTask(Context context)
    {
        super(context);
        areaManager = (AreaManager) ComponentEngine.getInstance(AreaManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            Area area = areaManager.getRootArea();
            return new TaskResult(area, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}

package com.suntek.ibmsapp.task.area;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.AreaManager;
import com.suntek.ibmsapp.model.Area;
import com.suntek.ibmsapp.task.base.BaseTask;

import java.util.List;

/**
 * 地域列表任务
 *
 * @author jimmy
 */
public class AreaListTask extends BaseTask
{
    private AreaManager areaManager;

    private String parentId;

    public AreaListTask(Context context,String parentId)
    {
        super(context);
        this.parentId = parentId;

        areaManager = (AreaManager) ComponentEngine.getInstance(AreaManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            List<Area> areas = areaManager.getAreaList(parentId);
            return new TaskResult(areas,null);
        }catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

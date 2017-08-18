package com.suntek.ibmsapp.task.camera;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraManager;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.task.base.BaseTask;

import java.util.List;

/**
 * 摄像机搜索
 *
 * @author jimmy
 */
public class CameraSearchTask extends BaseTask
{
    private int page;

    private String keyword;

    private CameraManager cameraManager;

    public CameraSearchTask(Context context,String keyword,int page)
    {
        super(context);
        this.keyword = keyword;
        this.page = page;

        cameraManager = (CameraManager) ComponentEngine.getInstance(CameraManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            List<Camera> cameraList = cameraManager.cameraSearch(keyword,page);
            return new TaskResult(cameraList,null);
        }
        catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

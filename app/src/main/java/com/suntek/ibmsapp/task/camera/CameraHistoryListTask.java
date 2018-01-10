package com.suntek.ibmsapp.task.camera;

import android.content.Context;

import com.suntek.ibmsapp.component.Page;
import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraManager;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.task.base.BaseTask;

import java.util.List;

/**
 * 历史列表
 *
 * @author jimmy
 */
public class CameraHistoryListTask extends BaseTask
{
    private int page;

    private String userCode;

    CameraManager cameraManager;

    public CameraHistoryListTask(Context context,String userCode,int page)
    {
        super(context);

        this.page = page;
        this.userCode = userCode;
        cameraManager = (CameraManager) ComponentEngine.getInstance(CameraManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            Page<List<Camera>> cameraPage = cameraManager.getCameraHistoryList(userCode,page);
            return new TaskResult(cameraPage,null);
        }
        catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

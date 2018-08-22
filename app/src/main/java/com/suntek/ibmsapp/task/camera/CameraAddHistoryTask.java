package com.suntek.ibmsapp.task.camera;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 添加查看历史记录
 *
 * @author jimmy
 */
public class CameraAddHistoryTask extends BaseTask
{
    private CameraManager cameraManager;

    private String cameraId;

    private String userCode;

    public CameraAddHistoryTask(Context context,String userCode,String cameraId)
    {
        super(context);
        this.cameraId = cameraId;
        this.userCode = userCode;

        cameraManager = (CameraManager) ComponentEngine.getInstance(CameraManager.class);

    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            cameraManager.addHistory(userCode,cameraId);
            return new TaskResult("",null);
        }catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

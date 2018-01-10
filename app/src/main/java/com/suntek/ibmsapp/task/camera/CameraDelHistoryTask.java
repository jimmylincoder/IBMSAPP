package com.suntek.ibmsapp.task.camera;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 删除历史记录
 *
 * @author jimmy
 */
public class CameraDelHistoryTask extends BaseTask
{
    private CameraManager cameraManager;

    private String cameraId;

    private String userCode;

    public CameraDelHistoryTask(Context context,String userCode,String cameraId)
    {
        super(context);
        this.cameraId = cameraId;
        this.userCode = userCode;

        cameraManager = (CameraManager) ComponentEngine.getInstance(CameraManager.class);    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            cameraManager.delHistory(userCode,cameraId);
            return new TaskResult("",null);
        }catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

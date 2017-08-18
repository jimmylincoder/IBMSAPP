package com.suntek.ibmsapp.task.camera;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 摄像头播放
 *
 * @author jimmy
 */
public class CameraPlayTask extends BaseTask
{
    private CameraManager cameraManager;

    public CameraPlayTask(Context context)
    {
        super(context);
        cameraManager = (CameraManager) ComponentEngine.getInstance(CameraManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            String address = cameraManager.getPlayUrl();
            return new TaskResult(address,null);
        }catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

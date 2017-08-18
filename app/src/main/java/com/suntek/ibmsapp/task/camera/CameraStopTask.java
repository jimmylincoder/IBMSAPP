package com.suntek.ibmsapp.task.camera;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 停止播放视频
 *
 * @author jimmy
 */
public class CameraStopTask extends BaseTask
{
    private CameraManager cameraManager;

    public CameraStopTask(Context context)
    {
        super(context);

        cameraManager = (CameraManager) ComponentEngine.getInstance(CameraManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            cameraManager.stopPlay();
            return new TaskResult("",null);
        }
        catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

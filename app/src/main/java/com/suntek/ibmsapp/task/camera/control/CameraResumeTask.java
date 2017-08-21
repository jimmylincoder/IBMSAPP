package com.suntek.ibmsapp.task.camera.control;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraControlManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 恢复播放
 *
 * @author jimmy
 */
public class CameraResumeTask extends BaseTask
{
    private CameraControlManager cameraControlManager;

    private String session;

    public CameraResumeTask(Context context, String session)
    {
        super(context);

        this.session = session;
        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            cameraControlManager.resumePlay(session);
            return new TaskResult("",null);
        }
        catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

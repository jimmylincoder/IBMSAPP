package com.suntek.ibmsapp.task.camera.control;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraControlManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 改变播放速度
 *
 * @author jimmy
 */
public class CameraChangeSpeedTask extends BaseTask
{
    private CameraControlManager cameraControlManager;

    private String session;

    private String speed;

    public CameraChangeSpeedTask(Context context, String session,String speed)
    {
        super(context);

        this.session = session;
        this.speed = speed;
        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            cameraControlManager.changeSpeed(session,speed);
            return new TaskResult("",null);
        }
        catch (FHttpException e)
        {
            return new TaskResult("",e);
        }
    }
}

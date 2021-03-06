package com.suntek.ibmsapp.task.camera.control;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraControlManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 改变位置
 *
 * @author jimmy
 */
public class CameraChangePositionTask extends BaseTask
{
    private CameraControlManager cameraControlManager;

    private String session;

    private String position;

    public CameraChangePositionTask(Context context, String session,String position)
    {
        super(context);

        this.session = session;
        this.position = position;
        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected BaseTask.TaskResult doInBackground(Void... params)
    {
        try
        {
            cameraControlManager.changePosition(session,position);
            return new BaseTask.TaskResult("", null);
        } catch (FHttpException e)
        {
            return new BaseTask.TaskResult("", e);
        }
    }
}

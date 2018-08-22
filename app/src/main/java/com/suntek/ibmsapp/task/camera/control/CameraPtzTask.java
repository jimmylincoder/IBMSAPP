package com.suntek.ibmsapp.task.camera.control;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraControlManager;
import com.suntek.ibmsapp.task.base.BaseTask;

/**
 * 云台控制
 *
 * @author jimmy
 */
public class CameraPtzTask extends BaseTask
{
    private String protocol;

    private String videoId;

    private String command;

    private String speed;

    private String stopFlag;

    private CameraControlManager cameraControlManager;

    public CameraPtzTask(Context context, String protocol, String videoId, String command, String speed,
                         String stopFlag)
    {
        super(context);
        this.protocol = protocol;
        this.videoId = videoId;
        this.command = command;
        this.speed = speed;
        this.stopFlag = stopFlag;

        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            cameraControlManager.ptzControl(protocol, videoId, command, speed, stopFlag);
            return new TaskResult("", null);
        } catch (FHttpException e)
        {
            return new TaskResult(null, e);
        }
    }
}

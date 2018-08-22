package com.suntek.ibmsapp.task.camera.control;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraControlManager;
import com.suntek.ibmsapp.task.base.BaseTask;

import java.util.Map;

/**
 * 视频播放任务
 *
 * @author jimmy
 */
public class CameraPlayGB28181Task extends BaseTask
{
    private CameraControlManager cameraControlManager;

    private String deviceId;

    private String beginTime;

    private String endTime;

    private String parentId;

    public CameraPlayGB28181Task(Context context, String deviceId, String parentId,
                                 String beginTime, String endTime)
    {
        super(context);

        this.deviceId = deviceId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.parentId = parentId;

        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            Map<String, Object> res = cameraControlManager.playByGb28181(deviceId, parentId,beginTime, endTime);
            return new TaskResult(res, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}

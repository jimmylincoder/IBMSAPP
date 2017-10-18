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

    private String ip;

    private String channel;

    private String user;

    private String password;

    private String beginTime;

    private String endTime;

    private String parentId;

    public CameraPlayGB28181Task(Context context, String deviceId, String parentId, String ip, String channel,
                                 String user, String password, String beginTime, String endTime)
    {
        super(context);

        this.deviceId = deviceId;
        this.ip = ip;
        this.channel = channel;
        this.user = user;
        this.password = password;
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
            Map<String, Object> res = cameraControlManager.playByGb28181(deviceId, parentId, ip, channel, user, password, beginTime, endTime);
            return new TaskResult(res, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}
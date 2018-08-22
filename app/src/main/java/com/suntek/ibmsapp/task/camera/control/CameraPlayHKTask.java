package com.suntek.ibmsapp.task.camera.control;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraControlManager;
import com.suntek.ibmsapp.task.base.BaseTask;

import java.util.Map;

/**
 * 海康sdk形式播放
 *
 * @author jimmy
 */

public class CameraPlayHKTask extends BaseTask
{
    private CameraControlManager cameraControlManager;

    private String deviceId;

    private String mediaChannel;

    private String streamType;

    private String beginTime;

    private String endTime;

    public CameraPlayHKTask(Context context,String deviceId, String mediaChannel,String streamType,
                            String beginTime, String endTime)
    {
        super(context);
        this.deviceId = deviceId;
        this.mediaChannel = mediaChannel;
        this.streamType = streamType;
        this.beginTime = beginTime;
        this.endTime = endTime;

        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            Map<String, Object> res = cameraControlManager.playByHK(deviceId,mediaChannel, streamType,
                    beginTime, endTime);

            return new TaskResult(res, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}

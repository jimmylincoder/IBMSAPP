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

    private String port;

    private String mediaChannel;

    private String ip;

    private String channel;

    private String user;

    private String password;

    private String streamType;

    private String beginTime;

    private String endTime;

    public CameraPlayHKTask(Context context, String mediaChannel, String ip, String port, String channel,
                            String user, String password, String streamType, String beginTime, String endTime)
    {
        super(context);
        this.mediaChannel = mediaChannel;
        this.ip = ip;
        this.channel = channel;
        this.user = user;
        this.password = password;
        this.streamType = streamType;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.port = port;

        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            Map<String, Object> res = cameraControlManager.playByHK(mediaChannel, streamType, ip, port,
                    channel, user, password, beginTime, endTime);

            return new TaskResult(res, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}

package com.suntek.ibmsapp.task.camera.control;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraControlManager;
import com.suntek.ibmsapp.model.RecordItem;
import com.suntek.ibmsapp.task.base.BaseTask;

import org.bouncycastle.jce.provider.symmetric.ARC4;

import java.util.List;
import java.util.Map;

/**
 * 查询录像
 *
 * @author jimmy
 */
public class CameraQueryRecordTask extends BaseTask
{
    private CameraControlManager cameraControlManager;

    private String deviceId;

    private String ip;

    private String channel;

    private String user;

    private String password;

    private String beginTime;

    private String endTime;

    public CameraQueryRecordTask(Context context, String deviceId, String ip, String channel,
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

        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            List<RecordItem> res = cameraControlManager.queryRecord(deviceId, ip, channel, user, password, beginTime, endTime);
            return new TaskResult(res, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}

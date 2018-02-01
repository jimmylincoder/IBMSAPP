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

    private String parentId;

    private String beginTime;

    private String endTime;

    private String protocol;

    public CameraQueryRecordTask(Context context, String deviceId, String parentId,
                                 String beginTime, String endTime,String protocol)
    {
        super(context);

        this.deviceId = deviceId;
        this.parentId = parentId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.protocol = protocol;

        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            List<RecordItem> res = cameraControlManager.queryRecord(deviceId, parentId,
                    beginTime, endTime,protocol);
            return new TaskResult(res, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}

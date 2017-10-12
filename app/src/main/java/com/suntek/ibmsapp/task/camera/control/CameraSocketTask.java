package com.suntek.ibmsapp.task.camera.control;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraControlManager;
import com.suntek.ibmsapp.task.base.BaseTask;

import java.util.Map;

/**
 * 获取tcp连接地址
 *
 * @author jimmy
 */
public class CameraSocketTask extends BaseTask
{
    private CameraControlManager cameraControlManager;

    public CameraSocketTask(Context context)
    {
        super(context);

        cameraControlManager = (CameraControlManager) ComponentEngine.getInstance(CameraControlManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            Map<String, Object> result = cameraControlManager.getSocketAddress();
            return new TaskResult(result, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}

package com.suntek.ibmsapp.task.camera;

import android.content.Context;

import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.manager.CameraManager;
import com.suntek.ibmsapp.model.Camera;
import com.suntek.ibmsapp.task.base.BaseTask;

import java.util.List;

/**
 * 获取摄像头列表
 *
 * @author jimmy
 */
public class CameraListTask extends BaseTask
{
    private CameraManager cameraManager;

    private String areaId;

    private int page;

    public CameraListTask(Context context, String areaId, int page)
    {
        super(context);
        this.areaId = areaId;
        this.page = page;

        cameraManager = (CameraManager) ComponentEngine.getInstance(CameraManager.class);
    }

    @Override
    protected TaskResult doInBackground(Void... params)
    {
        try
        {
            List<Camera> cameraList = cameraManager.getCameraList(areaId, page);
            return new TaskResult(cameraList, null);
        } catch (FHttpException e)
        {
            return new TaskResult("", e);
        }
    }
}

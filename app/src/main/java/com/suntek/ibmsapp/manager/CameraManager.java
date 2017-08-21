package com.suntek.ibmsapp.manager;

import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.IbmsHttpEngine;
import com.suntek.ibmsapp.component.Page;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.model.Camera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 摄像机管理类
 *
 * @author jimmy
 */
public class CameraManager extends BaseComponent
{
    @Autowired
    IbmsHttpEngine ibmsHttpEngine;

    /**
     * 获取摄像机列表
     *
     * @param areaId
     * @param page
     * @return
     */
    public Page<List<Camera>> getCameraList(String areaId, int page)
    {
        Map<String,Object> params = new HashMap<>();
        params.put("area_id",areaId);
        params.put("page",page + "");

        List<Camera> cameraList = new ArrayList<>();
        HttpResponse response = ibmsHttpEngine.request("camera.list",params);
        Page<List<Camera>> cameraPage = new Page<>();
        if(response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            List<Map<String,Object>> data = (List<Map<String, Object>>) response.getData().get("camera_list");
            for(Map<String,Object> map : data)
            {
                Camera camera = Camera.generateByJson(map);
                cameraList.add(camera);
            }
            cameraPage.setData(cameraList);
            cameraPage.setTotalPage((int)response.getData().get("total_page"));
            return cameraPage;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR,response.getErrorMessage());
        }
    }

    /**
     * 获取历史列表
     *
     * @param page
     * @return
     */
    public Page<List<Camera>> getCameraHistoryList(int page)
    {
        Map<String,Object> params = new HashMap<>();
        params.put("page",page + "");

        List<Camera> cameraList = new ArrayList<>();
        HttpResponse response = ibmsHttpEngine.request("camera.history_list",params);
        if(response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            Page<List<Camera>> cameraPage = new Page<>();
            List<Map<String,Object>> data = (List<Map<String, Object>>) response.getData().get("camera_list");
            for(Map<String,Object> map : data)
            {
                Camera camera = Camera.generateByJson(map);
                cameraList.add(camera);
            }
            cameraPage.setData(cameraList);
            cameraPage.setTotalPage((int) response.getData().get("total_page"));
            return cameraPage;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR,response.getErrorMessage());
        }
    }

    /**
     * 搜索摄像机
     *
     * @param keyword
     * @param page
     * @return
     */
    public Page<List<Camera>> cameraSearch(String keyword,int page)
    {
        Map<String,Object> params = new HashMap<>();
        params.put("page",page + "");
        params.put("keyword",keyword);
        List<Camera> cameraList = new ArrayList<>();
        HttpResponse response = ibmsHttpEngine.request("camera.list_by_keyword",params);
        if(response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            Page<List<Camera>> cameraPage = new Page<>();
            List<Map<String,Object>> data = (List<Map<String, Object>>) response.getData().get("camera_list");
            for(Map<String,Object> map : data)
            {
                Camera camera = Camera.generateByJson(map);
                cameraList.add(camera);
            }
            cameraPage.setData(cameraList);
            cameraPage.setTotalPage((int) response.getData().get("total_page"));
            return cameraPage;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR,response.getErrorMessage());
        }
    }

    /**
     * 添加历史记录
     *
     * @param cameraId
     */
    public void addHistory(String cameraId)
    {
        Map<String,Object> params = new HashMap<>();
        params.put("camera_id",cameraId);

        HttpResponse response = ibmsHttpEngine.request("camera.add_history",params);
        if(response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR,response.getErrorMessage());
        }
    }

    /**
     * 播放视频
     *
     * @return
     */
    public String getPlayUrl()
    {
        Map<String,Object> params = new HashMap<>();

        HttpResponse response = ibmsHttpEngine.request("camera.address",params);
        if(response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            return (String) response.getData().get("address");
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR,response.getErrorMessage());
        }
    }

    /**
     * 停止播放视频
     *
     */
    public void stopPlay()
    {
        Map<String,Object> params = new HashMap<>();
        HttpResponse response = ibmsHttpEngine.request("camera.stop",params);
        if(response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR,response.getErrorMessage());
        }
    }
}

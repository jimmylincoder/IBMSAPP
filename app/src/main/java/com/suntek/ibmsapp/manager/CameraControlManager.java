package com.suntek.ibmsapp.manager;

import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.IbmsHttpEngine;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.model.RecordItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 摄像机控制类
 *
 * @author jimmy
 */
public class CameraControlManager extends BaseComponent
{
    @Autowired
    IbmsHttpEngine ibmsHttpEngine;

    /**
     * 播放视频 GB28181方式
     *
     * @return
     */
    public Map<String, Object> playByGb28181(String deivceId, String parentId,
                                             String beginTime, String endTime)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("device_id", deivceId);
        params.put("parent_id", parentId);
        if (beginTime != null || !"".equals(beginTime))
            params.put("begin_time", beginTime);
        if (endTime != null || !"".equals(endTime))
            params.put("end_time", endTime);

        HttpResponse response = ibmsHttpEngine.request("camera.play_gb28181", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            return response.getData();
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 播放视频 海康sdk方式
     *
     * @return
     */
    public Map<String, Object> playByHK(String deviceId,String mediaChannel, String streamType,
                                        String beginTime, String endTime)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("device_id",deviceId);
        params.put("media_channel", mediaChannel);
        params.put("stream_type", streamType);
        if (beginTime != null || !"".equals(beginTime))
            params.put("begin_time", beginTime);
        if (endTime != null || !"".equals(endTime))
            params.put("end_time", endTime);

        HttpResponse response = ibmsHttpEngine.request("camera.play_hk", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            return response.getData();
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }


    /**
     * 停止播放视频
     */
    public void stopPlay(String session)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("session", session);
        HttpResponse response = ibmsHttpEngine.request("camera.stop", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 暂停播放视频
     */
    public void pausePlay(String session)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("session", session);
        HttpResponse response = ibmsHttpEngine.request("camera.pause", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 恢复播放视频
     */
    public void resumePlay(String session)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("session", session);
        HttpResponse response = ibmsHttpEngine.request("camera.resume", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 恢复播放视频
     */
    public void changeSpeed(String session, String speed)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("session", session);
        params.put("speed", speed);
        HttpResponse response = ibmsHttpEngine.request("camera.resume", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 改变播放位置
     */
    public void changePosition(String session, String position)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("session", session);
        params.put("position", position);
        HttpResponse response = ibmsHttpEngine.request("camera.change_position", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 查询进度
     *
     * @param session
     */
    public void queryProgress(String session)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("session", session);
        HttpResponse response = ibmsHttpEngine.request("camera.query_progress", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 查询录像
     *
     * @param deviceId
     * @param parentId
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<RecordItem> queryRecord(String deviceId, String parentId,
                                        String beginTime, String endTime, String protocol)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("device_id", deviceId);
        params.put("parent_id", parentId);
        params.put("protocol", protocol);
        if (beginTime != null || !"".equals(beginTime))
            params.put("begin_time", beginTime);
        if (endTime != null || !"".equals(endTime))
            params.put("end_time", endTime);

        HttpResponse response = ibmsHttpEngine.request("camera.query_record", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            List<Map<String, Object>> maps = (List<Map<String, Object>>) response.getData().get("records");
            List<RecordItem> recordItems = new ArrayList<>();
            for (Map<String, Object> map : maps)
            {
                RecordItem recordItem = new RecordItem();
                recordItem.setDeviceId((String) map.get("device_id"));
                recordItem.setStartTime((Long) map.get("start_time"));
                recordItem.setEndTime((Long) map.get("end_time"));

                recordItems.add(recordItem);
            }

            return recordItems;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    public Map<String, Object> getSocketAddress()
    {
        Map<String, Object> params = new HashMap<>();
        HttpResponse response = ibmsHttpEngine.request("camera.socket_address", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {
            Map<String, Object> content = response.getData();
            return content;
        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

    /**
     * 云台控制
     *
     * @param protocol
     * @param videoId
     * @param command
     * @param speed
     * @param stopFlag
     */
    public void ptzControl(String protocol, String videoId, String command, String speed,
                           String stopFlag)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("protocol", protocol);
        params.put("video_id", videoId);
        params.put("command", command);
        params.put("speed", speed);
        params.put("stop_flag", stopFlag);

        HttpResponse response = ibmsHttpEngine.request("camera.ptz", params);
        if (response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR, response.getErrorMessage());
        }
    }

}

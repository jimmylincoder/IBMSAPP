package com.suntek.ibmsapp.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 录像
 *
 * @author jimmy
 */
public class RecordItem implements Serializable
{
    //设备id
    private String deviceId;

    //开始时间
    private long startTime;

    //结束时间
    private long endTime;

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public static RecordItem generateByJson(Map<String,Object> map)
    {
        RecordItem recordItem = new RecordItem();
        recordItem.setDeviceId((String) map.get("device_id"));
        recordItem.setStartTime((Long) map.get("start_time"));
        recordItem.setEndTime((Long) map.get("end_time"));

        return recordItem;
    }
}

package com.suntek.ibmsapp.model;

import android.content.Intent;

import java.io.Serializable;
import java.util.Map;

/**
 * 摄像机
 *
 * @author jimmy
 */
public class Camera implements Serializable
{
    private String id;

    private String name;

    // 1-球机  2-半球  3-固定枪机  4-遥控枪机
    private String type;

    private String isUsed;

    private String deviceId;

    private String parentId;

    private String place;

    private String orgCode;

    private long playTime;

    private String orgName;

    private String vendorName;

    private String photoBase64;

    private String playCount;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPlace()
    {
        return place;
    }

    public void setPlace(String place)
    {
        this.place = place;
    }

    public String getOrgCode()
    {
        return orgCode;
    }

    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    public void setPlayTime(long playTime)
    {
        this.playTime = playTime;
    }

    public long getPlayTime()
    {
        return playTime;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getParentId()
    {
        return parentId;
    }

    public String getOrgName()
    {
        return orgName;
    }

    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    public String getVendorName()
    {
        return vendorName;
    }

    public void setVendorName(String vendorName)
    {
        this.vendorName = vendorName;
    }

    public void setIsUsed(String isUsed)
    {
        this.isUsed = isUsed;
    }

    public String getIsUsed()
    {
        return isUsed;
    }

    public void setPhotoBase64(String photoBase64)
    {
        this.photoBase64 = photoBase64;
    }

    public String getPhotoBase64()
    {
        return photoBase64;
    }

    public String getPlayCount()
    {
        return playCount;
    }

    public void setPlayCount(String playCount)
    {
        this.playCount = playCount;
    }

    public static Camera generateByJson(Map<String, Object> content)
    {
        Camera camera = new Camera();
        camera.setId((String) content.get("id"));
        camera.setName((String) content.get("name"));
        camera.setType((String) content.get("type"));
        camera.setPlace((String) content.get("place"));
        camera.setOrgCode((String) content.get("org_code"));
        camera.setDeviceId((String) content.get("device_id"));
        camera.setParentId((String) content.get("parent_id"));
        //camera.setPlayTime((Integer) content.get("play_time"));
        Number playTime = (Number) content.get("play_time");
        camera.setPlayTime(playTime.longValue());
        camera.setOrgName((String) content.get("org_name"));
        camera.setVendorName((String) content.get("vendor_name"));
        camera.setIsUsed((String) content.get("is_used"));
        camera.setPhotoBase64((String) content.get("photo_base64"));
        camera.setPlayCount((String) content.get("play_count"));

        return camera;
    }


}

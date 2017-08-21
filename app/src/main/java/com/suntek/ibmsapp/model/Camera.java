package com.suntek.ibmsapp.model;

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

    private String type;

    private String deviceId;

    private String place;

    private String channel;

    private String orgCode;

    private String ip;

    private String port;

    private String userName;

    private String password;

    private long playTime;

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

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getOrgCode()
    {
        return orgCode;
    }

    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
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

    public static Camera generateByJson(Map<String,Object> content)
    {
        Camera camera = new Camera();
        camera.setId((String) content.get("id"));
        camera.setName((String) content.get("name"));
        camera.setType((String) content.get("type"));
        camera.setPlace((String) content.get("place"));
        camera.setChannel((String) content.get("channel"));
        camera.setOrgCode((String) content.get("org_code"));
        camera.setIp((String) content.get("ip"));
        camera.setPort((String) content.get("port"));
        camera.setUserName((String) content.get("user_name"));
        camera.setPassword((String) content.get("password"));
        camera.setDeviceId((String) content.get("device_id"));
//        camera.setPlayTime((Integer) content.get("play_time"));

        return camera;
    }
}

package com.suntek.ibmsapp.component;

import com.alibaba.fastjson.annotation.JSONField;
import com.suntek.ibmsapp.component.http.BaseHttpRequest;

import java.io.Serializable;
import java.util.Map;

/**
 * http请求
 *
 * @author jimmy
 */
public class HttpRequest extends BaseHttpRequest implements Serializable
{
    @JSONField(name = "service")
    private String serviceName;

    private Map<String,Object> params;

    public Map<String, Object> getParams()
    {
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }
}

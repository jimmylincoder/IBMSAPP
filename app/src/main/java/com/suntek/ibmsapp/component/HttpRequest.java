package com.suntek.ibmsapp.component;

import java.util.Map;

/**
 * http请求
 *
 * @author jimmy
 */
public class HttpRequest
{
    private String os;

    private Map<String,Object> params;

    public String getOs()
    {
        return os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public Map<String, Object> getParams()
    {
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
}

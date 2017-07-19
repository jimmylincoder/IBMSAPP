package com.suntek.ibmsapp.component.base;

import com.suntek.ibmsapp.component.HttpRequest;

import java.util.Map;

/**
 * 请求engine
 *
 * @author jimmy
 */
public abstract class BaseHttpEngine
{
    //是否打印log
    protected boolean isLog;

    protected String baseUrl;

    protected BaseHttpEngine()
    {

    }

    protected HttpRequest getRequest(Map<String,Object> params, HttpRequest request)
    {
        request.setParams(params);
        return request;
    }




}

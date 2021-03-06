package com.suntek.ibmsapp.component;

import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.Config;
import com.suntek.ibmsapp.component.http.BaseHttpProcesser;


import java.util.Map;

/**
 * ibms请求
 *
 * @author jimmy
 */
public class IbmsHttpEngine extends BaseHttpProcesser
{
    @Config("http.ibms_url")
    private String ibmsUrl;

    //是否打印Log
    @Config("http.is_log")
    private boolean isLog;

    //超时
    @Config("http.time_out")
    private int timeOut;

    public IbmsHttpEngine()
    {
        //init(ibmsUrl,null,isLog,timeOut);
        init(ibmsUrl,"Yes###",isLog,timeOut);
    }

    @Override
    public HttpResponse request(String serviceName, Map<String,Object> params)
    {
        HttpResponse httpResponse = new HttpResponse();
        setBaseHttpResponse(httpResponse);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setServiceName(serviceName);
        setBaseHttpRequest(httpRequest);
        httpResponse = (HttpResponse) super.request(serviceName,params);
        return httpResponse;
    }
}

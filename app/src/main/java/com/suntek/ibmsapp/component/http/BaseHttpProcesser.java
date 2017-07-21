package com.suntek.ibmsapp.component.http;

import android.util.Log;


import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.core.ComponentEngine;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * 网络连接管理抽象类
 *
 * @author Devin
 */
public abstract class BaseHttpProcesser extends BaseComponent
{
    private BaseHttpEngine baseHttpEngine;
    private String oldUrl;

    private BaseHttpResponse baseHttpResponse;
    private BaseHttpRequest baseHttpRequest;

    public void init(String baseUrl, String secretKey, boolean isLog, int timeOut)
    {
        setIsExtends(true);
        /**-------通过反射获取操作类对象*********/
        try
        {
            String str = ComponentEngine.getInstance(BaseHttpEngine.class).getClass().getName();
            Class<?> clazz = Class.forName(str);
            this.oldUrl = baseUrl;
            Constructor constructor = clazz.getConstructor(String.class, String.class, boolean.class, int.class);
            baseHttpEngine = (BaseHttpEngine) constructor.newInstance
                    (baseUrl, secretKey, isLog, timeOut);
        }
        catch (Exception e)
        {
            Log.e("HttpEngine", "Http处理类初始化错误，请检查配置文件！");
        }

    }

    /**
     * 网络请求
     *
     * @param serviceName 服务器接口
     * @param params      参数
     * @return 网络回馈
     * @throws FHttpException
     */
    public BaseHttpResponse request(final String serviceName, final Map<String, Object> params) throws FHttpException
    {
        if(baseHttpResponse==null)
        {
            baseHttpResponse=new BaseHttpResponse();
        }
        if(baseHttpRequest==null)
        {
            baseHttpRequest=new BaseHttpRequest();
        }
        return baseHttpEngine.request(serviceName, params, baseHttpResponse,baseHttpRequest);
    }

    public void setUrl(String url)
    {
        baseHttpEngine.setBaseUrl(oldUrl + "?" + url);
    }

    public String getUrl()
    {
        return baseHttpEngine.getBaseUrl();
    }

    public BaseHttpResponse getBaseHttpResponse()
    {
        return baseHttpResponse;
    }

    public void setBaseHttpResponse(BaseHttpResponse baseHttpResponse)
    {
        this.baseHttpResponse = baseHttpResponse;
    }

    public BaseHttpRequest getBaseHttpRequest()
    {
        return baseHttpRequest;
    }

    public void setBaseHttpRequest(BaseHttpRequest baseHttpRequest)
    {
        this.baseHttpRequest = baseHttpRequest;
    }
}

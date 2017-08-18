package com.suntek.ibmsapp.component;

import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.core.Config;
import com.suntek.ibmsapp.network.RetrofitHelper;

import java.util.Map;


/**
 * @author jimmy
 */
public class HttpEngine extends BaseComponent
{
    @Config("ibms_server_ip")
    String serverIp;

    public HttpResponse request(String serviceName, Map<String, Object> params)
    {
        String[] service = serviceName.split(".");
        String model = service[0];
        String action = service[1];
        HttpResponse httpResponse = RetrofitHelper.getIbmsApi(serverIp)
                .api(model, action, getRequest(params));

        return httpResponse;
    }

    public HttpRequest getRequest(Map<String, Object> params)
    {
        HttpRequest request = new HttpRequest();
        request.setParams(params);
        return request;
    }
}
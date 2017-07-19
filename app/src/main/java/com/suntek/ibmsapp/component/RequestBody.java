package com.suntek.ibmsapp.component;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数
 *
 * @author jimmy
 */
public class RequestBody
{
    private HttpRequest request = new HttpRequest();

    private Map<String,Object> params = new HashMap<>();

    public RequestBody()
    {

    }

    public RequestBody putParams(String key,Object value,boolean check,String message) throws Exception
    {
        if(check && value == null || "".equals(value))
        {
            throw new Exception(message);
        }
        params.put(key,value);
        return this;
    }

    public HttpRequest build()
    {
        request.setParams(params);
        return request;
    }
}

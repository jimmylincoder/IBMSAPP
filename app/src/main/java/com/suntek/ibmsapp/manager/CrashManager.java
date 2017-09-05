package com.suntek.ibmsapp.manager;

import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.component.IbmsHttpEngine;
import com.suntek.ibmsapp.component.core.Autowired;
import com.suntek.ibmsapp.component.core.BaseComponent;
import com.suntek.ibmsapp.component.http.FHttpException;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常管理类
 *
 * @author jimmy
 */
public class CrashManager extends BaseComponent
{
    @Autowired
    IbmsHttpEngine ibmsHttpEngine;

    public void logCrash(String message)
    {
        Map<String,Object> params = new HashMap<>();
        params.put("error_message",message);

        HttpResponse response = ibmsHttpEngine.request("crash.log",params);
        if(response.getCode() == HttpResponse.STATUS_SUCCESS)
        {

        }
        else
        {
            throw new FHttpException(FHttpException.CODE_BUSINESS_ERROR,response.getErrorMessage());
        }
    }
}

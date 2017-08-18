package com.suntek.ibmsapp.component;

import com.alibaba.fastjson.annotation.JSONField;
import com.suntek.ibmsapp.component.http.BaseHttpResponse;

import java.util.Map;

/**
 * 接口返回数据
 *
 * @author jimmy
 */
public class HttpResponse extends BaseHttpResponse
{
    // 成功状态
    public static final int STATUS_SUCCESS = 200;

    // 失败状态
    public static final int STATUS_FAILURE = 1;

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "message")
    private String errorMessage;

    @JSONField(name = "data")
    private Map<String,Object> data;



    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }
}

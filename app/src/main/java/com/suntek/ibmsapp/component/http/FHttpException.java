package com.suntek.ibmsapp.component.http;

/**
 * API Exception
 *
 * @author jimmy
 */
public class FHttpException extends RuntimeException
{
    // 网络错误
    public static final int CODE_NETWORK_ERROR = 2000;

    // 应答数据格式错误
    public static final int CODE_RESPONSE_DATA_ERROR = 2001;

    // 业务逻辑错误
    public static final int CODE_BUSINESS_ERROR = 2002;

    //超时
    public static final int CODE_TIMEOUT_ERROR=2003;

    // status code
    private int code;

    public FHttpException(int code, String message)
    {
        super(message);
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }
}

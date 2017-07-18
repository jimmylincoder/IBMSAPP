package com.suntek.ibmsapp.component;

/**
 * 接口返回数据
 *
 * @author jimmy
 */
public class HttpResponse<T>
{
    // 成功状态
    public static final int STATUS_SUCCESS = 200;

    // 失败状态
    public static final int STATUS_FAILURE = 1;

    private int status;

    private String errorMessage;

    private T content;

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public T getContent()
    {
        return content;
    }

    public void setContent(T content)
    {
        this.content = content;
    }
}

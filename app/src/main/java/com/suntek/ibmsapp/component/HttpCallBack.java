package com.suntek.ibmsapp.component;

/**
 * @author jimmy
 */
public interface HttpCallBack
{
    void onSuccess(HttpResponse response);

    void onFail(Throwable throwable);
}

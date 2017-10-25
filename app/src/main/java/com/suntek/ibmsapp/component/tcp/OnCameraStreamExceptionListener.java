package com.suntek.ibmsapp.component.tcp;

/**
 * 视频流接收异常监听器
 *
 * @author jimmy
 */
public interface OnCameraStreamExceptionListener
{
    /**
     * 接收数据异常
     *
     * @param throwable
     */
    void onReceiveDataException(Throwable throwable);

    /**
     * 连接异常
     *
     * @param throwable
     */
    void onConnectException(Throwable throwable);

    /**
     * 处理数据异常
     *
     * @param throwable
     */
    void onHandleDataException(Throwable throwable);
}

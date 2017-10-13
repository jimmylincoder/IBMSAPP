package com.suntek.ibmsapp.component.tcp;

/**
 * 视频流异常
 *
 * @author jimmy
 */
public class CameraStreamException extends Exception
{
    public CameraStreamException()
    {
        super();
    }

    public CameraStreamException(String message)
    {
        super(message);
    }

    public CameraStreamException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CameraStreamException(Throwable cause)
    {
        super(cause);
    }

    protected CameraStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.suntek.ibmsapp.component.http;

import java.io.Serializable;

/**
 * Response
 *
 * @author kutzhang@gmail.com
 */
public class BaseHttpResponse implements Serializable
{
    // 成功状态
    public static final int STATUS_SUCCESS = 200;

    // 失败状态
    public static final int STATUS_FAILURE = 1;

}

package com.suntek.ibmsapp.component.httpclient;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.suntek.ibmsapp.component.base.BaseActivity;
import com.suntek.ibmsapp.component.cache.ACache;
import com.suntek.ibmsapp.component.core.ComponentEngine;
import com.suntek.ibmsapp.component.http.BaseHttpEngine;
import com.suntek.ibmsapp.component.http.BaseHttpRequest;
import com.suntek.ibmsapp.component.http.BaseHttpResponse;
import com.suntek.ibmsapp.component.http.Encryptor;
import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.util.SaveDataWithSharedHelper;

import org.apache.commons.io.IOUtils;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * HttpClient网络请求操作类
 *
 * @author Devin
 */
public class HttpClientResponse extends BaseHttpEngine
{
    // Http Client
    private HttpClient httpClient;

    private SaveDataWithSharedHelper sharedHelper;

    public HttpClientResponse()
    {

    }

    /**
     * 构造函数
     *
     * @param baseUrl   基础API服务地址
     * @param secretKey 加密KEY
     * @param isLog     是否打印log信息
     * @param timeout   设置超时时间
     */
    public HttpClientResponse(String baseUrl, String secretKey, boolean isLog, int timeout)
    {
        super(baseUrl, secretKey, isLog, timeout);
        this.httpClient = new DefaultHttpClient();
        this.sharedHelper = new SaveDataWithSharedHelper();

        //设置超时
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout * 1000);
    }

    /**
     * 网络请求
     *
     * @param serviceName      服务器接口名
     * @param params           参数
     * @param baseHttpResponse 要发送的分装类
     * @param baseHttpRequest  要返回的分装类
     */
    @Override
    public BaseHttpResponse request(String serviceName, Map<String, Object> params, BaseHttpResponse baseHttpResponse, BaseHttpRequest baseHttpRequest) throws FHttpException
    {
        Encryptor encryptor = (Encryptor) ComponentEngine.getInstance(Encryptor.class);
        baseHttpRequest = getRequest(params, baseHttpRequest);


        String debugMessage = JSON.toJSONString(baseHttpRequest, true);
        if (isLog)
        {
            Log.d("ApiEngine", String.format("request -> %s\n%s", serviceName, debugMessage));
        }


        byte[] jsonBytes = JSON.toJSONBytes(baseHttpRequest);
        byte[] encodedBytes =
                secretKey == null ? jsonBytes : encryptor.encode(jsonBytes, secretKey);

        String serverIp = sharedHelper.getString("server_ip");
        String serverPort = sharedHelper.getString("server_port");
        if(!"".equals(serverIp) && !"".equals(serverPort))
        {
            baseUrl = "http://" + serverIp + ":" + serverPort + "/api";
        }
        HttpPost post = new HttpPost(baseUrl);
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(encodedBytes);
        byteArrayEntity.setContentType("application/json");
        post.setEntity(byteArrayEntity);
        org.apache.http.HttpResponse httpResponse = null;
        try
        {
            httpResponse = httpClient.execute(post);
        }
        catch (ConnectTimeoutException e)
        {
            throw new FHttpException(FHttpException.CODE_TIMEOUT_ERROR, e.getMessage());
        }
        catch (SocketTimeoutException e)
        {
            throw new FHttpException(FHttpException.CODE_TIMEOUT_ERROR, e.getMessage());
        }
        catch (NoHttpResponseException e)
        {
            throw new FHttpException(FHttpException.CODE_TIMEOUT_ERROR, e.getMessage());
        }
        catch (IOException e)
        {
            throw new FHttpException(FHttpException.CODE_NETWORK_ERROR, e.getMessage());
        }


        byte[] responseBytes;
        if (secretKey != null)
        {
            try
            {
                responseBytes = encryptor.decode(
                        IOUtils.toByteArray(httpResponse.getEntity().getContent()), secretKey
                );
            }
            catch (IOException e)
            {
                throw new FHttpException(FHttpException.CODE_RESPONSE_DATA_ERROR, e.getMessage());
            }
        }
        else
        {
            try
            {
                responseBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            }
            catch (IOException e)
            {
                throw new FHttpException(FHttpException.CODE_RESPONSE_DATA_ERROR, e.getMessage());
            }
        }


        try
        {
            String responseString = new String(responseBytes, "utf-8");
            if (isLog)
            {
                Log.d("ApiEngine", String.format(
                        "response string -> %s\n%s", serviceName, responseString
                ));
            }
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }


        baseHttpResponse = JSON.parseObject(responseBytes, baseHttpResponse.getClass());


        String debugMessageResponse = JSON.toJSONString(baseHttpResponse, true);
        if (isLog)
        {
            Log.d("ApiEngine", String.format("response -> %s\n%s", serviceName, debugMessageResponse));
        }

        return baseHttpResponse;
    }
}

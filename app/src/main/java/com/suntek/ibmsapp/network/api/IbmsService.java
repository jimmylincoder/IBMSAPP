package com.suntek.ibmsapp.network.api;

import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * ibms请求
 *
 * @author jimmy
 */
public interface IbmsService
{
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("{model}/{action}")
    HttpResponse api(@Path("model") String model,
                     @Path("action") String action,
                     @Body HttpRequest request);
}

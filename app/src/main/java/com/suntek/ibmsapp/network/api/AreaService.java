package com.suntek.ibmsapp.network.api;

import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 区域api
 *
 * @author jimmy
 */
public interface AreaService
{
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("area/list")
    Observable<HttpResponse> list(@Body HttpRequest request);
}

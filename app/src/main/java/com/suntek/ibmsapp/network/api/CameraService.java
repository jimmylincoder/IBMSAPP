package com.suntek.ibmsapp.network.api;

import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.model.Camera;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 摄像机api
 *
 * @author jimmy
 */
public interface CameraService
{
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("camera/list")
    Observable<HttpResponse> list(@Body HttpRequest request);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("camera/list_by_keyword")
    Observable<HttpResponse> listByKeyWord(@Body HttpRequest request);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("camera/add_history")
    Observable<HttpResponse> addHistory(@Body HttpRequest request);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("camera/history_list")
    Observable<HttpResponse> history(@Body HttpRequest request);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("camera/address")
    Observable<HttpResponse> address(@Body HttpRequest request);

}

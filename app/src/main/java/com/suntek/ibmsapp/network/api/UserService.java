package com.suntek.ibmsapp.network.api;

import com.suntek.ibmsapp.component.HttpRequest;
import com.suntek.ibmsapp.component.HttpResponse;
import com.suntek.ibmsapp.model.User;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 用户api
 *
 * @author jimmy
 */
public interface UserService
{
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("user/login")
    Observable<HttpResponse<User>> login(@Body HttpRequest request);
}

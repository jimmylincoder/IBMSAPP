package com.suntek.ibmsapp.network.api;


import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 电影服务
 */
public interface MovieService
{
    @GET("top250")
    Observable<Map<String,Object>> getTopMovie(@Query("start") int start, @Query("count") int count);
}

package com.netease.study.practice;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by zw on 16/9/8.
 */


public interface HttpBinService {
    @GET("ip")
    Call<IpRepo> getIp();
}

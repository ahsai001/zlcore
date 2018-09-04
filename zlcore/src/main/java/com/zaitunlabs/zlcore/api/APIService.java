package com.zaitunlabs.zlcore.api;

import com.zaitunlabs.zlcore.models.GenericResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by ahsai on 6/9/2017.
 */

public interface APIService {
    @FormUrlEncoded
    @POST("login")
    Call<GenericResponseModel> login(@Header("Authorization") String basicAuth, @Field("app_type") String app_type);

    @FormUrlEncoded
    @POST("device_token")
    Call<GenericResponseModel> sendToken(@Header("Authorization") String basicAuth,
                                         @Field("app_type") String app_type,
                                         @Field("device_token") String device_token);

}

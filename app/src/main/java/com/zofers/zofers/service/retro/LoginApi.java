package com.zofers.zofers.service.retro;

import com.zofers.zofers.model.oldapi.AuthResult;
import com.zofers.zofers.model.oldapi.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

public interface LoginApi {

    @POST("/users/signup")
    Call<Object> register(@Body LoginRequest request);

    @POST("/users/login")
    Call<AuthResult> logIn(@Body LoginRequest request);


}

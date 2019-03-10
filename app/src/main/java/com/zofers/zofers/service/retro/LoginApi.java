package com.zofers.zofers.service.retro;

import com.zofers.zofers.model.AuthResult;
import com.zofers.zofers.model.LoginRequest;
import com.zofers.zofers.model.Offer;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

public interface LoginApi {

    @POST("/users/signup")
    Call<Object> register(@Body LoginRequest request);

    @POST("/users/login")
    Call<AuthResult> logIn(@Body LoginRequest request);


}

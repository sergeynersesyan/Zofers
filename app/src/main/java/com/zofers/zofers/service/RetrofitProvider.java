package com.zofers.zofers.service;

import android.content.Intent;

import com.zofers.zofers.App;
import com.zofers.zofers.service.retro.LoginApi;
import com.zofers.zofers.service.retro.OfferApi;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitProvider {

    private OfferApi offerApi;
    private LoginApi loginApi;
    private final String BASE_URL = "http://192.168.0.101:3000/";

    private static final RetrofitProvider ourInstance = new RetrofitProvider();

    public static RetrofitProvider getInstance() {
        return ourInstance;
    }

    private RetrofitProvider() {
    }

    public String getBASE_URL() {
        return BASE_URL;
    }

    public LoginApi getLoginApi() {
        if (loginApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            loginApi = retrofit.create(LoginApi.class);
        }
        return loginApi;
    }

    public OfferApi getOfferApi() {
        if (offerApi == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient
                    .addInterceptor(new HeaderInterceptor())
                    .authenticator((route, response) -> {
//                        App.getInstance().getUserManager().asumaUnauthorised();
                        return null;
                    });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            offerApi = retrofit.create(OfferApi.class);
        }
        return offerApi;
    }

}

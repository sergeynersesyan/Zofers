package com.zofers.zofers.service;


import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();


            request = request.newBuilder()
//                    .addHeader("Authorization", userManager.getAuthToken())
                    .build();

        return chain.proceed(request);
    }
}
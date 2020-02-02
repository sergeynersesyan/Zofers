package com.zofers.zofers.messenger.messenger.service;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//        if (MessengerService.getToken() != null) {
//            request = request.newBuilder()
//                    .addHeader("Authorization", "Bearer " + MessengerService.getToken())
//                    .build();
//        }
        Response response = chain.proceed(request);
        return response;
    }
}
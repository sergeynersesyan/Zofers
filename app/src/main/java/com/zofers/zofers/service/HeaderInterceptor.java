package com.zofers.zofers.service;


import android.support.annotation.NonNull;

import com.zofers.zofers.staff.UserManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    private UserManager userManager;

    public HeaderInterceptor (UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        if (userManager.hasAuthorized()) {
            request = request.newBuilder()
                    .addHeader("Authorization", userManager.getAuthToken())
                    .build();
        }

        return chain.proceed(request);
    }
}
package com.zofers.zofers;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zofers.zofers.service.ServiceApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

public class App extends Application {

    private static App app;
    private ServiceApi api;
    private final String BASE_URL = "http://192.168.0.103:3000/";

    @Override
    public void onCreate() {

        super.onCreate();
        Fresco.initialize(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ServiceApi.class);
        app = this;
    }

    public static App getInstance () {
        return app;
    }

    public ServiceApi getApi () {
        return api;
    }

    public String getBASE_URL() {
        return BASE_URL;
    }
}

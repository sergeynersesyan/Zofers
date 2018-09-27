package com.zofers.zofers;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zofers.zofers.setvice.ServiceApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

public class App extends Application {

    private ServiceApi api;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ServiceApi.class);
    }

}

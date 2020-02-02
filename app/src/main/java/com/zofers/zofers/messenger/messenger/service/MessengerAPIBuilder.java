package com.zofers.zofers.messenger.messenger.service;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessengerAPIBuilder {
//      192.168.88.55:3000
//      messenger9419.cloudapp.net
    public static final String BASE_URL = "https://messenger.sololearn.com/";
//    public static final String BASE_URL = "https://stage.sololearn.com/messenger/";
//    public static final String BASE_URL = "http://messenger9419.cloudapp.net:8445/";

    private static Retrofit retrofit = null;
    private static TokenAuthenticator tokenAuthenticator;

    public static Retrofit getClient(Context context) {

        if(retrofit == null) {
            tokenAuthenticator = new TokenAuthenticator();
            try {
                ProviderInstaller.installIfNeeded(context);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(new HeaderInterceptor())
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .authenticator(tokenAuthenticator);


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static TokenAuthenticator getTokenAuthenticator() {
        return tokenAuthenticator;
    }
}

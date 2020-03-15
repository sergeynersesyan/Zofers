package com.zofers.zofers.service.retro;

import com.zofers.zofers.model.Offer;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

public interface OfferApi {
    @Multipart
    @POST("/offers") // stack
    Call<ResponseBody> createOffer(@Part("offer") RequestBody offer, @Part MultipartBody.Part image);

    @GET("/offers")
    Call<List<Offer>> getFeed(@Query("q") String query);

    @DELETE("/offers/{id}")
    Call<ResponseBody> deleteOffer(@Path("id") String id);

}

package com.zofers.zofers.vvm.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.zofers.zofers.App;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.service.RetrofitProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedViewModel extends ViewModel {

    public void getFeed (Callback<List<Offer>> callback) {
        Call<List<Offer>> call = RetrofitProvider.getInstance().getOfferApi().getFeed();
        call.enqueue(callback);
    }
}

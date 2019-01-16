package com.zofers.zofers.vvm.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.zofers.zofers.App;

import okhttp3.ResponseBody;
import retrofit2.Callback;

public class OfferViewModel extends ViewModel {

    public void delete(String offerId, Callback<ResponseBody> callback) {
        App.getInstance().getApi().deleteOffer(offerId).enqueue(callback);
    }
}

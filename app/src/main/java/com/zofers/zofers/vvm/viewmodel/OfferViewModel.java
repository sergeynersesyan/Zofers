package com.zofers.zofers.vvm.viewmodel;


import androidx.lifecycle.ViewModel;

import com.zofers.zofers.service.RetrofitProvider;

import okhttp3.ResponseBody;
import retrofit2.Callback;

public class OfferViewModel extends ViewModel {

	public void delete(String offerId, Callback<ResponseBody> callback) {
		RetrofitProvider.getInstance().getOfferApi().deleteOffer(offerId).enqueue(callback);
	}
}

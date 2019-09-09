package com.zofers.zofers.vvm.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.service.RetrofitProvider;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ItemCreationViewModel extends ViewModel {

	private Offer offer;

	public void createOffer(File image, Callback<ResponseBody> callback) {
//
		RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), image);
		MultipartBody.Part body = MultipartBody.Part.createFormData("offerImage", image.getName(), reqFile);
		RequestBody offerParam = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(offer));

		Call<ResponseBody> call = RetrofitProvider.getInstance().getOfferApi().createOffer(offerParam, body);

		call.enqueue(callback);
	}

	public Offer getOffer() {
		if (offer == null) {
			offer = new Offer();
		}
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}
}

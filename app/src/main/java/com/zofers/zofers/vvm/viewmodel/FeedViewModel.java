package com.zofers.zofers.vvm.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.zofers.zofers.App;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.service.RetrofitProvider;
import com.zofers.zofers.staff.MessageHelper;
import com.zofers.zofers.vvm.activity.HomeActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedViewModel extends AppViewModel {

    private MutableLiveData<List<Offer>> offersList = new MutableLiveData<>();

    public MutableLiveData<List<Offer>> getOffersList() {
        return offersList;
    }

    public void load () {
        Call<List<Offer>> call = RetrofitProvider.getInstance().getOfferApi().getFeed();
        call.enqueue(new Callback<List<Offer>>() {
            @Override
            public void onResponse(Call<List<Offer>> call, Response<List<Offer>> response) {
                if (!response.isSuccessful()) {
                    state.setValue(States.ERROR);
                    return;
                }
                offersList.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Offer>> call, Throwable t) {
                state.setValue(States.FAIL);
            }
        });
    }
}

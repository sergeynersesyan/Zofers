package com.zofers.zofers.vvm.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zofers.zofers.model.Offer;
import com.zofers.zofers.service.RetrofitProvider;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AppViewModel extends ViewModel {
    protected MutableLiveData<Integer> state;
    protected RetrofitProvider retrofitProvider;

    public AppViewModel () {
        state = new MutableLiveData<>();
        state.setValue(States.NONE);
        retrofitProvider = RetrofitProvider.getInstance();
    }

    //todo move to helper class
    protected HashMap <String, Object> convertToHashMap (Object offer) {
        Gson gson = new Gson();
        String json = gson.toJson(offer);
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public MutableLiveData<Integer> getState() {
        return state;
    }
}

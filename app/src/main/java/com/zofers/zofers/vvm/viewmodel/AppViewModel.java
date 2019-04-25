package com.zofers.zofers.vvm.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.zofers.zofers.service.RetrofitProvider;

public class AppViewModel extends ViewModel {
    protected MutableLiveData<Integer> state;
    protected RetrofitProvider retrofitProvider;

    public AppViewModel () {
        state = new MutableLiveData<>();
        state.setValue(States.NONE);
        retrofitProvider = RetrofitProvider.getInstance();
    }

    public MutableLiveData<Integer> getState() {
        return state;
    }
}

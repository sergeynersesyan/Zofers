package com.zofers.zofers.vvm.viewmodel;

import com.zofers.zofers.App;
import com.zofers.zofers.model.AuthResult;
import com.zofers.zofers.model.LoginRequest;
import com.zofers.zofers.service.retro.LoginApi;
import com.zofers.zofers.staff.MessageHelper;
import com.zofers.zofers.vvm.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AppViewModel {

    private LoginApi api;

    public LoginViewModel() {
        api = retrofitProvider.getLoginApi();
    }

    public void register(String email, String password) {

        LoginRequest requestBody = new LoginRequest(email, password);
        state.setValue(States.LOADING);
        api.register(requestBody).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (!response.isSuccessful()) {
                    state.setValue(States.ERROR);
                    return;
                }
                login(email, password);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                state.setValue(States.FAIL);
            }
        });
    }

    public void login(String email, String password) {
        LoginRequest requestBody = new LoginRequest(email, password);
        state.setValue(States.LOADING);
        api.logIn(requestBody).enqueue(new Callback<AuthResult>() {
            @Override
            public void onResponse(Call<AuthResult> call, Response<AuthResult> response) {
                if (!response.isSuccessful()) {
                    state.setValue(States.ERROR);
                    return;
                }
                App.getInstance().getUserManager().setAuthToken(response.body().getToken());
                state.setValue(States.FINISH);
            }

            @Override
            public void onFailure(Call<AuthResult> call, Throwable t) {
                state.setValue(States.FAIL);
            }
        });

    }

    public boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 5;
    }


}

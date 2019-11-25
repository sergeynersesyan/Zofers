package com.zofers.zofers.staff;

import android.content.Intent;

import com.zofers.zofers.login.LoginActivity;

public class UserManager {
    private String KEY_AUTH_TOKEN = "k_a_tok";

    private PreferenceService preferences;
    private String authToken;

    public UserManager(PreferenceService preferenceService) {
        preferences = preferenceService;
    }

    public String getAuthToken() {
        if (authToken == null || authToken.length() == 0) {
            authToken = preferences.getString(KEY_AUTH_TOKEN, null);
        }
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        preferences.setString(KEY_AUTH_TOKEN, authToken);
    }

    public boolean hasAuthorized() {
        return getAuthToken() != null;
    }

    public void asumaUnauthorised() {
        setAuthToken(null);
        Intent intent = new Intent(preferences.getContext(), LoginActivity.class);
        preferences.getContext().startActivity(intent);
    }
}

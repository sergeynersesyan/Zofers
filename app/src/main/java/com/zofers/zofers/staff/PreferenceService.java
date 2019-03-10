package com.zofers.zofers.staff;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceService {
    private Context context;

    public PreferenceService (Context c) {
        context = c;
    }

    public void setString (String key, String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    public String getString (String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }


    public SharedPreferences getPreferences(){
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public Context getContext() {
        return context;
    }
}

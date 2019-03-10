package com.zofers.zofers;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zofers.zofers.staff.PreferenceService;
import com.zofers.zofers.staff.UserManager;

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

public class App extends Application {

    private static App app;
    private PreferenceService preferenceService;
    private UserManager userManager;

    @Override
    public void onCreate() {
        super.onCreate();
        preferenceService = new PreferenceService(getApplicationContext());
        userManager = new UserManager(preferenceService);
        Fresco.initialize(this);
        app = this;
    }

    public static App getInstance () {
        return app;
    }

    public PreferenceService getPreferenceService() {
        return preferenceService;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}

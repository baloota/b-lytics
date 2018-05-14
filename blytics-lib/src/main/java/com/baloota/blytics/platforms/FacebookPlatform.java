package com.baloota.blytics.platforms;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baloota.blytics.AnalyticsPlatform;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Sergey B on 10.05.2018.
 */
public class FacebookPlatform extends AnalyticsPlatform {

    private AppEventsLogger logger;

    @Override
    public boolean isEnabled(@NonNull Application application) {

        boolean enabled = false;

        try {
            enabled = Class.forName("com.facebook.appevents.AppEventsLogger") != null;
        } catch (ClassNotFoundException ignored) {
        }

        return enabled;
    }

    @Override
    public void initialize(@NonNull Application application) {
        Log.i("FacebookPlatform", "Initialized");

        if (FacebookSdk.isInitialized()) {
            AppEventsLogger.activateApp(application);
            logger = AppEventsLogger.newLogger(application);
        } else {
            throw new IllegalStateException("Please initialize Facebook SDK");
        }

    }

    @Override
    public void track(@NonNull String event, @NonNull Bundle params) {
        logger.logEvent(event, params);
    }
}

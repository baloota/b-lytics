package com.baloota.blytics.platforms;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baloota.blytics.AnalyticsPlatform;
import com.baloota.blytics.BuildConfig;
import com.baloota.blytics.model.Session;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Sergey B on 10.05.2018.
 */
public class FacebookPlatform extends AnalyticsPlatform {

    private static final int MAX_PARAM_LENGTH = 100;

    private AppEventsLogger logger;
    private Application application;

    @Override
    public boolean isEnabled(@NonNull Application application) {
        this.application = application;

        boolean enabled = false;

        try {
            enabled = Class.forName("com.facebook.appevents.AppEventsLogger") != null;
        } catch (ClassNotFoundException ignored) {
        }

        return enabled;
    }

    @Override
    public void initialize(@NonNull Application application) {
        if (FacebookSdk.isInitialized()) {
            AppEventsLogger.activateApp(application);
            logger = AppEventsLogger.newLogger(application);

            if (BuildConfig.DEBUG) {
                FacebookSdk.setIsDebugEnabled(true);
                FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
            }

            Log.i("FacebookPlatform", "Initialized");
        } else {
            throw new IllegalStateException("Please initialize Facebook SDK");
        }

    }

    @Override
    public void track(@NonNull String event, @NonNull Bundle params) {
        logger.logEvent(event, ensureParamsLength(params, MAX_PARAM_LENGTH));
    }

    @Override
    public void onSessionStart(Session session) {
        AppEventsLogger.activateApp(application);
    }

    @Override
    public void onSessionFinish(Session session) {
    }

    @Override
    public void setUserId(@NonNull String userId) {
        AppEventsLogger.setUserID(userId.length() > 100 ? userId.substring(0, 100) : userId);
    }

    @Override
    public void setUserProperty(String property, String value) {
        Bundle params = new Bundle();
        params.putString(property, value);
        AppEventsLogger.updateUserProperties(params, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                Log.i("FacebookPlatform", "User property update result: " + response.getRawResponse());
            }
        });
    }
}

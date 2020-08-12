package com.baloota.blytics.platforms;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baloota.blytics.AnalyticsPlatform;
import com.baloota.blytics.model.Session;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Sergey B on 14.05.2018.
 */
public class FirebasePlatform extends AnalyticsPlatform {

    private static final int MAX_PARAM_LENGTH = 100;

    private FirebaseAnalytics analytics;

    @Override
    public String getName() {
        return "Firebase";
    }

    @Override
    public boolean isEnabled(@NonNull Application application) {

        boolean enabled = false;

        try {
            enabled = Class.forName("com.google.firebase.analytics.FirebaseAnalytics") != null;
        } catch (ClassNotFoundException ignored) {
            Log.i("FirebasePlatform", "FirebaseAnalytics not found!");
        }

        return enabled;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void initialize(@NonNull Application application, boolean debug) {
        super.initialize(application, debug);
        analytics = FirebaseAnalytics.getInstance(application);
        Log.i("FirebasePlatform", "Initialized");
    }

    @Override
    public void track(@NonNull String event, @NonNull Bundle params) {
        analytics.logEvent(event, ensureParamsLength(params, MAX_PARAM_LENGTH));
    }

    @Override
    public void onSessionStart(Session session) {
    }

    @Override
    public void onSessionFinish(Session session) {

    }

    @Override
    public void setUserId(@NonNull String userId) {
        analytics.setUserId(userId.length() > 36 ? userId.substring(0, 36) : userId);
    }

    @Override
    public void setUserProperty(String property, String value) {
        analytics.setUserProperty(property, value);
    }
}

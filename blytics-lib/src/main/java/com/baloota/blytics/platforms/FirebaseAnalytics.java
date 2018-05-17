package com.baloota.blytics.platforms;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.baloota.blytics.AnalyticsPlatform;

/**
 * Created by Sergey B on 14.05.2018.
 */
public class FirebaseAnalytics extends AnalyticsPlatform {

    @Override
    public boolean isEnabled(@NonNull Application application) {

        boolean enabled = false;

        try {
            enabled = Class.forName("com.google.firebase.analytics.FirebaseAnalytics") != null;
        } catch (ClassNotFoundException ignored) {
        }

        return enabled;
    }

    @Override
    public void initialize(@NonNull Application application) {

    }

    @Override
    public void track(@NonNull String event, @NonNull Bundle params) {

    }
}

package com.baloota.blytics.platforms;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baloota.blytics.AnalyticsPlatform;
import com.baloota.blytics.model.Session;
import com.flurry.android.FlurryAgent;

public class FlurryPlatform extends AnalyticsPlatform {
    private Application application;

    @Override
    public boolean isEnabled(@NonNull Application application) {

        boolean enabled = false;

        try {
            enabled = Class.forName("com.flurry.android.FlurryAgent") != null;
        } catch (ClassNotFoundException ignored) {
            Log.i("FlurryPlatform", "FlurryAnalytics not found!");
        }

        return enabled;
    }

    @Override
    public void initialize(@NonNull Application application) {
        this.application = application;
    }

    @Override
    public void track(@NonNull String event, @NonNull Bundle params) {
        FlurryAgent.logEvent(event, asMap(ensureParamsLength(params, 100)));
    }

    @Override
    public void onSessionStart(Session session) {
        FlurryAgent.onStartSession(application);
    }

    @Override
    public void onSessionFinish(Session session) {
        FlurryAgent.onEndSession(application);
    }

    @Override
    public void setUserId(@NonNull String userId) {
        FlurryAgent.setUserId(userId);
    }

    @Override
    public void setUserProperty(String property, String value) {
    }
}

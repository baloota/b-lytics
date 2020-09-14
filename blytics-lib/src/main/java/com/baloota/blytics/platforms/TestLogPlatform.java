package com.baloota.blytics.platforms;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baloota.blytics.AnalyticsPlatform;
import com.baloota.blytics.model.Session;

/**
 * Created by Sergey B on 10.05.2018.
 */
public class TestLogPlatform extends AnalyticsPlatform {

    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public boolean isEnabled(@NonNull Application application) {
        return debug;
    }

    @Override
    public void initialize(@NonNull Application application, boolean debug) {
        super.initialize(application, debug);
        Log.i("TestLogPlatform", "Initialized");
    }

    @Override
    public void track(@NonNull String event, @NonNull Bundle params) {
        Log.d("TestLogPlatform", "Event: " + event + " Params: " + params.toString());
    }

    @Override
    public void onSessionStart(Session session) {
        Log.d("TestLogPlatform", "Session start: " + session.getId());
    }

    @Override
    public void onSessionFinish(Session session) {
        Log.d("TestLogPlatform", "Session finish: " + session.getId());
    }

    @Override
    public void setUserId(@NonNull String userId) {
        Log.d("TestLogPlatform", "Set user id: " + userId);
    }

    @Override
    public void setUserProperty(String property, String value) {
        Log.d("TestLogPlatform", "Set user property: " + property + "=" + value);
    }
}

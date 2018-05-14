package com.baloota.blytics.platforms;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baloota.blytics.AnalyticsPlatform;
import com.baloota.blytics.BuildConfig;

/**
 * Created by Sergey B on 10.05.2018.
 */
public class TestLogPlatform extends AnalyticsPlatform {

    @Override
    public boolean isEnabled(@NonNull Application application) {
        return BuildConfig.DEBUG;
    }

    @Override
    public void initialize(@NonNull Application application) {
        Log.i("TestLogPlatform", "Initialized");
    }

    @Override
    public void track(@NonNull String event, @NonNull Bundle params) {
        Log.d("TestLogPlatform", "Event: " + event + " Params: " + params.toString());
    }
}

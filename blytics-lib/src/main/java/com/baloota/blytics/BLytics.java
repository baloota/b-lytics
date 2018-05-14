package com.baloota.blytics;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.baloota.blytics.model.Event;

/**
 * Created by Sergey B on 10.05.2018.
 */
public class BLytics {

    private static BLytics INSTANCE;

    private final Application application;
    private final BLyticsEngine engine;

    private BLytics(Application application) {
        this.application = application;
        this.engine = new BLyticsEngine(application);
    }

    public static BLytics getLogger() {
        return INSTANCE;
    }

    public static void init(Application application) {
        INSTANCE = new BLytics(application);
        INSTANCE.engine.initialize();
    }

    public synchronized BLytics getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Y-Lytics not initialized");
        }

        return INSTANCE;
    }

    public void track(@NonNull Event event) {
        engine.track(event);
    }

    public void track(String event) {
        track(event, new Bundle());
    }

    public void track(String event, Bundle params) {
        engine.track(event, params);
    }

}

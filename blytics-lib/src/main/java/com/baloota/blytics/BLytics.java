package com.baloota.blytics;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
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

    private BLytics(Application application, LifecycleOwner lifecycleOwner) {
        this.application = application;
        this.engine = new BLyticsEngine(application, lifecycleOwner);
    }

    public static BLytics getLogger() {
        return INSTANCE;
    }

    public static void init(Application application, String eventPrefix) {
        INSTANCE = new BLytics(application, null);
        INSTANCE.engine.initialize(eventPrefix);
    }

    public static void init(Application application, LifecycleOwner lifecycleOwner, String eventPrefix) {
        INSTANCE = new BLytics(application, lifecycleOwner);
        INSTANCE.engine.initialize(eventPrefix);
    }

    public static void init(Application application) {
        init(application, null);
    }

    public synchronized BLytics getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("B-Lytics not initialized");
        }

        return INSTANCE;
    }

    public void startSession(boolean isAppInForeground) {
        engine.startSession(isAppInForeground);
    };

    public void stopSession() {
        engine.stopSession();
    };

    public void track(@NonNull Event event) {
        engine.track(event);
    }

    public void trackWithoutSession(@NonNull Event event) {
        engine.trackWithoutSession(event);
    }

    public void track(String event) {
        track(event, new Bundle());
    }

    public void track(String event, Bundle params) {
        engine.track(event, params);
    }

    public void track(@NonNull Event event, int interval) {
        engine.track(event, interval);
    }

    public void track(String event, int interval) {
        track(event, new Bundle(), interval);
    }

    public void track(String event, Bundle params, int interval) {
        engine.track(event, params, interval);
    }

    public void updateCounter(String name, int type) {
        engine.updateCounter(name, type);
    }

    public <T> void setProperty(String name, T value) {
        engine.setProperty(name, value);
    }

    public <T> void setUserProperty(String name, T value) {
        engine.setUserProperty(name, value);
    }

    public String getUserProperty(String name) {
        return engine.getUserProperty(name);
    }

    public void setEventsPrefix(String prefix) {
        engine.setEventsPrefix(prefix);
    }

    public void setUserId(@NonNull String userId) {
        engine.setUserId(userId);
    }
}

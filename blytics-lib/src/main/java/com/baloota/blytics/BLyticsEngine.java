package com.baloota.blytics;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import com.baloota.blytics.model.Counter;
import com.baloota.blytics.model.Event;
import com.baloota.blytics.model.Property;
import com.baloota.blytics.model.Session;
import com.baloota.blytics.platforms.FacebookPlatform;
import com.baloota.blytics.platforms.TestLogPlatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergey B on 10.05.2018.
 */
class BLyticsEngine {

    private static final String PARAM_SESSION_ID = "SessionId";

    private final Application application;
    private final CounterRepository globalCounterRepository;
    private final PropertiesRepository propertiesRepository;

    private Session session;
    private SessionThread sessionThread;

    private List<AnalyticsPlatform> platforms = Collections.emptyList();

    BLyticsEngine(Application application) {
        this.application = application;

        globalCounterRepository = new GlobalCounterRepository(application);
        propertiesRepository = new PropertiesRepositoryImpl(application);

        sessionThread = new SessionThread(this);

        startLifecycleObserver();
    }

    public void initialize() {
        Log.i("YLytics", "Initializing...");

        platforms = getSupportedPlatforms();

        for (AnalyticsPlatform platform : platforms) {
            platform.initialize(application);
        }
    }

    private List<AnalyticsPlatform> getSupportedPlatforms() {
        List<AnalyticsPlatform> platforms = new ArrayList<>();

        for (AnalyticsPlatform platform : getPlatforms()) {
            if (platform.isEnabled(application)) {
                platforms.add(platform);
            }
        }

        return platforms;
    }

    private List<AnalyticsPlatform> getPlatforms() {
        List<AnalyticsPlatform> platforms = new ArrayList<>();
        platforms.add(new FacebookPlatform());
        platforms.add(new TestLogPlatform());
        return platforms;
    }

    public void track(String event, Bundle params) {
        track(new Event(event, params));
    }

    public void track(@NonNull Event event) {
        if (sessionThread == null) {
            sessionThread = new SessionThread(BLyticsEngine.this);
        }

        sessionThread.sendEvent(Event.copyOf(event));
    }

    private void addSessionParams(Event event) {
        event.setParam(PARAM_SESSION_ID, session.getId());
    }

    void sendToPlatforms(Event event) {

        addSessionParams(event);
        addCounters(event);
        addReferencedCounters(event);
        addReferencedProperties(event);

        for (AnalyticsPlatform platform : platforms) {
            platform.track(event.getName(), event.getParams());
        }
    }

    private void addReferencedProperties(Event event) {
        for (Property property : event.getReferencedProperties()) {
            event.setParam(property.getName(), propertiesRepository.getProperty(property.getName(), property.getValue()));
        }
    }

    private void addCounters(Event event) {

        for (Counter counter : event.getCounters()) {

            switch (counter.getScope()) {
                case Counter.UNDEFINED:
                    break;
                case Counter.SESSION:
                    event.setParam(counter.getName(), session.incrementCounter(counter).getValue());
                    break;
                case Counter.GLOBAL:
                    event.setParam(counter.getName(), globalCounterRepository.incrementCounter(counter).getValue());
                    break;
                case Counter.DAILY: {

                    Counter c = globalCounterRepository.getCounter(counter);

                    if (c != null) {
                        if (!DateUtils.isToday(c.getTimestamp())) {
                            globalCounterRepository.resetCounter(c);
                        }
                    }

                    event.setParam(counter.getName(), globalCounterRepository.incrementCounter(counter).getValue());

                    break;
                }
            }
        }

    }

    private void addReferencedCounters(Event event) {

        for (Counter counter : event.getReferencedCounters()) {

            CounterRepository repository = globalCounterRepository;

            if (session.hasCounter(counter)) {
                repository = session;
            }

            Counter c = repository.getCounter(counter);

            if (c != null && c.getScope() == Counter.DAILY) {
                if (!DateUtils.isToday(c.getTimestamp())) {
                    repository.resetCounter(c);
                }
            }

            if (event.getParams().containsKey(counter.getName())) {
                event.setParam(counter.getFullName(), c != null ? c.getValue() : 0);
            } else {
                event.setParam(counter.getName(), c != null ? c.getValue() : 0);
            }

        }
    }

    private void startLifecycleObserver() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleObserver() {

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            public void onEnterForeground() {
                Log.i("YLytics", "App is FOREGROUND");
                session = new Session();

                if (sessionThread == null) {
                    sessionThread = new SessionThread(BLyticsEngine.this);
                }

                sessionThread.startSession();
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            public void onEnterBackground() {
                Log.i("YLytics", "App is BACKGROUND");
                sessionThread.stopSession();
                sessionThread = null;
            }

        });
    }

    public void updateCounter(String name, int type) {
        switch (type) {
            case Counter.SESSION:
                session.incrementCounter(null, name, type);
                break;
            case Counter.GLOBAL:
                globalCounterRepository.incrementCounter(null, name, type);
                break;
            case Counter.DAILY:

                Counter c = globalCounterRepository.getCounter(null, name);

                if (c != null) {
                    if (!DateUtils.isToday(c.getTimestamp())) {
                        globalCounterRepository.resetCounter(c);
                    }
                }

                globalCounterRepository.incrementCounter(null, name, type);
                break;
        }

    }

    public <T> void setProperty(String name, T value) {
        propertiesRepository.setProperty(name, value);
    }
}

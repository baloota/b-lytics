package com.baloota.blytics;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;

import com.baloota.blytics.model.Counter;
import com.baloota.blytics.model.Event;
import com.baloota.blytics.model.Property;
import com.baloota.blytics.model.Session;
import com.baloota.blytics.platforms.FacebookPlatform;
import com.baloota.blytics.platforms.FirebasePlatform;
import com.baloota.blytics.platforms.TestLogPlatform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergey B on 10.05.2018.
 */
class BLyticsEngine {

    private static final String PARAM_SESSION_EVENT = "com.baloota.blytics.#session";
    private static final String PARAM_SESSION_NUMBER = "session";
    private static final String PARAM_SESSION_ID = "SessionId";
    private static final String PARAM_SESSION_FOREGROUND = "isForegroundSession";

    private final Application application;
    private final CounterRepository globalCounterRepository;
    private final PropertiesRepository propertiesRepository;

    private Session session;
    private SessionThread sessionThread;

    private List<AnalyticsPlatform> platforms = Collections.emptyList();
    private String prefix;

    BLyticsEngine(Application application, LifecycleOwner lifecycleOwner) {
        this.application = application;

        globalCounterRepository = new GlobalCounterRepository(application);
        propertiesRepository = new PropertiesRepositoryImpl(application);

        sessionThread = new SessionThread(this);

        startLifecycleObserver(lifecycleOwner);
    }

    public void initialize(String eventPrefix) {
        Log.i("BLytics", "Initializing...");

        prefix = eventPrefix;

        platforms = getSupportedPlatforms();

        for (AnalyticsPlatform platform : platforms) {
            try {
                platform.initialize(application);
            } catch (Throwable e) {
                Log.e("BLytics", "Failed to initialize platform");
            }
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
        platforms.add(new FirebasePlatform());
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

    public void track(String event, Bundle params, int interval) {
        track(new Event(event, params), interval);
    }

    public void track(@NonNull Event event, int interval) {
        if (sessionThread == null) {
            sessionThread = new SessionThread(BLyticsEngine.this);
        }

        sessionThread.sendPeriodicEvent(Event.copyOf(event), interval);
    }

    private void addSessionParams(Event event) {
        Counter c = globalCounterRepository.getCounter(PARAM_SESSION_EVENT, PARAM_SESSION_NUMBER);
        event.setParam(PARAM_SESSION_NUMBER, c.getValue());
        event.setParam(PARAM_SESSION_FOREGROUND, session.isForegroundSession());
    }

    void sendToPlatforms(Event event) {

        addSessionParams(event);
        addCounters(event);
        addReferencedCounters(event);
        addReferencedProperties(event);

        String eventName = event.getName();

        if (!TextUtils.isEmpty(prefix)) {
            eventName = prefix + eventName;
        }

        for (AnalyticsPlatform platform : platforms) {
            platform.track(eventName, event.getParams());
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

        for (Pair<String, Counter> item : event.getReferencedCounters()) {

            String parameterName = item.first;
            Counter counter = item.second;

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

            event.setParam(parameterName, c != null ? c.getValue() : 0);
        }
    }

    private void startLifecycleObserver(LifecycleOwner lifecycleOwner) {

        final boolean isForegroundSession;

        if (lifecycleOwner == null) {
            lifecycleOwner = ProcessLifecycleOwner.get();
            isForegroundSession = true;
        } else {
            isForegroundSession = ! (lifecycleOwner instanceof LifecycleService);
        }

        lifecycleOwner.getLifecycle().addObserver(new LifecycleObserver() {

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            public void onEnterForeground() {
                Log.i("BLytics", "App is FOREGROUND");
                startSession(isForegroundSession);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            public void onEnterBackground() {
                Log.i("BLytics", "App is BACKGROUND");
                stopSession();
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

    public <T> void setUserProperty(String name, T value) {
        propertiesRepository.setUserProperty(name, value);

        for (AnalyticsPlatform platform : platforms) {
            platform.setUserProperty(name, String.valueOf(value));
        }

    }

    public String getUserProperty(String name) {
        return propertiesRepository.getUserProperty(name, null);
    }

    public void setEventsPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void onSessionStarted() {
        for (AnalyticsPlatform platform : platforms) {
            platform.onSessionStart(session);
        }
    }

    private void onSessionFinished() {
        for (AnalyticsPlatform platform : platforms) {
            platform.onSessionFinish(session);
        }
    }

    public void setUserId(@NonNull String userId) {
        for (AnalyticsPlatform platform : platforms) {
            platform.setUserId(userId);
        }
    }

    public void startSession(boolean isForegroundSession) {
        session = new Session(isForegroundSession);

        if (sessionThread == null) {
            sessionThread = new SessionThread(BLyticsEngine.this);
        }

        if (isForegroundSession) {
            globalCounterRepository.incrementCounter(PARAM_SESSION_EVENT, PARAM_SESSION_NUMBER, Counter.GLOBAL);
        }

        sessionThread.startSession();
    }

    public void stopSession() {
        sessionThread.stopSession();
        sessionThread = null;
        onSessionFinished();
    }
}

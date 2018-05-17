package com.baloota.blytics.model;

import android.os.Bundle;

import com.baloota.blytics.BLytics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey B on 11.05.2018.
 */
public class Event {

    private final String name;
    private final Bundle params = new Bundle();
    private final List<Counter> counters = new ArrayList<>();

    public static Event copyOf(Event event) {
        return new Event(event);
    }

    public Event(Event src) {
        this.name = src.name;
        this.params.putAll(src.params);
        this.counters.addAll(src.counters);
    }

    public Event(String name) {
        this.name = name;
    }

    public Event(String name, Bundle params) {
        this.name = name;
        this.params.putAll(params);
    }

    public <T> Event setParam(String param, T value) {
        return setParam(param, String.valueOf(value));
    }

    public Event setParam(String param, String value) {
        params.putString(param, value);
        return this;
    }

    public Event count(String name, int type) {
        counters.add(new Counter(this.name, name, type));
        return this;
    }

    public Event getCountValue(String eventName, String name) {
        counters.add(new Counter(eventName, name, Counter.GET_VALUE));
        return this;
    }

    public String getName() {
        return name;
    }

    public Bundle getParams() {
        return params;
    }

    public List<Counter> getCounters() {
        return counters;
    }

    public void track() {
        BLytics.getLogger().track(this);
    }
}

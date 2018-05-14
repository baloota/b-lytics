package com.baloota.blytics.model;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey B on 11.05.2018.
 */
public class Event {

    private final String name;
    private final Bundle params = new Bundle();
    private final List<Counter> counters = new ArrayList<>();

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
        counters.add(new Counter(name, type));
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
}

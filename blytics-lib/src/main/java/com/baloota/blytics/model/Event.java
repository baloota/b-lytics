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
    private final List<Counter> referencedCounters = new ArrayList<>();
    private final List<Property> referencedProperties = new ArrayList<>();

    public static Event copyOf(Event event) {
        return new Event(event);
    }

    public Event(Event src) {
        this.name = src.name;
        this.params.putAll(src.params);
        this.counters.addAll(src.counters);
        this.referencedCounters.addAll(src.referencedCounters);
        this.referencedProperties.addAll(src.referencedProperties);
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

    public Event counterValue(String name) {
        return counterValue(null, name);
    }

    public Event counterValue(String eventName, String name) {
        referencedCounters.add(new Counter(eventName, name, Counter.UNDEFINED));
        return this;
    }

    public <T> Event propertyValue(String propertyName, T defaultValue) {
        referencedProperties.add(new Property(propertyName, defaultValue));
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

    public List<Counter> getReferencedCounters() {
        return referencedCounters;
    }

    public List<Property> getReferencedProperties() {
        return referencedProperties;
    }

    public void track() {
        BLytics.getLogger().track(this);
    }
}

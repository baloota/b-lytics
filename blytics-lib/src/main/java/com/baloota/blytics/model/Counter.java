package com.baloota.blytics.model;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Sergey B on 11.05.2018.
 */
public class Counter {

    public static final int UNDEFINED = -1;
    public static final int SESSION = 1;
    public static final int GLOBAL = 2;
    public static final int DAILY = 3;

    @IntDef({SESSION, GLOBAL, DAILY, UNDEFINED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Scope {}

    private final String eventName;
    private final String name;
    private final @Scope int scope;
    private int value;
    private long timestamp;

    public Counter(String eventName, String name, @Scope int scope) {
        this.eventName = eventName;
        this.name = name;
        this.scope = scope;
    }

    public static String fullName(String eventName, String counterName) {
        String result = null;

        if (!TextUtils.isEmpty(eventName)) {
            result = eventName + ".";
        }

        return result + counterName;
    }

    public String getEventName() {
        return eventName;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName(eventName, name);
    }

    public @Scope int getScope() {
        return scope;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        timestamp = System.currentTimeMillis();
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int increment() {
        timestamp = System.currentTimeMillis();
        return ++value;
    }
}

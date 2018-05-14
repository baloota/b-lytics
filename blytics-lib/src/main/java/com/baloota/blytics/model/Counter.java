package com.baloota.blytics.model;

/**
 * Created by Sergey B on 11.05.2018.
 */
public class Counter {

    public static final int SESSION = 1;
    public static final int GLOBAL = 2;
    public static final int DAILY = 3;

    private final String name;
    private final int type;
    private int value;
    private long timestamp;

    public Counter(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
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

package com.baloota.blytics.model;

/**
 * Created by Sergey B on 17.05.2018.
 */
public class Property {

    private final String name;
    private String value;

    public Property(String name) {
        this.name = name;
    }

    public <T> Property(String name, T value) {
        this.name = name;
        this.value = String.valueOf(value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public <T> void setValue(T value) {
        this.value = String.valueOf(value);
    }
}

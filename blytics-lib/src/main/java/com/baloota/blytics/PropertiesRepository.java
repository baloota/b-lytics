package com.baloota.blytics;

/**
 * Created by Sergey B on 17.05.2018.
 */
public interface PropertiesRepository {

    <T> String getProperty(String name, T defaultValue);
    <T> void setProperty(String name, T value);

}

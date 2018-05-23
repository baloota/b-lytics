package com.baloota.blytics;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey B on 17.05.2018.
 */
public class PropertiesRepositoryImpl implements PropertiesRepository {

    private final SharedPreferences preferences;

    public PropertiesRepositoryImpl(Context context) {
        preferences = context.getSharedPreferences("com.baloota.blytics.properties.global", Context.MODE_PRIVATE);
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        return preferences.getString(name, defaultValue);
    }

    @Override
    public <T> void setProperty(String name, T value) {
        preferences.edit().putString(name, String.valueOf(value)).apply();
    }

    @Override
    public String getUserProperty(String name, String defaultValue) {
        String propertyName = "blytics_user." + name;
        return getProperty(propertyName, defaultValue);
    }

    @Override
    public <T> void setUserProperty(String name, T value) {
        String propertyName = "blytics_user." + name;
        setProperty(propertyName, value);
    }

    @Override
    public Map<String, String> getUserProperties() {

        Map<String, String> result = new HashMap<>();

        final Map<String, ?> allPrefs = preferences.getAll();

        for (String key : allPrefs.keySet()) {
            if (key.startsWith("blytics_user.")) {
                result.put(key.substring(13), String.valueOf(allPrefs.get(key)));
            }
        }

        return result;
    }
}

package com.baloota.blytics;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sergey B on 17.05.2018.
 */
public class PropertiesRepositoryImpl implements PropertiesRepository {

    private final SharedPreferences preferences;

    public PropertiesRepositoryImpl(Context context) {
        preferences = context.getSharedPreferences("com.baloota.blytics.properties.global", Context.MODE_PRIVATE);
    }

    @Override
    public <T> String getProperty(String name, T defaultValue) {
        return preferences.getString(name, String.valueOf(defaultValue));
    }

    @Override
    public <T> void setProperty(String name, T value) {
        preferences.edit().putString(name, String.valueOf(value)).apply();
    }
}

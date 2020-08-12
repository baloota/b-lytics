package com.baloota.blytics;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.baloota.blytics.model.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergey B on 10.05.2018.
 */
public abstract class AnalyticsPlatform {

    protected boolean debug = false;

    public abstract String getName();

    public abstract boolean isEnabled(@NonNull Application application);

    public void initialize(@NonNull Application application, boolean debug) {
        this.debug = debug;
    }

    public abstract void track(@NonNull String event, @NonNull Bundle params);

    public abstract void onSessionStart(Session session);

    public abstract void onSessionFinish(Session session);

    public abstract void setUserId(@NonNull String userId);

    public abstract void setUserProperty(String property, String value);

    public Bundle ensureParamsLength(Bundle params, int maxLength) {
        for (String key : params.keySet()) {
            if (params.get(key) instanceof String) {
                String value = params.getString(key);
                if (value != null && value.length() > maxLength) {
                    params.putString(key, value.substring(0, maxLength));
                }
            }
        }
        return params;
    }

    public Map<String, String> asMap(Bundle params) {

        Map<String, String> result = new HashMap<>();

        for (String key : params.keySet()) {
            result.put(key, String.valueOf(params.get(key)));
        }

        return result;
    }

}

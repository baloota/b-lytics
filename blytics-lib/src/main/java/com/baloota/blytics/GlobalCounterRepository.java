package com.baloota.blytics;

import android.content.Context;
import android.content.SharedPreferences;

import com.baloota.blytics.model.Counter;
import com.google.gson.Gson;

/**
 * Created by Sergey B on 11.05.2018.
 */
public class GlobalCounterRepository implements CounterRepository {

    private final SharedPreferences preferences;

    public GlobalCounterRepository(Context context) {
        preferences = context.getSharedPreferences("com.baloota.ylytics.counters.global", Context.MODE_PRIVATE);
    }

    @Override
    public Counter getCounter(Counter counter) {

        if (preferences.contains(counter.getName())) {
            String json = preferences.getString(counter.getName(), null);
            return new Gson().fromJson(json, Counter.class);
        }

        return null;
    }

    private void storeCounter(Counter c) {
        String json = new Gson().toJson(c);
        preferences.edit().putString(c.getName(), json).apply();
    }


    @Override
    public Counter incrementCounter(Counter c) {

        Counter counter = getCounter(c);

        if (counter == null) {
            counter = new Counter(c.getName(), c.getType());
        }

        counter.increment();
        storeCounter(counter);
        c.setValue(counter.getValue());

        return c;
    }

    @Override
    public Counter resetCounter(Counter c) {

        Counter counter = getCounter(c);

        if (counter == null) {
            counter = new Counter(c.getName(), c.getType());
        }

        counter.setValue(0);
        storeCounter(counter);
        c.setValue(counter.getValue());

        return c;
    }
}

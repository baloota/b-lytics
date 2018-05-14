package com.baloota.blytics.model;

import com.baloota.blytics.CounterRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Sergey B on 10.05.2018.
 */
public class Session implements CounterRepository {

    private Map<String, Counter> counters = new HashMap<>();

    private final String sessionId;

    public Session() {
        sessionId = UUID.randomUUID().toString();
    }

    public String getId() {
        return sessionId;
    }

    @Override
    public Counter getCounter(Counter c) {
        return counters.get(c.getName());
    }

    @Override
    public Counter incrementCounter(Counter c) {

        Counter counter = counters.get(c.getName());

        if (counter == null) {
            counter = new Counter(c.getName(), c.getType());
        }

        counter.increment();
        counters.put(counter.getName(), counter);
        c.setValue(counter.getValue());

        return c;
    }

    @Override
    public Counter resetCounter(Counter c) {

        Counter counter = counters.get(c.getName());

        if (counter == null) {
            counter = new Counter(c.getName(), c.getType());
        }

        counter.setValue(0);
        counters.put(counter.getName(), counter);
        c.setValue(counter.getValue());

        return c;
    }
}

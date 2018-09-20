package com.baloota.blytics.model;

import com.baloota.blytics.CounterRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Sergey B on 10.05.2018.
 */
public class Session extends CounterRepository {

    private Map<String, Counter> counters = new HashMap<>();

    private final String sessionId;
    private final boolean isForegroundSession;

    public Session(boolean isForegroundSession) {
        this.isForegroundSession = isForegroundSession;
        sessionId = UUID.randomUUID().toString();
    }

    public String getId() {
        return sessionId;
    }

    @Override
    public Counter getCounter(String eventName, String counterName) {
        return counters.get(Counter.fullName(eventName, counterName));
    }

    @Override
    public Counter getCounter(Counter c) {
        return getCounter(c.getEventName(), c.getName());
    }

    @Override
    protected void storeCounter(Counter counter) {
        counters.put(counter.getFullName(), counter);
    }

    public boolean isForegroundSession() {
        return isForegroundSession;
    }
}

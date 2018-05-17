package com.baloota.blytics;

import com.baloota.blytics.model.Counter;

/**
 * Created by Sergey B on 11.05.2018.
 */
public abstract class CounterRepository {

    public abstract Counter getCounter(String eventName, String counterName);

    public Counter getCounter(Counter counter) {
        return getCounter(counter.getEventName(), counter.getName());
    }

    public boolean hasCounter(Counter c) {
        return getCounter(c) != null;
    }

    public Counter incrementCounter(Counter c) {

        Counter counter = getCounter(c);

        if (counter == null) {
            counter = new Counter(c.getEventName(), c.getName(), c.getScope());
        }

        counter.increment();
        storeCounter(counter);
        c.setValue(counter.getValue());

        return c;

    }

    public Counter incrementCounter(String eventName, String counterName, int type) {

        Counter c = getCounter(eventName, counterName);

        if (c == null) {
            c = new Counter(eventName, counterName, type);
        }

        return incrementCounter(c);

    }

    public Counter resetCounter(Counter c) {

        Counter counter = getCounter(c);

        if (counter == null) {
            counter = new Counter(c.getEventName(), c.getName(), c.getScope());
        }

        counter.setValue(0);
        storeCounter(counter);
        c.setValue(counter.getValue());

        return c;
    }

    protected abstract void storeCounter(Counter c);
}

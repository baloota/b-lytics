package com.baloota.blytics;

import com.baloota.blytics.model.Counter;

/**
 * Created by Sergey B on 11.05.2018.
 */
public interface CounterRepository {

    Counter getCounter(Counter counter);

    Counter incrementCounter(Counter counter);

    Counter resetCounter(Counter counter);

}

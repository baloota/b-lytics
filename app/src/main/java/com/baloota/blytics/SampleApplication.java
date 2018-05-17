package com.baloota.blytics;

import android.app.Application;

/**
 * Created by Sergey B on 17.05.2018.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BLytics.init(this);
    }
}

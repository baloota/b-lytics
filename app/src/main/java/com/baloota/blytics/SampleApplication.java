package com.baloota.blytics;

import android.app.Application;
import android.text.format.DateUtils;

/**
 * Created by Sergey B on 17.05.2018.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BLytics.init(this);

        BLytics.getLogger().track("MINUTE_MARK", (int) DateUtils.MINUTE_IN_MILLIS);
    }
}

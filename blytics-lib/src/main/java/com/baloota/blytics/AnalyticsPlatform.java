package com.baloota.blytics;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by Sergey B on 10.05.2018.
 */
public abstract class AnalyticsPlatform {

    public abstract boolean isEnabled(@NonNull Application application);

    public abstract void initialize(@NonNull Application application);

    public abstract void track(@NonNull String event, @NonNull Bundle params);
}

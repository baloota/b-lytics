package com.baloota.blytics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.baloota.blytics.model.Counter;
import com.baloota.blytics.model.Event;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButton1(View view) {

        new Event("Event A")
                .count("clicks", Counter.GLOBAL)
                .count("clicks_session", Counter.SESSION)
                .count("clicks_daily", Counter.DAILY)
                .track();

        BLytics.getLogger().updateCounter("Some Click", Counter.GLOBAL);

        BLytics.getLogger().setProperty("Last button", "Button 1");

        BLytics.getLogger().setUserProperty("UserName", "Buttono One");
    }

    public void onButton2(View view) {
        new Event("Event B")
                .count("clicks", Counter.GLOBAL)
                .count("clicks_session", Counter.SESSION)
                .count("clicks_daily", Counter.DAILY)
                .counterValue("Event A", "clicks")
                .track();

        BLytics.getLogger().setProperty("Last button", "Button 2");

        BLytics.getLogger().setUserProperty("UserName", "Buttono Two");
    }

    public void onButton3(View view) {
        new Event("Event C")
                .count("clicks", Counter.GLOBAL)
                .count("clicks_session", Counter.SESSION)
                .count("clicks_daily", Counter.DAILY)
                .counterValue("Some Click")
                .propertyValue("Last button", "none")
                .track();

        BLytics.getLogger().setUserProperty("UserName", "Buttono Three");
    }
}

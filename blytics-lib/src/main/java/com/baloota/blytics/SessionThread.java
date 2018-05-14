package com.baloota.blytics;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.baloota.blytics.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey B on 11.05.2018.
 */
class SessionThread extends HandlerThread {

    private static final int MSG_EVENT = 1;

    private final BLyticsEngine engine;

    private Handler handler;
    private List<Message> messageBuf = new ArrayList<>();

    SessionThread(BLyticsEngine engine) {
        super("SessionThread");
        this.engine = engine;
    }

    public void startSession() {
        start();
    }

    public void stopSession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            quitSafely();
        } else {
            quit();
        }
    }

    public synchronized void sendEvent(Event event) {

        Message msg = new Message();
        msg.what = MSG_EVENT;
        msg.obj = event;

        if (handler != null) {
            handler.sendMessage(msg);
        } else {
            messageBuf.add(msg);
        }

    }

    @Override
    protected void onLooperPrepared() {

        Log.d("SessionThread", "Session thread ready");

        synchronized (this) {

            handler = new Handler(getLooper()) {

                @Override
                public void handleMessage(Message msg) {

                    switch (msg.what) {
                        case MSG_EVENT:
                            engine.sendToPlatforms((Event) msg.obj);
                            break;
                    }

                }
            };

            flushMessageBuffer();

            notifyAll();
        }

    }

    private void flushMessageBuffer() {
        for (Message message : messageBuf) {
            handler.sendMessage(message);
        }
    }

}

package com.keepfit.triggers.thread;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Edward on 4/8/2016.
 */
public class LocationThread extends TriggerThread {
    private static final String TAG = "LocationThread";
    private static final String TITLE = "Location";
    private static int num;

    public LocationThread() {
        super(TITLE, false);
    }

    public LocationThread(Context context) {
        super(context, TITLE, false);
    }

    @Override
    public void doRunAction() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRunning()) {
                    txtDisplay.setText(String.valueOf(num++));
                    if (num == 20) {
                        sendNotification();
                    }
                }
            }
        });
    }

    @Override
    public void doStartAction() {
        num = 0;
    }

    @Override
    public void doStopAction() {

    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getMessage() {
        return String.format("You reached the goal for location!");
    }
}
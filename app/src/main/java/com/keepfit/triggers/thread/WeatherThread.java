package com.keepfit.triggers.thread;

import android.app.Activity;
import android.content.Context;

import com.keepfit.triggers.utils.enums.TriggerType;

/**
 * Created by Edward on 4/8/2016.
 */
public class WeatherThread extends TriggerThread<String> {
    private static final String TAG = "WeatherThread";
    private static final String TITLE = "Weather";
    private static int num;

    public WeatherThread(Context context) {
        super(TITLE, TriggerType.WEATHER, false, context);
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
    public String getTriggerObject() {
        return "Nothing.";
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getMessage() {
        return String.format("You reached the goal for weather!");
    }
}

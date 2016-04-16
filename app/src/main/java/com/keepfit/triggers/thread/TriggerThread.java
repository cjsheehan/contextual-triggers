package com.keepfit.triggers.thread;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.keepfit.triggers.utils.Extension;
import com.keepfit.triggers.utils.enums.TriggerType;

/**
 * Created by Edward on 4/8/2016.
 */
public abstract class TriggerThread<T> extends BaseThread {
    private static final String TAG = "TriggerThread";

    protected TriggerType triggerType;
    protected TextView txtDisplay;
    // How long the thread has been running, in seconds.
    // This is used to determine if a thread should be refreshed, and once it is reset, this should also be reset so
    // that the refresh time can start over.
    protected int runtime;
    // API requests should max out at 10, so since there are 86,400 seconds in one day, make the max amount of
    // refresh attempts 10 new requests a day
    protected static final int REFRESH_TIME = 8640;

    public TriggerThread(String name, TriggerType triggerType, boolean enabled, Context context) {
        super(name, enabled, context);
        this.triggerType = triggerType;
    }

    public void setDisplay(TextView textView) {
        txtDisplay = textView;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    protected abstract String getTitle();

    protected abstract void doTriggerRunAction();

    protected abstract String getTextToDisplayOnUI();

    @Override
    public void doRunAction() {
        doTriggerRunAction();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtDisplay.setText(getTextToDisplayOnUI());
            }
        });
        runtime++;
    }

    protected boolean shouldRefresh() {
        boolean refresh = REFRESH_TIME <= runtime;
        if (refresh) {
            runtime = 0;
        }
        return refresh;
    }

}

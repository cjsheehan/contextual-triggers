package com.keepfit.triggers.thread;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.keepfit.triggers.utils.Extension;

/**
 * Created by Edward on 4/8/2016.
 */
public abstract class TriggerThread extends BaseThread {
    private static final String TAG = "TriggerThread";

    protected Context context;
    protected TextView txtDisplay;

    public TriggerThread(String name, boolean enabled) {
        super(name, enabled);
    }

    public TriggerThread(Context context, String name, boolean enabled) {
        super(name, enabled);
        this.context = context;
    }

    public void setDisplay(TextView textView) {
        txtDisplay = textView;
    }

    protected void sendNotification() {
        Extension.sendNotification(context, getTitle(), getMessage());
    }

    protected abstract String getTitle();
    protected abstract String getMessage();

}

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
    }

}

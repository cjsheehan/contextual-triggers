package com.mcnedward.triggers.thread;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

/**
 * Created by Edward on 4/8/2016.
 */
public class FirstThread extends TriggerThread {
    private static final String TAG = "FirstThread";

    private static int num;
    private Context context;
    private TextView txtDisplay;

    public FirstThread() {
        super(TAG, false);
    }

    public FirstThread(Context context, TextView txtDisplay) {
        super(TAG, false);
        this.context = context;
        this.txtDisplay = txtDisplay;
    }

    @Override
    public void doRunAction() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRunning()) {
                    txtDisplay.setText(String.valueOf(num++));
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
}

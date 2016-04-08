package com.mcnedward.triggers.thread;

/**
 * Created by Edward on 4/8/2016.
 */
public abstract class TriggerThread extends BaseThread {
    private static final String TAG = "TriggerThread";

    public TriggerThread(String name) {
        super(name);
    }

    public TriggerThread(String name, boolean enabled) {
        super(name, enabled);
    }

}

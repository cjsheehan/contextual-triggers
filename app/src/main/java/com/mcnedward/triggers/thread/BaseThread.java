package com.mcnedward.triggers.thread;

import android.util.Log;

/**
 * Created by Edward on 4/8/2016.
 */
public abstract class BaseThread extends Thread implements IThread {
    private static final String TAG = "BaseThread";

    private boolean started, running, enabled;

    public BaseThread(String name) {
        this(name, true);
    }

    public BaseThread(String name, boolean enabled) {
        super(name);
        started = false;
        running = false;
        this.enabled = enabled;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doRunAction();
        }
    }

    @Override
    public void start() {
        started = true;
        super.start();
    }

    @Override
    public void startThread() {
        Log.d(TAG, "Starting thread!");
        if (!started)
            start();
        running = enabled;
        doStartAction();
    }

    @Override
    public void stopThread() {
        Log.d(TAG, "Stopping thread!");
        running = false;
        doStopAction();
        boolean retry = true;
        while (retry) {
            try {
                join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void pauseThread(boolean pause) {
        running = pause;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

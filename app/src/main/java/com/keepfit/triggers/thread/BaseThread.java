package com.keepfit.triggers.thread;

import android.content.Context;
import android.util.Log;

/**
 * Created by Edward on 4/8/2016.
 */
public abstract class BaseThread extends Thread implements IThread {
    private static final String TAG = "BaseThread";

    private boolean started, running, paused, enabled;

    protected Context context;

    public BaseThread(String name) { this(name, true, null); }

    public BaseThread(String name, boolean enabled, Context context) {
        super(name);
        started = false;
        running = false;
        this.enabled = enabled;
        this.context = context;
    }

    /**
     * Run the thread.
     */
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (paused || !enabled) continue;
            doRunAction();
        }
    }

    /**
     * Start the thread.
     */
    @Override
    public void start() {
        started = true;
        super.start();
    }

    /**
     * Starts the thread and runs the abstract start action.
     */
    @Override
    public void startThread() {
        Log.d(TAG, "Starting " + getName() + " thread!");
        if (!started)
            start();
        running = enabled;
        doStartAction();
    }

    /**
     * Stops the thread and runs the abstract stop action.
     */
    @Override
    public void stopThread() {
        Log.d(TAG, "Stopping " + getName() + " thread!");
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

    /**
     * Pause the thread.
     * @param pause
     */
    @Override
    public void pauseThread(boolean pause) {
        paused = pause;
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

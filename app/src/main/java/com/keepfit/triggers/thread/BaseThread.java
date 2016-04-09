package com.keepfit.triggers.thread;

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

    /**
     * Run the thread.
     */
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        Log.d(TAG, "Starting thread!");
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

    /**
     * Pause the thread.
     * @param pause
     */
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

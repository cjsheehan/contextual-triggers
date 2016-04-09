package com.keepfit.triggers.thread;

import android.content.Context;

/**
 * Created by Edward on 4/8/2016.
 */
public interface IThread {
    /**
     * The action to run on every step of the thread.
     */
    void doRunAction();

    /**
     * The action to run when the thread is started.
     */
    void doStartAction();

    /**
     * The action to run when the thread is stopped.
     */
    void doStopAction();

    /**
     * Starts the thread and runs the start action.
     */
    void startThread();

    /**
     * Stops the thread and runs the stop action.
     */
    void stopThread();

    /**
     * Pause the thread.
     * @param pause
     */
    void pauseThread(boolean pause);

    boolean isRunning();

    void setEnabled(boolean enabled);

    boolean isEnabled();
}

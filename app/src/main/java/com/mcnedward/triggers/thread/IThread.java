package com.mcnedward.triggers.thread;

/**
 * Created by Edward on 4/8/2016.
 */
public interface IThread {
    void doRunAction();
    void doStartAction();
    void doStopAction();
    void startThread();
    void stopThread();
    boolean isRunning();
    void pauseThread(boolean pause);

    void setEnabled(boolean enabled);

    boolean isEnabled();
}

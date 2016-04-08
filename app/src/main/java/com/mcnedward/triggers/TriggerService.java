package com.mcnedward.triggers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mcnedward.triggers.thread.BaseThread;
import com.mcnedward.triggers.thread.IThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 4/8/2016.
 */
public class TriggerService extends Service {
    private static final String TAG = "TriggerService";

    private static AlgorithmBaseThread thread;
    private static List<IThread> threads;
    private static boolean running;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate!");
        thread = new AlgorithmBaseThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand!");

        thread.startThread();
        running = true;

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void stop() {
        thread.stopThread();
        thread = null;
        running = false;
    }

    public static void addThread(IThread thread) {
        if (threads == null)
            threads = new ArrayList<>();
        threads.add(thread);
    }

    public static boolean isRunning() {
        return running;
    }

    final class AlgorithmBaseThread extends BaseThread {

        public AlgorithmBaseThread() {
            super("AlgorithmBaseThread");
        }

        @Override
        public void doRunAction() {
            for (IThread thread : threads) {
                thread.doRunAction();
            }
        }

        @Override
        public void doStartAction() {
            Log.d(TAG, "STARTING THREAD!");
            for (IThread thread : threads) {
                thread.startThread();
            }
        }

        @Override
        public void doStopAction() {
            Log.d(TAG, "STOPPING THREAD!");
            for (IThread thread : threads) {
                thread.stopThread();
            }
        }

        @Override
        public void pauseThread(boolean pause) {

        }
    }

}

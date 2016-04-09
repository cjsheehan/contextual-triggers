package com.keepfit.triggers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.keepfit.triggers.thread.BaseThread;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.utils.enums.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 4/8/2016.
 */
public class TriggerService extends Service {
    private static final String TAG = "TriggerService";

    private static AlgorithmBaseThread thread;
    private static List<TriggerThread> threads;
    private static boolean running;
    private static Context context;
    private TriggerReceiver receiver;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate!");
        thread = new AlgorithmBaseThread();
        registerReceivers();
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
        stop();
        unregisterReceiver(receiver);
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

    private void registerReceivers() {
        receiver = new TriggerReceiver();
        for (Action action : Action.values()) {
            registerReceiver(receiver, new IntentFilter(action.title));
        }
    }

    public static void addThread(TriggerThread thread) {
        if (threads == null)
            threads = new ArrayList<>();
        threads.add(thread);
    }

    public static List<TriggerThread> getThreads() {
        return threads;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setContext(Context mainContext){
        context = mainContext;
    }

    final class AlgorithmBaseThread extends BaseThread {

        public AlgorithmBaseThread() {
            super("AlgorithmBaseThread");
        }

        @Override
        public void doRunAction() {
            for (TriggerThread thread : threads) {
                thread.doRunAction();
            }
        }

        @Override
        public void doStartAction() {
            Log.d(TAG, "STARTING THREAD!");
            for (TriggerThread thread : threads) {
                thread.startThread();
            }
        }

        @Override
        public void doStopAction() {
            Log.d(TAG, "STOPPING THREAD!");
            for (TriggerThread thread : threads) {
                thread.stopThread();
            }
        }

        @Override
        public void pauseThread(boolean pause) {

        }
    }

    private void handleDateReceived() {

    }

    private void handleLocationReceived() {

    }

    private void handleStepCounterReceived() {

    }

    private void handleTimeReceived() {

    }

    private void handleWeatherReceived() {

    }

    class TriggerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Action action = Action.getById(intent.getIntExtra("action", 0));
            switch (action) {
                case DATE:
                    handleDateReceived();
                    break;
                case LOCATION:
                    handleLocationReceived();
                    break;
                case STEP_COUNTER:
                    handleStepCounterReceived();
                    break;
                case TIME:
                    handleTimeReceived();
                    break;
                case WEATHER:
                    handleWeatherReceived();
                    break;
            }
        }
    }

}

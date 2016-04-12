package com.keepfit.triggers.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.keepfit.triggers.thread.BaseThread;
import com.keepfit.triggers.thread.DateThread;
import com.keepfit.triggers.thread.LocationThread;
import com.keepfit.triggers.thread.TimeThread;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.Extension;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.weather.WeatherEvent;

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
        for (TriggerType triggerType : TriggerType.values()) {
            registerReceiver(receiver, new IntentFilter(triggerType.title));
        }
    }

    public static void addThread(TriggerThread thread) {
        if (threads == null)
            threads = new ArrayList<>();
        for (TriggerThread t : threads) {
            if (t.getTriggerType() == thread.getTriggerType()) {
                // Thread already added!
                return;
            }
        }
        threads.add(thread);
    }

    public static List<TriggerThread> getThreads() {
        return threads;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setContext(Context mainContext) {
        context = mainContext;
    }

    public static void locationPermissionGranted() {
        LocationThread thread = (LocationThread) getTrigger(TriggerType.LOCATION);
        thread.locationPermissionGranted();
    }

    final class AlgorithmBaseThread extends BaseThread {

        public AlgorithmBaseThread() {
            super("AlgorithmBaseThread");
        }

        @Override
        public void doRunAction() {
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

    private void handleDateReceived(Intent intent) {
        ArrayList<KeepFitCalendarEvent> events = (ArrayList<KeepFitCalendarEvent>) intent.getSerializableExtra("events");
        if (events.isEmpty()) return;
        Extension.sendNotification(context, "EVENT", events.get(0).getName() + " at " + events.get(0).getStart());
    }

    private void handleLocationReceived() {

    }

    private void handleStepCounterReceived() {

    }

    private void handleTimeReceived(Intent intent) {
        String timeStamp = intent.getStringExtra("timeStamp");
        DateThread dateThread = getDateThread();
        Extension.sendNotification(context, "TIMESTAMP!", timeStamp + " Events: " + dateThread.getTriggerObject().size());
    }

    private void handleWeatherReceived(Intent intent) {
        WeatherEvent weatherEvent = (WeatherEvent) intent.getSerializableExtra("weatherEvent");
        if (weatherEvent == null) return;
        Extension.sendNotification(context, "WEATHER", String.format("Lat: %s; Long: %s; TZ: %s; Off: %s", weatherEvent.getLatitude(), weatherEvent.getLongitude(), weatherEvent.getTimezone(), weatherEvent.getOffset()));
    }

    private static TriggerThread getTrigger(TriggerType triggerType) {
        TriggerThread thread = null;
        for (TriggerThread t : threads) {
            if (t.getTriggerType().equals(triggerType)) {
                thread = t;
                break;
            }
        }
        return thread;
    }

    private DateThread getDateThread() {
        return (DateThread) getTrigger(TriggerType.CALENDAR);
    }

    class TriggerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            TriggerType triggerType = TriggerType.getById(intent.getIntExtra(Broadcast.ACTION, 0));
            switch (triggerType) {
                case CALENDAR:
                    handleDateReceived(intent);
                    break;
                case LOCATION:
                    handleLocationReceived();
                    break;
                case STEP_COUNTER:
                    handleStepCounterReceived();
                    break;
                case TIME:
                    handleTimeReceived(intent);
                    break;
                case WEATHER:
                    handleWeatherReceived(intent);
                    break;
            }
        }
    }

}

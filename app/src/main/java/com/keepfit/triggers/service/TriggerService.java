package com.keepfit.triggers.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.keepfit.triggers.interests.Item;
import com.keepfit.triggers.interests.Results;
import com.keepfit.triggers.notification.Notification;
import com.keepfit.triggers.thread.BaseThread;
import com.keepfit.triggers.thread.CalendarThread;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.DataProcessor;
import com.keepfit.triggers.utils.Dates;
import com.keepfit.triggers.utils.Extension;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.utils.enums.Scenario;
import com.keepfit.triggers.utils.enums.TriggerPreference;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.weather.Forecast;
import com.keepfit.triggers.weather.WeatherEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by Edward on 4/8/2016.
 */
public class TriggerService extends Service {
    private static final String TAG = "TriggerService";

    private static TriggerBaseThread thread;
    private static List<TriggerThread> threads;
    private static Context context;
    private TriggerReceiver receiver;
    private static boolean running, started;
    private boolean notificationSent = false;
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate!");
        thread = new TriggerBaseThread();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        registerReceivers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand!");

        thread.startThread();
        running = true;
        started = true;

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
        started = false;
    }

    private void registerReceivers() {
        receiver = new TriggerReceiver();
        for (TriggerType triggerType : TriggerType.values()) {
            registerReceiver(receiver, new IntentFilter(triggerType.title));
        }
        for (Scenario scenario : Scenario.values()) {
            registerReceiver(receiver, new IntentFilter(scenario.title));
        }
    }

    public static void addThread(TriggerThread thread) {
        if (threads == null)
            threads = new ArrayList<>();
        for (TriggerThread t : threads) {
            if (t.getTriggerType() == thread.getTriggerType()) {
                Log.w(TAG, String.format("The trigger %s has already been added!", thread.getName()));
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

    public static boolean isStarted() {
        return started;
    }

    public static void pauseService(boolean pause) {
        thread.pauseThread(pause);
    }

    public static void setContext(Context mainContext) {
        context = mainContext;
    }

    final class TriggerBaseThread extends BaseThread {

        public TriggerBaseThread() {
            super("TriggerBaseThread");
        }

        private static final int TIMEOUT = 2000;
        private final static int MAX_UPDATE_ATTEMPTS = 10;
        private int firstUpdateAttempts, secondUpdateAttempts, thirdUpdateAttempts;
        private boolean waitForFirst, waitForSecond, waitForThird;

        @Override
        public void doRunAction() {
            if (waitForFirst) {
                if (firstUpdateAttempts > MAX_UPDATE_ATTEMPTS) {
                    Log.w(TAG, "First scenario has reached the max attempts.");
                    waitForFirst = false;
                    firstUpdateAttempts = 0;
                } else {
                    sleep();
                    firstUpdateAttempts++;
                    if (checkFirstScenario()) {
                        // Scenario was hit, so this is done
                        waitForFirst = false;
                        firstUpdateAttempts = 0;
                    }
                }
            }
            if (waitForSecond) {
                if (secondUpdateAttempts > MAX_UPDATE_ATTEMPTS) {
                    Log.w(TAG, "Second scenario has reached the max attempts.");
                    waitForSecond = false;
                    secondUpdateAttempts = 0;
                } else {
                    sleep();
                    secondUpdateAttempts++;
                    if (checkSecondScenario()) {
                        // Scenario was hit, so this is done
                        waitForSecond = false;
                        secondUpdateAttempts = 0;
                    }
                }
            }
            if (waitForThird) {
                if (thirdUpdateAttempts > MAX_UPDATE_ATTEMPTS) {
                    Log.w(TAG, "Third scenario has reached the max attempts.");
                    waitForThird = false;
                    thirdUpdateAttempts = 0;
                } else {
                    sleep();
                    thirdUpdateAttempts++;
                    if (checkThirdScenario()) {
                        // Scenario was hit, so this is done
                        waitForThird = false;
                        thirdUpdateAttempts = 0;
                    }
                }
            }
        }

        private void sleep() {
            try {
                Thread.sleep(TIMEOUT);
            } catch(InterruptedException e) {
                Log.w(TAG, "Trigger Thread interrupted.", e);
            }
        }

        @Override
        public void doStartAction() {
            Log.d(TAG, "STARTING TRIGGER SERVICE!");
            for (TriggerThread thread : threads) {
                thread.startThread();
            }
            firstUpdateAttempts = secondUpdateAttempts = thirdUpdateAttempts = 0;
        }

        @Override
        public void doStopAction() {
            Log.d(TAG, "STOPPING TRIGGER SERVICE!");
            for (TriggerThread thread : threads) {
                thread.stopThread();
                thread = null;
            }
        }

        @Override
        public void pauseThread(boolean pause) {
            Log.d(TAG, "PAUSING TRIGGER SERVICE!");
            for (TriggerThread thread : threads) {
                thread.pauseThread(pause);
            }
            running = !pause;
        }

        protected void notifyWaitForFirst() {
            waitForFirst = true;
        }

        protected void notifyWaitForSecond() {
            waitForSecond = true;
        }

        protected void notifyWaitForThird() {
            waitForThird = true;
        }

    }

    private void handleDateReceived(Intent intent) {
        CalendarThread calendarThread = (CalendarThread) getTrigger(TriggerType.CALENDAR);
    }

    private void handleLocationReceived(Intent intent) {
     String  geofenceId = (String) intent.getSerializableExtra("geofenceEvent");
        Notification.sendNotification(context, "You entered " + geofenceId," ", Scenario.THIRD);
        checkScenarios();


    }

    private void handleStepCounterReceived(Intent intent) {
        double completeness = intent.getDoubleExtra("completeness", 0);
        if (completeness >= 95) {
            checkScenarios();
        }
    }

    private void handleTimeReceived(Intent intent) {
        checkScenarios();
    }

    private void handleWeatherReceived(Intent intent) {
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        Forecast forecast = weatherEvent.getCurrentForecast();
    }

    private void handlePointsOfInterestReceived(Intent intent) {
        Results poiEvent = (Results) intent.getSerializableExtra("poiEvent");
        if (poiEvent == null) return;
        int numPoi = poiEvent.getItems().length;
        if (numPoi > 0) {
            String info = String.format("%s poi found for Lat: %s Long: %s", numPoi, poiEvent.getSourceLatitude(),
                    poiEvent.getSourceLongitude());
            Item[] item = poiEvent.getItems();

            String poi = String.format("1st poi : Title: %s; Distance(m): %s; Category: %s; Lat: %s; Long: %s;",
                    item[0].getTitle(),
                    item[0].getDistance(),
                    item[0].getCategory().getTitle(),
                    item[0].getPosition()[0],
                    item[0].getPosition()[1]);

        } else {
        }
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

    class TriggerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {;
            boolean isScenario = intent.getBooleanExtra(Broadcast.IS_SCENARIO, false);
            if (!isScenario) {
                TriggerType triggerType = TriggerType.getById(intent.getIntExtra(Broadcast.ACTION, 0));
                switch (triggerType) {
                    case CALENDAR:
                        handleDateReceived(intent);
                        break;
                    case LOCATION:
                        handleLocationReceived(intent);
                        break;
                    case STEP_COUNTER:
                        handleStepCounterReceived(intent);
                        break;
                    case TIME:
                        handleTimeReceived(intent);
                        break;
                    case WEATHER:
                        handleWeatherReceived(intent);
                        break;
                    case POI:
                        handlePointsOfInterestReceived(intent);
                        break;
                }
            } else {
                Scenario scenario = Scenario.getById(intent.getIntExtra(Broadcast.ACTION, 0));
                switch (scenario) {
                    case FIRST:
                        checkFirstScenario();
                        break;
                    case SECOND:
                        checkSecondScenario();
                        break;
                    case THIRD:
                        checkThirdScenario();
                        break;
                }
            }
        }
    }

    private void checkScenarios() {
        checkFirstScenario();
        checkSecondScenario();
        checkThirdScenario();
    }

    private boolean checkThirdScenario() {
        long twoHours = 2 * 60 * 60 * 1000;

        ArrayList<KeepFitCalendarEvent> calendarEvents = (ArrayList<KeepFitCalendarEvent>) TriggerCache.get
                (TriggerType.CALENDAR);
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        if (weatherEvent == null || calendarEvents == null) {
            thread.notifyWaitForThird();
            return false;
        }

        if (!DataProcessor.isThereAnyCalendarEventInTheWay(twoHours, calendarEvents)) {
            if (!DataProcessor.isTheWeatherBad(weatherEvent)) {
                if (DataProcessor.isLaterThan(11,0)) {
                    Notification.sendNotification(context, "You should go out!", "It's early, You don't have any " +
                            "calendar events and the weather is good :" + weatherEvent.getCurrentForecast()
                            .getSummary(), Scenario.THIRD);
                }
            }
        }
        return true;
    }

    private boolean checkSecondScenario() {
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        Double stepCounterPercentage = TriggerCache.get(TriggerType.STEP_COUNTER, Double.class);
        if (weatherEvent == null || stepCounterPercentage == null) {
            thread.notifyWaitForSecond();
            return false;
        }
        if (!DataProcessor.isTheWeatherBad(weatherEvent) && DataProcessor.isCompletenessLowerThan(30.0,
                stepCounterPercentage)) {
            Notification.sendNotification(context, "You should go out!", "The weather is good :" + weatherEvent
                    .getCurrentForecast().getSummary(), Scenario.SECOND);
            notificationSent = true;
        }
        return true;
    }

    private boolean checkFirstScenario() {
        Double stepCounterPercentage = TriggerCache.get(TriggerType.STEP_COUNTER, Double.class);
        if (stepCounterPercentage == null) {
            thread.notifyWaitForFirst();
            return false;
        }
        if (DataProcessor.isLaterThan(17,0)) {
            if (DataProcessor.isCompletenessLowerThan(70.0, stepCounterPercentage)) {
                Notification.sendNotification(context, "MOVE!", "The day is almost over and your goal is not completed.", Scenario.FIRST);
                notificationSent = true;
            }
        }
        return true;
    }


}

package com.keepfit.triggers.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.keepfit.triggers.interests.Item;
import com.keepfit.triggers.interests.PointsOfInterestResponse;
import com.keepfit.triggers.interests.Results;
import com.keepfit.triggers.thread.BaseThread;
import com.keepfit.triggers.thread.DateThread;
import com.keepfit.triggers.thread.StepCounterThread;
import com.keepfit.triggers.thread.LocationThread;
import com.keepfit.triggers.thread.TimeThread;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.thread.WeatherThread;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.DataProcessor;
import com.keepfit.triggers.utils.Dates;
import com.keepfit.triggers.utils.Extension;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.TriggerObject;
import com.keepfit.triggers.utils.enums.Scenario;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.weather.Forecast;
import com.keepfit.triggers.weather.WeatherEvent;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 4/8/2016.
 */
public class TriggerService extends Service {
    private static final String TAG = "TriggerService";

    private static AlgorithmBaseThread thread;
    private static List<TriggerThread> threads;
    private static Context context;
    private TriggerReceiver receiver;
    private static boolean running, started;
    private boolean notificationSent = false;

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

    final class AlgorithmBaseThread extends BaseThread {

        public AlgorithmBaseThread() {
            super("AlgorithmBaseThread");
        }

        @Override
        public void doRunAction() {
        }

        @Override
        public void doStartAction() {
            Log.d(TAG, "STARTING TRIGGER SERVICE!");
            for (TriggerThread thread : threads) {
                thread.startThread();
            }
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

        @Override
        protected int getTimeout() {
            return 1000;
        }
    }

    private void handleDateReceived(Intent intent) {
        DateThread dateThread = (DateThread) getTrigger(TriggerType.CALENDAR);
    }

    private void handleLocationReceived() {

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
        Extension.sendNotification(context, "WEATHER", String.format("Summary: %s; Temp: %s; precipProb: %s; Lat: %s;" +
                        " Long: %s; ",
                forecast.getSummary(),
                forecast.getTemperature(),
                forecast.getPrecipProbability(),
                forecast.getLatitude(),
                forecast.getLongitude()));
        Extension.sendNotification(context, "WEATHER", String.format("Lat: %s; Long: %s; TZ: %s; Off: %s",
                weatherEvent.getLatitude(), weatherEvent.getLongitude(), weatherEvent.getTimezone(), weatherEvent
                        .getOffset()));
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

            Extension.sendNotification(context, "POI", info + " : " + poi);
        } else {
            Extension.sendNotification(context, "POI", String.format("No poi available for Lat: %s Long: %s",
                    poiEvent.getSourceLatitude(), poiEvent.getSourceLongitude()));
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
                        handleLocationReceived();
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

    private final static int MAX_UPDATE_ATTEMPTS = 10;
    private int updateCount = 0;

    private void checkScenarios() {
        checkFirstScenario();
        checkSecondScenario();
        checkThirdScenario();
    }

    private void checkThirdScenario() {
        if (updateCount > MAX_UPDATE_ATTEMPTS) {
            Log.w(TAG, "Max update attempts were reached for third scenario.");
            updateCount = 0;
            return;
        }
        long twoHours = 2 * 60 * 60 * 1000;

        ArrayList<KeepFitCalendarEvent> calendarEvents = (ArrayList<KeepFitCalendarEvent>) TriggerCache.get
                (TriggerType.WEATHER);
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        if (weatherEvent == null || calendarEvents == null) {
            Broadcast.broadcastUpdateForThirdScenario(context);
            updateCount++;
            return;
        }

        if (!DataProcessor.isThereAnyCalendarEventInTheWay(twoHours, calendarEvents)) {
            if (!DataProcessor.isTheWeatherBad(weatherEvent)) {
                if (DataProcessor.isLaterThan(Dates.getDateFromHours("11:00:00"))) {
                    Extension.sendNotification(context, "You should go out!", "It's early, You don't have any " +
                            "calendar events and the weather is good :" + weatherEvent.getCurrentForecast()
                            .getSummary());
                }
            }
        }
        updateCount = 0;
    }

    private void checkSecondScenario() {
        if (updateCount > MAX_UPDATE_ATTEMPTS) {
            Log.w(TAG, "Max update attempts were reached for second scenario.");
            updateCount = 0;
            return;
        }
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        Double stepCounterPercentage = TriggerCache.get(TriggerType.STEP_COUNTER, Double.class);
        if (weatherEvent == null || stepCounterPercentage == null || stepCounterPercentage == 0) {
            Broadcast.broadcastUpdateForSecondScenario(context);
            updateCount++;
            return;
        }
        if (!DataProcessor.isTheWeatherBad(weatherEvent) && DataProcessor.isCompletenessLowerThan(30.0,
                stepCounterPercentage)) {
            Extension.sendNotification(context, "You should go out!", "The weather is good :" + weatherEvent
                    .getCurrentForecast().getSummary());
            notificationSent = true;
        }
        updateCount = 0;
    }

    private void checkFirstScenario() {
        if (updateCount > MAX_UPDATE_ATTEMPTS) {
            Log.w(TAG, "Max update attempts were reached for first scenario.");
            updateCount = 0;
            return;
        }
        Double stepCounterPercentage = TriggerCache.get(TriggerType.STEP_COUNTER, Double.class);
        if (stepCounterPercentage == null) {
            Broadcast.broadcastUpdateForFirstScenario(context);
            updateCount++;
            return;
        }
        if (DataProcessor.isLaterThan(Dates.getDateFromHours("17:00:00"))) {
            if (DataProcessor.isCompletenessLowerThan(70.0, stepCounterPercentage)) {
                Extension.sendNotification(context, "MOVE!", "The day is almost over and your goal is not completed.");
                notificationSent = true;
            }
        }
        updateCount = 0;
    }

}

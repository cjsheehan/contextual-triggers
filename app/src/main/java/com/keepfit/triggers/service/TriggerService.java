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

import com.keepfit.triggers.interests.Item;
import com.keepfit.triggers.interests.Results;
import com.keepfit.triggers.notification.Notification;
import com.keepfit.triggers.thread.BaseThread;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.DataProcessor;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.utils.enums.Scenario;
import com.keepfit.triggers.utils.enums.TriggerPreference;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.weather.Forecast;
import com.keepfit.triggers.weather.WeatherEvent;

import java.util.ArrayList;
import java.util.List;

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
    private String closestPoi;

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

        @Override
        public void doRunAction() {
            if (holders != null)
                for (Holder holder : holders) {
                    waitForEvent(holder);
                }
        }

        private Holder waitForEvent(Holder holder) {
            if (holder.attempts > MAX_UPDATE_ATTEMPTS) {
                Log.w(TAG, holder.scenario.title + " has reached the max attempts.");
                holder.wait = false;
                holder.attempts = 0;
            } else {
                sleep();
                holder.attempts++;
                boolean success = false;
                switch (holder.scenario) {
                    case STEP_PERCENTAGE:
                        success = checkStepPercentageAtTime();
                        break;
                    case TIME_BETWEEN:
                        success = checkTimeBetween();
                        break;
                    case NO_CALENDAR_EVENTS:
                        success = checkCalendarEvents();
                        break;
                    case GOOD_WEATHER:
                        success = checkForWeather();
                        break;
                    case BAD_WEATHER:
                        success = checkForWeather();
                        break;
                }
                holder.wait = success;
                if (success)
                    holder.attempts = 0;
            }
            return holder;
        }

        private void sleep() {
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                Log.w(TAG, "Trigger Thread interrupted.", e);
            }
        }

        private List<Holder> holders;
        private Holder stepPercentageHolder, timeBetweenHolder, badWeatherHolder, goodWeatherHolder,
                calendarEventsHolder, pointsOfInterestHolder;

        @Override
        public void doStartAction() {
            Log.d(TAG, "STARTING TRIGGER SERVICE!");
            for (TriggerThread thread : threads) {
                thread.startThread();
            }
            stepPercentageHolder = new Holder(Scenario.STEP_PERCENTAGE);
            timeBetweenHolder = new Holder(Scenario.TIME_BETWEEN);
            badWeatherHolder = new Holder(Scenario.BAD_WEATHER);
            goodWeatherHolder = new Holder(Scenario.GOOD_WEATHER);
            calendarEventsHolder = new Holder(Scenario.NO_CALENDAR_EVENTS);
            pointsOfInterestHolder = new Holder(Scenario.POI);
            holders = new ArrayList<>();
            holders.add(stepPercentageHolder);
            holders.add(timeBetweenHolder);
            holders.add(badWeatherHolder);
            holders.add(goodWeatherHolder);
            holders.add(calendarEventsHolder);
            holders.add(pointsOfInterestHolder);
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

        protected void notifyWaitForStepPercentage() {
            stepPercentageHolder.wait = true;
        }

        public void notifyWaitForTimeBetween() {
            timeBetweenHolder.wait = true;
        }

        protected void notifyWaitForWeather() {
            badWeatherHolder.wait = true;
        }

        protected void notifyWaitForCalendarEvents() {
            calendarEventsHolder.wait = true;
        }

        public void notifyWaitForPointsOfInterest() {
            pointsOfInterestHolder.wait = true;
        }

        private class Holder {
            boolean wait = true;
            int attempts = 0;
            Scenario scenario;

            Holder(Scenario scenario) {
                this.scenario = scenario;
            }
        }
    }

    private void handleCalendarEvents() {
        checkScenarios();
    }


    private void handleLocationReceived(Intent intent) {

    }

    private void handleGeofenceReceived(Intent intent) {
        String geofenceName = (String) intent.getSerializableExtra("geofenceEvent");
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        Forecast forecast = weatherEvent.getCurrentForecast();
        if (!DataProcessor.isTheWeatherBad(weatherEvent)) {
            Notification.sendNotification(context, "You left just" + geofenceName, "You should walk home the weather is good" + forecast.getSummary
                    (), Scenario.getById(intent.getIntExtra(Broadcast.ACTION, 0)));
        } else {
            Notification.sendNotification(context, "You left just" + geofenceName, "You should take the bus the weather is bad" + forecast
                    .getSummary(), Scenario.getById(intent.getIntExtra(Broadcast.ACTION, 0)));

        }
    }

    private void handleStepCounterReceived(Intent intent) {
        double completeness = TriggerCache.get(TriggerType.STEP_COUNTER, Double.class);
        if (!DataProcessor.isCompletenessLowerThan(100, completeness)) {
            Notification.sendNotification(context, "Daily goal completed!", "Congratulation you reached your daily goal", Scenario.getById(intent
                    .getIntExtra(Broadcast.ACTION, 0)));
        } else {
            checkScenarios();
        }
    }

    private void handleTimeReceived(Intent intent) {
        checkScenarios();
    }

    public static TriggerThread getTrigger(TriggerType triggerType) {
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
        public void onReceive(Context context, Intent intent) {
            ;
            boolean isScenario = intent.getBooleanExtra(Broadcast.IS_SCENARIO, false);
            if (!isScenario) {
                TriggerType triggerType = TriggerType.getById(intent.getIntExtra(Broadcast.ACTION, 0));
                switch (triggerType) {
                    case CALENDAR:
                        handleCalendarEvents();
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
                        break;
                    case POI:
                        break;
                    case GEOFENCE:
                        handleGeofenceReceived(intent);
                        break;
                }
            } else {
                Scenario scenario = Scenario.getById(intent.getIntExtra(Broadcast.ACTION, 0));
                switch (scenario) {
                    case STEP_PERCENTAGE:
                        checkStepPercentageAtTime();
                        break;
                    case TIME_BETWEEN:
                        checkTimeBetween();
                        break;
                    case GOOD_WEATHER:
                        checkForWeather();
                        break;
                    case BAD_WEATHER:
                        checkForWeather();
                        break;
                    case NO_CALENDAR_EVENTS:
                        checkCalendarEvents();
                        break;
                    case POI:
                        checkPointsOfInterest();
                        break;
                }
            }
        }
    }

    private void checkScenarios() {
        checkStepPercentageAtTime();
        checkTimeBetween();
        checkCalendarEvents();
        checkForWeather();
        checkPointsOfInterest();
    }

    private boolean checkPointsOfInterest() {
        if (checkIfNotificationAlreadySent(Scenario.POI))
            return true;
        Results results = TriggerCache.get(TriggerType.POI, Results.class);
        Location location = TriggerCache.get(TriggerType.LOCATION, Location.class);
        if (results == null || location == null) {
            thread.notifyWaitForPointsOfInterest();
            return false;
        }
        Item[] items = results.getItems();
        if (items == null || items.length == 0) {
            Log.w(TAG, "There were no points of interest.");
            return false;
        }
        Item pointOfInterest = results.getItems()[0];
        if (DataProcessor.checkPointOfInterestNearLocation(pointOfInterest, location)) {
            Notification.sendNotification(context, "Near Event!", String.format("You are near %s!\nYou should " +
                    "consider going!", pointOfInterest.getTitle()), Scenario.POI);
        }
        return true;
    }

    private boolean checkForWeather() {
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        Double stepCounterPercentage = TriggerCache.get(TriggerType.STEP_COUNTER, Double.class);
        if (weatherEvent == null || stepCounterPercentage == null) {
            thread.notifyWaitForWeather();
            return false;
        }
        if (DataProcessor.isCompletenessLowerThan(70, stepCounterPercentage)) {
            if (DataProcessor.isTheWeatherBad(weatherEvent)) {
                if (checkIfNotificationAlreadySent(Scenario.BAD_WEATHER))
                    return true;
                Notification.sendNotification(context, "Bad weather!",
                        String.format("The weather is not too great: %s and %s.\nYou should go to the gym, or do " +
                                "something inside.", weatherEvent.getCurrentForecast().getSummary(), weatherEvent
                                .getCurrentForecast().getTemperature()), Scenario.BAD_WEATHER);

            } else {
                if (checkIfNotificationAlreadySent(Scenario.GOOD_WEATHER))
                    return true;
                Notification.sendNotification(context, "You should go out!", String.format("The weather is good: %s and " +
                                "%s.\nYou should go out and do something!", weatherEvent.getCurrentForecast().getSummary(),
                        weatherEvent.getCurrentForecast().getTemperature()), Scenario.GOOD_WEATHER);
            }
        }
        notificationSent = true;
        return true;
    }

    private boolean checkCalendarEvents() {
        if (checkIfNotificationAlreadySent(Scenario.NO_CALENDAR_EVENTS))
            return true;
        long twoHours = 2 * 60 * 60 * 1000;

        ArrayList<KeepFitCalendarEvent> calendarEvents = (ArrayList<KeepFitCalendarEvent>) TriggerCache.get(TriggerType.CALENDAR);
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        if (weatherEvent == null || calendarEvents == null) {
            thread.notifyWaitForCalendarEvents();
            return false;
        }

        if (!DataProcessor.isThereAnyCalendarEventInTheWay(twoHours, calendarEvents)) {
            if (!DataProcessor.isTheWeatherBad(weatherEvent)) {
                if (DataProcessor.isLaterThan(11, 0)) {
                    Notification.sendNotification(context, "You should go out!", "It's early, You don't have any calendar events and the weather is" +
                            " good :" + weatherEvent.getCurrentForecast().getSummary(), Scenario.NO_CALENDAR_EVENTS);
                }
            }
        }
        return true;
    }

    private boolean checkStepPercentageAtTime() {
        if (checkIfNotificationAlreadySent(Scenario.STEP_PERCENTAGE))
            return true;
        Double stepCounterPercentage = TriggerCache.get(TriggerType.STEP_COUNTER, Double.class);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        double goal = Double.parseDouble(prefs.getString(TriggerPreference.STEP_LENGTH.title, "1000"));
        if (stepCounterPercentage == null) {
            thread.notifyWaitForStepPercentage();
            return false;
        }
        if (DataProcessor.isLaterThan(17, 0)) {
            if (DataProcessor.isCompletenessLowerThan(70.0, stepCounterPercentage)) {
                Notification.sendNotification(context, "You haven't completed your daily goal!", String.format("The day is almost over and your step " +
                        "goal of %s is not completed [%s%%].", (int) goal, stepCounterPercentage.intValue()), Scenario.STEP_PERCENTAGE);
                notificationSent = true;
            }
        }
        return true;
    }

    private boolean checkTimeBetween() {
        if (checkIfNotificationAlreadySent(Scenario.TIME_BETWEEN))
            return true;
        WeatherEvent weatherEvent = TriggerCache.get(TriggerType.WEATHER, WeatherEvent.class);
        ArrayList<KeepFitCalendarEvent> calendarEvents = (ArrayList<KeepFitCalendarEvent>) TriggerCache.get
                (TriggerType.CALENDAR);
        Results results = TriggerCache.get(TriggerType.POI, Results.class);
        // Only rely on the weather for this trigger, but if calendar or points of interest events are available, use those
        if (weatherEvent == null || results == null || results.getItems() == null || results.getItems()[0] == null) {
            thread.notifyWaitForTimeBetween();
            return false;
        }
        if (DataProcessor.isTimeBetween(8, 0, 10, 0)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            double goal = Double.parseDouble(prefs.getString(TriggerPreference.STEP_LENGTH.title, "1000"));
            StringBuilder notificationMessage = new StringBuilder(String.format("You have a step goal of %s for " +
                    "today!", (int) goal));
            Item poiEvent;
            if (results != null && results.getItems() != null && results.getItems()[0] != null) {
                poiEvent = results.getItems()[0];
                notificationMessage.append(String.format("\nYou should consider going to %s", poiEvent.getTitle()));
            }
            if (calendarEvents != null && !calendarEvents.isEmpty()) {
                KeepFitCalendarEvent calendarEvent = calendarEvents.get(0);
                notificationMessage.append(String.format("\nJust so you know: %s at %s - %s", calendarEvent.getName(),
                        calendarEvent.getStartDate(), calendarEvent.getEndDate()));
            }
            Notification.sendNotification(context, "Good morning!", notificationMessage.toString(), Scenario
                    .TIME_BETWEEN);
        }
        return true;
    }

    private boolean checkIfNotificationAlreadySent(Scenario scenario) {
        Notification notification = TriggerCache.get(scenario, Notification.class);
        return notification != null;
    }


}

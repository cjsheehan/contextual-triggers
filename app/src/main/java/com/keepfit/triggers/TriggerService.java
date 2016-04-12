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
import com.keepfit.triggers.thread.DateThread;
import com.keepfit.triggers.thread.StepCounterThread;
import com.keepfit.triggers.thread.TimeThread;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.thread.WeatherThread;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.Dates;
import com.keepfit.triggers.utils.Extension;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.weather.WeatherEvent;
import com.keepfit.triggers.weather.WeatherService;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private List<KeepFitCalendarEvent> calendarEvents;
    private WeatherEvent weatherEvent;
    private Double stepcounterPercentage;
    private String time;
    boolean  notificationSent = false;

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

    private TriggerThread getTrigger(TriggerType triggerType) {
        TriggerThread thread = null;
        for (TriggerThread t : threads) {
            if (t.getTriggerType().equals(triggerType)) {
                thread = t;
                break;
            }
        }
        return thread;
    }

    private void handleDateReceived(Intent intent) {
        DateThread dateThread = (DateThread) getTrigger(TriggerType.CALENDAR);
        calendarEvents = dateThread.getTriggerObject();
    }

    private void handleLocationReceived() {
         }

    private void handleStepCounterReceived() {
        StepCounterThread stepCounterThread = (StepCounterThread) getTrigger(TriggerType.STEP_COUNTER);
        stepcounterPercentage = stepCounterThread.getTriggerObject();
    }

    private void handleTimeReceived(Intent intent) {
        TimeThread timeThread = (TimeThread) getTrigger(TriggerType.TIME);
        time = timeThread.getTriggerObject();
    }

    private void handleWeatherReceived(Intent intent) {
        WeatherThread weatherThread = (WeatherThread) getTrigger(TriggerType.WEATHER);
        weatherEvent = weatherThread.getTriggerObject();
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


            checkFirstScenario();
            checkSecondScenario();
        }


    }

    private void checkSecondScenario() {

    }

    private void checkFirstScenario() {
        if (isLaterThan(Dates.getDateFromHours("17:00:00"))){
            if(isCompletenessLowerThan(70)){
                Extension.sendNotification(context, "MOVE!", "The day is almost over and your goal is not completed.");
                notificationSent = true;
            }
        }
    }




    private boolean isThereAnyCalendarEventInTheWay(long differenceParam){
        for (KeepFitCalendarEvent event : this.calendarEvents) {
            long startDifference = Math.abs(event.getStart().getTime() - new Date(System.currentTimeMillis()));
            long endDifference = Math.abs(event.getEnd().getTime() - new Date(System.currentTimeMillis()));
            if (startDifference < differenceParam || endDifference < differenceParam){
                System.out.println("Annoying calendar event found: " + event.getName());
                return true;
            }
        }
        System.out.println("There are no annoying calendar events");
        return false;
    };

    private boolean isLaterThan(Date date){
        Date currentTime = new Date(System.currentTimeMillis());
        if (currentTime.getTime()>date.getTime()){
            System.out.println("The current time is: " + currentTime.getTime() + " later than " + date.getTime());
            return true;
        }
        System.out.println("The current time is: " + currentTime.getTime() + " earlier than " + date.getTime());
        return false;
    }

    private boolean isCompletenessLowerThan(double percentage){
        if (this.stepcounterPercentage< percentage){
            System.out.println("The dailiy step goal completeness is: " + this.stepcounterPercentage + " lower than " + percentage);
            return true;
        }
        System.out.println("The dailiy step goal completeness is: " + this.stepcounterPercentage + " larger than " + percentage);
        return false;
    }





}

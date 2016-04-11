package com.keepfit.triggers.utils;

import android.content.Context;
import android.content.Intent;

import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.weather.WeatherEvent;

import java.util.ArrayList;

/**
 * Created by Edward on 4/9/2016.
 */
public class Broadcast {

    public static String ACTION = "action";

    /*****
     * Broadcasting
     *****/

    public static void broadcastTimeReached(Context context, String timeStamp) {
        Intent intent = new Intent(TriggerType.TIME.title);
        intent.putExtra("timeStamp", timeStamp);
        intent.putExtra(ACTION, TriggerType.TIME.id);
        context.sendBroadcast(intent);
    }

    public static void broadcastCalendarEvents(Context context, ArrayList<KeepFitCalendarEvent> events) {
        Intent intent = new Intent(TriggerType.CALENDAR.title);
        intent.putExtra("events", events);
        intent.putExtra(ACTION, TriggerType.CALENDAR.id);
        context.sendBroadcast(intent);
    }

    public static void broadcastStepCompleteness(Context context, int completeness) {
        Intent intent = new Intent(TriggerType.STEP_COUNTER.title);
        intent.putExtra("completeness", completeness);
        intent.putExtra(ACTION, TriggerType.STEP_COUNTER.id);
        context.sendBroadcast(intent);
    }

    public static void broadcastWeatherEvents(Context context, WeatherEvent weatherEvent) {
        Intent intent = new Intent(TriggerType.WEATHER.title);
        intent.putExtra("weatherEvent", weatherEvent);
        intent.putExtra(ACTION, TriggerType.WEATHER.id);
        context.sendBroadcast(intent);
    }

}

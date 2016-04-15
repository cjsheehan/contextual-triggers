package com.keepfit.triggers.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.GeofencingEvent;
import com.keepfit.triggers.interests.Results;
import com.keepfit.triggers.utils.enums.Scenario;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.weather.WeatherEvent;

import java.util.ArrayList;

/**
 * Created by Edward on 4/9/2016.
 */
public class Broadcast {

    public static final String ACTION = "action";
    public static final String IS_SCENARIO = "isScenario";

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

    public static void broadcastStepCompleteness(Context context, double completeness) {
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

    public static void broadcastGeofenceEvents(Context context, String geofencingEvent) {
        Intent intent = new Intent(TriggerType.LOCATION.title);
        intent.putExtra("geofenceEvent", geofencingEvent);
        intent.putExtra(ACTION, TriggerType.LOCATION.id);
        context.sendBroadcast(intent);
    }

    public static void broadcastLocationReceived(Context context, Location location) {
        Intent intent = new Intent(TriggerType.LOCATION.title);
        intent.putExtra("location", location);
        intent.putExtra(ACTION, TriggerType.LOCATION.id);
        context.sendBroadcast(intent);
    }

    public static void broadcastPointsOfInterestEvents(Context context, Results poiEvent) {
        Intent intent = new Intent(TriggerType.POI.title);
        intent.putExtra("poiEvent", poiEvent);
        intent.putExtra(ACTION, TriggerType.POI.id);
        context.sendBroadcast(intent);
    }

    public static void broadcastUpdateForFirstScenario(Context context) {
        Intent intent = new Intent(Scenario.FIRST.title);
        intent.putExtra(ACTION, Scenario.FIRST.id);
        intent.putExtra(IS_SCENARIO, true);
        context.sendBroadcast(intent);
    }

    public static void broadcastUpdateForSecondScenario(Context context) {
        Intent intent = new Intent(Scenario.SECOND.title);
        intent.putExtra(ACTION, Scenario.SECOND.id);
        intent.putExtra(IS_SCENARIO, true);
        context.sendBroadcast(intent);
    }

    public static void broadcastUpdateForThirdScenario(Context context) {
        Intent intent = new Intent(Scenario.THIRD.title);
        intent.putExtra(ACTION, Scenario.THIRD.id);
        intent.putExtra(IS_SCENARIO, true);
        context.sendBroadcast(intent);
    }
}

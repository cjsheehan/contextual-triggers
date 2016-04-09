package com.keepfit.triggers.utils;

import android.content.Context;
import android.content.Intent;

import com.keepfit.triggers.utils.enums.Action;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;

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
        Intent intent = new Intent(Action.TIME.title);
        intent.putExtra("timeStamp", timeStamp);
        intent.putExtra(ACTION, Action.TIME.id);
        context.sendBroadcast(intent);
    }

    public static void broadcastCalendarEvents(Context context, ArrayList<KeepFitCalendarEvent> events) {
        Intent intent = new Intent(Action.DATE.title);
        intent.putExtra("events", events);
        intent.putExtra(ACTION, Action.DATE.id);
        context.sendBroadcast(intent);
    }

    public static void broadcastStepCompleteness(Context context, int completeness) {
        Intent intent = new Intent(Action.STEP_COUNTER.title);
        intent.putExtra("completness", completeness);
        intent.putExtra(ACTION, Action.STEP_COUNTER.id);
        context.sendBroadcast(intent);
    }

}

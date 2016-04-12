package com.keepfit.triggers.thread;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.Keep;
import android.text.format.DateUtils;
import android.util.Log;


import com.keepfit.triggers.utils.Broadcast;

import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.utils.enums.TriggerType;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Edward on 4/8/2016.
 */
public class DateThread extends TriggerThread<List<KeepFitCalendarEvent>> {
    private static final String TAG = "DateThread";
    private static final String TITLE = "Date";

    public ArrayList<KeepFitCalendarEvent> events = new ArrayList<>();

    public static ArrayList<String> nameOfEvent = new ArrayList<>();
    public static ArrayList<String> startDates = new ArrayList<>();
    public static ArrayList<String> endDates = new ArrayList<>();
    public static ArrayList<String> descriptions = new ArrayList<>();

    public DateThread(Context context) {
        super(TITLE, TriggerType.CALENDAR, false, context);
    }

    boolean sent = false;

    @Override
    public void doRunAction() {
        if(isRunning()) {
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.DATE, 1);

            String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startDate.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endDate.getTimeInMillis() + " ))";


            Cursor cursor = null;
            cursor = context.getContentResolver()
                    .query(
                            Uri.parse("content://com.android.calendar/events"),
                            new String[]{"calendar_id", "title", "description",
                                    "dtstart", "dtend", "eventLocation"}, selection,
                            null, null);
            cursor.moveToFirst();
            String CNames[] = new String[cursor.getCount()];

            // fetching calendars id
            nameOfEvent.clear();
            startDates.clear();
            endDates.clear();
            descriptions.clear();
            for (int i = 0; i < CNames.length; i++) {

                String eventName = cursor.getString(1);
                String startDt = getDate(Long.parseLong(cursor.getString(3)));
                String endDt = getDate(Long.parseLong(cursor.getString(4)));
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Date start = new Date();
                Date end = new Date();
                try {
                    start = dateFormat.parse(startDt);
                    end = dateFormat.parse(endDt);
                }catch(ParseException e){

                }

//            descriptions.add(cursor.getString(2));
//            CNames[i] = cursor.getString(1);
                KeepFitCalendarEvent newCalendarEvent = new KeepFitCalendarEvent(eventName, start, end);
                events.add(newCalendarEvent);
                cursor.moveToNext();

            }

//        for(int i = 0; i < events.size(); i++) {
//            Log.d("Name Of Event", events.get(i).getName());
//            Log.d("Start Time", events.get(i).getStart());
//            Log.d("End Time", events.get(i).getEnd());
//        }
            if (!sent) {
                Broadcast.broadcastCalendarEvents(context, events);
                sent = true;
            }
        }
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }



    @Override
    public void doStartAction() {
    }

    @Override
    public void doStopAction() {

    }

    @Override
    public List<KeepFitCalendarEvent> getTriggerObject() {
        return events;
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getMessage() {
        return String.format("You reached the goal for the date!");
    }
}

package com.keepfit.triggers.thread;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.Dates;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.utils.enums.TriggerType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Edward on 4/8/2016.
 */
public class CalendarThread extends TriggerThread<List<KeepFitCalendarEvent>> {
    private static final String TAG = "CalendarThread";
    private static final String TITLE = "Calendar";

    public ArrayList<KeepFitCalendarEvent> events;

    public CalendarThread(Context context) {
        super(TITLE, TriggerType.CALENDAR, false, context);
    }

    @Override
    public void doTriggerRunAction() {
        if (shouldRefresh()) {
            populateCalendarEvents();
        }
    }

    private void populateCalendarEvents() {
        events = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 1);

        String[] projection = new String[]{
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_LOCATION};
        String selection = String.format("((%s >= ?) AND (%s <= ?))", CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND);
        String[] selectionArgs = new String[]{String.valueOf(startDate.getTimeInMillis()), String.valueOf(endDate.getTimeInMillis())};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(
                            Uri.parse("content://com.android.calendar/events"), projection, selection, selectionArgs, null);
            while (cursor.moveToNext()) {
                String eventName = cursor.getString(cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE));
                String startDt = Dates.getDate(cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART)));
                String endDt = Dates.getDate(cursor.getLong(cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND)));

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Date start = new Date();
                Date end = new Date();
                try {
                    start = dateFormat.parse(startDt);
                    end = dateFormat.parse(endDt);
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                KeepFitCalendarEvent newCalendarEvent = new KeepFitCalendarEvent(eventName, start, end);
                events.add(newCalendarEvent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception when getting calendar events...", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        createTestEvent();
        TriggerCache.put(TriggerType.CALENDAR, events);
        Broadcast.broadcastCalendarEvents(context, events);
    }

    private void createTestEvent() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Dates.DATE);
        Date start = new Date();
        Date end = new Date();
        try {
            start = dateFormat.parse(Dates.convertDate(startDate.getTime(), Dates.DATE));
            end = dateFormat.parse(Dates.convertDate(endDate.getTime(), Dates.DATE));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        KeepFitCalendarEvent event = new KeepFitCalendarEvent("Android Class Today", start, end);
        events.add(event);
    }

    @Override
    public void doStartAction() {
        populateCalendarEvents();
    }

    @Override
    public void doStopAction() {

    }

    @Override
    public String getTextToDisplayOnUI() {
        if (events == null || events.size() == 0)
            return "No events";
        return String.format("%s [%s - %s]", events.get(0).getName(), events.get(0).getStartDate(), events.get(0).getEndDate());
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

}

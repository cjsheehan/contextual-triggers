package com.keepfit.triggers.utils;

import android.location.Location;
import android.util.Log;

import com.keepfit.triggers.interests.Item;
import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.weather.Forecast;
import com.keepfit.triggers.weather.WeatherEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kornelkotan on 12/04/2016.
 */
public class DataProcessor {
    private static final String TAG = "DataProcessor";

    public static boolean isThereAnyCalendarEventInTheWay(long differenceParam, List<KeepFitCalendarEvent>
            calendarEvents) {
        for (KeepFitCalendarEvent event : calendarEvents) {
            long startDifference = Math.abs(event.getStart().getTime() - new Date(System.currentTimeMillis()).getTime
                    ());
            long endDifference = Math.abs(event.getEnd().getTime() - new Date(System.currentTimeMillis()).getTime());
            if (startDifference < differenceParam || endDifference < differenceParam) {
                Log.i(TAG, "Annoying calendar event found: " + event.getName());
                return true;
            }
        }
        Log.i(TAG, "There are no annoying calendar events");
        return false;
    }

    public static boolean isLaterThan(int hourOfDay, int minute) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, hourOfDay);
        today.set(Calendar.MINUTE, minute);
        today.set(Calendar.SECOND, 0);

        Date currentTime = new Date(System.currentTimeMillis());
        if (currentTime.getTime() > today.getTime().getTime()) {
            Log.i(TAG, "The current time is: " + currentTime.getTime() + " later than " + today.getTime());
            return true;
        }
        Log.i(TAG, "The current time is: " + currentTime.getTime() + " earlier than " + today.getTime());
        return false;
    }

    public static boolean isTimeBetween(int startHour, int startMinute, int endHour, int endMinute) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, startHour);
        todayStart.set(Calendar.MINUTE, startMinute);
        todayStart.set(Calendar.SECOND, 0);
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, endHour);
        todayEnd.set(Calendar.MINUTE, endMinute);
        todayEnd.set(Calendar.SECOND, 0);

        Date currentTime = new Date(System.currentTimeMillis());
        if (currentTime.after(todayStart.getTime()) && currentTime.before(todayEnd.getTime())) {
            Log.i(TAG, String.format("The current time[%s] is between %s and %s", currentTime.getTime(), todayStart.getTime(), todayEnd.getTime()));
            return true;
        }
        Log.i(TAG, String.format("The current time[%s] is not between %s and %s", currentTime.getTime(), todayStart.getTime(), todayEnd.getTime()));
        return false;
    }

    public static boolean isCompletenessLowerThan(double percentage, double completenessPercentage) {
        if (completenessPercentage < percentage) {
            Log.i(TAG, "The daily step goal completeness is: " + completenessPercentage + " lower than " +
                    percentage);
            return true;
        }
        Log.i(TAG, "The daily step goal completeness is: " + completenessPercentage + " larger than " +
                percentage);
        return false;
    }

    public static boolean isTheWeatherBad(WeatherEvent weatherEvent) {
        Forecast currentForecast = weatherEvent.getCurrentForecast();
        if (currentForecast.getTemperature() < 40.0)
            return true;
        if(currentForecast.getPrecipProbability() > 0.5)
            return true;
        return false;
    }

    public static boolean checkPointOfInterestNearLocation(Item pointOfInterest, Location location) {
        double pointLat = Double.parseDouble(pointOfInterest.getPosition()[0]);
        double pointLong = Double.parseDouble(pointOfInterest.getPosition()[1]);

        // Create a new location for the point of interest by copying the current location
        Location pointLocation = new Location(location);
        pointLocation.setLatitude(pointLat);
        pointLocation.setLongitude(pointLong);

        float distanceTo = location.distanceTo(location);

        return distanceTo < 50;
    }

}

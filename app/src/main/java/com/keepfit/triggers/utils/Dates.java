package com.keepfit.triggers.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Edward on 4/9/2016.
 */
public class Dates {

    public static String PRETTY_DATE = "dd/MM/yyyy";
    public static String NUMBER_DATE = "yyyyMMdd";
    public static String TIME = "HH:mm:ss";
    public static String DATE = "MM/dd/yyyy HH:mm";

    /**
     * Get the specified date in the pretty date format (dd/MM/yyyy).
     *
     * @param date The date to convert to pretty date format.
     * @return The date.
     */
    public static String getCalendarPrettyDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PRETTY_DATE);
        return simpleDateFormat.format(date);
    }

    public static String getCalendarPrettyDate() {
        Date currentDate = getDate();
        return getCalendarPrettyDate(currentDate);
    }

    public static String getDateFromRange(int dateRange, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1 * dateRange);
        Date previousDate = calendar.getTime();
        return new SimpleDateFormat(format).format(previousDate);
    }

    public static String getNumberDateStamp() {
        Date date = getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NUMBER_DATE);
        return simpleDateFormat.format(date);
    }

    public static String getTime() {
        Date date = getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME);
        return simpleDateFormat.format(date);
    }

    public static String getPrettyDateWithTime() {
        Date date = getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE);
        return simpleDateFormat.format(date);
    }

    public static boolean timeIntervalPassed(String timeInterval) {
        return timeIntervalPassed(getTime(), timeInterval);
    }

    public static boolean timeIntervalPassed(String currentTime, String timeInterval) {
        Calendar currentCal = getCalendarForTime(currentTime);
        Calendar intervalCal = getCalendarForTime(timeInterval);
        return currentCal.after(intervalCal);
    }

    private static Calendar getCalendarForTime(String time) {
        String[] times = time.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, Integer.parseInt(times[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(times[1]));
        calendar.set(Calendar.SECOND, Integer.parseInt(times[2]));
        return calendar;
    }

    public static String getDateFromNumber(double date) {
        return convertDate(String.valueOf(date), NUMBER_DATE, PRETTY_DATE);
    }

    public static Date getDate() {
        return new Date();
    }

    public static String convertDate(String date, String fromFormat, String toFormat) {
        SimpleDateFormat fromDateFormat = new SimpleDateFormat(fromFormat);
        String d = null;
        try {
            Date theDate = fromDateFormat.parse(date);
            SimpleDateFormat toDateFormat = new SimpleDateFormat(toFormat);
            d = toDateFormat.format(theDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String convertDate(Date date, String toFormat) {
        SimpleDateFormat toDateFormat = new SimpleDateFormat(toFormat);
        return toDateFormat.format(date);
    }

    public static Date getDateFromHours(String hours) {
        Date date1 = null;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            date1 = format.parse(hours);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}

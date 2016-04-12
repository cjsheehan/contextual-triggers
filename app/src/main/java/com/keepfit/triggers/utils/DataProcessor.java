package com.keepfit.triggers.utils;

import com.keepfit.triggers.utils.enums.KeepFitCalendarEvent;
import com.keepfit.triggers.weather.Forecast;
import com.keepfit.triggers.weather.WeatherEvent;

import java.util.Date;
import java.util.List;

/**
 * Created by kornelkotan on 12/04/2016.
 */
public class DataProcessor {


    public static boolean isThereAnyCalendarEventInTheWay(long differenceParam, List<KeepFitCalendarEvent> calendarEvents){
        for (KeepFitCalendarEvent event : calendarEvents) {
            long startDifference = Math.abs(event.getStart().getTime() - new Date(System.currentTimeMillis()).getTime());
            long endDifference = Math.abs(event.getEnd().getTime() - new Date(System.currentTimeMillis()).getTime());
            if (startDifference < differenceParam || endDifference < differenceParam){
                System.out.println("Annoying calendar event found: " + event.getName());
                return true;
            }
        }
        System.out.println("There are no annoying calendar events");
        return false;
    };

    public static boolean isLaterThan(Date date){
        Date currentTime = new Date(System.currentTimeMillis());
        if (currentTime.getTime()>date.getTime()){
            System.out.println("The current time is: " + currentTime.getTime() + " later than " + date.getTime());
            return true;
        }
        System.out.println("The current time is: " + currentTime.getTime() + " earlier than " + date.getTime());
        return false;
    }

    public static boolean isCompletenessLowerThan(double percentage, double completenessPercentage){
        if (completenessPercentage< percentage){
            System.out.println("The dailiy step goal completeness is: " + completenessPercentage + " lower than " + percentage);
            return true;
        }
        System.out.println("The dailiy step goal completeness is: " + completenessPercentage + " larger than " + percentage);
        return false;
    }

    public static boolean isTheWeatherBad(WeatherEvent weatherEvent){
        Forecast currentForecast =  weatherEvent.getCurrentForecast();
        if(currentForecast.getTemperature()<10.0)
            return true;
        if(currentForecast.getPrecipProbability()>50.0)
            return true;
        return false;
    }

}

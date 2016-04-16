package com.keepfit.triggers.utils.enums;

import java.io.Serializable;

/**
 * Created by Edward on 4/14/2016.
 */
public enum Scenario implements Serializable {
    STEP_PERCENTAGE(1, "stepPercentage"),
    TIME_BETWEEN(2, "timeBetween"),
    NO_CALENDAR_EVENTS(3, "noCalendarEvents"),
    GOOD_WEATHER(4, "goodWeather"),
    BAD_WEATHER(5, "badWeather"),
    POI(6, "poi");

    public int id;
    public String title;

    Scenario(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Scenario getById(int id) {
        for (Scenario scenario : Scenario.values())
            if (id == scenario.id)
                return scenario;
        return null;
    }
}

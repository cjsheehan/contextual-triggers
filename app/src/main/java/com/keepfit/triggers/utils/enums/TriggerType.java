package com.keepfit.triggers.utils.enums;

/**
 * Created by Edward on 4/9/2016.
 */
public enum TriggerType {

    CALENDAR(1, "Date"),
    LOCATION(2, "Location"),
    STEP_COUNTER(3, "Step Counter"),
    TIME(4, "Time"),
    WEATHER(5, "Weather");

    public int id;
    public String title;

    TriggerType(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static TriggerType getById(int id) {
        for (TriggerType triggerType : TriggerType.values())
            if (id == triggerType.id)
                return triggerType;
        return null;
    }
}

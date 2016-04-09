package com.keepfit.triggers.utils.enums;

/**
 * Created by Edward on 4/9/2016.
 */
public enum Action {

    DATE(1, "Date"),
    LOCATION(2, "Location"),
    STEP_COUNTER(3, "Step Counter"),
    TIME(4, "Time"),
    WEATHER(5, "Weather");

    public int id;
    public String title;

    Action(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Action getById(int id) {
        for (Action action : Action.values())
            if (id == action.id)
                return action;
        return null;
    }
}

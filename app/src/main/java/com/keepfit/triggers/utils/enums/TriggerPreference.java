package com.keepfit.triggers.utils.enums;

/**
 * Created by Edward on 4/12/2016.
 */
public enum TriggerPreference {

    STEP_LENGTH("step_length"),
    HOME_LONGITUDE("home_longitude"),
    HOME_LATITUDE("home_latitude"),
    WORK_LONGITUDE("work_longitude"),
    WORK_LATITUDE("work_latitude"),
    CUSTOM_LONGITUDE("custom_longitude"),
    CUSTOM_LATITUDE("custom_latitude");

    public String title;

    TriggerPreference(String title) {
        this.title = title;
    }

}

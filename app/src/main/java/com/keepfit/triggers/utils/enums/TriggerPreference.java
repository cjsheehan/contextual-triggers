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
    CUSTOM_LATITUDE("custom_latitude"),
    HOME_CURRENT_LOCATION("home_location_button"),
    WORK_CURRENT_LOCATION("work_location_button"),
    CUSTOM_CURRENT_LOCATION("custom_location_button"),
    HOME_ADDRESS("home_address"),
    WORK_ADDRESS("work_address"),
    CUSTOM_ADDRESS("custom_address"),
    EXIT_GEOFENCES("exit_geofences"),
    ENTER_GEOFENCES("enter_geofences");

    public String title;

    TriggerPreference(String title) {
        this.title = title;
    }

}

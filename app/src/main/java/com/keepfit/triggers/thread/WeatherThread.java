package com.keepfit.triggers.thread;

import android.content.Context;
import android.location.Location;

import com.android.volley.VolleyError;
import com.keepfit.triggers.listener.PermissionRequestListener;
import com.keepfit.triggers.listener.PermissionResponseListener;
import com.keepfit.triggers.listener.ResponseListener;
import com.keepfit.triggers.service.LocationService;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.weather.WeatherEvent;
import com.keepfit.triggers.utils.enums.TriggerType;

import com.keepfit.triggers.service.WeatherService;

public class WeatherThread extends TriggerThread<WeatherEvent> implements ResponseListener<WeatherEvent> {
    private static final String TAG = "WeatherThread";
    private static final String TITLE = "Weather Service";
    private static final int STANDARD_TIMEOUT = 1000 * 3600 * 2;  // 2 hours
    private static int WAIT_TIMEOUT = 1000;
    private static int TIMEOUT;

    private Context context;
    private WeatherService weatherService;
    private WeatherEvent weatherEvent;
    private boolean waitForLocation = false;

    public WeatherThread(Context context, PermissionRequestListener listener) {
        super(TITLE, TriggerType.WEATHER, false, context);
        this.context = context;
        TIMEOUT = WAIT_TIMEOUT;
    }

    @Override
    public void doRunAction() {
        if (waitForLocation) {
            Location location = TriggerCache.get(TriggerType.LOCATION, Location.class);
            if (location != null) {
                requestWeather(location.getLatitude(), location.getLongitude());
                waitForLocation = false;
            }
        }
    }

    @Override
    public void doStartAction() {
        weatherService = new WeatherService(context);
        weatherService.registerResponseListener(this);

        Location location = TriggerCache.get(TriggerType.LOCATION, Location.class);
        if (location == null) {
            // Need to wait for LocationThread to get location
            TIMEOUT = WAIT_TIMEOUT;
            waitForLocation = true;
        } else {
            requestWeather(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void doStopAction() {

    }

    public void requestWeather(double latitude, double longitude) {
        weatherService.requestWeather(latitude, longitude);
    }

    @Override
    public WeatherEvent getTriggerObject() {
        return weatherEvent;
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getMessage() {
        return String.format("You reached the goal for weather!");
    }

    @Override
    public void onResponse(WeatherEvent response) {
        if(response != null) {
            weatherEvent  = response;
            TriggerCache.put(TriggerType.WEATHER, weatherEvent);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    protected int getTimeout() {
        return TIMEOUT;
    }
}

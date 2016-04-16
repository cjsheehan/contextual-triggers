package com.keepfit.triggers.thread;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.VolleyError;
import com.keepfit.triggers.listener.ResponseListener;
import com.keepfit.triggers.service.WeatherService;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.TriggerType;
import com.keepfit.triggers.weather.WeatherEvent;

public class WeatherThread extends TriggerThread<WeatherEvent> implements ResponseListener<WeatherEvent> {
    private static final String TAG = "WeatherThread";
    private static final String TITLE = "Weather Service";

    private Context context;
    private WeatherService weatherService;
    private WeatherEvent weatherEvent;
    private boolean waitForLocation = false, waitForConnectionPermission = true;

    public WeatherThread(Context context) {
        super(TITLE, TriggerType.WEATHER, false, context);
        this.context = context;
    }

    @Override
    public void doTriggerRunAction() {
        if (waitForLocation || weatherEvent == null) {
            Location location = TriggerCache.get(TriggerType.LOCATION, Location.class);
            if (location != null) {
                requestWeather(location.getLatitude(), location.getLongitude());
                waitForLocation = false;
            }
        }
        if (shouldRefresh()) {
            // Need to do a request again, so setting this flag to true will be all we need to trigger that
            waitForLocation = true;
        }
    }

    @Override
    public void doStartAction() {
        weatherService = new WeatherService(context);
        weatherService.registerResponseListener(this);

        Location location = TriggerCache.get(TriggerType.LOCATION, Location.class);
        if (location == null) {
            // Need to wait for LocationThread to get location
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
    protected String getTitle() {
        return TITLE;
    }

    @Override
    public String getTextToDisplayOnUI() {
        return weatherEvent == null ? "No weather info" : String.format("%s - %sF", weatherEvent.getCurrentForecast()
                .getSummary(), weatherEvent.getCurrentForecast().getTemperature());
    }

    @Override
    public void onResponse(WeatherEvent response) {
        if (response != null) {
            weatherEvent = response;
            TriggerCache.put(TriggerType.WEATHER, weatherEvent);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}

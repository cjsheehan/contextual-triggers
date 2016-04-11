package com.keepfit.triggers.thread;

import android.content.Context;

import com.android.volley.VolleyError;
import com.keepfit.triggers.listener.ResponseListener;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.weather.WeatherEvent;
import com.keepfit.triggers.utils.enums.TriggerType;

import com.keepfit.triggers.weather.WeatherService;

public class WeatherThread extends TriggerThread<WeatherEvent> implements ResponseListener<WeatherEvent> {
    private static final String TAG = "WeatherThread";
    private static final String TITLE = "Weather Service";

    private Context context;
    private WeatherService weatherService;
    private WeatherEvent weatherEvent;
    private double lat, lon;
    private boolean sent = false;

    public WeatherThread(Context context) {
        super(TITLE, TriggerType.WEATHER, false, context);
        this.context = context;
    }

    @Override
    public void doRunAction() {
    }

    @Override
    public void doStartAction() {
        weatherService = new WeatherService(context);
        weatherService.registerResponseListener(this);
        requestWeather(55.744197, -4.190944);
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
            Broadcast.broadcastWeatherEvents(context, weatherEvent);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}

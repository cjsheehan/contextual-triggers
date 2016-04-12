package com.keepfit.triggers.weather;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Chris on 10/04/2016.
 */
public class WeatherEvent implements Serializable {
    private static final String TAG = "WeatherEvent";
    private String latitude;
    private String longitude;
    private String timezone;
    private String offset;
    private DataPoint currently;
    private DataBlock minutely;
    private DataBlock hourly;
    private DataBlock daily;
    private AlertObject[] alerts;

    public WeatherEvent(String latitude, String longitude, String timezone, String offset, DataPoint currently, DataBlock minutely, DataBlock hourly, DataBlock daily, AlertObject[] alerts) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.offset = offset;
        this.currently = currently;
        this.minutely = minutely;
        this.hourly = hourly;
        this.daily = daily;
        this.alerts = alerts;
    }

    public Forecast getCurrentForecast() {
        Forecast forecast = new Forecast();
        try {
            forecast.setLatitude(Double.parseDouble(latitude));
            forecast.setLongitude(Double.parseDouble(longitude));
            forecast.setTime(Integer.parseInt(currently.time));
            forecast.setSummary(currently.summary);
            forecast.setTemperature(Double.parseDouble(currently.temperature));
            forecast.setPrecipProbability(Double.parseDouble(currently.precipProbability));
            if(currently.sunriseTime != null)
                forecast.setSunriseTime(Integer.parseInt(currently.sunriseTime));
            if(currently.sunsetTime != null)
                forecast.setSunsetTime(Integer.parseInt(currently.sunsetTime));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to convert params for Forecast object");
        }
        return forecast;
    }

    @Override
    public String toString() {
        return String.format("Weather Event");
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public DataPoint getCurrently() {
        return currently;
    }

    public void setCurrently(DataPoint currently) {
        this.currently = currently;
    }

    public DataBlock getMinutely() {
        return minutely;
    }

    public void setMinutely(DataBlock minutely) {
        this.minutely = minutely;
    }

    public DataBlock getHourly() {
        return hourly;
    }

    public void setHourly(DataBlock hourly) {
        this.hourly = hourly;
    }

    public DataBlock getDaily() {
        return daily;
    }

    public void setDaily(DataBlock daily) {
        this.daily = daily;
    }

    public AlertObject[] getAlerts() {
        return alerts;
    }

    public void setAlerts(AlertObject[] alerts) {
        this.alerts = alerts;
    }

    class DataBlock implements Serializable {
        String summary;
        String icon;
        DataPoint[] data;

        public DataBlock(String summary, String icon, DataPoint[] data) {
            this.summary = summary;
            this.icon = icon;
            this.data = data;
        }
    }

    class DataPoint implements Serializable {
        String time;
        String summary;
        String icon;
        String sunriseTime;
        String sunsetTime;
        String moonPhase;
        String nearestStormDistance;
        String nearestStormBearing;
        String precipIntensity;
        String precipIntensityMax;
        String precipProbability;
        String precipType;
        String precipAccumulation;
        String temperature;
        String temperatureMin;
        String temperatureMinTime;
        String temperatureMax;
        String temperatureMaxTime;
        String dewPoint;
        String windSpeed;
        String cloudCover;
        String humidity;
        String pressure;
        String visibility;
        String ozone;

        public DataPoint(String time, String summary, String icon, String sunriseTime, String sunsetTime, String moonPhase, String nearestStormDistance, String nearestStormBearing, String precipIntensity, String precipIntensityMax, String precipProbability, String precipType, String precipAccumulation, String temperature, String temperatureMin, String temperatureMinTime, String temperatureMax, String temperatureMaxTime, String dewPoint, String windSpeed, String cloudCover, String humidity, String pressure, String visibility, String ozone) {
            this.time = time;
            this.summary = summary;
            this.icon = icon;
            this.sunriseTime = sunriseTime;
            this.sunsetTime = sunsetTime;
            this.moonPhase = moonPhase;
            this.nearestStormDistance = nearestStormDistance;
            this.nearestStormBearing = nearestStormBearing;
            this.precipIntensity = precipIntensity;
            this.precipIntensityMax = precipIntensityMax;
            this.precipProbability = precipProbability;
            this.precipType = precipType;
            this.precipAccumulation = precipAccumulation;
            this.temperature = temperature;
            this.temperatureMin = temperatureMin;
            this.temperatureMinTime = temperatureMinTime;
            this.temperatureMax = temperatureMax;
            this.temperatureMaxTime = temperatureMaxTime;
            this.dewPoint = dewPoint;
            this.windSpeed = windSpeed;
            this.cloudCover = cloudCover;
            this.humidity = humidity;
            this.pressure = pressure;
            this.visibility = visibility;
            this.ozone = ozone;
        }
    }

    class AlertObject implements Serializable {
        String title;
        String expires;
        String description;
        String uri;

        public AlertObject(String title, String expires, String description, String uri) {
            this.title = title;
            this.expires = expires;
            this.description = description;
            this.uri = uri;
        }
    }
}

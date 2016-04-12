package com.keepfit.triggers.thread;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.keepfit.triggers.listener.PermissionListener;
import com.keepfit.triggers.service.GeofenceTransitionsIntentService;
import com.keepfit.triggers.utils.enums.TriggerPreference;
import com.keepfit.triggers.utils.enums.TriggerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 4/8/2016.
 */
public class LocationThread extends TriggerThread<Object> implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = "LocationThread";
    private static final String TITLE = "Location";
    private static final int GEOFENCE_RADIUS_IN_METERS = 1000;
    private static final int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 600000;
    private static final int GEOFENCE_LOITERING_DELAY = 10000;
    private static final String HOME_KEY = "home";
    private static final String WORK_KEY = "work";
    private static final String CUSTOM_KEY = "custom";

    private PermissionListener listener;
    private SharedPreferences prefs;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private List<Geofence> geoFences;
    private PendingIntent geofencePendingIntent;
    private boolean accessGranted, locationReceived;

    public LocationThread(Context context, PermissionListener listener) {
        super(TITLE, TriggerType.LOCATION, false, context);
        this.listener = listener;
        geoFences = new ArrayList<>();
    }

    @Override
    public void doRunAction() {
        if (accessGranted && !locationReceived) {


            locationReceived = true;
        }
    }

    @Override
    public void doStartAction() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        createGoogleApiClient();
        googleApiClient.connect();
    }

    @Override
    public void doStopAction() {
        List<String> requestIds = new ArrayList<>();
        for (Geofence geofence : geoFences)
            requestIds.add(geofence.getRequestId());

        LocationServices.GeofencingApi.removeGeofences(googleApiClient, requestIds)
            .setResultCallback(this);

        geoFences = new ArrayList<>();
        googleApiClient.disconnect();
    }

    @Override
    public void onResult(Status status) {
        Log.d(TAG, "GEOFENCES WORKED!!! " + status);
    }

    private void createGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.notifyPermissionRequested();
            return;
        } else {
            accessGranted = true;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        GeofencingRequest request = createGeoFences();
        geofencePendingIntent = getGeofencePendingIntent();

        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                geofencePendingIntent
        ).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "Connection suspended... " + i);
    }

    private GeofencingRequest createGeoFences() {
        String homeLat = prefs.getString(TriggerPreference.HOME_LATITUDE.title, "");
        String homeLong = prefs.getString(TriggerPreference.HOME_LATITUDE.title, "");
        String workLat = prefs.getString(TriggerPreference.WORK_LATITUDE.title, "");
        String workLong = prefs.getString(TriggerPreference.WORK_LONGITUDE.title, "");
        String customLat = prefs.getString(TriggerPreference.CUSTOM_LATITUDE.title, "");
        String customLong = prefs.getString(TriggerPreference.CUSTOM_LONGITUDE.title, "");

        geoFences.add(createGeoFence(homeLat, homeLong, HOME_KEY));
        geoFences.add(createGeoFence(workLat, workLong, WORK_KEY));
        geoFences.add(createGeoFence(customLat, customLong, CUSTOM_KEY));

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geoFences);
        return builder.build();
    }

    private Geofence createGeoFence(String latitude, String longitude, String key) {
        double lat, lon;

        try {
            lat = Double.parseDouble(latitude);
            lon = Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            Log.w(TAG, String.format("The latitude[%s] or longitude[%s] was invalid.", latitude, longitude), e);
            return null;
        }

        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(
                        lat,
                        lon,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setLoiteringDelay(GEOFENCE_LOITERING_DELAY)
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .build();
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }

    public void locationPermissionGranted() {
        accessGranted = true;
    }

    /**
     * Source: http://developer.android.com/training/location/geofencing.html
     *
     * @return
     */
    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null)
            return geofencePendingIntent;

        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public Object getTriggerObject() {
        return lastLocation;
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getMessage() {
        return String.format("You reached the goal for location!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

package com.keepfit.triggers.location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.barcode.Barcode;
import com.keepfit.triggers.listener.PermissionRequestListener;
import com.keepfit.triggers.listener.PermissionResponseListener;
import com.keepfit.triggers.service.GeofenceTransitionsIntentService;
import com.keepfit.triggers.utils.enums.TriggerPreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 4/12/2016.
 */
public class LocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener, ResultCallback<Status> {
    private static final String TAG = "LocationService";
    private static final int MAX_ADDRESSES = 5;
    private static final int VERSION_CODE = 1;
    private static final int GEOFENCE_RADIUS_IN_METERS = 1000;
    private static final int GEOFENCE_EXPIRATION_IN_MILLISECONDS = 600000;
    private static final int GEOFENCE_LOITERING_DELAY = 10000;
    private static final String HOME_KEY = "home";
    private static final String WORK_KEY = "work";
    private static final String CUSTOM_KEY = "custom";

    private Context context;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private Geocoder coder;
    private List<Address> address;
    private List<Geofence> geoFences;
    private PermissionRequestListener listener;
    private SharedPreferences prefs;
    private PendingIntent geofencePendingIntent;

    public LocationService(Context context) {
        this.context = context;
        coder = new Geocoder(context);
        geoFences = new ArrayList<>();
    }

    public LocationService(Context context, PermissionRequestListener listener) {
        this(context);
        this.listener = listener;
    }

    public void connect() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        createGoogleApiClient();
        googleApiClient.connect();
    }

    public void disconnect() {
        List<String> requestIds = new ArrayList<>();
        for (Geofence geofence : geoFences) {
            if (geofence != null)
                requestIds.add(geofence.getRequestId());
        }
        if (!requestIds.isEmpty())
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, requestIds)
                    .setResultCallback(this);

        geoFences = new ArrayList<>();
        googleApiClient.disconnect();
    }

    public Barcode.GeoPoint getLocationFromAddress(String streetAddress) {
        try {
            address = coder.getFromLocationName(streetAddress, MAX_ADDRESSES);
        } catch (IOException e) {
            Log.e(TAG, "Error getting address for " + streetAddress + ".", e);
        }
        if (address == null)
            return null;
        Address location = address.get(0);

        return new Barcode.GeoPoint(VERSION_CODE, location.getLatitude(), location.getLongitude());
    }

    public void requestLocation(PermissionResponseListener permissionResponseListener) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        permissionResponseListener.setLocation(lastLocation);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.notifyPermissionRequested(permissionResponseListener);
            return;
        } else {
            permissionResponseListener.notifyPermissionGranted();
        }
    }

    @Override
    public void onResult(Status status) {
        Log.d(TAG, "GEOFENCES WORKED!!! " + status);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        handleConnection();
    }

    private boolean connectionPermissionGranted;

    private void handleConnection() {
        if (!connectionPermissionGranted || ActivityCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission
                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.notifyPermissionRequested(new PermissionResponseListener() {
                @Override
                public void permissionGranted(Location location) {
                    connectionPermissionGranted = true;
                    handleConnection();
                }

                @Override
                public void permissionDenied() {
                    connectionPermissionGranted = false;
                }
            });
            return;
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

    private void createGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void setPermissionRequestListener(PermissionRequestListener permissionRequestListener) {
        listener = permissionRequestListener;
    }

    public Location getLocation() {
        if (connectionPermissionGranted)
            return lastLocation;
        else {
            Log.w(TAG, "Permission for locations has not been granted.");
            return null;
        }
    }

}

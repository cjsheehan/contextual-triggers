package com.keepfit.triggers.thread;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.keepfit.triggers.TriggerService;
import com.keepfit.triggers.listener.PermissionListener;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.enums.TriggerType;

/**
 * Created by Edward on 4/8/2016.
 */
public class LocationThread extends TriggerThread<Object> implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LocationThread";
    private static final String TITLE = "Location";

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private boolean accessGranted, locationReceived;
    private PermissionListener listener;

    public LocationThread(Context context, PermissionListener listener) {
        super(TITLE, TriggerType.LOCATION, false, context);
        this.listener = listener;
    }

    @Override
    public void doRunAction() {
        if (accessGranted && !locationReceived) {
            locationReceived = true;
        }
    }

    @Override
    public void doStartAction() {
        createGoogleApiClient();
        createGeoFences();
        googleApiClient.connect();
    }

    @Override
    public void doStopAction() {
        googleApiClient.disconnect();
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

    private void createGeoFences() {
        
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
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "Connection suspended... " + i);
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

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
import com.keepfit.triggers.listener.PermissionRequestListener;
import com.keepfit.triggers.listener.PermissionResponseListener;
import com.keepfit.triggers.location.LocationService;
import com.keepfit.triggers.service.GeofenceTransitionsIntentService;
import com.keepfit.triggers.utils.enums.TriggerPreference;
import com.keepfit.triggers.utils.enums.TriggerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 4/8/2016.
 */
public class LocationThread extends TriggerThread<Object> {
    private static final String TAG = "LocationThread";
    private static final String TITLE = "Location";
    private static final int TIMEOUT = 1000;

    private LocationService locationService;
    private boolean accessGranted, locationReceived;

    public LocationThread(Context context, PermissionRequestListener listener) {
        super(TITLE, TriggerType.LOCATION, false, TIMEOUT, context);
        locationService = new LocationService(context, listener);
    }

    @Override
    public void doRunAction() {
        if (accessGranted && !locationReceived) {
            locationReceived = true;
        }
    }

    @Override
    public void doStartAction() {
        locationService.connect();
    }

    @Override
    public void doStopAction() {
        locationService.disconnect();
    }

    @Override
    public Object getTriggerObject() {
        return locationService.getLocation();
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getMessage() {
        return String.format("You reached the goal for location!");
    }

}

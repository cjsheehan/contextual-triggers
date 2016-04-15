package com.keepfit.triggers.thread;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.barcode.Barcode;
import com.keepfit.triggers.listener.PermissionRequestListener;
import com.keepfit.triggers.listener.PermissionResponseListener;
import com.keepfit.triggers.service.LocationService;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.TriggerType;

/**
 * Created by Edward on 4/8/2016.
 */
public class LocationThread extends TriggerThread<Object> {
    private static final String TAG = "LocationThread";
    private static final String TITLE = "Location";

    private LocationService locationService;
    private Location lastLocation;
    private boolean waitForSetup = false, waitForLocation = false;

    public LocationThread(Context context, PermissionRequestListener listener) {
        super(TITLE, TriggerType.LOCATION, false, context);
        locationService = new LocationService(context, listener);
    }

    @Override
    public void doTriggerRunAction() {
        if (waitForLocation) {
            locationService.requestLocation(new PermissionResponseListener() {
                @Override
                protected void permissionGranted(Location location) {
                    if (location == null) return;
                    lastLocation = location;
                    Broadcast.broadcastLocationReceived(context, location);
                    TriggerCache.put(TriggerType.LOCATION, locationService.getLocation());
                    waitForLocation = false;
                }

                @Override
                protected void permissionDenied() {

                }
            });
        }
        if (waitForSetup) {
            locationService.setupLocation();
        }
    }

    @Override
    public void doStartAction() {
        locationService.connect(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                waitForLocation = true;
            }

            @Override
            public void onConnectionSuspended(int i) {
                Toast.makeText(context, "No locations have been setup. Please go into Settings > Locations and " +
                        "enable and define at least one location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Barcode.GeoPoint getLocationFromAddress(String address) {
        if (!isRunning()) {
            Log.w(TAG, "LocationTrigger is not running.");
            return null;
        }
        return locationService.getLocationFromAddress(address);
    }

    public void requestLocation(PermissionResponseListener listener) {
        locationService.requestLocation(listener);
    }

    public void updateLocation() {
        waitForLocation = true;
    }

    public boolean hasLocation() {
        return !waitForLocation;
    }

    @Override
    public void doStopAction() {
        locationService.disconnect();
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getTextToDisplayOnUI() {
        return lastLocation == null ? "No location" : String.format("Lat[%s] - Long[%s]", lastLocation.getLatitude(),
                lastLocation.getLongitude());
    }

}

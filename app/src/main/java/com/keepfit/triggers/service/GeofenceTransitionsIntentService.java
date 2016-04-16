package com.keepfit.triggers.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.TriggerType;

import java.util.List;

/**
 * Created by Edward on 4/12/2016.
 */
public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = "GeofenceTransitions";

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Creating Service");
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        // Test that the reported transition was of interest.
        Broadcast.broadcastGeofenceEvents(this, triggeringGeofences.get(0).getRequestId());
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

        } else {
            // Log the error.
            Log.e(TAG, "GEOFENCE ERROR...");
        }
    }

    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown geofence error";
        }
    }
}

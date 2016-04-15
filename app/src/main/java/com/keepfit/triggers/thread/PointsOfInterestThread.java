package com.keepfit.triggers.thread;

import android.content.Context;
import android.location.Location;

import com.android.volley.VolleyError;
import com.keepfit.triggers.listener.PermissionRequestListener;
import com.keepfit.triggers.listener.PermissionResponseListener;
import com.keepfit.triggers.service.LocationService;
import com.keepfit.triggers.service.PointsOfInterestService;
import com.keepfit.triggers.interests.Results;
import com.keepfit.triggers.listener.ResponseListener;
import com.keepfit.triggers.utils.Broadcast;
import com.keepfit.triggers.utils.TriggerCache;
import com.keepfit.triggers.utils.enums.TriggerType;

/**
 * Created by Chris on 12/04/2016.
 */
public class PointsOfInterestThread extends TriggerThread<Results> implements ResponseListener<Results> {
    private static final String TAG = "PointsOfInterestThread";
    private static final String TITLE = "POI Service";
    private static final int STANDARD_TIMEOUT = 1000 * 3600 * 2;  // 2 hours
    private static int WAIT_TIMEOUT = 1000;
    private static int TIMEOUT;

    private Context context;
    private PointsOfInterestService poiService;
    private Results poiEvent;
    private boolean waitForLocation = false;

    public PointsOfInterestThread(Context context, PermissionRequestListener listener) {
        super(TITLE, TriggerType.POI, false, context);
        this.context = context;
        TIMEOUT = STANDARD_TIMEOUT;
    }

    @Override
    public void doRunAction() {
        if (waitForLocation) {
            Location location = TriggerCache.get(TriggerType.LOCATION, Location.class);
            if (location != null) {
                requestPointsOfInterest(location);
                TIMEOUT = WAIT_TIMEOUT;
                waitForLocation = false;
            }
        }
    }

    @Override
    public void doStartAction() {
        poiService = new PointsOfInterestService(context);
        poiService.registerResponseListener(this);
        Location location = TriggerCache.get(TriggerType.LOCATION, Location.class);
        if (location == null) {
            waitForLocation = true;
            TIMEOUT = WAIT_TIMEOUT;
            return;
        }
        requestPointsOfInterest(location);
    }

    @Override
    public void doStopAction() {
    }

    public void requestPointsOfInterest(Location location) {
        poiService.requestPointsOfInterest(location.getLatitude(), location.getLatitude());
    }

    @Override
    public Results getTriggerObject() {
        return poiEvent;
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getMessage() {
        return String.format("You reached the goal for poi!");
    }

    @Override
    public void onResponse(Results response) {
        if(response != null) {
            poiEvent = response;
            TriggerCache.put(TriggerType.POI, poiEvent);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    protected int getTimeout() {
        return TIMEOUT;
    }
}
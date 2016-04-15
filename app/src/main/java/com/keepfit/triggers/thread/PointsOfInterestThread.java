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

    private Context context;
    private PointsOfInterestService poiService;
    private Results poiEvent;
    private boolean waitForLocation = false;

    public PointsOfInterestThread(Context context, PermissionRequestListener listener) {
        super(TITLE, TriggerType.POI, false, context);
        this.context = context;
    }

    @Override
    public void doTriggerRunAction() {
        if (waitForLocation) {
            Location location = TriggerCache.get(TriggerType.LOCATION, Location.class);
            if (location != null) {
                requestPointsOfInterest(location);
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
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected String getTextToDisplayOnUI() {
        if (poiEvent == null || poiEvent.getItems() == null || poiEvent.getItems().length == 0)
            return "No events";
        return String.format("%s", poiEvent.getItems()[0].getTitle());
    }

    @Override
    public void onResponse(Results response) {
        if(response != null) {
            poiEvent = response;
            TriggerCache.put(TriggerType.POI, response);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

}
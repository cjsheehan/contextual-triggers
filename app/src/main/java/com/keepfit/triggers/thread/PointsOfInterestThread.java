package com.keepfit.triggers.thread;

import android.content.Context;

import com.android.volley.VolleyError;
import com.keepfit.triggers.interests.PointsOfInterestService;
import com.keepfit.triggers.interests.Results;
import com.keepfit.triggers.listener.ResponseListener;
import com.keepfit.triggers.utils.Broadcast;
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
    private double lat, lon;
    private boolean sent = false;

    public PointsOfInterestThread(Context context) {
        super(TITLE, TriggerType.POI, false, 1000, context);
        this.context = context;
    }

    @Override
    public void doRunAction() {
    }

    @Override
    public void doStartAction() {
        poiService = new PointsOfInterestService(context);
        poiService.registerResponseListener(this);
        this.lat = 55.744197;
        this.lon = -4.190944;
        requestPointsOfInterest(this.lat, this.lon);
    }

    @Override
    public void doStopAction() {

    }

    public void requestPointsOfInterest(double latitude, double longitude) {
        poiService.requestPointsOfInterest(latitude, longitude);
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
            Broadcast.broadcastPointsOfInterestEvents(context, poiEvent);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}
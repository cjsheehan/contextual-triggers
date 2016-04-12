package com.keepfit.triggers.interests;

/**
 * Created by Chris on 12/04/2016.
 */
public class PointsOfInterestResponse {
    Results results;

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public PointsOfInterestResponse(Results results) {

        this.results = results;
    }
}
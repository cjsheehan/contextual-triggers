package com.keepfit.triggers.interests;

import java.io.Serializable;

/**
 * Created by Chris on 12/04/2016.
 */
public class Results implements Serializable {
    Item[] items;
    double sourceLatitude;
    double sourceLongitude;

    public double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public double getSourceLongitude() {
        return sourceLongitude;
    }

    public void setSourceLongitude(double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    public Results(Item[] items) {
        this.items = items;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }
}
package com.keepfit.triggers.listener;

import android.location.Location;

/**
 * Created by Edward on 4/12/2016.
 */
public abstract class PermissionResponseListener {

    Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public void notifyPermissionGranted() {
        permissionGranted(location);
    }

    public void notifyPermissionDenied() {
        permissionDenied();
    }

    protected abstract void permissionGranted(Location location);

    protected abstract void permissionDenied();

}

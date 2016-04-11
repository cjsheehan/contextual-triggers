package com.keepfit.triggers.listener;

import com.android.volley.VolleyError;

/**
 * Created by Edward on 4/11/2016.
 */
public interface ResponseListener<T> {

    void onResponse(T response);

    void onErrorResponse(VolleyError error);
}
package com.keepfit.triggers.interests;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.keepfit.triggers.listener.ResponseListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 12/04/2016.
 */
public class PointsOfInterestService {
    private static final String TAG = "PointsOfInterestService";
    // EXAMPLE_URL = https://places.cit.api.here.com/places/v1/discover/explore?at=40.74917,-73.98529&app_id=9Dgt6MxRPvpgfwpUwmKX&app_code=xIM0Lg2YP8qKNjWJF9bHJw

    private static final String URL = "https://places.cit.api.here.com/places/v1/discover/explore?at=";
    private static final String KEY = "&app_id=9Dgt6MxRPvpgfwpUwmKX&app_code=xIM0Lg2YP8qKNjWJF9bHJw";


    private Context context;
    private RequestQueue queue = null;
    int maxReq = 10;
    int numReq = 0;
    private List<ResponseListener<Results>> listeners;

    public PointsOfInterestService(Context context) {
        this.context = context;
        listeners = new ArrayList<>();
    }

    public void requestPointsOfInterest(final double lat, final double lon) {
        String url = formatRequest(lat, lon);
        Log.d(TAG, "WeatherService request url=" + url);

        // Instantiate the RequestQueue.
        if (queue == null) {
            try {
                queue = Volley.newRequestQueue(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (numReq <= maxReq) {
            // Only request
            StringRequest stringRequest = null;
            try {
                stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                PointsOfInterestResponse results = gson.fromJson(response, PointsOfInterestResponse.class);
                                Results poiResults = results.getResults();
                                poiResults.setSourceLatitude(lat);
                                poiResults.setSourceLongitude(lon);
                                for (ResponseListener listener : listeners)
                                    listener.onResponse(poiResults);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley request failed");
                        for (ResponseListener listener : listeners)
                            listener.onErrorResponse(error);
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                numReq++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("WARNING: max number of pointsOfInterest API request reached :" + numReq);
            Log.w(TAG, "WARNING: max number of pointsOfInterest API request reached :" + numReq);
        }
    }

    public void registerResponseListener(ResponseListener<Results> listener) {
        listeners.add(listener);
    }

    private String formatRequest(double lat, double lon) {
        return URL + lat + "," + lon + KEY;
    }
}
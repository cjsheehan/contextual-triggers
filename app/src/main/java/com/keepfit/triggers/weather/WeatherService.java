package com.keepfit.triggers.weather;

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
 * Created by Chris on 10/04/2016.
 */
public class WeatherService {
    private static final String TAG = "WeatherService";
    private static final String FORECAST_IO_URL = "https://api.forecast.io/forecast/";
    private static final String FORECAST_IO_KEY = "a991d862d6211132d233ebc988081823";

    private Context context;
    private RequestQueue queue = null;
    int maxReq = 10;
    int numReq = 0;
    private List<ResponseListener<WeatherEvent>> listeners;

    public WeatherService(Context context) {
        this.context = context;
        listeners = new ArrayList<>();
    }

    public void requestWeather(double lat, double lon) {
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
                                WeatherEvent event = gson.fromJson(response, WeatherEvent.class);
                                for (ResponseListener listener : listeners)
                                    listener.onResponse(event);
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
            System.out.println("WARNING: max number of weather API request reached :" + numReq);
            Log.w(TAG, "WARNING: max number of weather API request reached :" + numReq);
        }
    }

    public void registerResponseListener(ResponseListener<WeatherEvent> listener) {
        listeners.add(listener);
    }

    private String formatRequest(double lat, double lon) {
        return FORECAST_IO_URL + FORECAST_IO_KEY + "/" + lat + "," + lon;
    }

}

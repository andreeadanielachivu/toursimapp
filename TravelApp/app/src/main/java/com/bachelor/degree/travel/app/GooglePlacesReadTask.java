package com.bachelor.degree.travel.app;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Created by Andreea on 8/14/2016.
 */
public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {

    String googlePlacesData = null;
    //TextView displayNearbyPlaces;
    TableLayout displayNearbyPlaces;
    Context context;
    String request_type;
    String googlePlacesUrl;

    @Override
    protected String doInBackground(Object... params) {
        try {
            displayNearbyPlaces = (TableLayout) params[0];
            googlePlacesUrl = (String) params[1];
            context = (Context) params[2];
            if (params.length == 4) {
                request_type = (String) params[3];
            }
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("Google Place Read Task", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        Object[] toPass;
        if (null != request_type && !request_type.isEmpty()) {
            toPass = new Object[5];
            toPass[4] = request_type;
        } else {
            toPass = new Object[4];
        }
        toPass[0] = displayNearbyPlaces;
        toPass[1] = result;
        toPass[2] = context;
        toPass[3] = googlePlacesUrl;
        placesDisplayTask.execute(toPass);
    }
}

package com.bachelor.degree.travel.app;

import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreea on 8/18/2016.
 */
public class GetNearTransportTask extends AsyncTask<Object, Void, List<String>> {
    TextView nearFlight = null;
    List<String> airports = new ArrayList<String>();
    @Override
    protected List<String> doInBackground(Object... params) {
        String url = (String)params[0];
        nearFlight = (TextView)params[1];
        Http http = new Http();
        String data = "";
        try {
            data = http.read(url);
            JSONObject jsonObject = new JSONObject(data);
            JSONArray result = jsonObject.getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                JSONObject jsonAirport = result.getJSONObject(i);
                String data_airport = "";
                if (jsonAirport.has("name")) {
                    data_airport += "Name: " + jsonAirport.getString("name") + "\n";
                }
                if (jsonAirport.has("formatted_address")) {
                    data_airport += "Address: " + jsonAirport.getString("formatted_address");
                }
                airports.add(data_airport);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return airports;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        String data = "";
        for (int i = 0; i < strings.size(); i++) {
            data += (i + 1) + "." + strings.get(i) + "\n";
        }
        nearFlight.setText(data);
    }
}

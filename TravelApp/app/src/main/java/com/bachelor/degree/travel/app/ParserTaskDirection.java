package com.bachelor.degree.travel.app;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 8/17/2016.
 */
public class ParserTaskDirection extends AsyncTask<Object, Integer, List<List<HashMap<String,String>>>> {
    GoogleMap map = null;
    TableLayout table = null;
    String url = "";
    String duration = "";
    String mode_travel = "";
    String distance = "";
    String summary = "";
    StringBuilder details = new StringBuilder();
    String available_modes = "";
    LatLng origin = null;
    LatLng dest = null;
    Context context = null;
    @Override
    protected List<List<HashMap<String,String>>> doInBackground(Object... params) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject((String) params[0]);
                table = (TableLayout)params[1];
                map = (GoogleMap)params[2];
                url = (String)params[3];
                origin = (LatLng)params[4];
                dest = (LatLng) params[5];
                mode_travel = (String) params[6];
                context = (Context)params[7];
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
                duration = parser.getDuration();
                distance = parser.getDistance();
                summary = parser.getSummary();
                details = parser.getDetailsRoute();
                available_modes = parser.getAvailable_travel_mode();
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        setTableData();
        if (result == null || result.size() == 0) {
            summary = available_modes;
            return;
        }
        // Traversing through all the routes
        for(int i=0;i<result.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(7);
            if (mode_travel.compareToIgnoreCase("driving") == 0) {
                lineOptions.color(Color.BLUE);

            } else if (mode_travel.compareToIgnoreCase("walking") == 0) {
                lineOptions.color(Color.GREEN);
            } else if (mode_travel.compareToIgnoreCase("transit") == 0) {
                lineOptions.color(Color.RED);
            }

        }

        // Drawing polyline in the Google Map for the i-th route
        map.addPolyline(lineOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin);
        builder.include(dest);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 13));

    }
    public void setTableData() {
        TableRow tbrow = new TableRow(context);
        tbrow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView t1v = new TextView(context);
        t1v.setText(mode_travel);
        tbrow.addView(t1v);

        TextView t2v = new TextView(context);
        if (mode_travel.compareToIgnoreCase("transit") == 0) {
            if (details != null) {
                summary = details.toString();
            }
        }
        t2v.setText(summary);
        t2v.setMaxWidth(450);
        tbrow.addView(t2v);

        TextView t3v = new TextView(context);
        t3v.setText(duration);
        tbrow.addView(t3v);

        TextView t4v = new TextView(context);
        t4v.setText(distance);
        tbrow.addView(t4v);

        tbrow.setBackgroundColor(Color.LTGRAY);
        t1v.setTextColor(Color.BLACK);
        t2v.setTextColor(Color.BLACK);
        t3v.setTextColor(Color.BLACK);
        t4v.setTextColor(Color.BLACK);
        table.addView(tbrow);
    }
}

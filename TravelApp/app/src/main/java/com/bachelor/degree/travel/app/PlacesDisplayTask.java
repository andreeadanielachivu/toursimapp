package com.bachelor.degree.travel.app;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.bachelor.degree.travel.app.R.color.grayDark;

/**
 * Created by Andreea on 8/14/2016.
 */
public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    TableLayout displayNearbyPlaces;
    Context context;
    String request_type;
    String url;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        PlacesParser placeJsonParser = new PlacesParser();

        try {
            displayNearbyPlaces = (TableLayout) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            context = (Context)inputObj[2];
            url = (String)inputObj[3];
            if (inputObj.length == 5) {
                request_type = (String) inputObj[4];
            }
            googlePlacesList = placeJsonParser.parse(googlePlacesJson, url);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        if (null != request_type && !request_type.isEmpty() && request_type.compareToIgnoreCase("textsearchCurrentLocation") == 0) {
            setGeneralInfo(list);
        } else {
            setDetailedInfo(list);
        }
        //displayNearbyPlaces.setText(sb);
    }
    public void setGeneralInfo(List<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> googlePlace = list.get(i);
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("address");
            String open = googlePlace.get("open");
            TableRow tbrow = new TableRow(context);
            tbrow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            TextView t1v = new TextView(context);
            t1v.setText(placeName);
            t1v.setMaxWidth(310);
            t1v.setPadding(2,2,3,2);
            tbrow.addView(t1v);

            TextView t2v = new TextView(context);
            t2v.setText(vicinity);
            t2v.setMaxWidth(480);
            t2v.setPadding(2,2,3,2);
            tbrow.addView(t2v);

            TextView t3v = new TextView(context);
            t3v.setText(open);
            t3v.setPadding(2,2,3,2);
            tbrow.addView(t3v);
            if (i % 2 == 0) {
                tbrow.setBackgroundColor(Color.DKGRAY);
                t1v.setTextColor(Color.WHITE);
                t2v.setTextColor(Color.WHITE);
                t3v.setTextColor(Color.WHITE);
            } else {
                tbrow.setBackgroundColor(Color.LTGRAY);
                t1v.setTextColor(Color.BLACK);
                t2v.setTextColor(Color.BLACK);
                t3v.setTextColor(Color.BLACK);
            }
            displayNearbyPlaces.addView(tbrow);
        }
    }

    public void setDetailedInfo(List<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> googlePlace = list.get(i);
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            String open = googlePlace.get("open");
            TableRow tbrow = new TableRow(context);
            tbrow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            TextView t1v = new TextView(context);
            t1v.setText(placeName);
            t1v.setMaxWidth(310);
            t1v.setPadding(2,2,3,2);
            tbrow.addView(t1v);

            TextView t2v = new TextView(context);
            t2v.setText(vicinity);
            t2v.setMaxWidth(480);
            t2v.setPadding(2,2,3,2);
            tbrow.addView(t2v);

            TextView t3v = new TextView(context);
            t3v.setText(open);
            t3v.setPadding(2,2,3,2);
            //t3v.setMaxWidth(12);
            tbrow.addView(t3v);
            if (i % 2 == 0) {
                tbrow.setBackgroundColor(Color.DKGRAY);
                t1v.setTextColor(Color.WHITE);
                t2v.setTextColor(Color.WHITE);
                t3v.setTextColor(Color.WHITE);
            } else {
                tbrow.setBackgroundColor(Color.LTGRAY);
                t1v.setTextColor(Color.BLACK);
                t2v.setTextColor(Color.BLACK);
                t3v.setTextColor(Color.BLACK);
            }
            displayNearbyPlaces.addView(tbrow);
        }
    }
}

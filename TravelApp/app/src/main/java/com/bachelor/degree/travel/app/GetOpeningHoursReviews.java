package com.bachelor.degree.travel.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Andreea on 8/22/2016.
 */
public class GetOpeningHoursReviews extends AsyncTask<Object, Void, JSONObject> {
    private TableLayout tableDays, tableReviews;
    private Context context;
    private TextView textView;
    private PlaceDetailsActivity activity;
    @Override
    protected JSONObject doInBackground(Object... params) {
        tableDays = (TableLayout)params[1];
        tableReviews = (TableLayout)params[2];
        String url = (String) params[0];
        context = (Context)params[3];
        textView = (TextView)params[4];
        activity = (PlaceDetailsActivity)params[5];
        JSONObject jsonObject= null;
        Http http = new Http();
        String data="";
        try {
            data = http.read(url);
            jsonObject = new JSONObject(data);
            String status = jsonObject.getString("status");
            while (status.equals("OVER_QUERY_LIMIT")) {
                Thread.sleep(2000);
                Log.d("BUG", "Retry geocode");
                data = http.read(url);
                jsonObject = new JSONObject(data);
                status = jsonObject.getString("status");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        PrintPlaceDetailsTask parseJsonPlaceDetails = new PrintPlaceDetailsTask();
        Object object[] = new Object[6];
        object[0] = tableDays;
        object[1] = tableReviews;
        object[2] = jsonObject;
        object[3] = context;
        object[4] = textView;
        object[5] = activity;
        parseJsonPlaceDetails.execute(object);
    }
}

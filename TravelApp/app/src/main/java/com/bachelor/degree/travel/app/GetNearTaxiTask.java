package com.bachelor.degree.travel.app;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreea on 8/21/2016.
 */
public class GetNearTaxiTask extends AsyncTask<Object, Void, List<String>> {
    TableLayout tableTaxi;
    List<String> placeIdList;
    Context context;
    @Override
    protected List<String> doInBackground(Object... params) {

        String url = (String)params[0];
        tableTaxi = (TableLayout) params[1];
        context = (Context) params[2];
        Http http = new Http();
        String data = "";
        placeIdList = new ArrayList<String>();
        try {
            data = http.read(url);
            JSONObject jsonObject = new JSONObject(data);
            JSONArray result = jsonObject.getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                JSONObject jsonTaxi = result.getJSONObject(i);
                if (jsonTaxi.has("place_id")) {
                    placeIdList.add(jsonTaxi.getString("place_id"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placeIdList;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        List<String> list_url = new ArrayList<String>();
        for (int i = 0; i < strings.size(); i++) {
            String url = Constants.DETAILS_DATA + "placeid=" + strings.get(i) + "&"
                    + "key=" + Constants.SERVER_KEY;
            list_url.add(url);
        }
        Object []obj = new Object[3];
        obj[0] = list_url;
        obj[1] = tableTaxi;
        obj[2] = context;
        TaxiDetailTask task = new TaxiDetailTask();
        task.execute(obj);
    }
}

package com.bachelor.degree.travel.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Andreea on 8/21/2016.
 */
public class GetNearRestaurants extends AsyncTask<Object, Void, JSONObject> {
    TableLayout tableLayout;
    Context context;
    @Override
    protected JSONObject doInBackground(Object... params) {
        String url = (String)params[0];
        tableLayout = (TableLayout)params[1];
        context = (Context)params[2];
        Http http = new Http();
        String data = "";
        JSONObject jsonObject = null;
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
        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        PrintRestaurantTask task = new PrintRestaurantTask();
        Object []obj = new Object[3];
        obj[0] = jsonObject;
        obj[1] = tableLayout;
        obj[2] = context;
        task.execute(obj);
    }
}

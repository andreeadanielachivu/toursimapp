package com.bachelor.degree.travel.app;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Andreea on 9/6/2016.
 */
public class HistoryList extends AsyncTask<Object, Void, String> {
    private Context context;
    private TableLayout tableLayout;
    private AppOfflineActivity activity;

    @Override
    protected String doInBackground(Object... params) {
        Map<String, String> map = (Map<String,String>)params[0];
        tableLayout = (TableLayout)params[1];
        context = (Context)params[2];
        activity = (AppOfflineActivity)params[3];
        URL url;
        String response = "";
        try {
            url = new URL(Constants.URL_HISTORY);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", map.get("username"));
            jsonObject.put("email", map.get("email"));
            jsonObject.put("request", "getCategory");
            jsonObject.put("category", map.get("category"));
            String message = jsonObject.toString();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.connect();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(message);

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    @Override
    protected void onPostExecute(String strings) {
        PrintHistoryTask task = new PrintHistoryTask();
        Object obj[] = new Object[4];
        obj[0] = tableLayout;
        obj[1] = context;
        obj[2] = strings;
        obj[3] = activity;
        task.execute(obj);
    }
}

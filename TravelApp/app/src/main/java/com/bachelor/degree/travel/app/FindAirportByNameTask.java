package com.bachelor.degree.travel.app;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Andreea on 9/7/2016.
 */
public class FindAirportByNameTask extends AsyncTask<Object, Void, String> {
    private TextView textView;
    private ListFlightActivity activity;
    @Override
    protected String doInBackground(Object... params) {
        String iataCode = (String)params[0];
        activity = (ListFlightActivity) params[1];
        URL url;
        String response = "";
        Log.d("Here", "eneter find");
        try {
            String urlAirports = Constants.URL_AIRPORTS;
            url = new URL(urlAirports);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("request", "request_iata_code");
            jsonObject.put("iataCode", iataCode.toUpperCase());
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
        Log.d("Here", "response " + response);
        ParserJsonData parserJsonData = new ParserJsonData();
        String name = parserJsonData.getAirportByCode(response);
        Log.d("Here", "name " + name);
        return name;
    }

    @Override
    protected void onPostExecute(String s) {
        //textView.setText(s);
        activity.setTextAirport(s);
    }
}

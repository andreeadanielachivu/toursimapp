package com.bachelor.degree.travel.app;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

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
 * Created by Andreea on 8/18/2016.
 */
public class HttpRequestFlights extends AsyncTask<Object, Void, String> {
    ArrayAdapter<String> adapter = null;
    AutoCompleteTextView fromFlight = null;
    AutoCompleteTextView toFlight;
    String request;
    @Override
    protected String doInBackground(Object... params) {
        URL url;
        String response = "";
        try {
            String urlAirports = (String)params[3];
            adapter = (ArrayAdapter<String>)params[0];
            fromFlight = (AutoCompleteTextView) params[1];
            toFlight = (AutoCompleteTextView) params[2];
            request = (String)params[4];

            url = new URL(urlAirports);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("request", "airports");
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
    protected void onPostExecute(String s) {
        Object []obj = new Object[5];
        obj[0] = adapter;
        obj[1] = fromFlight;
        obj[2] = toFlight;
        obj[3] = s;
        obj[4] = request;
        ParseResponseFromServer parser = new ParseResponseFromServer();
        parser.execute(obj);
    }
}

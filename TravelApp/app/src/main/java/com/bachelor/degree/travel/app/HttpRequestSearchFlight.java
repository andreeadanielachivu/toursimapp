package com.bachelor.degree.travel.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Andreea on 8/19/2016.
 */
public class HttpRequestSearchFlight extends AsyncTask<Object, Void, String> {
    TableLayout tableLayout;
    Context context;
    ListFlightActivity activity;

    @Override
    protected String doInBackground(Object... params) {
        String url = (String) params[0];
        String codeFrom = ((String)params[1]).trim();
        String codeTo = ((String)params[2]).trim();
        String dataFrom = ((String)params[3]).trim();
        String dataTo = ((String)params[4]).trim();
        tableLayout = (TableLayout)params[5];
        context = (Context)params[6];
        activity = (ListFlightActivity)params[7];
        JSONObject jdata = new JSONObject();
        try {
            jdata.put("from", codeFrom);
            jdata.put("to", codeTo);
            jdata.put("dateTur", dataFrom);
            jdata.put("dateRetur", dataTo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String response = getResponse(url, jdata);
        return response;
    }

    public String getResponse(String url_link, JSONObject data) {
        String response = "";
        URL url = null;
        try {
            url = new URL(url_link);
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
            String message = data.toString();
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Object []obj= new Object[4];
        obj[0] = tableLayout;
        obj[1] = s;
        obj[2] = context;
        obj[3] = activity;
        PrintFlightsTask parser = new PrintFlightsTask();
        parser.execute(obj);
    }
}

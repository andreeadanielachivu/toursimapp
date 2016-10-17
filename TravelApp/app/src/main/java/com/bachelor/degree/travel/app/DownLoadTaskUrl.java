package com.bachelor.degree.travel.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TableLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Andreea on 8/17/2016.
 */
public class DownLoadTaskUrl extends AsyncTask<Object, Void, String> {
    public static String urlRequest;
    TableLayout tb;
    GoogleMap map;
    LatLng source;
    LatLng dest;
    String mode;
    Context context;
    @Override
    protected String doInBackground(Object... params) {
        String data = "";
        try{
            // Fetching the data from web service
            urlRequest = (String)params[0];
            tb = (TableLayout)params[1];
            map = (GoogleMap)params[2];
            source = (LatLng)params[3];
            dest = (LatLng)params[4];
            mode = (String) params[5];
            context = (Context)params[6];
            data = downloadUrl(urlRequest);
        }catch(Exception e){
            Log.d("Background Task",e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        ParserTaskDirection parserTask = new ParserTaskDirection();
        Object[] toPass = new Object[8];
        toPass[0] = s;
        toPass[1] = tb;
        toPass[2] = map;
        toPass[3] = urlRequest;
        toPass[4] = source;
        toPass[5] = dest;
        toPass[6] = mode;
        toPass[7] = context;
        // Invokes the thread for parsing the JSON data
        parserTask.execute(toPass);
    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception donwload url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}

package com.bachelor.degree.travel.app;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Andreea on 7/19/2016.
 */
public class DetailsJsonParser {

    public JSONObject getJSONfromUrl(String strUrl) {
        // initialize
        InputStream is = null;
        HttpURLConnection urlConnection = null;
        String result = "";
        JSONObject jObject = null;

        // http post
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            result = sb.toString();
            jObject = new JSONObject(result);
            br.close();
            is.close();
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        } catch (IOException e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        } finally{
            urlConnection.disconnect();
        }
        return jObject;
    }

    public Document getXMLfromUrl(String strUrl) {
        // initialize
        InputStream is = null;
        HttpURLConnection urlConnection = null;
        String result = "";
        Document document = null;

        // http post
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            result = sb.toString();
            br.close();
            is.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(result)));
        } catch (IOException e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        } finally{
            urlConnection.disconnect();
        }
        return document;
    }
}

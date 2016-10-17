package com.bachelor.degree.travel.app;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 8/21/2016.
 */
public class TaxiDetailTask extends AsyncTask<Object,Void,HashMap<String, List<String>>> {
    TableLayout tableLayout = null;
    HashMap<String, List<String>> taxi = new HashMap<String, List<String>>();
    Context context;
    @Override
    protected HashMap<String, List<String>> doInBackground(Object... params) {
        List<String> list = (List<String>)params[0];
        tableLayout = (TableLayout)params[1];
        context = (Context)params[2];
        Http http = new Http();
        String data = "";
        List<String> name = new ArrayList<String>();
        List<String> address = new ArrayList<String>();
        List<String> phone = new ArrayList<String>();
        List<String> website = new ArrayList<String>();
        List<String> rating = new ArrayList<String>();
        try {
            for (int i = 0; i < list.size(); i++) {
                data = http.read(list.get(i));
                JSONObject jsonObject = new JSONObject(data);
                String status = jsonObject.getString("status");
                while (status.equals("OVER_QUERY_LIMIT")) {
                    Thread.sleep(2000);
                    Log.d("BUG", "Retry geocode");
                    data = http.read(list.get(i));
                    jsonObject = new JSONObject(data);
                    status = jsonObject.getString("status");
                }
                String phoneNr = "";
                JSONObject jsonData = jsonObject.getJSONObject("result");
                if (jsonData.has("name")) {
                    name.add(jsonData.getString("name")+ " ");
                } else {
                    name.add("no name ");
                }
                if (jsonData.has("formatted_address")) {
                    address.add(" " + jsonData.getString("formatted_address")+ " ");
                } else {
                    address.add(" no address ");
                }
                if (jsonData.has("formatted_phone_number")) {
                    phoneNr = jsonData.getString("formatted_phone_number") + " /" + "\n";
                }
                if (jsonData.has("international_phone_number")) {
                    phoneNr += jsonData.getString("international_phone_number");
                }
                if (phoneNr.isEmpty() || phoneNr.length() == 0) {
                    phoneNr = " no phone ";
                }
                phone.add(phoneNr);
                if (jsonData.has("website")) {
                    website.add(" " + jsonData.getString("website")+ " ");
                } else {
                    website.add(" no website ");
                }
                if (jsonData.has("rating")) {
                    rating.add(" " +jsonData.getString("rating")+ " ");
                } else {
                    rating.add(" no rating ");
                }
            }
            taxi.put("name", name);
            taxi.put("address", address);
            taxi.put("phone", phone);
            taxi.put("website", website);
            taxi.put("rating", rating);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  taxi;
    }

    @Override
    protected void onPostExecute(HashMap<String, List<String>> stringListHashMap) {
        List<String> name = stringListHashMap.get("name");
        List<String> address = stringListHashMap.get("address");
        List<String> phone = stringListHashMap.get("phone");
        List<String> website = stringListHashMap.get("website");
        List<String> rating = stringListHashMap.get("rating");
        for (int i = 0; i < name.size(); i++) {
            TableRow tbrow = new TableRow(context);
            tbrow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            TextView t1v = new TextView(context);
            t1v.setText(name.get(i));
            tbrow.addView(t1v);
            TextView t2v = new TextView(context);
            t2v.setText(address.get(i));
            t2v.setMaxWidth(400);
            tbrow.addView(t2v);
            TextView t3v = new TextView(context);
            t3v.setText(phone.get(i));
            t3v.setMaxWidth(300);
            tbrow.addView(t3v);
            TextView t4v = new TextView(context);
            t4v.setText(website.get(i));
            Linkify.addLinks(t4v, Linkify.WEB_URLS);
            tbrow.addView(t4v);
            TextView t5v = new TextView(context);
            t5v.setText(rating.get(i));
            tbrow.addView(t5v);
            tableLayout.addView(tbrow);
            if (i % 2 == 0) {
                tbrow.setBackgroundColor(Color.LTGRAY);
                t1v.setTextColor(Color.BLACK);
                t2v.setTextColor(Color.BLACK);
                t3v.setTextColor(Color.BLACK);
                t4v.setTextColor(Color.BLACK);
                t5v.setTextColor(Color.BLACK);
            } else {
                tbrow.setBackgroundColor(Color.DKGRAY);
                t1v.setTextColor(Color.WHITE);
                t2v.setTextColor(Color.WHITE);
                t3v.setTextColor(Color.WHITE);
                t4v.setTextColor(Color.WHITE);
                t5v.setTextColor(Color.WHITE);
            }
        }
    }

}

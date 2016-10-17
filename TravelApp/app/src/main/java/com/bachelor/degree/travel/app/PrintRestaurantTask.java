package com.bachelor.degree.travel.app;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.util.Linkify;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 8/21/2016.
 */
public class PrintRestaurantTask extends AsyncTask<Object, Void, HashMap<String, List<String>> > {
    private TableLayout tableLayout;
    private Context context;

    @Override
    protected HashMap<String, List<String>>  doInBackground(Object... params) {
        HashMap<String, List<String>> stringListHashMap = null;
        JSONObject jsonObject = (JSONObject)params[0];
        tableLayout = (TableLayout)params[1];
        context = (Context)params[2];
        ParseJsonRestaurant parser = new ParseJsonRestaurant();
        stringListHashMap = parser.parseRestaurants(jsonObject);
        return stringListHashMap;
    }

    @Override
    protected void onPostExecute(HashMap<String, List<String>> stringListHashMap) {
        List<String> name = stringListHashMap.get("name");
        List<String> address = stringListHashMap.get("address");
        List<String> open = stringListHashMap.get("open");
        for (int i = 0; i < name.size(); i++) {
            TableRow tbrow = new TableRow(context);
            tbrow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            TextView t1v = new TextView(context);
            t1v.setText(name.get(i));
            t1v.setMaxWidth(310);
            t1v.setPadding(2,2,3,2);
            tbrow.addView(t1v);
            TextView t2v = new TextView(context);
            t2v.setText(address.get(i));
            t2v.setMaxWidth(500);
            t2v.setPadding(2,2,3,2);
            tbrow.addView(t2v);
            TextView t3v = new TextView(context);
            t3v.setText(open.get(i));
            t3v.setPadding(2,2,3,2);
            tbrow.addView(t3v);
            tableLayout.addView(tbrow);
            if (i % 2 == 0) {
                tbrow.setBackgroundColor(Color.LTGRAY);
                t1v.setTextColor(Color.BLACK);
                t2v.setTextColor(Color.BLACK);
                t3v.setTextColor(Color.BLACK);
            } else {
                tbrow.setBackgroundColor(Color.DKGRAY);
                t1v.setTextColor(Color.WHITE);
                t2v.setTextColor(Color.WHITE);
                t3v.setTextColor(Color.WHITE);
            }
        }
    }
}

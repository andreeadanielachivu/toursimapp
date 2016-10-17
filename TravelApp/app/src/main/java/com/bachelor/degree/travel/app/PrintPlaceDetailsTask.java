package com.bachelor.degree.travel.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 8/22/2016.
 */
public class PrintPlaceDetailsTask extends AsyncTask<Object, Void, DetailsOpenReviews> {
    private TableLayout tableDays, tableReviews;
    Context context;
    TextView textView;
    PlaceDetailsActivity activity;
    @Override
    protected DetailsOpenReviews doInBackground(Object... params) {
        tableDays = (TableLayout)params[0];
        tableReviews = (TableLayout)params[1];
        JSONObject jsonObject = (JSONObject)params[2];
        context = (Context) params[3];
        textView = (TextView)params[4];
        activity = (PlaceDetailsActivity)params[5];
        ParserPlaceDetails parser = new ParserPlaceDetails();
        DetailsOpenReviews detailsOpenReviews = parser.parse(jsonObject);
        return detailsOpenReviews;
    }

    @Override
    protected void onPostExecute(DetailsOpenReviews detailsOpenReviews) {
        String type = "Place type: " + detailsOpenReviews.getTypePlace();
        OpenItem item = detailsOpenReviews.getItem();
        String day = "";
        //set the opening hours: if it's open
        String isOpen = item.getIsOpen();
        if (isOpen.compareToIgnoreCase("false") == 0) {
            textView.setText(type + "\n" + "Location is closed");
        } else if (isOpen.compareToIgnoreCase("true") == 0){
            textView.setText(type + "\n" + "Location is open");
        } else if (isOpen.compareToIgnoreCase("no info") == 0) {
            textView.setText(type + "\n" + "No info about opening hours");
        }
        HashMap<String, String> itemDays =  item.getDays();
        if (null!= itemDays && itemDays.size() > 0) {
            initTableHeaderDays();
            //get the days
            if (itemDays.containsKey("Monday")) {
                day = itemDays.get("Monday");
                addRow(day);
            }
            if (itemDays.containsKey("Tuesday")) {
                day = itemDays.get("Tuesday");
                addRow(day);
            }
            if (itemDays.containsKey("Wednesday")) {
                day = itemDays.get("Wednesday");
                addRow(day);
            }
            if (itemDays.containsKey("Thursday")) {
                day = itemDays.get("Thursday");
                addRow(day);
            }
            if (itemDays.containsKey("Friday")) {
                day = itemDays.get("Friday");
                addRow(day);
            }
            if (itemDays.containsKey("Saturday")) {
                day = itemDays.get("Saturday");
                addRow(day);
            }
            if (itemDays.containsKey("Sunday")) {
                day = itemDays.get("Sunday");
                addRow(day);
            }
        }
        activity.addHistory();
        TableRow tbrow = new TableRow(context);
        tbrow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        Button t1v = new Button(context);
        t1v.setText("See Reviews");
        tbrow.addView(t1v);
        t1v.setTextColor(Color.BLACK);
        t1v.setBackgroundColor(Color.LTGRAY);
        tableReviews.addView(tbrow);
        final List<Review> listReview = detailsOpenReviews.getReviews();
        t1v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.displayWindow(listReview);
            }
        });
    }

    public void addRow(String dayItem) {
        int indexPoints = dayItem.indexOf(":");
        if (indexPoints > -1) {
            String name_day = dayItem.substring(0, indexPoints);
            String hours = dayItem.substring(indexPoints + 1);
            TableRow tbrow = new TableRow(context);
            tbrow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            TextView t1v = new TextView(context);
            t1v.setText(name_day);
            tbrow.addView(t1v);
            TextView t2v = new TextView(context);
            t2v.setText(hours);
            tbrow.addView(t2v);
            tbrow.setBackgroundColor(Color.LTGRAY);
            t1v.setTextColor(Color.BLACK);
            t2v.setTextColor(Color.BLACK);
            tableDays.addView(tbrow);
        }
    }

    public void initTableHeaderDays() {
        TableRow tbrow0 = new TableRow(context);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        TextView tv0 = new TextView(context);
        tv0.setText("Day");
        tv0.setTextColor(Color.WHITE);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(context);
        tv1.setText("Hours");
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        tableDays.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

}

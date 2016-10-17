package com.bachelor.degree.travel.app;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 9/6/2016.
 */
public class PrintHistoryTask extends AsyncTask<Object, Void, List<String>> implements View.OnClickListener{
    private Context context;
    private TableLayout tableLayout;
    private HashMap<String, List<String>> map;
    private AppOfflineActivity activity;

    @Override
    protected List<String> doInBackground(Object... params) {
        tableLayout = (TableLayout)params[0];
        context = (Context)params[1];
        String response  = (String) params[2];
        activity = (AppOfflineActivity)params[3];
        ParserJsonData parserJsonData = new ParserJsonData();
        map = parserJsonData.parseHistory(response);
        List<String> names = map.get("name");
        return names;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
         for (int i = 0; i < strings.size(); i++) {
            TableRow tbrow0 = new TableRow(context);
            tbrow0.setBackgroundColor(Color.LTGRAY);
            tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            TextView tv1 = new TextView(context);
            tv1.setText(strings.get(i));
            tv1.setTextColor(Color.BLACK);
            tv1.setPadding(5, 5, 5, 5);
            tbrow0.addView(tv1);
            Button btn = new Button(context);
            btn.setId(i);
            btn.setOnClickListener(this);
            btn.setText("Details");
            tbrow0.addView(btn);
            tableLayout.addView(tbrow0);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        TableRow row = (TableRow)tableLayout.getChildAt(id+1);
        TextView textView = (TextView)row.getChildAt(0);
        String text = textView.getText().toString();//name objectiv
        List<String> name = map.get("name");
        int pos = -1;
        for (int i = 0; i < name.size(); i++) {
            if (name.get(i).compareTo(text) == 0) {
                pos = i;
                break;
            }
        }
        String data = text + "\n" + map.get("address").get(pos) +  "\n" +
                map.get("phone").get(pos) + "\n" + map.get("website").get(pos) +
                "\nRating: " + map.get("rating").get(pos) + "\nCoordonates: " +
                map.get("coords").get(pos);
        activity.displayWindow(data, text, map.get("place_id").get(pos));
    }
}

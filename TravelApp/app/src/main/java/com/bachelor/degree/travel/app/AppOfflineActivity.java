package com.bachelor.degree.travel.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppOfflineActivity extends BaseNavigationActivity implements AdapterView.OnItemSelectedListener {
    private TableLayout table_history;
    private SessionManager sessionManager;
    private SQLiteHandler db;
    private Map<String, String> jsonParams;
    private List<String> names;
    private TableLayout tableDialog;
    private Spinner spinner;
    private String category = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_offline);

        spinner = (Spinner)findViewById(R.id.spinnerCategory);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //noinspection ConstantConditions
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        table_history = (TableLayout) findViewById(R.id.table_history);
        initHeaderTable();

        sessionManager = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        jsonParams = new HashMap<String,String>();
        //request to server => parameter name, email and password
        /*if (sessionManager.isHistoryActivated()) {
            //if history for attractions is activate => save data
            jsonParams.put("request", "getCategory");
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

            String name = user.get("name");
            String email = user.get("email");
            jsonParams.put("username", name);
            jsonParams.put("email", email);
            jsonParams.put("category",category);
            HistoryList task = new HistoryList();
            Object [] obj= new Object[4];
            obj[0] = jsonParams;
            obj[1] = table_history;
            obj[2] = getApplicationContext();
            obj[3] = AppOfflineActivity.this;
            task.execute(obj);
        }*/
    }

    public void displayWindow(String data, final String name, final String placeid) {
        final Dialog dialog = new Dialog(AppOfflineActivity.this);
        dialog.setContentView(R.layout.main_history);
        //set up text
        tableDialog = (TableLayout)dialog.findViewById(R.id.table_history);
        Button btnEntire = (Button)dialog.findViewById(R.id.idPageEntire);
        dialog.setTitle("Details history");
        dialog.setCancelable(true);
        TextView textView = (TextView)dialog.findViewById(R.id.idTextHistory);
        textView.setText(data);
        Linkify.addLinks(textView, Linkify.WEB_URLS);

        //set up button
        Button button = (Button) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnEntire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        DetailedAttractionActivity.class);
                i.putExtra(Constants.NAME_ATTRACTION, name);
                i.putExtra(Constants.PLACE_ID, placeid);
                startActivity(i);
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }


    public void initHeaderTable() {
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv1 = new TextView(this);
        tv1.setText(R.string.name);
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5, 5, 5, 5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(R.string.details);
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5, 5, 5, 5);
        tbrow0.addView(tv2);
        table_history.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }



    @Override
    protected int getNavigationItemID() {
        return R.id.historyItem;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner)parent;
        if (spinner.getId() == R.id.spinnerCategory) {
            String item_category = parent.getItemAtPosition(position).toString();
            initTableCategory(item_category);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void initTableCategory(String category) {
        int count = table_history.getChildCount();
        if (count > 1) {
            table_history.removeViews(1, count - 1);
        }
        jsonParams = new HashMap<String,String>();
        if (sessionManager.isHistoryActivatedByCategory(category)) {
            //if history for attractions is activate => save data
            jsonParams.put("request", "getCategory");
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

            String name = user.get("name");
            String email = user.get("email");
            jsonParams.put("username", name);
            jsonParams.put("email", email);
            jsonParams.put("category",category);
            HistoryList task = new HistoryList();
            Object [] obj= new Object[4];
            obj[0] = jsonParams;
            obj[1] = table_history;
            obj[2] = getApplicationContext();
            obj[3] = AppOfflineActivity.this;
            task.execute(obj);
        }
    }
}

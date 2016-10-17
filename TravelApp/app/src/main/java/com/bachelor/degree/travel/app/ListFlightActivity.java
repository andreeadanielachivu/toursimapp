package com.bachelor.degree.travel.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListFlightActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView titleTextView;
    private TableLayout tableLayout;
    private TableLayout tableDialog;
    private EditText iataEditText;
    private TextView nameAirport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_flight);

        titleTextView = (TextView) findViewById(R.id.titleFlight);
        Intent intent = getIntent();
        String nameAirportFrom = intent.getStringExtra(Constants.FROM_AIRPORT);
        String nameAirportTo = intent.getStringExtra(Constants.TO_AIRPORT);
        titleTextView.setText(nameAirportFrom + " - " + nameAirportTo);

        tableLayout = (TableLayout)findViewById(R.id.table_flight);
        initTable();
        searchFlight(intent);

    }

    public void searchFlight(Intent intent) {
        String codeFrom = intent.getStringExtra(Constants.FROM_AIRPORT_CODE);
        String codeTo = intent.getStringExtra(Constants.TO_AIRPORT_CODE);
        String dataFrom = intent.getStringExtra(Constants.DATAFROM);
        String dataTo = intent.getStringExtra(Constants.DATATO);
        HttpRequestSearchFlight flight = new HttpRequestSearchFlight();
        Object []objFlight = new Object[8];
        objFlight[0] = Constants.URL_FLIGHTS;
        objFlight[1] = codeFrom;
        objFlight[2] = codeTo;
        objFlight[3] = dataFrom;
        objFlight[4] = dataTo;
        objFlight[5] = tableLayout;
        objFlight[6] = getApplicationContext();
        objFlight[7] = ListFlightActivity.this;
        flight.execute(objFlight);
    }

    public void initTable() {
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv0 = new TextView(this);
        tv0.setText("Price");
        tv0.setTextColor(Color.WHITE);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Duration");
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Details");
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        tableLayout.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public void displayWindow(List<List<String>> data) {
        final Dialog dialog = new Dialog(ListFlightActivity.this);
        dialog.setContentView(R.layout.main_dialog);
        //set up text
        tableDialog = (TableLayout)dialog.findViewById(R.id.table_list_flight);
        dialog.setTitle("Details flight");
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //get iata edit text, btn, textview
        iataEditText = (EditText)dialog.findViewById(R.id.id_textConverter);
        Button convertBtn = (Button)dialog.findViewById(R.id.convertBtn);
        nameAirport = (TextView)dialog.findViewById(R.id.nameAirportText);

        //listener on btn
        convertBtn.setOnClickListener(this);

        initTableHeader();
        for (int i = 0; i < data.size(); i++) {
            List<String> rowsData = data.get(i);
            for (int j = 0; j < rowsData.size(); j++) {
                setRowTable(rowsData.get(j));
            }
            endTable();
        }
        //set up button
        Button button = (Button) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    public void setRowTable(String rowTable) {
        TableRow tbrow = new TableRow(getApplicationContext());
        tbrow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        int index = rowTable.indexOf(",");
        int begin = 0;
        int end = index;
        while (index != -1) {
            String text = rowTable.substring(begin, index);
            begin = index + 1;
            index = rowTable.indexOf("," , begin);

            if ( Character.isDigit(text.charAt(0)) ) {
                int idT = text.indexOf("T");
                int idPlus = text.indexOf("+");
                int idMinus = text.lastIndexOf("-");
                if (idT != -1) {
                    if (idPlus != -1) {
                        text = " " + text.substring(0, idT) + " " + text.substring(idT+1, idPlus) + " ";
                    } else if (idMinus != -1) {
                        text = " " + text.substring(0, idT) + " " + text.substring(idT+1, idMinus) + " ";
                    }
                }
            }

            TextView t1v = new TextView(getApplicationContext());
            t1v.setText(text);
            t1v.setTextColor(Color.BLACK);
            t1v.setPadding(5,5,5,5);
            tbrow.addView(t1v);
        }
        String lastrow = rowTable.substring(begin);
        TextView t2v = new TextView(getApplicationContext());
        t2v.setText("  " + lastrow);
        t2v.setTextColor(Color.BLACK);
        t2v.setPadding(5,5,5,5);
        //t2v.setMaxWidth(500);
        tbrow.addView(t2v);
        tableDialog.addView(tbrow);
    }


    public void initTableHeader() {
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv0 = new TextView(this);
        tv0.setText("Origin");
        tv0.setTextColor(Color.WHITE);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Destination");
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Departure");
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText("Arrival");
        tv3.setTextColor(Color.WHITE);
        tv3.setPadding(5,5,5,5);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText("Aircraft");
        tv4.setTextColor(Color.WHITE);
        tv4.setPadding(5,5,5,5);
        tbrow0.addView(tv4);
        if (tableDialog != null)
            tableDialog.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public void endTable() {
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv0 = new TextView(this);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setPadding(5,5,5,5);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setPadding(5,5,5,5);
        tbrow0.addView(tv4);
        tableDialog.addView(tbrow0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.convertBtn) {
            String iataCode = iataEditText.getText().toString();
            Object obj[] = new Object[2];
            obj[0] = iataCode;
            obj[1] = ListFlightActivity.this;
            FindAirportByNameTask task = new FindAirportByNameTask();
            task.execute(obj);
        }
    }

    public void setTextAirport(String s) {
        nameAirport.setText(s);
    }
}

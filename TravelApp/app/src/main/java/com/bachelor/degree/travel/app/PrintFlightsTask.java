package com.bachelor.degree.travel.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 8/20/2016.
 */
public class PrintFlightsTask extends AsyncTask<Object, Void, List<Flight>> implements View.OnClickListener{
    private TableLayout tableLayout;
    private List<Flight> listFlights = null;
    private Context context;
    private ListFlightActivity activity;

    private HashMap<Integer, List<List<String>>> buttonData = new HashMap<Integer, List<List<String>>>();
    @Override
    protected List<Flight> doInBackground(Object... params) {
        tableLayout = (TableLayout)params[0];
        String data = (String)params[1];
        context = (Context)params[2];
        activity = (ListFlightActivity)params[3];
        ParserJsonData parser = new ParserJsonData();
        listFlights = parser.parseFlights(data);
        return listFlights;
    }

    @Override
    protected void onPostExecute(List<Flight> strings) {
        StringBuilder infoFlight = new StringBuilder();
        SingleFlight tur = null;
        List<String> originplace;
        List<String> destplace;
        List<String> dtime;
        List<String> atime;
        List<String> aircraft;
        List<String> listFlightsTour;
        List<String> listFlightsRetour;
        int indexT1, indexPlus1, indexT2, indexPlus2, indexMinus1, indexMinus2;
        HashMap<String, List<String>> infoData;
        int duration;

        for (int i = 0; i < strings.size(); i++) {
            Flight flight = strings.get(i);
            String priceFlight = flight.getPrice();
            String price = priceFlight.substring(3) + " " + priceFlight.substring(0, 3);
            List<SingleFlight> singelFlight = flight.getFlightTR();

            //dus
            tur = singelFlight.get(0);
            duration = tur.getDuration();
            String data = "Tur: " + duration + "min.\n";
            infoData = tur.getOneFlight();
            originplace = infoData.get("origin");
            destplace = infoData.get("destination");
            dtime = infoData.get("departureTime");
            atime = infoData.get("arrivalTime");
            aircraft = infoData.get("aircraft");
            listFlightsTour = new ArrayList<String>();
            for (int j = 0; j < originplace.size(); j++) {
                String detailsFLight = originplace.get(j) + "," + destplace.get(j) + ","
                        + dtime.get(j) + "," + atime.get(j) + "," + aircraft.get(j);
                listFlightsTour.add(detailsFLight);
            }

            String start_date = dtime.get(0);
            indexT1 = start_date.indexOf("T");
            indexPlus1 = start_date.indexOf("+");
            indexMinus1 = start_date.lastIndexOf("-");
            String end_date = atime.get(atime.size() - 1);
            indexT2 = end_date.indexOf("T");
            indexPlus2 = end_date.indexOf("+");
            indexMinus2 = end_date.lastIndexOf("-");
            data += start_date.substring(0, indexT1);
            if (indexPlus1 != -1) {
                data += " " + start_date.substring(indexT1 + 1, indexPlus1) + " \n";
            } else if (indexMinus1 != -1) {
                data += " " + start_date.substring(indexT1 + 1, indexMinus1) + " \n";
            } else {
                data += " " + start_date + " \n";
            }
            data += end_date.substring(0, indexT2) + " ";
            if (indexPlus2 != -1) {
                data += end_date.substring(indexT2 + 1, indexPlus2) + "\n";
            } else if (indexMinus2  != -1) {
                data += end_date.substring(indexT2 + 1, indexMinus2) + "\n";
            } else {
                data += end_date + "\n";
            }
            //data += start_date.substring(0, indexT1) + " - " + end_date.substring(0, indexT2) + "\n";

            //intors
            tur = singelFlight.get(1);
            duration = tur.getDuration();
            data += "Retur: " + duration + "min.\n";
            infoData = tur.getOneFlight();
            originplace = infoData.get("origin");
            destplace = infoData.get("destination");
            dtime = infoData.get("departureTime");
            atime = infoData.get("arrivalTime");
            aircraft = infoData.get("aircraft");
            listFlightsRetour = new ArrayList<String>();
            for (int j = 0; j < originplace.size(); j++) {
                String detailsFLight = originplace.get(j) + "," + destplace.get(j) + ","
                        + dtime.get(j) + "," + atime.get(j) + "," + aircraft.get(j);
                listFlightsRetour.add(detailsFLight);
            }

            String start_date_retur = dtime.get(0);
            indexT1 = start_date_retur.indexOf("T");
            indexPlus1 = start_date_retur.indexOf("+");
            indexMinus1 = start_date_retur.lastIndexOf("-");
            String end_date_retur = atime.get(atime.size() - 1);
            indexT2 = end_date_retur.indexOf("T");
            indexPlus2 = end_date_retur.indexOf("+");
            indexMinus2 = end_date_retur.lastIndexOf("-");
            data += start_date_retur.substring(0, indexT1);
            if (indexPlus1 != -1) {
                data += " " + start_date_retur.substring(indexT1 + 1, indexPlus1) + " \n";
            } else if (indexMinus1 != -1) {
                data += " " + start_date_retur.substring(indexT1 + 1, indexMinus1) + " \n";
            } else {
                data += " " + start_date_retur + " \n";
            }
            data += end_date_retur.substring(0, indexT2) + " ";
            if (indexPlus2 != -1) {
                data += end_date_retur.substring(indexT2 + 1, indexPlus2) + "\n";
            } else if (indexMinus2 != -1) {
                data += end_date_retur.substring(indexT2 + 1, indexMinus2) + "\n";
            }else {
                data += end_date_retur + "\n";
            }
            //data += "\n"  + start_date_retur.substring(indexT1+1, indexPlus1) + " - " +
               //      end_date_retur.substring(indexT2+1, indexPlus2) + "\n";
            //data += start_date_retur.substring(0, indexT1) + " - " + end_date_retur.substring(0, indexT2) + "\n";
            setTable(price, data, listFlightsTour, listFlightsRetour, i);
        }
    }

    public void setTable(String price, String duration, List<String> tour, List<String> retour, Integer index) {
        TableRow tbrow = new TableRow(context);
        tbrow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView t1v = new TextView(context);
        t1v.setText(price);
        tbrow.addView(t1v);

        TextView t2v = new TextView(context);
        t2v.setText(duration);
        t2v.setMaxWidth(500);
        t2v.setPadding(5,5,5,5);
        tbrow.addView(t2v);

        Button btn = new Button(context);
        btn.setId(index);
        btn.setText("Details");
        btn.setOnClickListener(this);
        btn.setPadding(2,2,5,2);
        tbrow.addView(btn);

        List<List<String>> completeFlight = new ArrayList<List<String>>();
        completeFlight.add(tour);
        completeFlight.add(retour);

        buttonData.put(index, completeFlight);

        tbrow.setBackgroundColor(Color.LTGRAY);
        t1v.setTextColor(Color.BLACK);
        t2v.setTextColor(Color.BLACK);
        tableLayout.addView(tbrow);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case 0:
                activity.displayWindow(buttonData.get(0));
                break;
            case 1:
                activity.displayWindow(buttonData.get(1));
                break;
            case 2:
                activity.displayWindow(buttonData.get(2));
                break;
            case 3:
                activity.displayWindow(buttonData.get(3));
                break;
            case 4:
                activity.displayWindow(buttonData.get(4));
                break;
        }
    }
}

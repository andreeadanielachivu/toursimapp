package com.bachelor.degree.travel.app;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreea on 8/19/2016.
 */
public class ParseResponseFromServer extends AsyncTask<Object, Void, List<String>> {
    ArrayAdapter<String> adapter;
    AutoCompleteTextView spinner;//, spinnerTo;
    AutoCompleteTextView spinnerTo;
    @Override
    protected List<String> doInBackground(Object... params) {
        List<String> airports = null;
        adapter = (ArrayAdapter<String>)params[0];
        spinner = (AutoCompleteTextView) params[1];
        spinnerTo = (AutoCompleteTextView) params[2];
        String response = (String)params[3];
        String request = (String)params[4];
        ParserJsonData data = new ParserJsonData();
        airports = data.parseAirports(response);
        return airports;
    }

    @Override
    protected void onPostExecute(List<String> s) {
        for (int i = 0; i < s.size(); i++) {
            adapter.add(s.get(i));
            adapter.notifyDataSetChanged();
        }
        spinner.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }
}

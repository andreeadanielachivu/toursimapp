package com.bachelor.degree.travel.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andreea on 9/5/2016.
 */
public class HistoryPlaceDetails {
    public static final String TAG = "TAG";
    //private ProgressDialog pDialog;
    private Context context;
    private List<String> names;


    public HistoryPlaceDetails(Context context) {
        // Progress dialog
        this.context = context;
        //pDialog = new ProgressDialog(this.context);
        //pDialog.setCancelable(false);
    }

    public void registerPlace(final Map infoPlace) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        //pDialog.setMessage("Saving history ...");
        //showDialog();

        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.URL_HISTORY,
                new JSONObject(infoPlace),

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "History Response: " + response);
                        //hideDialog();

                        try {
                            boolean error = response.getBoolean("error");
                            if (!error) {

                                Log.d("ON RESPONSE", "On response for the first time");
                            } else {
                                // Error occurred in registration. Get the error
                                // message
                                String errorMsg = response.getString("error_msg");
                                Toast.makeText(context,
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "History Error: " + error.getMessage());
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        //hideDialog();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(myRequest, tag_string_req);
    }

    private void showDialog() {
        //if (!pDialog.isShowing())
        //    pDialog.show();
    }

    private void hideDialog() {
       // if (pDialog.isShowing())
        //    pDialog.dismiss();
    }

    public List<String> parseDataTable(JSONObject response) {
        List<String> names = new ArrayList<String>();
        try {
            JSONArray arrayplaces = response.getJSONArray("places");
            for (int i =0 ; i < arrayplaces.length(); i++) {
                JSONObject object = arrayplaces.getJSONObject(i);
                names.add(object.getString("place_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return names;
    }

    public List<String> getNames() {
        return this.names;
    }
}

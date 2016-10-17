package com.bachelor.degree.travel.app;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andreea on 8/17/2016.
 */
public class GetAddressPlaceTask extends AsyncTask<Object, Void, String> {
    double lat;
    double longit;
    PlaceAutocompleteFragment source;
    Context context;
    String adminArea = "";
    String country = "";
    TextView invisbleData = null;
    @Override
    protected String doInBackground(Object... params) {
        Geocoder geocoder;
        List<Address> addresses;
        lat = (double)params[0];
        longit = (double)params[1];
        source = (PlaceAutocompleteFragment) params[2];
        context = (Context) params[3];
        invisbleData = (TextView)params[4];
        geocoder = new Geocoder(context, Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        try {
            addresses = geocoder.getFromLocation(lat, longit, 1);
            while (addresses == null || addresses.size() == 0) {
                Thread.sleep(2000);
                addresses = geocoder.getFromLocation(lat, longit, 1);
                Log.d("TAG", "Retry to geocode transport");
            }
            if (addresses != null && addresses.size() != 0) {
                Address address = addresses.get(0);
                if (address != null) {
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                }
                adminArea = address.getAdminArea();
                country = address.getCountryName();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return "IOE EXCEPTION";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            source.setText(s);
        } else {
            source.setText("");
        }
        invisbleData.setText(adminArea + "|" + country);
    }

}

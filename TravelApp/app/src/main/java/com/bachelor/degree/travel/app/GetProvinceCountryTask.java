package com.bachelor.degree.travel.app;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andreea on 8/22/2016.
 */
public class GetProvinceCountryTask extends AsyncTask<Object, Void, HashMap<String, String>>{
    public AsyncResponse delegate = null;
    @Override
    protected HashMap<String, String> doInBackground(Object... params) {
        Geocoder geocoder;
        List<Address> addresses;
        HashMap<String, String> hashMap = new HashMap<String, String>();
        double lat = (double)params[0];
        double longit = (double)params[1];
        Context context = (Context)params[2];
        StringBuilder sb = new StringBuilder();
        geocoder = new Geocoder(context, Locale.getDefault());
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
                    String adminArea = address.getAdminArea();
                    String country = address.getCountryName();
                    String locality = address.getLocality();
                    hashMap.put("adminarea", adminArea);
                    hashMap.put("country", country);
                    hashMap.put("locality", locality);
                    hashMap.put("address", sb.toString());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
        delegate.processFinish(stringStringHashMap);
    }
}

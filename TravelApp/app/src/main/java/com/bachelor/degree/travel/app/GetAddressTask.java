package com.bachelor.degree.travel.app;

import android.location.Geocoder;
import android.os.AsyncTask;
import android.location.Address;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by Andreea on 6/18/2016.
 */
public class GetAddressTask extends AsyncTask<String, Void, String> {
    private CastelActivity activity;
    private static String province = "";
    private static String country = "";

    public GetAddressTask(CastelActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(activity, Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        String locality = "";
        String postalCode = "";
        String knownName = "";
        String sublocality = "";
        String adminarea = "";

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(params[0]), Double.parseDouble(params[1]), 1);
            Log.d("LAT and LONG in here", "LAT: " + Double.parseDouble(params[0]) + " LONG: " + Double.parseDouble(params[1]));
            //get current Street name
            if (addresses != null) {
                Address address = addresses.get(0);
                if (address != null) {
                    if (addresses.size() > 0) {
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        locality = address.getLocality();
                        province = address.getAdminArea();
                        country = address.getCountryName();
                        postalCode = address.getPostalCode();
                        knownName = address.getFeatureName();
                        sublocality = address.getSubLocality();
                        adminarea = address.getSubAdminArea();
                    }
                }
            }
            String result = "";
            if (sb != null) {
                result = "Street: " + sb.toString() + "\n";
            }
            if (null != locality && !locality.isEmpty()) {
                result += "Locality: " + locality + "\n";
            }
            if (null != province && !province.isEmpty()) {
                result += "City/Province: " + province + "\n";
            }
            if (null != country && !country.isEmpty()) {
                result += "Country: " + country + "\n";
            }
            if (null!= adminarea && !adminarea.isEmpty()) {
                result += "Admin area: " + adminarea + "\n";
            }
            return result;

        } catch (IOException ex) {
            ex.printStackTrace();
            return "IOE EXCEPTION";

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return "IllegalArgument Exception";
        }

    }

    /**
     * When the task finishes, onPostExecute() call back data to Activity UI and displays the address.
     * @param address
     */
    @Override
    protected void onPostExecute(String address) {
        // Call back Data and Display the current address in the UI
        activity.callBackDataFromAsyncTask(address);
    }

    public static String getProvince() {
        return province;
    }

    public static String getCountry() {
        return country;
    }
}

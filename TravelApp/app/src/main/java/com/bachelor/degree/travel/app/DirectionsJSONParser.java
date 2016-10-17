package com.bachelor.degree.travel.app;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 7/16/2016.
 */
public class DirectionsJSONParser {
    private String distance = "";
    private String duration = "";
    private String start_address = "";
    private String end_address = "";
    private String summary = "";
    private StringBuilder detailsRoute = new StringBuilder();
    private String available_travel_mode = "Available travel mode: ";
    private static final String status_result = "ZERO_RESULTS";

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");
            String status = jObject.getString("status");
            //atentie posibil sa scrapre ptr directions din DetailedAttraction!!!!
            while (status.equals("OVER_QUERY_LIMIT")) { //cazul pentru celallt request...
                Thread.sleep(2000);
                Log.d("BUG", "Retry geocode directions");
                String data = DownLoadTaskUrl.downloadUrl(DownLoadTaskUrl.urlRequest);//set url as parameter
                JSONObject jData = new JSONObject(data);
                status = jData.getString("status");
                jRoutes = jData.getJSONArray("routes");
            }
            if (jRoutes.length() == 0 || status.compareToIgnoreCase(status_result) == 0) {
                JSONArray jtravel_modes = jObject.getJSONArray("available_travel_modes");
                for (int i = 0; i < jtravel_modes.length(); i++) {
                    if (i == jtravel_modes.length()-1) {
                        available_travel_mode += jtravel_modes.get(i).toString().toLowerCase();
                    } else {
                        available_travel_mode += jtravel_modes.get(i).toString().toLowerCase() + ", ";
                    }
                }
                return null;
            }
            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();
                JSONObject summaryRoute = jRoutes.getJSONObject(i);
                if (summaryRoute.has("summary")) {
                    summary = summaryRoute.getString("summary");
                }
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++) {
                    JSONObject leg = jLegs.getJSONObject(i);
                    if (null != leg){
                        if (leg.has("distance")) {
                            distance = leg.getJSONObject("distance").getString("text");
                        }
                        if (leg.has("duration")) {
                            duration = jLegs.getJSONObject(i).getJSONObject("duration").getString("text");
                        }
                        if (leg.has("start_address")) {
                            start_address = jLegs.getJSONObject(i).getString("start_address");
                        }
                        if (leg.has("end_address")) {
                            end_address = jLegs.getJSONObject(i).getString("end_address");
                        }
                    }
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        JSONObject step = jSteps.getJSONObject(k);
                        if (null != step && jSteps.length() != 0) {
                            if (step.has("html_instructions")) {
                                detailsRoute.append(step.getString("html_instructions"));
                            }
                            if (step.has("transit_details")) {
                                JSONObject transitDetails = step.getJSONObject("transit_details");
                                if (transitDetails != null && transitDetails.has("arrival_stop")) {
                                    detailsRoute.append(" - " + transitDetails.getJSONObject("arrival_stop").getString("name") + ", ");
                                }
                            }
                            if (step.has("travel_mode")) {
                                //detailsRoute.append("Travel mode:" + step.getString("travel_mode")+ ", ");
                            }
                            if (step.has("duration")) {
                                detailsRoute.append("(" +step.getJSONObject("duration").getString("text") + ", ");
                            }
                            if (step.has("distance")) {
                                detailsRoute.append(step.getJSONObject("distance").getString("text")+ ")" + "\n");
                            }
                        }

                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }
    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public String getData() {
        return "From: " + start_address + "\n" + "To: " + end_address + "\n" +
                "Distance: " + distance + "\n" + "Duration: " + duration + "\n" +
                "Summary: " + summary + "\n";
    }

    public String getAvailable_travel_mode() {
        return  this.available_travel_mode;
    }

    public StringBuilder getDetailsRoute() {
        return this.detailsRoute;
    }
    public String getDistance() {
        return this.distance;
    }
    public String getDuration() {
        return this.duration;
    }
    public String getSummary() {
        return this.summary;
    }
}

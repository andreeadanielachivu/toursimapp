package com.bachelor.degree.travel.app;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Andreea on 8/19/2016.
 */
public class ParserJsonData {
    public List<String> parseAirports(String data) {
        List<String> airports = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("error")) {
                String error = jsonObject.getString("error");
                if (error.compareToIgnoreCase("false") == 0) {
                    if (jsonObject.has("airports")) {
                        JSONArray list =  jsonObject.getJSONArray("airports");
                        for (int i = 0; i < list.length(); i++) {
                            String item = list.get(i).toString();
                            airports.add(item);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return airports;
    }

    //flgihtTR=List<HashMap<String, HashMap<String, String>>>

    public List<Flight> parseFlights(String data) {
        List<Flight> flights = new ArrayList<Flight>();
        List<SingleFlight> singleFlightsList = null;
        HashMap<String, List<String>> detailSingleFLight = null;
        List<String> origin = null;
        List<String> destination = null;
        List<String> departureTime = null;
        List<String> arrivalTime = null;
        List<String> aircraft = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("error")) {
                boolean error = jsonObject.getBoolean("error");
                if (!error) {
                    if (jsonObject.has("flights")) {
                        JSONArray arrayFlights = jsonObject.getJSONArray("flights");
                        for (int i = 0; i < arrayFlights.length(); i++) {
                            JSONObject jflight = arrayFlights.getJSONObject(i);
                            singleFlightsList = new ArrayList<SingleFlight>();
                            if (jflight.has("flightTR")) {
                                JSONArray flightTR = jflight.getJSONArray("flightTR");
                                for (int j = 0; j < flightTR.length(); j++) {
                                    //flightTR => flight + duration = item
                                    JSONObject jflighTRitem = flightTR.getJSONObject(j);
                                    int duratin = jflighTRitem.getInt("duration");
                                    detailSingleFLight = new HashMap<String, List<String>>();
                                    JSONArray detailsFLight = jflighTRitem.getJSONArray("flight");
                                    origin = new ArrayList<String>();
                                    destination = new ArrayList<String>();
                                    departureTime = new ArrayList<String>();
                                    arrivalTime = new ArrayList<String>();
                                    aircraft = new ArrayList<String>();
                                     for (int k = 0; k < detailsFLight.length(); k++) {
                                            JSONObject detailFlightitem = detailsFLight.getJSONObject(k);
                                            String originFrom = detailFlightitem.getString("origin");
                                            origin.add(originFrom);
                                            String destinationTo = detailFlightitem.getString("destination");
                                            destination.add(destinationTo);
                                            String dateFrom = detailFlightitem.getString("departureTime");
                                            departureTime.add(dateFrom);
                                            String dateTo = detailFlightitem.getString("arrivalTime");
                                            arrivalTime.add(dateTo);
                                            String aircraftFlight = detailFlightitem.getString("aircraft");
                                            aircraft.add(aircraftFlight);
                                    }
                                    detailSingleFLight.put("origin", origin);
                                    detailSingleFLight.put("destination", destination);
                                    detailSingleFLight.put("departureTime", departureTime);
                                    detailSingleFLight.put("arrivalTime", arrivalTime);
                                    detailSingleFLight.put("aircraft", aircraft);
                                    SingleFlight singleFlight = new SingleFlight();
                                    singleFlight.setDuration(duratin);
                                    singleFlight.setOneFlight(detailSingleFLight);
                                    singleFlightsList.add(singleFlight);
                                }
                            }
                            String price = "";
                            if (jflight.has("price")) {
                                price = jflight.getString("price");
                            }
                            Flight completeFlight = new Flight();
                            completeFlight.setPrice(price);
                            completeFlight.setFlightTR(singleFlightsList);
                            flights.add(completeFlight);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public HashMap<String, List<String>> parseHistory(String data) {
        HashMap<String, List<String>> map = new HashMap<String,List<String>>();
        List<String> name = new ArrayList<String>();
        List<String> address = new ArrayList<String>();
        List<String> placeids = new ArrayList<String>();
        List<String> phone = new ArrayList<String>();
        List<String> website = new ArrayList<String>();
        List<String> rating = new ArrayList<String>();
        List<String> coordonates = new ArrayList<String>();
        try {
            JSONObject response = new JSONObject(data);
            boolean error = response.getBoolean("error");
            if (!error) {
                JSONArray jsonArray = response.getJSONArray("places");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    name.add(object.getString("place_name"));
                    address.add(object.getString("place_address"));
                    placeids.add(object.getString("place_id"));
                    phone.add(object.getString("place_phone"));
                    website.add(object.getString("place_website"));
                    rating.add(object.getString("place_rating"));
                    coordonates.add(object.getString("place_lat") + ", " + object.getString("place_lng"));
                }
                map.put("name", name);
                map.put("address", address);
                map.put("place_id", placeids);
                map.put("phone", phone);
                map.put("website", website);
                map.put("rating", rating);
                map.put("coords", coordonates);
            } else {
                // Error occurred in registration. Get the error
                // message
                String errorMsg = response.getString("error_msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getAirportByCode(String data) {
        String name = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("error")) {
                String error = jsonObject.getString("error");
                if (error.compareToIgnoreCase("false") == 0) {
                    if (jsonObject.has("name")) {
                        name = jsonObject.getString("name");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }
}

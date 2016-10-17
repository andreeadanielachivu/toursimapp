package com.bachelor.degree.travel.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 8/21/2016.
 */
public class ParseJsonRestaurant {

    public HashMap<String, List<String>> parseRestaurants(JSONObject jsonObject) {
        HashMap<String, List<String>> stringListHashMap = new HashMap<String, List<String>>();
        List<String> name = new ArrayList<String>();
        List<String> address = new ArrayList<String>();
        List<String> open = new ArrayList<String>();
        try {
            JSONArray result = jsonObject.getJSONArray("results");
            for (int i = 0; i < result.length(); i++) {
                JSONObject object = result.getJSONObject(i);
                if (object.has("name")) {
                    name.add(object.getString("name") + " ");
                } else {
                    name.add("no name ");
                }
                if (object.has("formatted_address")) {
                    address.add(" " + object.getString("formatted_address") + " ");
                } else {
                    address.add(" no address ");
                }
                if (object.has("opening_hours")) {
                    JSONObject isOpen = object.getJSONObject("opening_hours");
                    if (isOpen.has("open_now")) {
                        open.add(" " + isOpen.getString("open_now"));
                    } else {
                        open.add(" " + "no info");
                    }
                } else {
                    open.add(" " + "no info");
                }
            }
            stringListHashMap.put("name", name);
            stringListHashMap.put("address", address);
            stringListHashMap.put("open", open);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringListHashMap;
    }
}

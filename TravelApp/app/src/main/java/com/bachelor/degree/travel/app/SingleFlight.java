package com.bachelor.degree.travel.app;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 8/20/2016.
 */
public class SingleFlight {
    private HashMap<String, List<String>> oneFlight = null;
    private int duration;

    public SingleFlight() {
        this.oneFlight = new HashMap<String, List<String>>();
        this.duration = 0;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOneFlight(HashMap<String, List<String>> flight) {
        List<String> origin = flight.get("origin");
        List<String> desination = flight.get("destination");
        List<String> departureTime = flight.get("departureTime");
        List<String> arrivalTime = flight.get("arrivalTime");
        List<String> aircrafts = flight.get("aircraft");
        oneFlight.put("origin", origin);
        oneFlight.put("destination", desination);
        oneFlight.put("departureTime", departureTime);
        oneFlight.put("arrivalTime", arrivalTime);
        oneFlight.put("aircraft", aircrafts);
    }

    public int getDuration() {
        return this.duration;
    }

    public HashMap<String, List<String>> getOneFlight() {
        return this.oneFlight;
    }
}

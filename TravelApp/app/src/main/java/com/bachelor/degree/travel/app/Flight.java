package com.bachelor.degree.travel.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreea on 8/20/2016.
 */
public class Flight {
    private String price;
    List<SingleFlight> flightTR = null;

    public Flight() {
        price = "";
        flightTR = new ArrayList<SingleFlight>();
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setFlightTR(List<SingleFlight> flight) {
        for (int i = 0; i < flight.size(); i++) {
            flightTR.add(flight.get(i));
        }
    }

    public String getPrice() {
        return this.price;
    }

    public List<SingleFlight> getFlightTR() {
        return this.flightTR;
    }
}

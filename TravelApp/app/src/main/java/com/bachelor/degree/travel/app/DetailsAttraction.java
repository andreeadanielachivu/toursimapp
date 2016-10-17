package com.bachelor.degree.travel.app;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreea on 7/21/2016.
 */
public class DetailsAttraction {

    private String name;
    private String phone_number;
    private String international_phone_number;
    private String address;
    private double latitude;
    private double longitude;
    private JSONArray program;
    private boolean isOpen;
    private double rating;
    private String type;
    private String website;
    private String location;
    private List<Review> reviews;

    public DetailsAttraction() {
        this.name = "";
        this.international_phone_number="";
        this.phone_number = "";
        this.address = "";
        this.latitude = 0;
        this.longitude = 0;
        this.program = new JSONArray();
        isOpen = false;
        this.rating = 0;
        this.type = "";
        this.website = "";
        this.location = "";
        this.reviews = new ArrayList<Review>();
    }

    public DetailsAttraction(boolean isOpen) {
        this();
        this.isOpen = isOpen;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocalPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setInternationalPhoneNumber(String international_phone_number) {
        if (international_phone_number != null) {
            this.international_phone_number = international_phone_number;
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLocation(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    public void setProgram(JSONArray program) {
        this.program = program;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setLocationOnMap(String location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReviews(List<Review> reviews) {
        for (int i = 0; i < reviews.size(); i++) {
            Review obj = reviews.get(i);
            if (obj != null) {
                this.reviews.add(obj);
            }
        }
    }

    public String toString() {
        String open = "";
        if (!isOpen) {
            open += "closed";
        } else {
            open += "open";
        }
        String nameLocation = this.name + " is " +open + "\n";
        String myaddress = "";
        String myphone = "";
        String mycoords = "";
        String myType = "";
        String myRating = "";
        if (null != this.address && !this.address.isEmpty()) {
            myaddress = "Adress: " + this.address + "\n";
        }
        if (null != this.phone_number && !this.phone_number.isEmpty()) {
            myphone = "Phone: " + this.phone_number;
        }
        if (null != this.international_phone_number && !this.international_phone_number.isEmpty()) {
            myphone += " / " + this.international_phone_number + "\n";
        } else {
            myphone += "\n";
        }
        if (this.latitude != 0 && this.longitude != 0) {
            mycoords = "Coordinates: " + this.latitude + ", " + this.longitude + "\n";
        }
        if (null != this.type && !this.type.isEmpty()) {
            myType = "Type: " + this.type + "\n";
        }
        if (this.rating != 0) {
            myRating = "Rating: " + this.rating;
        }
        return nameLocation + myaddress + myphone + mycoords + myType + myRating;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public JSONArray getProgram() {
        return this.program;
    }

    public String getWebsite() {
        return this.website;
    }

    public List<Review> getReviews() {
        return this.reviews;
    }
}

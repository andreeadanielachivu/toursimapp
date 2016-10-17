package com.bachelor.degree.travel.app;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Andreea on 8/22/2016.
 */
public class DetailsOpenReviews {
    private OpenItem item;
    private String typePlace;
    private List<Review> reviews;

    public DetailsOpenReviews() {
        item = new OpenItem();
        this.typePlace = "";
        reviews = new ArrayList<Review>();
    }

    public void setItem(OpenItem item) {
        this.item = item;
    }

    public void setReviews(List<Review> reviews) {
        for (int i = 0; i < reviews.size(); i++) {
            this.reviews.add(reviews.get(i));
        }
    }

    public void setTypePlace(String typePlace) {
        this.typePlace = typePlace;
    }

    public String getTypePlace() {
        return this.typePlace;
    }

    public OpenItem getItem() {
        return this.item;
    }

    public List<Review> getReviews() {
        return this.reviews;
    }
}

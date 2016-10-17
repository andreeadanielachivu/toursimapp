package com.bachelor.degree.travel.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andreea on 8/22/2016.
 */
public class ParserPlaceDetails {

    public DetailsOpenReviews parse (JSONObject jsonObject) {
        List<Review> listReviews = new ArrayList<Review>();
        HashMap<String,String> hashMapDays = new HashMap<String, String>();
        DetailsOpenReviews detailsOpenReviews = new DetailsOpenReviews();
        String typePlace = "";
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            OpenItem openItem = new OpenItem();
            if (result.has("types")) {
                JSONArray array = result.getJSONArray("types");
                for (int idx = 0; idx < array.length(); idx++) {
                    if (idx == array.length() - 1) {
                        typePlace += array.get(idx);
                    } else {
                        typePlace += array.get(idx) + ", ";
                    }
                }
            }
            if (result.has("opening_hours")) {
                JSONObject open = result.getJSONObject("opening_hours");
                if (open.has("open_now")) {
                    String isOpen = open.getString("open_now");
                    openItem.setOpen(isOpen);
                } else {
                    openItem.setOpen("no info");
                }
                if (open.has("weekday_text")) {
                    JSONArray array = open.getJSONArray("weekday_text");
                    String monday = (String) array.get(0);
                    hashMapDays.put("Monday", monday);
                    String tuesday = (String) array.get(1);
                    hashMapDays.put("Tuesday", tuesday);
                    String wednsday = (String) array.get(2);
                    hashMapDays.put("Wednesday", wednsday);
                    String thursday = (String) array.get(3);
                    hashMapDays.put("Thursday", thursday);
                    String friday = (String) array.get(4);
                    hashMapDays.put("Friday", friday);
                    String saturday = (String) array.get(5);
                    hashMapDays.put("Saturday", saturday);
                    String sunday = (String) array.get(6);
                    hashMapDays.put("Sunday", sunday);
                }
                openItem.setDays(hashMapDays);
            } else {
                openItem.setOpen("no info");
            }
            if (result.has("reviews")) {
                JSONArray reviewsArray = result.getJSONArray("reviews");
                int min = Math.min(3, reviewsArray.length());
                for (int j = 0; j < reviewsArray.length(); j++) {
                    Review review = new Review();
                    JSONObject singleReview = reviewsArray.getJSONObject(j);
                    String author = "", text = "", rating = "", time = "", profil_photo = "";
                    if (singleReview.has("author_name")) {
                        author = singleReview.getString("author_name");
                    } else {
                        author = "no author";
                    }
                    review.setAuthor_name(author);
                    if (singleReview.has("text")) {
                        text = singleReview.getString("text");
                    } else {
                        text = "no comment";
                    }
                    review.setTextAuthor(text);
                    if (singleReview.has("rating")) {
                        rating = singleReview.getString("rating");
                    } else {
                        rating = "no rating";
                    }
                    review.setRating(rating);
                    if (singleReview.has("time")) {
                        time = singleReview.getString("time");
                    } else {
                        time = "";
                    }
                    review.setTime(time);
                    if (singleReview.has("profile_photo_url")) {
                        profil_photo = "https:" + singleReview.getString("profile_photo_url");
                    } else {
                        profil_photo = "";
                    }
                    review.setProfil_photo(profil_photo);
                    listReviews.add(review);
                }
            }
            detailsOpenReviews.setTypePlace(typePlace);
            detailsOpenReviews.setItem(openItem);
            detailsOpenReviews.setReviews(listReviews);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detailsOpenReviews;
    }
}

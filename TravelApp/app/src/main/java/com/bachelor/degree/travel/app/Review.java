package com.bachelor.degree.travel.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Andreea on 8/22/2016.
 */
public class Review {
    String author_name;
    String profil_photo_url;
    String textAuthor;
    String rating;
    String time;
    Bitmap profil_photo_icon = null;

    public void setAuthor_name(String author) {
        this.author_name = author;
    }

    public void setProfil_photo(String profil_photo) {

        this.profil_photo_url = profil_photo;
        URL urlPhoto = null;
        try {
            urlPhoto = new URL(this.profil_photo_url);
            profil_photo_icon = BitmapFactory.decodeStream(urlPhoto.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTextAuthor(String text) {
        this.textAuthor = text;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor_name() {
        return this.author_name;
    }

    public Bitmap getProfil_photo_icon() {
        return this.profil_photo_icon;
    }

    public String getTextAuthor() {
        return this.textAuthor;
    }
}

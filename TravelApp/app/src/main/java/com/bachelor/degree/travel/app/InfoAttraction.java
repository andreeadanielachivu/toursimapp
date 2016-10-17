package com.bachelor.degree.travel.app;

import android.graphics.Bitmap;

/**
 * Created by Andreea on 7/20/2016.
 */
public class InfoAttraction {
    private String text = "";
    private Bitmap image = null;

    public InfoAttraction(String text, Bitmap image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return this.text;
    }

    public Bitmap getImage() {
        return this.image;
    }
}

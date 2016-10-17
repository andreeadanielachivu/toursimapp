package com.bachelor.degree.travel.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreea on 7/16/2016.
 */
public class ManageDataTask extends AsyncTask<String, Void, InfoAttraction> {

    private DetailedAttractionActivity activity;

    public ManageDataTask(DetailedAttractionActivity myactivity) {
        super();
        this.activity = myactivity;
    }

    @Override
    protected InfoAttraction doInBackground(String... params) {
        // For storing data from web service
        String data = "";
        InfoAttraction info = null;
        InfoAttraction details = null;
        try {
            // Fetching the data from web service
            org.w3c.dom.Document wikiData = new DetailsJsonParser().getXMLfromUrl(params[1]);
            details = getInfoTextPicture(wikiData);
            if (details != null) {
                return  details;
            } else {
                wikiData = new DetailsJsonParser().getXMLfromUrl(params[0]);
                details = getInfoTextPicture(wikiData);
                if (details != null) {
                    return details;
                }

            }
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(InfoAttraction s) {
        InfoAttraction data = null;
        //sometimes here is a crash
        if (s != null) {
            data = new InfoAttraction(s.getText(), s.getImage());
        }
        //activity.refreshGuiData(s);
        //activity.setImage(s.getImage());
        if (data != null) {
            activity.setDataPicture(data);
        }
    }

    public InfoAttraction getInfoTextPicture(org.w3c.dom.Document wiki) {
        String data = "";
        InfoAttraction info = null;
         try{
            if (wiki != null) {
                NodeList list_search = wiki.getChildNodes();
                int len_root = list_search.getLength();
                if (len_root > 0) {
                    Node node = list_search.item(0);// this should be query
                    NodeList list_elements = node.getChildNodes();
                    if (list_elements.getLength() > 0) {
                        Node section = list_elements.item(1);//section
                        if (section.hasChildNodes()) {
                            NodeList dataitem = section.getChildNodes();
                            Node iteminfo = dataitem.item(0);//get the item node
                            NodeList child_items = iteminfo.getChildNodes();
                            Node title = child_items.item(0);//text
                            String text_title = title.getTextContent();
                            Node url = child_items.item(1);//url
                            String url_text = url.getTextContent();
                            Node description = child_items.item(2);//description
                            String description_text= description.getTextContent();
                            Node image_link = child_items.item(3);
                            String uri_link = image_link.getAttributes().getNamedItem("source").getNodeValue();
                            String pattern = "[0-9]*px";
                            String urlImg = "";
                            // Create a Pattern object
                            Pattern r = Pattern.compile(pattern);

                            // Now create matcher object.
                            Matcher m = r.matcher(uri_link);
                            if (m.find( )) {
                                String dim = m.group();
                                int indexPx = dim.indexOf("px");
                                int size = Integer.parseInt(dim.substring(0, indexPx)) * 4;
                                int findPx = uri_link.indexOf(dim);
                                String begin_url_image = uri_link.substring(0, findPx);
                                String end_url_image = uri_link.substring(findPx + dim.length());
                                urlImg = begin_url_image + String.valueOf(size) + "px" + end_url_image;
                            }
                            Bitmap mIcon11 = null;
                            try {
                                InputStream in = new java.net.URL(urlImg).openStream();
                                mIcon11 = BitmapFactory.decodeStream(in);
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }

                            data = text_title + "\n" + description_text + "\n" + url_text;
                            info = new InfoAttraction(data, mIcon11);
                        } else {
                            return null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }

        return info;
    }

}
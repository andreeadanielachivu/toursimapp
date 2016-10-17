package com.bachelor.degree.travel.app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * Created by Andreea on 6/19/2016.
 */
public class CrawlWebPageTask extends AsyncTask<String, Void, String> {

    private Context context;

    public CrawlWebPageTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String word = params[0];
        Log.d("PARAM PROVINCE", "param is: " + word);
        Spider spider = new Spider(this.context);
        //spider.search("https://ro.wikipedia.org/wiki/List%C4%83_de_castele_din_Rom%C3%A2nia", "Bucure");
        return word;
    }
}

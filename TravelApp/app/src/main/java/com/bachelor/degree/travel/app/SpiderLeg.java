package com.bachelor.degree.travel.app;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreea on 6/26/2016.
 */
public class SpiderLeg {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;
    private static Context context;

    public SpiderLeg(Context context) {
        this.context = context;
    }

    public boolean crawl(String url)
    {
        try
        {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
            // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
                //Log.d("LINK MAIN:", "link name:" + link.toString());
                this.links.add(link.absUrl("href"));
            }
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }

    public boolean searchForWord(String searchWord)
    {
        // Defensive coding. This method should only be used after a successful crawl.
        if(this.htmlDocument == null)
        {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = this.htmlDocument.body().text();
        System.out.println("BODY TEXT: " + bodyText);
        System.out.println("FUNCTION:" + getWholePage());
        String pattern = "^castel(.*)\\s(in|din)\\s" + searchWord.toLowerCase();
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(bodyText.toLowerCase());
        if (m.find()) {
            System.out.println("FIND THE TEXT " + m.group(0));
            return true;
        } else {
            return false;
        }
        //return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }


    public List<String> getLinks()
    {
        return this.links;
    }

    public static String getWholePage() {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("https://ro.wikipedia.org/wiki/List%C4%83_de_castele_din_Rom%C3%A2nia");
        HttpResponse response = null;
        InputStream in = null;
        OutputStreamWriter outputStreamWriter = null;
        String html = "";
        try {
            response = client.execute(request);
            in = response.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            Log.d("GET_WHOLE", "here write file");
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput("testfile.txt", Context.MODE_PRIVATE));
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }



        StringBuilder str = new StringBuilder();
        String line = null;
        try {
            while((line = reader.readLine()) != null)
            {
                str.append(line);
                outputStreamWriter.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return Jsoup.parse(str).text();
        html = str.toString();
        return html;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}

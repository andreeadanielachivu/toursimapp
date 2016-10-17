package com.bachelor.degree.travel.app;

import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.text.Html;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreea on 7/27/2016.
 */
public class CrawlTopCastels extends AsyncTask<String, Void, InfoAttraction> {
    private HashMap<String, Integer> topCastels = null;
    private ArrayList<String> tops = null;
    private HashMap<String, String> prices_programs = null;
    private Element data = null;

    public CrawlTopCastels() {
        topCastels = new HashMap<String, Integer>();
        tops = new ArrayList<String>();
    }

    @Override
    protected InfoAttraction doInBackground(String... params) {
        int nrTop = 3;
        int nr = 0;
        Set<String> first_list = getListCastels(params[0]);
        Set<String> second_list = getListCastels(params[1]);
        Set<String> third_list = getListCastels(params[2]);
        ArrayList<Set<String>> top = new ArrayList<Set<String>>();
        top.add(first_list);
        top.add(second_list);
        top.add(third_list);
        topCastels = getTopCastels(top);
        int maxValue = Collections.max(topCastels.values());
        for(Map.Entry<String,Integer> entry : topCastels.entrySet()) {
            if (entry.getValue() == maxValue && nr != nrTop) {
                tops.add(entry.getKey());
                nr++;
            } else {
                if (nr == nrTop) {
                    break;
                }
            }
        }
        getDataTopCastels(tops);
        return null;
    }

    public void getDataTopCastels(ArrayList<String> top) {
            Elements h3tags = data.getElementsByTag("h3");
            for (Element e: h3tags) {
                String text = e.text();
                for (int i = 0; i < top.size(); i++) {
                    if (text.contains(top.get(i))) {
                        Element url_sibling = e.nextElementSibling();
                        //get link picture
                        Element url_img = url_sibling.select("a").first();
                        // Get the URL here
                        String urlImage = url_img.attr("href");
                        Element paragraph = url_sibling.nextElementSibling();
                        String textParagraph = paragraph.text();
                        Element next_sibling = paragraph.nextElementSibling();
                        while (!next_sibling.tagName().equals("div")) {
                            /*if (next_sibling.child(0).tagName().equals("strong")) {
                                String data = next_sibling.text();
                            }*/
                            int nrChild = next_sibling.children().size();
                            if (nrChild != 0) {
                                String tag = next_sibling.child(0).tagName();
                                String data = next_sibling.text();
                                String child = next_sibling.child(0).text();
                                String data1 = next_sibling.ownText();
                                String child1 = next_sibling.child(0).ownText();
                                System.out.println("endcvvcbc");
                            }
                            next_sibling = next_sibling.nextElementSibling();
                        }
                    }
                }
            }
    }

    public HashMap<String, Integer> getTopCastels(ArrayList<Set<String>> castels) {
        int value = 1;
        for (int j = 0; j < castels.size(); j++) {
            Set<String> castel = castels.get(j);
            for (String c : castel) {
                if (topCastels.containsKey(c)) {
                    topCastels.put(c, topCastels.get(c) + 1);
                } else {
                    topCastels.put(c, value);
                }
            }
        }
        return topCastels;
    }

    public Set<String> getListCastels(String url) {
        Document htmlDocument = null;
        Connection connection = Jsoup.connect(url);
        Set<String> castels = new HashSet<String>();
        try {
            htmlDocument = connection.get();
            if (url.equals(Constants.TOP_CASTEL1)) {
                data = htmlDocument.body();
            }

            String bodyText = htmlDocument.body().text();
            /*htmlDocument.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
            htmlDocument.select("br").append("\\n");
            htmlDocument.select("p").prepend("\\n\\n");
            String s = htmlDocument.html().replaceAll("\\\\n", "\n");
            String bodyText = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
            */
            String pattern = "(([0-9]+\\.[\\s]*Castelul)|(Castelul))[\\s]*[A-Z]+[^,.\\s]+" ;
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(bodyText);
            while (m.find()) {
                //System.out.println("WHOLE group " + m.group());
                String castel = m.group();
                int id = castel.indexOf("Castelul");
                castels.add(castel.substring(id));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return castels;
    }
}

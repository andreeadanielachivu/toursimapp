package com.bachelor.degree.travel.app;

import android.content.Context;
import android.util.Log;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Andreea on 6/26/2016.
 */
public class Spider {
    private static final int MAX_PAGES_TO_VISIT = 10;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
    private Context context;

    public Spider(Context context) {
        this.context = context;
    }

    private String nextUrl() {
        String nextUrl;
        nextUrl = this.pagesToVisit.remove(0);
        while(this.pagesVisited.contains(nextUrl)) {
            nextUrl = this.pagesToVisit.remove(0);
        }
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }

    public void search(String url, String searchWord)
    {
        while(this.pagesVisited.size() < MAX_PAGES_TO_VISIT)
        {
            String currentUrl;
            SpiderLeg leg = new SpiderLeg(context);
            if(this.pagesToVisit.isEmpty())
            {
                currentUrl = url;
                this.pagesVisited.add(url);
            }
            else
            {
                currentUrl = this.nextUrl();
            }
            leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
            Log.d("SITE  CRAWLED: " , "name site: " + currentUrl);
            // SpiderLeg
            boolean success = leg.searchForWord(searchWord);
            if(success)
            {
                System.out.println(String.format("**Success** Word %s found at %s", searchWord, currentUrl));
                Log.d("SUCCESS", "find a match: " + currentUrl);
                break;
            }
            this.pagesToVisit.addAll(leg.getLinks());
            //Log.d("LINKS:", "links from list: " + this.pagesToVisit.toString());
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }
}

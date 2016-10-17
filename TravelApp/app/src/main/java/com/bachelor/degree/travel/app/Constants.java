package com.bachelor.degree.travel.app;

/**
 * Created by Andreea on 6/7/2016.
 */
public class Constants {
    // Server user login url
    public static final String BASE_URL = "http://192.168.43.231/";
    public static final String URL_LOGIN = BASE_URL + "android_login_api/login.php";
    public static final String URL_HISTORY = BASE_URL + "android_login_api/history.php";

    // Server user register url
    public static final String URL_REGISTER = BASE_URL + "android_login_api/register.php";
    public static final String URL_LOGINFB = BASE_URL + "android_login_api/loginFb.php";
    public static final String URL_AIRPORTS = BASE_URL + "android_flights_api/airportsRoumania.php";
    public static final String URL_FLIGHTS = BASE_URL + "android_flights_api/QPXExpressGoogleFlights.php";

    //  Castel
    public final static String CASTEL = "castel";

    // request code for castel
    public final static int CASTEL_REQUEST_CODE = 1;

    //  word to search
    public final static String WORD_TO_SEARCH = "search";

    public final static String AREA = "Province:";

    public final static String COUNTRY ="Country";

    public final static String CASTELS_CRAWLER = "https://ro.wikipedia.org/wiki/";
    public final static String NAME_ATTRACTION = "name_attraction";
    public final static String JSON_DATA_REQEUST= "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
    //link for get place id => address and serverkey
    public final static String MAPS_BY_ADDRESS= "https://maps.googleapis.com/maps/api/geocode/json?address=";
    //get details : request place id and server key
    public final static String DETAILS_DATA = "https://maps.googleapis.com/maps/api/place/details/json?";
    //autocomplete
    public final static String AUTOCOMPLETE_DATA = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    public final static String SERVER_KEY = "AIzaSyCKML6Gaqp-ztsOo1DV5A6GtH4eu5U7BSM";
    public final static String PLACE_ID = "place_id";
    public final static String WIKI_ENGLISH ="https://en.wikipedia.org/w/api.php?";
    public final static String WIKI_ROMANIAN = "https://ro.wikipedia.org/w/api.php?";
    public final static String NEARBYPLACES = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public final static String TEXTSEARCH = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    public final static String TRANSPORTSEARCH = "FLIGHT";

    public final static String DIRECTIONS= "https://maps.googleapis.com/maps/api/directions/";

    public final static String TOP_CASTEL1 = "http://www.tuktuk.ro/2014/11/top-10-cele-mai-frumoase-castele-din-romania/";
    public final static String TOP_CASTEL2 = "http://greatnews.ro/top-10-cele-mai-frumoase-castele-din-romania/";
    public final static String TOP_CASTEL3 = "http://www.kudika.ro/articol/special/35443/cele-mai-frumoase-palate-si-castele-din-romania.html";
    public final static String TOP_CASTEL4 = "http://www.ghiduri-turistice.info/ghid-turistic-descopera-romania-5-castele-pe-care-trebuie-sa-le-vizitezi";
    public final static String TOP_CASTEL5 = "http://metropotam.ro/La-zi/cele-mai-frumoase-castele-din-romania-art4892371427/";
    public final static String FROM_AIRPORT = "FROM_AIRPORT_NAME";
    public final static String FROM_AIRPORT_CODE = "FROM_AIRPORT_CODE";
    public final static String TO_AIRPORT = "TO_AIRPORT_NAME";
    public final static String TO_AIRPORT_CODE = "TO_AIRPORT_CODE";
    public final static String DATAFROM = "DEPARTURE_TIME";
    public final static String DATATO = "ARRIVAL_TIME";

    public final static String PLACEID = "placeid";
    public final static String DATAPLACE = "dataPlace";
    public final static String LATLNG = "latLng";
    public final static String CATEGORY = "category";
    public final static String HASHMAPHISTORY = "hashmapHistory";
}

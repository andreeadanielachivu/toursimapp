package com.bachelor.degree.travel.app;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.plus.Plus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DetailedAttractionActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private TextView info;
    private GoogleMap map;
    ArrayList<LatLng> markerPoints;
    private ImageView transparentImageView;
    private ImageView imageAttraction;
    private ScrollView mainScrollView;
    private LatLng origin;
    private LatLng dest;
    private String name_attraction;
    private TextView program;
    private String placeId;
    private TextView website;
    private String travelModeItem;
    private List<Bitmap> imagesSlideShow = null;
    //private TextView nearbyPlaces;
    private TextView[] days = new TextView[7];
    private TextView[] hours = new TextView[7];
    private GridLayout gridLayout;
    private Spinner spinnerTypes;
    private Spinner spinnerSize;
    private Spinner spinnerTravelMode;
    private TextView dataTravelMode;
    private String dataRoute;
    private String typeItem;
    private String sizeItem;
    private Button okBtnFilter;
    private TableLayout t1;
    private ImageView left;
    private ImageView right;
    private Button morePhoto;
    private GoogleApiClient mGoogleApiClient;
    private Button showReviewsBtn;
    private List<Review> reviewsComments;
    private TextView sugestionAround;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;
    protected LocationManager locationManager;
    private SessionManager sessionManager;
    Map<String, String> jsonParams;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_attraction);

        sessionManager = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        jsonParams = new HashMap<String, String>();
        imagesSlideShow = new ArrayList<Bitmap>();
        info = (TextView) findViewById(R.id.idDescription);
        imageAttraction = (ImageView) findViewById(R.id.idAttraction);
        transparentImageView = (ImageView) findViewById(R.id.transparent_imageDetailedPage);
        mainScrollView = (ScrollView) findViewById(R.id.idscrollViewDetailedPage);
        program = (TextView)findViewById(R.id.idProgram);
        website = (TextView)findViewById(R.id.website);
        t1 = (TableLayout) findViewById(R.id.main_table);
        //initTable();
        left = (ImageView)findViewById(R.id.arrowLeftPlace);
        right = (ImageView)findViewById(R.id.arrowRightPlace);
        morePhoto = (Button)findViewById(R.id.morePhotos);
        morePhoto.setOnClickListener(this);
        showReviewsBtn = (Button)findViewById(R.id.idReviewsBtn);
        showReviewsBtn.setOnClickListener(this);
        sugestionAround = (TextView)findViewById(R.id.sugestionAround);
        sugestionAround.setVisibility(View.GONE);

        //get data for grid layout
        days[0] = (TextView)findViewById(R.id.idMonday);
        hours[0] = (TextView)findViewById(R.id.idMondayProgram);
        days[1] = (TextView)findViewById(R.id.idTuesday);
        hours[1] = (TextView)findViewById(R.id.idTuesdayProgram);
        days[2] = (TextView)findViewById(R.id.idWendsday);
        hours[2] = (TextView)findViewById(R.id.idWendsdayProgram);
        days[3] = (TextView)findViewById(R.id.idTuesday);
        hours[3] = (TextView)findViewById(R.id.idTuesdayProgram);
        days[4] = (TextView)findViewById(R.id.idFriday);
        hours[4] = (TextView)findViewById(R.id.idFridayProgram);
        days[5] = (TextView)findViewById(R.id.idSaturday);
        hours[5] = (TextView)findViewById(R.id.idSaturdayProgram);
        days[6] = (TextView)findViewById(R.id.idSunday);
        hours[6] = (TextView)findViewById(R.id.idSundayProgram);

        gridLayout = (GridLayout)findViewById(R.id.displayProgram);
        okBtnFilter = (Button) findViewById(R.id.okBtnFilter);
        dataTravelMode = (TextView)findViewById(R.id.dataRoute);
        spinnerTravelMode = (Spinner) findViewById(R.id.mode_travel_spinner);

        okBtnFilter.setOnClickListener(this);
        //spinner for nearby places
        spinnerTypes = (Spinner) findViewById(R.id.types_spinner);
        spinnerSize = (Spinner) findViewById(R.id.size_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.types_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterSize = ArrayAdapter.createFromResource(this,
                R.array.size_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterModeTravel = ArrayAdapter.createFromResource(this,
                R.array.mode_travel_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterModeTravel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerTypes.setAdapter(adapter);
        spinnerSize.setAdapter(adapterSize);
        spinnerTravelMode.setAdapter(adapterModeTravel);
        spinnerTypes.setOnItemSelectedListener(this);
        spinnerSize.setOnItemSelectedListener(this);
        spinnerTravelMode.setOnItemSelectedListener(this);
        //nearbyPlaces = (TextView) findViewById(R.id.nearbyPlaceResult);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name_attraction = extras.getString(Constants.NAME_ATTRACTION);
            placeId = extras.getString(Constants.PLACE_ID);
            //String url = Constants.CASTELS_CRAWLER + name_attraction;

            String output = null;
            try {
                output = "action=opensearch&search=" + URLEncoder.encode(name_attraction , "UTF-8") + "&limit=1&format=xml";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url_en = Constants.WIKI_ENGLISH + output;
            String url_ro = Constants.WIKI_ROMANIAN + output;
            new ManageDataTask(DetailedAttractionActivity.this).execute(url_en, url_ro);
        }

        //replace GOOGLE MAP fragment in this Activity
        replaceMapFragment();
    }

    public void initTable() {

        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv0 = new TextView(this);
        tv0.setText("Name");
        tv0.setTextColor(Color.WHITE);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Adress");
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Open");
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        t1.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Spinner spinner = (Spinner) parent;
        if (parent.getId() == R.id.types_spinner) {
            typeItem = parent.getItemAtPosition(position).toString();
        } else if (parent.getId() == R.id.size_spinner) {
            sizeItem = parent.getItemAtPosition(position).toString();
        } else if (parent.getId() == R.id.mode_travel_spinner) {
            travelModeItem = parent.getItemAtPosition(position).toString();
            //buil url link
            if (origin != null && dest != null){
                map.clear();
                map.addMarker(new MarkerOptions().position(origin).title("I'm here!"));
                map.addMarker(new MarkerOptions().position(dest).title("I'm here!"));
                String output = "json?";
                String start_place = "origin=" + origin.latitude + "," + origin.longitude;
                String end_place = "destination=" + dest.latitude + "," + dest.longitude;
                // Sensor enabled
                String sensor = "sensor=true";
                String key = "key=" + Constants.SERVER_KEY;
                String mode_travel = "mode=" + travelModeItem;
                String urlDirections = Constants.DIRECTIONS + output + start_place + "&" + end_place + "&" +
                        sensor + "&" + mode_travel + "&" + key;
                DownloadTask task = new DownloadTask();
                task.execute(urlDirections);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void refreshGuiData(String data) {
        info.setText(data);
    }

    public void setImage(Bitmap drawable) {
        imageAttraction.setImageBitmap(drawable);
    }

    public void setDataPicture(InfoAttraction data) {
        Bitmap picture = data.getImage();
        String text = data.getText();
        if (picture != null) {
            imageAttraction.setImageBitmap(picture);
        }
        if (text != null) {
            info.setText(text);
            Linkify.addLinks(info, Linkify.WEB_URLS);
        }
       // Display display = getWindowManager().getDefaultDisplay();
       // FlowTextHelper.tryFlowText("SOMETHING", imageAttraction, info, display);
    }

    private void replaceMapFragment() {
        //map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapGoogleAttraction);
        map = mapFragment.getMap();

        // Enable Zoom
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        //set Map TYPE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //enable Current location Button
        //map.setMyLocationEnabled(true);

        if (map != null) {
            //get current location
            Location location = getLocation();
            map.clear();
            origin = new LatLng(location.getLatitude(), location.getLongitude());
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Marker marker;
            marker = map.addMarker(new MarkerOptions().position(origin).title("I'm here!"));
            GetAdditionalInfo info  = new GetAdditionalInfo();
            info.execute(placeId);
        }
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=true";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (locationManager != null) {
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return null;
                        }
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }


    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception donwload url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // The Wearable API is unavailable
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.okBtnFilter) {
            StringBuilder googlePlacesUrl = new StringBuilder(Constants.NEARBYPLACES);
            googlePlacesUrl.append("location=" + dest.latitude + "," + dest.longitude);
            int idx = sizeItem.indexOf(" ");
            int valueM = 0;
            if (idx > -1) {
                String radius_value = sizeItem.substring(0, idx);
                valueM = Integer.parseInt(radius_value) * 1000;
            }
            googlePlacesUrl.append("&radius=" + String.valueOf(valueM));
            googlePlacesUrl.append("&types=" + typeItem);
            googlePlacesUrl.append("&sensor=true");
            googlePlacesUrl.append("&key=" + Constants.SERVER_KEY);
            GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
            //clear table
            int count = t1.getChildCount();
            if (count > 0) {
                t1.removeAllViews();
            }
            initTable();
            Object[] toPass = new Object[3];
            /*int count = t1.getChildCount();
            if (count > 1) {
                t1.removeViews(1, count - 1);
            }*/
            toPass[0] = t1;
            toPass[1] = googlePlacesUrl.toString();
            toPass[2] = getApplicationContext();
            googlePlacesReadTask.execute(toPass);
            sugestionAround.setVisibility(View.VISIBLE);
        } else if (id == R.id.morePhotos) {
            placePhotosTask(placeId);
        } else if (id == R.id.idReviewsBtn) {
            displayWindow();
        }
    }

    public void displayWindow() {
        final Dialog dialog = new Dialog(DetailedAttractionActivity.this);
        dialog.setContentView(R.layout.menu_reviews);
        //set up text
        TextView textView = (TextView)dialog.findViewById(R.id.idTitle);
        textView.setText("Reviews");
        dialog.setTitle("Reviews");
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!
        TableLayout tableDialog = (TableLayout)dialog.findViewById(R.id.table_reviews);
        for (int i = 0; i < reviewsComments.size(); i++) {
            Review review = reviewsComments.get(i);
            //set the review
            addRowToReview(review, tableDialog);
        }

        //set up button
        Button button = (Button) dialog.findViewById(R.id.Button01);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    public void addRowToReview(Review review, TableLayout tableDialog) {
        String text = review.getAuthor_name() + ": ";
        text += review.getTextAuthor();
        Bitmap icon = review.getProfil_photo_icon();

        /*TableRow tbrow = new TableRow(getApplicationContext());*/

        /*tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));*/
        //tbrow0.setOrientation(LinearLayout.HORIZONTAL);
        //tbrow0.setWeightSum(10);

        TableRow tbrow0 = new TableRow(this);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView t1v = new TextView(getApplicationContext());
        t1v.setText(text);
        t1v.setMaxWidth(700);
        ImageView t2v = new ImageView(getApplicationContext());
        t2v.setAdjustViewBounds(true);
        t2v.setMaxWidth(150);
        t2v.setMaxHeight(150);
        if (icon != null) {
            t2v.setImageBitmap(icon);
        }
        //t1v.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 8));
        tbrow0.addView(t2v);
        tbrow0.addView(t1v);
        tbrow0.setPadding(5,5,5,5);
        tbrow0.setBackgroundColor(Color.LTGRAY);
        t1v.setTextColor(Color.BLACK);
        tableDialog.addView(tbrow0);
    }

    private void placePhotosTask(String placeId) {
        //final String placeId = "ChIJrTLr-GyuEmsRBfy61i59si0"; // Australian Cruise Group

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        int width = 250;
        int height = 210;
        new PhotoTask(width, height, mGoogleApiClient) {

            @Override
            protected void onPostExecute(List<AttributedPhoto> attributedPhotoList) {
                if (attributedPhotoList != null) {
                    for (int i = 0; i < attributedPhotoList.size(); i++) {
                        imagesSlideShow.add(attributedPhotoList.get(i).bitmap);
                    }
                }
                ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPageAndroidPlace);
                if (imagesSlideShow.size() > 0) {
                    imageAttraction.setVisibility(View.GONE);
                    left.setVisibility(View.VISIBLE);
                    right.setVisibility(View.VISIBLE);
                    morePhoto.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);
                    AndroidImageAdapter adapterView = new AndroidImageAdapter(getApplicationContext(), imagesSlideShow);
                    mViewPager.setAdapter(adapterView);
                }
            }
        }.execute(placeId);
    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                if (routes == null || routes.size() == 0) {
                    dataRoute = parser.getAvailable_travel_mode();
                } else {
                    dataRoute = parser.getData();
                    if (null != travelModeItem || !travelModeItem.isEmpty()) {
                        dataRoute += "Travel mode: " + travelModeItem;
                        if (travelModeItem.compareToIgnoreCase("transit") == 0) {
                            dataRoute += "\n" + parser.getDetailsRoute() + "\n";
                        }
                    } else {
                        dataRoute += "Travel mode: DRIVING";
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            //MarkerOptions markerOptions = new MarkerOptions();

            //set data about the route
            dataTravelMode.setText(dataRoute);

            if (result == null || result.size() == 0) {
                return;
            }
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(Color.BLUE);

            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(dest);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 13));
        }
    }


    private class GetAdditionalInfo extends AsyncTask<String,Void,DetailsAttraction> {
        @Override
        protected DetailsAttraction doInBackground(String... params) {
            String url = Constants.DETAILS_DATA + "placeid=" + params[0] + "&key=" + Constants.SERVER_KEY;
            jsonParams.put("placeid", params[0]);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject getAdressGeocoder = new DetailsJsonParser().getJSONfromUrl(url);
            String status = null;
            String number_international = "";
            String name = "";
            String address = "";
            String number_local = "";
            String website_url = "";
            String url_map = "";
            String types = "";
            Double rating = 0.0;
            JSONObject open_hours = null;
            JSONArray program = null;
            DetailsAttraction detailsAttraction = null;
            List<Review> items = new ArrayList<Review>();
            try {
                if (getAdressGeocoder != null) {
                    status = getAdressGeocoder.getString("status");
                    while (status.equals("OVER_QUERY_LIMIT")) {
                        Thread.sleep(2000);
                        Log.d("BUG", "Retry geocode");
                        getAdressGeocoder = new DetailsJsonParser().getJSONfromUrl(url);
                        status = getAdressGeocoder.getString("status");
                    }
                    if (status.equalsIgnoreCase("OK")) {
                        JSONObject results = getAdressGeocoder.getJSONObject("result");
                        if (results != null) {
                            if (results.has("name")) {
                                name = results.getString("name");
                                jsonParams.put("name", name);
                            } else {
                                jsonParams.put("name", "no name");
                            }
                            if (results.has("formatted_address")) {
                                address = results.getString("formatted_address");
                                jsonParams.put("address", address);
                            } else {
                                jsonParams.put("address", "no address");
                            }
                            if (results.has("international_phone_number")) {
                                number_international = results.getString("international_phone_number");
                                jsonParams.put("phone", number_international);
                            } else {
                                jsonParams.put("phone", "no phone");
                            }
                            if (results.has("formatted_phone_number"))
                            {
                                number_local = results.getString("formatted_phone_number");
                                jsonParams.put("phone", number_international);
                            } else {
                                jsonParams.put("phone", "no phone");
                            }
                            JSONObject location_coords = results.getJSONObject("geometry").getJSONObject("location");
                            double lat = Double.parseDouble(location_coords.getString("lat"));
                            double lng = Double.parseDouble(location_coords.getString("lng"));
                            jsonParams.put("latitude", String.valueOf(lat));
                            jsonParams.put("longitude", String.valueOf(lng));
                            if (results.has("opening_hours")) {
                                open_hours = results.getJSONObject("opening_hours");
                                if (open_hours.has("weekday_text")) {
                                    program = open_hours.getJSONArray("weekday_text");
                                }
                            }
                            if (open_hours != null) {
                                //boolean open = Boolean.parseBoolean(open_hours.getString("open_now"));
                                boolean open = open_hours.getBoolean("open_now");
                                detailsAttraction = new DetailsAttraction(open);
                            } else {
                                detailsAttraction = new DetailsAttraction();
                            }
                            if (results.has("rating")) {
                                rating = Double.parseDouble(results.getString("rating"));
                                jsonParams.put("rating", String.valueOf(rating));
                            } else  {
                                jsonParams.put("rating", "no rating");
                            }
                            if (results.has("types")) {
                                JSONArray types_place = results.getJSONArray("types");
                                for (int index = 0; index < types_place.length(); index++) {
                                    String one_type = (String) types_place.get(index);
                                    if (index == types_place.length() - 1) {
                                        types += one_type;
                                    } else {
                                        types += one_type + ", ";
                                    }
                                }
                                jsonParams.put("typeplace", (String) types_place.get(0));
                            } else {
                                jsonParams.put("typeplace", "no types");
                            }
                            if (results.has("website")) {
                                website_url = results.getString("website");
                                jsonParams.put("website", website_url);
                            } else {
                                jsonParams.put("website", "no website");
                            }
                            if (results.has("url")) {
                                url_map = results.getString("url");
                            }
                            // get reviews
                            if (results.has("reviews")) {
                                JSONArray reviewsArray = results.getJSONArray("reviews");
                                int min = Math.min(5, reviewsArray.length());
                                for (int i = 0; i < min; i++) {
                                    //if (reviewsArray.length() > 3) {
                                        JSONObject item = reviewsArray.getJSONObject(i);
                                        Review review = new Review();
                                        if (item.has("profile_photo_url")) {
                                            String profile_photo = item.getString("profile_photo_url");
                                            String url_profile_photo = "https:" + profile_photo;
                                            review.setProfil_photo(url_profile_photo);
                                        }
                                        if (item.has("author_name")) {
                                            review.setAuthor_name(item.getString("author_name"));
                                        }
                                        if (item.has("text")) {
                                            review.setTextAuthor(item.getString("text"));
                                        }
                                        if (item.has("time")) {
                                            review.setTime(item.getString("time"));
                                        }
                                        if (item.has("rating")) {
                                            review.setRating(item.getString("rating"));
                                        }
                                        items.add(review);
                                    //}
                                }
                            }

                            detailsAttraction.setName(name);
                            detailsAttraction.setLocalPhoneNumber(number_local);
                            detailsAttraction.setInternationalPhoneNumber(number_international);
                            detailsAttraction.setAddress(address);
                            detailsAttraction.setLocation(lat, lng);
                            detailsAttraction.setProgram(program);
                            detailsAttraction.setRating(rating);
                            detailsAttraction.setWebsite(website_url);
                            detailsAttraction.setType(types);
                            detailsAttraction.setLocationOnMap(url_map);
                            detailsAttraction.setReviews(items);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return detailsAttraction;
        }

        @Override
        protected void onPostExecute(DetailsAttraction detailsAttraction) {
            double latitude_dest = 0;
            double longitude_dest = 0;
            //here is crash.....!!!
            if (detailsAttraction != null) {
                latitude_dest = detailsAttraction.getLatitude();
                longitude_dest = detailsAttraction.getLongitude();
            }

            dest = new LatLng(latitude_dest, longitude_dest);
            Marker markerDest;
            markerDest = map.addMarker(new MarkerOptions().position(dest).title("I'm here!"));
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            program.setText(detailsAttraction.toString());
            String mywebsite = detailsAttraction.getWebsite();
            if (!mywebsite.isEmpty()) {
                website.setText("Website: " + mywebsite);
                Linkify.addLinks(website, Linkify.WEB_URLS);
            }
            JSONArray program = detailsAttraction.getProgram();
            if (program != null) {
                gridLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < 7; i++) {
                    try {
                        String value = program.get(i).toString();
                        int idx = value.indexOf(":");
                        days[i].setText(value.substring(0, idx));
                        hours[i].setText(value.substring(idx+2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            //get review an post it in GridLayout
            reviewsComments = detailsAttraction.getReviews();

            //save data in history
            if (sessionManager.isHistoryAttractionsActivate()) {
                //if history for attractions is activate => save data
                jsonParams.put("request", "save");
                // Fetching user details from sqlite
                HashMap<String, String> user = db.getUserDetails();

                String name = user.get("name");
                String email = user.get("email");
                jsonParams.put("username", name);
                jsonParams.put("email", email);
                jsonParams.put("category", "attractions");
                HistoryPlaceDetails historyPlaceDetails = new HistoryPlaceDetails(getApplicationContext());
                historyPlaceDetails.registerPlace(jsonParams);
            }
        }
    }

}

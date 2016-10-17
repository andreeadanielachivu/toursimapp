package com.bachelor.degree.travel.app;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceDetailsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private TextView detailsTextView, isOpen;
    private TableLayout tableDays, tableReviews, tableTravel;
    private TableLayout tableDialog;
    private GoogleMap map;
    private LocationManager locationManager;
    private Location location;
    private ScrollView mainScrollView;
    private ImageView transparentImage;
    double latitude;
    double longitude;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private LatLng destinationLatLng = null;
    private LatLng originLatLng = null;
    private GoogleApiClient mGoogleApiClient;
    private List<Bitmap> imagesSlideShow = null;
    private ImageView imgView, left, right;
    private String category;
    private SessionManager sessionManager;
    private SQLiteHandler db;
    private HashMap<String, String> hashMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ui_back_54);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sessionManager = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        mainScrollView = (ScrollView) findViewById(R.id.idScrollMainDetails);
        transparentImage = (ImageView)findViewById(R.id.transparent_imageDetails);
        imgView = (ImageView)findViewById(R.id.previewImg);
        left = (ImageView)findViewById(R.id.arrowLeft);
        left.setVisibility(View.GONE);
        right = (ImageView) findViewById(R.id.arrowRight);
        right.setVisibility(View.GONE);
        imagesSlideShow = new ArrayList<Bitmap>();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        //get info from Intent
        Intent intent = getIntent();
        Bundle dataIntent = intent.getExtras();
        String placeid = dataIntent.getString(Constants.PLACE_ID);
        String data = (String) dataIntent.get(Constants.DATAPLACE);
        category = dataIntent.getString(Constants.CATEGORY);
        hashMap = (HashMap<String, String>) dataIntent.getSerializable(Constants.HASHMAPHISTORY);
        hashMap.put("placeid", placeid);
        //hashMap.put("category", category);
        destinationLatLng = (LatLng)dataIntent.get(Constants.LATLNG);

        //details text view
        detailsTextView = (TextView)findViewById(R.id.idDetailsPlace);
        detailsTextView.setText(data);
        Linkify.addLinks(detailsTextView, Linkify.WEB_URLS);
        isOpen = (TextView)findViewById(R.id.idOpen);

        //table
        tableDays = (TableLayout)findViewById(R.id.table_days);
        tableReviews = (TableLayout)findViewById(R.id.table_reviews);
        tableTravel = (TableLayout) findViewById(R.id.table_modeTravel);
        //initTableHeaderDays();
        initTableModeTravel();

        //request for set opening hours and reviews
        String url = Constants.DETAILS_DATA + "placeid=" + placeid +
                "&" + "key=" + Constants.SERVER_KEY;
        GetOpeningHoursReviews moreData = new GetOpeningHoursReviews();
        Object object[] = new Object[6];
        object[0] = url;
        object[1] = tableDays;
        object[2] = tableReviews;
        object[3] = getApplicationContext();
        object[4] = isOpen;
        object[5] = PlaceDetailsActivity.this;
        moreData.execute(object);

        //set map
        transparentImage.setOnTouchListener(new View.OnTouchListener() {
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

        //set my current location
        replaceMapFragment();
        //set a marker for destination location

        //draw the lines on the map
        drawLines();

        //get photos
        placePhotosTask(placeid);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    private void placePhotosTask(String placeId) {
        //final String placeId = "ChIJrTLr-GyuEmsRBfy61i59si0"; // Australian Cruise Group

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        int width = 250;
        int height = 210;
        new PhotoTask(width, height, mGoogleApiClient) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loadin.g.
                if (category.compareTo("restaurants") == 0) {
                    imgView.setImageResource(R.drawable.restaurants);
                } else if (category.compareTo("hotels") == 0) {
                    imgView.setImageResource(R.drawable.hotelicon);
                } else {
                    imgView.setImageResource(R.drawable.neutru);
                }
            }

            @Override
            protected void onPostExecute(List<AttributedPhoto> attributedPhotoList) {
                if (attributedPhotoList != null) {
                    for (int i = 0; i < attributedPhotoList.size(); i++) {
                        imagesSlideShow.add(attributedPhotoList.get(i).bitmap);
                    }
                }
                if (imagesSlideShow.size() > 0) {
                    imgView.setVisibility(View.GONE);
                    left.setVisibility(View.VISIBLE);
                    right.setVisibility(View.VISIBLE);
                }
                ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPageAndroid);
                AndroidImageAdapter adapterView = new AndroidImageAdapter(getApplicationContext(), imagesSlideShow);
                mViewPager.setAdapter(adapterView);
            }
        }.execute(placeId);
    }

    public void drawLines() {
        map.addMarker(new MarkerOptions().position(destinationLatLng).title("I'm here!"));
        String url_request = buildUrlDirections(destinationLatLng, originLatLng);
        List<String> mode_travel = new ArrayList<String>();
        mode_travel.add("driving");
        mode_travel.add("walking");
        mode_travel.add("bicycling");
        mode_travel.add("transit");
        Object []toPass = new Object[7];
        for  (int i = 0; i < mode_travel.size(); i++) {
            DownLoadTaskUrl task = new DownLoadTaskUrl();
            String url = url_request + "&" + "mode=" + mode_travel.get(i);
            toPass[0] = url;
            toPass[1] = tableTravel;
            toPass[2] = map;
            toPass[3] = originLatLng;
            toPass[4] = destinationLatLng;
            toPass[5] = mode_travel.get(i);
            toPass[6] = getApplicationContext();
            try {
                task.execute(toPass);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String buildUrlDirections(LatLng destination, LatLng source) {
        String output = "json?";
        String start_place = "origin=" + source.latitude + "," + source.longitude;
        String end_place = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=true";
        String key = "key=" + Constants.SERVER_KEY;
        String url = Constants.DIRECTIONS + output + start_place + "&" +
                end_place + "&" + sensor + "&" + key;
        return url;
    }

    private void replaceMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapGoogleDetails);
        map = mapFragment.getMap();

        // Enable Zoom
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        //set Map TYPE
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //enable Current location Button
        map.setMyLocationEnabled(true);

        if (map != null) {
            Location location = getLocation();
            map.clear();
            originLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(originLatLng).title("I'm here!"));
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 13.5f));
        }
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


    public void displayWindow( List<Review> listReviewsListener) {
        final Dialog dialog = new Dialog(PlaceDetailsActivity.this);
        dialog.setContentView(R.layout.menu_reviews);
        //set up text
        TextView textView = (TextView)dialog.findViewById(R.id.idTitle);
        textView.setText("Reviews");
        tableDialog = (TableLayout)dialog.findViewById(R.id.table_reviews);
        dialog.setTitle("Reviews");
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //initTableHeaderReviews();
        for (int i = 0; i < listReviewsListener.size(); i++) {
            Review review = listReviewsListener.get(i);
            //set the review
            addRowToReview(review);
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

    public void addHistory() {
        String dataPlace = isOpen.getText().toString();
        int twoPoint = dataPlace.indexOf(":");
        int comma = dataPlace.indexOf(",");
        String place = "";
        if (twoPoint >0 && comma > 0) {
            place = dataPlace.substring(twoPoint+1, comma);
        }
        hashMap.put("typeplace", place.trim());
        if (sessionManager.isHistoryActivatedByCategory(category)) {
            //if history for attractions is activate => save data
            hashMap.put("request", "save");
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();

            String name = user.get("name");
            String email = user.get("email");
            hashMap.put("username", name);
            hashMap.put("email", email);
            hashMap.put("category", category);
            HistoryPlaceDetails historyPlaceDetails = new HistoryPlaceDetails(getApplicationContext());
            historyPlaceDetails.registerPlace(hashMap);
        }
    }


    public void addRowToReview(Review review) {
        String text = review.getAuthor_name()+ ": ";
        text += review.getTextAuthor();
        Bitmap icon = review.getProfil_photo_icon();

        TableRow tbrow0 = new TableRow(this);
        //tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        //tbrow0.setOrientation(LinearLayout.HORIZONTAL);
        //tbrow0.setWeightSum(10);
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

    public void initTableHeaderReviews() {
        TableRow tbrow0 = new TableRow(this);
        //tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        TextView tv0 = new TextView(this);
        tv0.setText("Reviews");
        tv0.setTextColor(Color.BLACK);
        tv0.setTextSize(10);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        tableReviews.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public void initTableModeTravel() {
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv0 = new TextView(this);
        tv0.setText("Mode");
        tv0.setTextColor(Color.WHITE);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText("Summary");
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Duration");
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText("Distance");
        tv3.setTextColor(Color.WHITE);
        tv3.setPadding(5,5,5,5);
        tbrow0.addView(tv3);
        tableTravel.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }


}

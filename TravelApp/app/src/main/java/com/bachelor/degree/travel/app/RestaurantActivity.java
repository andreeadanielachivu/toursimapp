package com.bachelor.degree.travel.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RestaurantActivity extends BaseNavigationActivity implements AsyncResponse{
    private PlaceAutocompleteFragment searchRestaurant;
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
    public static final String TAG = "TAG";
    private TableLayout tableRestaurant = null;
    private TextView textView;
    private Map<String, String> hashMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        hashMap = new HashMap<String, String>();
        mainScrollView = (ScrollView) findViewById(R.id.idscrollViewRestaurant);
        transparentImage = (ImageView)findViewById(R.id.transparent_imageRestaurant);

        textView = (TextView)findViewById(R.id.invisibleRest);
        textView.setVisibility(View.GONE);
        if (textView != null) {
            System.out.print("dgd");
        } else {
            System.out.print("dsgdgdf");
        }

        //set searching bar
        searchRestaurant = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.searchRestaurant);
        searchRestaurant.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String placeid = place.getId();
                StringBuilder sb = new StringBuilder();
                sb.append("Name: " + place.getName() + "\n");
                sb.append("Address: " + place.getAddress() + "\n");
                sb.append("Location: " + place.getLatLng().latitude + ", " + place.getLatLng().longitude + "\n");
                sb.append("Phone: " + place.getPhoneNumber() + "\n");
                sb.append("Price level: " + place.getPriceLevel() + "\n");
                sb.append("Rating: " + place.getRating() + "\n");
                //sb.append("Place type: " + place.getPlaceTypes() + "\n");
                sb.append("Webiste: " + place.getWebsiteUri() + "\n");
                hashMap.put("name", place.getName().toString());
                hashMap.put("address", place.getAddress().toString());
                if (place.getPhoneNumber() != null) {
                    hashMap.put("phone", place.getPhoneNumber().toString());
                } else {
                    hashMap.put("phone", "no phone");
                }
                hashMap.put("latitude", String.valueOf(place.getLatLng().latitude));
                hashMap.put("longitude", String.valueOf(place.getLatLng().longitude));
                hashMap.put("rating", String.valueOf(place.getRating()));
                if (place.getWebsiteUri() != null) {
                    hashMap.put("website", place.getWebsiteUri().toString());
                } else {
                    hashMap.put("website", "no website");
                }

                Intent intentdetails = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                intentdetails.putExtra(Constants.PLACE_ID, placeid);
                intentdetails.putExtra(Constants.DATAPLACE, sb.toString());
                intentdetails.putExtra(Constants.LATLNG, place.getLatLng());
                intentdetails.putExtra(Constants.CATEGORY, "restaurants");
                intentdetails.putExtra(Constants.HASHMAPHISTORY, (Serializable) hashMap);
                startActivity(intentdetails);
            }

            @Override
            public void onError(Status status) {

            }
        });

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

        replaceMapFragment();

        //initialize table
        tableRestaurant = (TableLayout)findViewById(R.id.table_restaurant);
        initHeaderTable();

        //get the admin / country
        //this to set delegate/listener back to this class
        GetProvinceCountryTask asyncTask = new GetProvinceCountryTask();
        asyncTask.delegate = this;
        Object [] toPass = new Object[3];
        toPass[0] = latitude;
        toPass[1] = longitude;
        toPass[2] = getApplicationContext();
        asyncTask.execute(toPass);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .title("Position");
                map.addMarker(marker);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f));
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                // get address by geocoding
                getAddressByGeocoding(latLng);
            }
        });
    }

    public void getAddressByGeocoding(LatLng point) {
        GetProvinceCountryTask asyncTask = new GetProvinceCountryTask();
        asyncTask.delegate = this;
        Object [] toPass = new Object[3];
        toPass[0] = point.latitude;
        toPass[1] = point.longitude;
        toPass[2] = getApplicationContext();
        asyncTask.execute(toPass);
    }


    public String getUrl() {
        String query = getQuery();
        String location = "location=" + latitude + "," + longitude;
        //String radius = "radius=50000";
        //remove restaurants type
        //String type = "type=restaurant";
        String key = "key=" + Constants.SERVER_KEY;
        String url = Constants.TEXTSEARCH + query + "&" + location + "&" + key;
        return url;
    }

    public String getQuery() {
        String query_data = "restaurants in ";
        String query = "query=";
        String areaToParse = "";
        String adminarea = "";
        String country = "";
        if (textView.getText().length() != 0) {
            areaToParse = textView.getText().toString();
            System.out.print("Trying to get text");
        }
        int idPipe = areaToParse.indexOf("|");
        if (idPipe >= 0) {
            adminarea = areaToParse.substring(0, idPipe);
            country = areaToParse.substring(idPipe+1);
        }
        try {
            if (!adminarea.isEmpty()) {
                query_data += adminarea;
                query += URLEncoder.encode(query_data, "UTF-8");
            } else if (!country.isEmpty()) {
                query_data += country;
                query += URLEncoder.encode(query_data, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return query;
    }


    private void replaceMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapGoogleRestaurant);
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
            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(origin).title("I'm here!"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 13.5f));
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

    public void initHeaderTable() {
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
        tv1.setText("Address");
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Open");
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        tableRestaurant.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void processFinish(HashMap<String, String> output) {
        String adminarea = output.get("adminarea");
        String country = output.get("country");
        textView.setText(adminarea+"|"+country);
        //clean the table
        int count = tableRestaurant.getChildCount();
        if (count > 1) {
            tableRestaurant.removeViews(1, count - 1);
        }
        GetNearRestaurants restaurants = new GetNearRestaurants();
        Object []objRestaurant = new Object[3];
        String url = getUrl();
        objRestaurant[0] = url;
        objRestaurant[1] = tableRestaurant;
        objRestaurant[2] = getApplicationContext();
        restaurants.execute(objRestaurant);
    }

    @Override
    protected int getNavigationItemID() {
        return R.id.restaurantItem;
    }
}

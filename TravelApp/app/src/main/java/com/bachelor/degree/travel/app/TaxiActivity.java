package com.bachelor.degree.travel.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TaxiActivity extends AppCompatActivity {
    private PlaceAutocompleteFragment searchTaxi;
    private GoogleMap map;
    public static final String TAG = "TAG";
    private LocationManager locationManager;
    Location location;
    double latitude;
    double longitude;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private TextView invisbleTextView;
    private ImageView transparentImageView = null;
    private ScrollView mainScrollView = null;

    private TableLayout tableTaxi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi);


        //autocomplete bar
        searchTaxi = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.addressTaxi);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        searchTaxi.setFilter(typeFilter);
        searchTaxi.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place source: " + place.getName());
                //update the map
                LatLng latLng = place.getLatLng();
                map.clear();
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .title("Position");
                map.addMarker(marker);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.5f));

                getAddressByGeocoding(latLng);
                //build url
                String url = buildUrlForDetails(latLng);
                //clean the table
                int count = tableTaxi.getChildCount();
                if (count > 1) {
                    tableTaxi.removeViews(1, count - 1);
                }
                GetNearTaxiTask taxi = new GetNearTaxiTask();
                Object []obj = new Object[3];
                obj[0] = url;
                obj[1] = tableTaxi;
                obj[2] = getApplicationContext();
                taxi.execute(obj);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        mainScrollView = (ScrollView)findViewById(R.id.idscrollView);
        transparentImageView = (ImageView)findViewById(R.id.transparent_image);

        //map image
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

        //map location
        replaceMapFragment();

        //table layout
        tableTaxi = (TableLayout)findViewById(R.id.table_taxi);
        initHeaderTable();

        Intent intent = getIntent();
        String url = intent.getStringExtra(Constants.TRANSPORTSEARCH);
        GetNearTaxiTask taxi = new GetNearTaxiTask();
        Object []obj = new Object[3];
        obj[0] = url;
        obj[1] = tableTaxi;
        obj[2] = getApplicationContext();
        taxi.execute(obj);

        //invisble texview
        invisbleTextView = (TextView)findViewById(R.id.textInvisible);
        invisbleTextView.setVisibility(View.GONE);

        //on map click
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.clear();
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title("Position");
                map.addMarker(marker);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 13.5f));

                // get address by geocoding
                getAddressByGeocoding(point);

                //build url
                String url = buildUrlForDetails(point);

                //clean the table
                int count = tableTaxi.getChildCount();
                if (count > 1) {
                    tableTaxi.removeViews(1, count - 1);
                }
                GetNearTaxiTask taxi = new GetNearTaxiTask();
                Object []obj = new Object[3];
                obj[0] = url;
                obj[1] = tableTaxi;
                obj[2] = getApplicationContext();
                taxi.execute(obj);
            }
        });
    }

    public String buildUrlForDetails(LatLng point) {
        String query = getProvinceOrCountry();
        String location = "location=" + point.latitude + "," + point.longitude;
        String type = "type=taxi_stand";
        String name = "name=taxi";
        String key = "key=" + Constants.SERVER_KEY;
        String url = Constants.TEXTSEARCH + query + "&" + location + "&" +
                type + "&" + name + "&" + key;

        return url;
    }

    public void getAddressByGeocoding(LatLng point) {
        Object [] toPass = new Object[5];
        toPass[0] = point.latitude;
        toPass[1] = point.longitude;
        toPass[2] = searchTaxi;
        toPass[3] = getApplicationContext();
        toPass[4] = invisbleTextView;
        GetAddressPlaceTask task = new GetAddressPlaceTask();
        task.execute(toPass);
    }

    public String getProvinceOrCountry() {
        String areaToParse = "";
        String adminarea = "";
        String country = "";
        String query_data = "taxi in ";
        String query = "query=";
        if (invisbleTextView.getText().length() != 0) {
            areaToParse = invisbleTextView.getText().toString();
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

    public void replaceMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapGoogleTaxi);
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
        tv2.setText("Phone");
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText("Website");
        tv3.setTextColor(Color.WHITE);
        tv3.setPadding(5,5,5,5);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText("Rating");
        tv4.setTextColor(Color.WHITE);
        tv4.setPadding(5,5,5,5);
        tbrow0.addView(tv4);
        tableTaxi.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

}

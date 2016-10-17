package com.bachelor.degree.travel.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

public class TransportActivity extends BaseNavigationActivity implements AdapterView.OnItemSelectedListener {
    private GoogleMap map;
    Location location;
    private ScrollView mainScrollView;
    double latitude;
    double longitude;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    public static final String TAG = "TAG";
    private LatLng sourceLatLng = null;
    private LatLng destLatLng = null;
    private TextView start_endPoints = null;
    private TableLayout tableTransport = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        mainScrollView = (ScrollView) findViewById(R.id.idscrollViewTransport);
        ImageView transparentImage = (ImageView) findViewById(R.id.transparent_imageTransport);
        start_endPoints = (TextView) findViewById(R.id.start_endPoints);
        //noinspection ConstantConditions
        start_endPoints.setVisibility(View.GONE);
        tableTransport = (TableLayout) findViewById(R.id.table_transport);
        //initTable();
        Spinner transportWays = (Spinner) findViewById(R.id.transport_way_spinner);
        PlaceAutocompleteFragment source = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.source);
        PlaceAutocompleteFragment destination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.destination);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        source.setFilter(typeFilter);
        destination.setFilter(typeFilter);
        source.setHint("From here...");
        destination.setHint("To destination...");

        //noinspection ConstantConditions
        transportWays.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTransport = ArrayAdapter.createFromResource(this,
                R.array.transport_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTransport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        transportWays.setAdapter(adapterTransport);
        //noinspection ConstantConditions
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
        Object [] toPass = new Object[5];
        toPass[0] = latitude;
        toPass[1] = longitude;
        toPass[2] = source;
        toPass[3] = getApplicationContext();
        toPass[4] = start_endPoints;
        GetAddressPlaceTask task = new GetAddressPlaceTask();
        task.execute(toPass);

        source.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place source: " + place.getName());
                sourceLatLng = place.getLatLng();
                map.clear();
                map.addMarker(new MarkerOptions().position(sourceLatLng).title("I'm here!"));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        destination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                map.clear();
                setMyCurrentLocation();

                Log.i(TAG, "Place destination: " + place.getName());
                destLatLng = place.getLatLng();
                map.addMarker(new MarkerOptions().position(destLatLng).title("I'm here!"));
                LatLng fromHereSource;
                if (null == sourceLatLng) {
                    fromHereSource = new LatLng(latitude, longitude);
                } else {
                    fromHereSource = sourceLatLng;
                }
                String url_request = buildUrlDirections(destLatLng, fromHereSource);
                List<String> mode_travel = new ArrayList<>();
                mode_travel.add("driving");
                mode_travel.add("walking");
                mode_travel.add("bicycling");
                mode_travel.add("transit");
                Object []toPass = new Object[7];
                if (tableTransport.getChildCount() > 0) {
                    tableTransport.removeAllViews();
                }
                initTable();
                for  (int i = 0; i < mode_travel.size(); i++) {
                    DownLoadTaskUrl task = new DownLoadTaskUrl();
                    String url = url_request + "&" + "mode=" + mode_travel.get(i);
                    toPass[0] = url;
                    toPass[1] = tableTransport;
                    toPass[2] = map;
                    toPass[3] = fromHereSource;
                    toPass[4] = destLatLng;
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

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    public String buildUrlDirections(LatLng destination, LatLng source) {
        String output = "json?";
        String start_place = "origin=" + source.latitude + "," + source.longitude;
        String end_place = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=true";
        String key = "key=" + Constants.SERVER_KEY;
        return Constants.DIRECTIONS + output + start_place + "&" +
                    end_place + "&" + sensor + "&" + key;
    }

    private void replaceMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapGoogleTransport);
        //noinspection deprecation
        map = mapFragment.getMap();

        // Enable Zoom
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        //set Map TYPE
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //enable Current location Button
        map.setMyLocationEnabled(true);

//        if (map != null) {
//            Location location = getLocation();
//            map.clear();
//            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
//            map.addMarker(new MarkerOptions().position(origin).title("I'm here!"));
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 13.5f));
//        }
        setMyCurrentLocation();
    }

    private void setMyCurrentLocation() {
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
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //noinspection StatementWithEmptyBody
            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    //noinspection ConstantConditions
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
                        //noinspection ConstantConditions
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

    public void initTable() {
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv0 = new TextView(this);
        tv0.setText(R.string.mode);
        tv0.setTextColor(Color.WHITE);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(R.string.summary);
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(R.string.duration);
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(R.string.distance);
        tv3.setTextColor(Color.WHITE);
        tv3.setPadding(5,5,5,5);
        tbrow0.addView(tv3);
        tableTransport.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner item = (Spinner) parent;
        if (item.getId() == R.id.transport_way_spinner) {
            String item_transport = parent.getItemAtPosition(position).toString();
            if (item_transport.compareToIgnoreCase("flights") == 0) {
                //open a new activity for Flights
                Intent intentFlight = new Intent(getApplicationContext(), FlightActivity.class);
                String query_data = "airports in ";
                String query = "query=";
                String areaToParse = "";
                String adminarea = "";
                String country = "";
                if (start_endPoints.getText().length() != 0) {
                    areaToParse = start_endPoints.getText().toString();
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
                String location = "location=" + latitude + "," + longitude;
                String radius = "radius=50000";
                String type = "type=airport";
                String key = "key=" + Constants.SERVER_KEY;
                String url = Constants.TEXTSEARCH + query + "&" + location + "&" +
                        radius + "&" + type + "&" + key;
                intentFlight.putExtra(Constants.TRANSPORTSEARCH, url);
                startActivity(intentFlight);
            } else if (item_transport.compareToIgnoreCase("taxi") == 0) {
                //open a new activity for Flights
                Intent intentTaxi = new Intent(getApplicationContext(), TaxiActivity.class);
                String query_data = "taxi in ";
                String query = "query=";
                String areaToParse = "";
                String adminarea = "";
                String country = "";
                if (start_endPoints.getText().length() != 0) {
                    areaToParse = start_endPoints.getText().toString();
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
                String location = "location=" + latitude + "," + longitude;
                //String radius = "radius=50000";
                String type = "type=taxi_stand";
                String name = "name=taxi";
                String key = "key=" + Constants.SERVER_KEY;
                String url = Constants.TEXTSEARCH + query + "&" + location + "&" +
                         type + "&" + name + "&" + key;
                intentTaxi.putExtra(Constants.TRANSPORTSEARCH, url);
                startActivity(intentTaxi);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected int getNavigationItemID() {
        return R.id.transportItem;
    }
}

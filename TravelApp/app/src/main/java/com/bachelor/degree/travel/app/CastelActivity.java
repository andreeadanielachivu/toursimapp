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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class CastelActivity extends BaseNavigationActivity implements AdapterView.OnItemSelectedListener {
    @Override
    protected int getNavigationItemID() {
        return R.id.atractionItem;
    }
    private GoogleMap map;
    //private TextView locationText;
    private TextView addressText;
    ScrollView mainScrollView;
    ImageView transparentImageView;
    final static String TAG = "something";
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    //private Spinner areaAttr;
//    private double latitude_Marker;
//    private double longitude_Marker;
    private String type_item = "";

    Location location;
    double latitude;
    double longitude;
    protected LocationManager locationManager;
    private TableLayout infoAttraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_castel);
        mainScrollView = (ScrollView) findViewById(R.id.idscrollView);
        transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        Spinner typeAttr = (Spinner) findViewById(R.id.typeAttraction);
        //areaAttr = (Spinner) findViewById(R.id.areaAttraction);
        infoAttraction = (TableLayout)findViewById(R.id.table_attractions);
        initTable();

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTypeAttr = ArrayAdapter.createFromResource(this,
                R.array.type_attraction_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTypeAttr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //noinspection ConstantConditions
        typeAttr.setAdapter(adapterTypeAttr);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterAreaAttr = ArrayAdapter.createFromResource(this,
                R.array.area_attraction_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterAreaAttr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //areaAttr.setAdapter(adapterAreaAttr);

        typeAttr.setOnItemSelectedListener(this);
        //areaAttr.setOnItemSelectedListener(this);

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

        //locationText = (TextView) findViewById(R.id.location);
        addressText = (TextView) findViewById(R.id.address);
        //noinspection ConstantConditions
        addressText.setVisibility(View.GONE);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.myLocation);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                String word = place.getName().toString();
                Log.i(TAG, "Place on selected:" + word);
                Log.i(TAG, "Get id place:" + place.getId());
                Log.i(TAG, "Attr:" + place.getAttributions());
                Intent i = new Intent(getApplicationContext(),
                        DetailedAttractionActivity.class);
                i.putExtra(Constants.NAME_ATTRACTION, word);
                i.putExtra(Constants.PLACE_ID, place.getId());
                startActivity(i);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //replace GOOGLE MAP fragment in this Activity
        replaceMapFragment();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.clear();
                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title("New Marker");
                map.addMarker(marker);
//                latitude_Marker = point.latitude;
//                longitude_Marker = point.longitude;
                //locationText.setText("You are at [" + point.longitude + " ; " + point.latitude + " ]");
                new GetAddressTask(CastelActivity.this).execute(String.valueOf(point.latitude), String.valueOf(point.longitude));

            }
        });

    }

    public void initTable() {

        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.GRAY);
        tbrow0.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView tv0 = new TextView(this);
        tv0.setText(R.string.name);
        tv0.setTextColor(Color.WHITE);
        tv0.setPadding(5,5,5,5);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(R.string.address);
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(R.string.open);
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        infoAttraction.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        //mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void replaceMapFragment() {
        //map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapGoogle);
        //noinspection deprecation
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
            Location location = getLocation();
            map.clear();
            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            map.addMarker(new MarkerOptions().position(origin).title("I'm here!"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 13.5f));
            //locationText.setText("You are at [" + longitude + " ; " + latitude + " ]");
            //get current address by invoke an AsyncTask object
            Log.d("LAT and LONG in castel", "LAT: " + latitude + " LONG: " + longitude);
            new GetAddressTask(CastelActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //noinspection StatementWithEmptyBody
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

    public void
    callBackDataFromAsyncTask(String address) {
        addressText.setText(address);
        postNearbyPlaces();
    }

    public void postNearbyPlaces() {
        String nameCountry =  GetAddressTask.getCountry();
        String nameProvince = GetAddressTask.getProvince();
        String typeLocation = "";
        String nameLocation = "";
        if (type_item.compareToIgnoreCase("all") == 0) {
            typeLocation = "church|cemetery|establishment|zoo|park|museum";
        } else {
            try {
                typeLocation = URLEncoder.encode(type_item, "UTF-8") + "|establishment";
                nameLocation = "&name=" + URLEncoder.encode(type_item, "UTF-8") + "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String queryString;
        if (null != nameProvince && !nameProvince.isEmpty()) {
            queryString = "attractions in " + nameProvince;
        } else {
            queryString = "attractions in" + nameCountry;
        }
        String url_NearbyPlaces = null;
        try {
            url_NearbyPlaces = Constants.TEXTSEARCH + "query=" +  URLEncoder.encode(queryString, "UTF-8") +
                    "&location=" + location.getLatitude() + "," + location.getLongitude() +
                    "&type=" + typeLocation + nameLocation + "&key=" + Constants.SERVER_KEY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[4];
        String request_type = "textsearchCurrentLocation";
        int count = infoAttraction.getChildCount();
        if (count > 1) {
            infoAttraction.removeViews(1, count - 1);
        }
        toPass[0] = infoAttraction;
        toPass[1] = url_NearbyPlaces;
        toPass[2] = getApplicationContext();
        toPass[3] = request_type;
        googlePlacesReadTask.execute(toPass);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner)parent;
        if (spinner.getId() == R.id.typeAttraction) {
            type_item = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}

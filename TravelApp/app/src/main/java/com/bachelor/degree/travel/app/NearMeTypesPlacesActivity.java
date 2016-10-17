package com.bachelor.degree.travel.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class NearMeTypesPlacesActivity extends AppCompatActivity implements View.OnClickListener {
    private Button placeTypeBtn, radiusBtn, searchBtn;
    private TableLayout table_NearMeLocation;
    final String[] items = {" airport "," amusement_park "," atm "," bank ",
    " bar ", " beauty_salon ", " bicycle_store ", " book_store ", " bus_station ", " cafe ", " car_rental ", " cemetery ", " dentist ", " doctor ",
    " embassy ", " florist ", " funeral_home ", " gas_station ", " gym ", " home_goods_store ", " hospital ", " laundry ", " lawyer ", " meal_delivery ",
            " movie_rental ", " movie_theater ", " moving_company ", " night_club ", " museum ", " parking ", " pharmacy ", " police ", " florist ",
    " train_station ", " subway_station ", " taxi_stand ", " embassy ", " mosque ", " movie_rental ", " movie_theater "};
    final String[] radius = { " 1 km"," 3 km"," 5 km", " 7 km", " 10 km", " 20 km", " 30 km", " 40 km", " 50 km"};
    private LatLng currentLocationLatLng;
    private String radius_value = "", type_value = "", value="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me_types_places);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        currentLocationLatLng = (LatLng) bundle.get(Constants.LATLNG);

        placeTypeBtn = (Button)findViewById(R.id.idChoosePlaceBtn);
        placeTypeBtn.setOnClickListener(this);

        radiusBtn = (Button)findViewById(R.id.idChooseKmBtn);
        radiusBtn.setOnClickListener(this);

        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);

        table_NearMeLocation = (TableLayout)findViewById(R.id.table_NearMeLocation);
    }

    public String buildUrl() {
        String location = "location=" + currentLocationLatLng.latitude + "," + currentLocationLatLng.longitude;
        String key = "key=" + Constants.SERVER_KEY;
        radius_value = radius_value.substring(0, radius_value.lastIndexOf(" ")).trim();
        int radius_value_numeric = Integer.parseInt(radius_value) * 1000;
        String radius = "radius=" + String.valueOf(radius_value_numeric);
        String type = "type=" + type_value.trim();
        String url = Constants.NEARBYPLACES + location + "&" + key + "&" + radius + "&" + type;
        return url;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.idChoosePlaceBtn:
                displayMenuTypes(items, " Select a place type ", "place_type");
                Log.d("TAH", "value of type value" + type_value);
                break;
            case R.id.idChooseKmBtn:
                displayMenuTypes(radius, " Select radius ", "radius");
                Log.d("TAH", "value of radius value" + radius_value);
                break;
            case R.id.searchBtn:
                initHeaderTable();
                String url = buildUrl();
                int count = table_NearMeLocation.getChildCount();
                if (count > 1) {
                    table_NearMeLocation.removeViews(1, count - 1);
                }
                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                Object object[] = new Object[3];
                object[0] = table_NearMeLocation;
                object[1] = url;
                object[2] = getApplicationContext();
                googlePlacesReadTask.execute(object);
                break;
        }
    }

    public void displayMenuTypes(final String[] listData, String messageTitle, final String type_request) {
        // Strings to Show In Dialog with Radio Buttons
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(messageTitle);
        builder.setSingleChoiceItems(listData, -1 , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (type_request.compareToIgnoreCase("place_type") == 0) {
                    type_value = listData[item];
                } else if (type_request.compareToIgnoreCase("radius") == 0) {
                    radius_value = listData[item];
                }
                dialog.dismiss();
            }
        });
        final AlertDialog levelDialog = builder.create();
        levelDialog.show();
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
        tv1.setText("Adress");
        tv1.setTextColor(Color.WHITE);
        tv1.setPadding(5,5,5,5);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText("Open");
        tv2.setTextColor(Color.WHITE);
        tv2.setPadding(5,5,5,5);
        tbrow0.addView(tv2);
        table_NearMeLocation.addView(tbrow0, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }
}

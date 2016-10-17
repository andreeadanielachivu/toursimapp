package com.bachelor.degree.travel.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends BaseNavigationActivity {

    private Button attractionBtn;
    private Button hotelBtn;
    private Button restaurantBtn;
    private Button transportBtn;
    private Button nearMeNowBtn;
    private Button detailsAccountBtn;
    private Button appOfflineBtn;

    private ButtonListener Button_listener = new ButtonListener();
    public class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.attractions_btn:
                    Intent intentAttraction = new Intent(getApplicationContext(),
                            CastelActivity.class);
                    startActivity(intentAttraction);
                    //finish();
                    break;
                case R.id.hotels_btn:
                    Intent intentHotel = new Intent(getApplicationContext(),
                            HotelActivity.class);
                    startActivity(intentHotel);
                    break;
                case R.id.restaurants_btn:
                    Intent intentRestaurant = new Intent(getApplicationContext(),
                            RestaurantActivity.class);
                    startActivity(intentRestaurant);
                    break;
                case R.id.transport_btn:
                    Intent intentTransport = new Intent(getApplicationContext(),
                            TransportActivity.class);
                    startActivity(intentTransport);
                    //finish();
                    break;
                case R.id.near_me_now_btn:
                    Intent intentNearMe = new Intent(getApplicationContext(),
                            NearMeNowActivity.class);
                    startActivity(intentNearMe);
                    break;
                case R.id.details_account_btn:
                    Intent intentDetailsAccount = new Intent(getApplicationContext(),
                            DetailsAccountActivity.class);
                    startActivity(intentDetailsAccount);
                    break;
                case R.id.system_info_btn:
                    Intent intentAppOffline = new Intent(getApplicationContext(),
                            AppOfflineActivity.class);
                    startActivity(intentAppOffline);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //get buttons by id
        attractionBtn = (Button)findViewById(R.id.attractions_btn);
        hotelBtn = (Button)findViewById(R.id.hotels_btn);
        restaurantBtn = (Button)findViewById(R.id.restaurants_btn);
        transportBtn = (Button)findViewById(R.id.transport_btn);
        nearMeNowBtn = (Button) findViewById(R.id.near_me_now_btn);
        detailsAccountBtn = (Button) findViewById(R.id.details_account_btn);
        appOfflineBtn = (Button)findViewById(R.id.system_info_btn);

        //set listener on buttons
        attractionBtn.setOnClickListener(Button_listener);
        hotelBtn.setOnClickListener(Button_listener);
        restaurantBtn.setOnClickListener(Button_listener);
        transportBtn.setOnClickListener(Button_listener);
        nearMeNowBtn.setOnClickListener(Button_listener);
        detailsAccountBtn.setOnClickListener(Button_listener);
        appOfflineBtn.setOnClickListener(Button_listener);
    }

    @Override
    protected int getNavigationItemID() {
        return R.id.dashboardItem;
    }

}

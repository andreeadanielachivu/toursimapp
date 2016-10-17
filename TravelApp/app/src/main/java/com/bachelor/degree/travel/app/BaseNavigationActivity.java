package com.bachelor.degree.travel.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public abstract class BaseNavigationActivity extends AppCompatActivity implements DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int POST_DELAY_MS = 300;
    protected DrawerLayout mDrawerLayout;
    private Handler mHandler;
    protected NavigationView mNavigationView;
    protected Toolbar mToolbar;

    /**
     * @return the ID of the menu item representing this activity in the navigation drawer
     */
    protected abstract int getNavigationItemID();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setActionBarInfo();
        setSelectedItemNavigation(getNavigationItemID());

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        InputMethodManager imn = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imn.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        final int itemId = item.getItemId();
        if (itemId == getNavigationItemID()) {
            return true;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(itemId);
            }
        }, POST_DELAY_MS);
        return true;
    }


    private void navigate(int itemId) {
        Intent intent = null;
        switch (itemId) {
            case R.id.dashboardItem:
                finish();
                intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case R.id.restaurantItem:
                intent = new Intent(this, RestaurantActivity.class);
                break;
            case R.id.atractionItem:
                intent = new Intent(this, CastelActivity.class);
                break;
            case R.id.hotelItem:
                intent = new Intent(this, HotelActivity.class);
                break;
            case R.id.transportItem:
                intent = new Intent(this, TransportActivity.class);
                break;
            case R.id.nearmeItem:
                intent = new Intent(this, NearMeNowActivity.class);
                break;
            case R.id.accountItem:
                intent = new Intent(this, DetailsAccountActivity.class);
                break;
            case R.id.historyItem:
                intent = new Intent(this, AppOfflineActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }

        if (!(this instanceof DashboardActivity)) {
            finish();
        }
    }


    protected void setActionBarInfo() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        if (mNavigationView != null && mDrawerLayout != null) {
            mDrawerLayout.addDrawerListener(this);
            mNavigationView.setNavigationItemSelectedListener(this);
            mNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.dialog_background));
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.mipmap.ui_sidebar);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(mNavigationView);
                }
            });
//            updateHeaderMenu();
        }
        View header = mNavigationView.getHeaderView(0);
        TextView textViewEmail = (TextView) header.findViewById(R.id.nav_header_usermail);
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String email = user.get("email");
        textViewEmail.setText(email);
    }


    private void setSelectedItemNavigation(int itemID) {
        if (mNavigationView == null || mDrawerLayout == null) {
            return;
        }
        boolean status = true;

        mNavigationView.getMenu().findItem(itemID).setChecked(status);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mNavigationView != null) {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawer(mNavigationView);
                return;
            }
        }
        super.onBackPressed();
    }
}

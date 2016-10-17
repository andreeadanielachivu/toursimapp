package com.bachelor.degree.travel.app;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AttractionActivity extends AppCompatActivity {

    private Button castelBtn;
    private Button museumBtn;
    private Button monumentBtn;
    private Button monasteryBtn;
    private Button naturalBtn;

    private AttractionsListener Attraction_listener = new AttractionsListener();
    public class AttractionsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.castelBtn:
                    Intent i = new Intent(getApplicationContext(),
                            CastelActivity.class);
                    i.putExtra(Constants.CASTEL, "Castels");
                    startActivity(i);
                    //finish();
                    break;
                case R.id.museumBtn:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);
        handleIntent(getIntent());

        //get the id of the buttons
        castelBtn = (Button)findViewById(R.id.castelBtn);
        museumBtn = (Button)findViewById(R.id.museumBtn);
        monumentBtn = (Button)findViewById(R.id.monumentBtn);
        monasteryBtn = (Button)findViewById(R.id.monasteryBtn);
        naturalBtn = (Button)findViewById(R.id.naturalBtn);

        //set listener to btn
        castelBtn.setOnClickListener(Attraction_listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            Log.d("query is: ", query);
        }
    }


}
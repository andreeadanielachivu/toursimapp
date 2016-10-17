package com.bachelor.degree.travel.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AdvancedSearchActivity extends AppCompatActivity {

    private Button findBtn;
    private EditText nameText;
    private EditText locationText;
    private EditText distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        Intent intentFromParent = getIntent();
        Bundle data = intentFromParent.getExtras();
        String nameCastel;
        if (data != null) {
            nameCastel = data.getString(Constants.WORD_TO_SEARCH);
        } else {
            nameCastel = "";
        }

        nameText = (EditText)findViewById(R.id.name);
        locationText = (EditText)findViewById(R.id.location);
        distanceText = (EditText)findViewById(R.id.distance);
        nameText.setText(nameCastel);

        findBtn = (Button)findViewById(R.id.findCastel);

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String location = locationText.getText().toString();
                String distance = distanceText.getText().toString();

                //start searching data for castel
            }
        });
    }
}

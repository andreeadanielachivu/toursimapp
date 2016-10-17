package com.bachelor.degree.travel.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class FlightActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton imageBtnFrom;
    private ImageButton imageBtnTo;
    private Calendar calendarFrom;
    private Calendar calendarTo;
    private EditText FromEditText;
    private EditText ToEditText;
    private int dayFrom, dayTo;
    private int monthFrom, monthTo;
    private int yearFrom, yearTo;
    private TextView nearMeAirport;
    private Button searchFlightBtn;
    private String fromAirport = "";
    private String toAirport = "";
    private String departureTime = "";
    private String arrivalTime = "";
    private AutoCompleteTextView textFrom, textTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);

        textFrom = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        textTo = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView2);

        //set dynamic adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);

        textFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fromAirport = (String)parent.getItemAtPosition(position);
            }
        });
        textTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toAirport = (String)parent.getItemAtPosition(position);
            }
        });

        FromEditText = (EditText) findViewById(R.id.FromEditText);
        ToEditText = (EditText) findViewById(R.id.ToEditText);

        calendarFrom = Calendar.getInstance();
        yearFrom = calendarFrom.get(Calendar.YEAR);
        monthFrom = calendarFrom.get(Calendar.MONTH);
        dayFrom = calendarFrom.get(Calendar.DAY_OF_MONTH);
        showDateFrom(yearFrom, monthFrom+1, dayFrom);

        calendarTo = Calendar.getInstance();
        yearTo = calendarFrom.get(Calendar.YEAR);
        monthTo = calendarFrom.get(Calendar.MONTH);
        dayTo = calendarFrom.get(Calendar.DAY_OF_MONTH);
        showDateTo(yearTo, monthTo+1, dayTo);

        nearMeAirport = (TextView) findViewById(R.id.nearMeAiroport);
        Intent intent = getIntent();
        String url_flight = intent.getStringExtra(Constants.TRANSPORTSEARCH);
        //set the near airports
        GetNearTransportTask taskTransport = new GetNearTransportTask();
        Object []obj = new Object[2];
        obj[0] = url_flight;
        obj[1] = nearMeAirport;
        taskTransport.execute(obj);

        searchFlightBtn = (Button) findViewById(R.id.searchFlight);
        searchFlightBtn.setOnClickListener(this);

        HttpRequestFlights http = new HttpRequestFlights();
        Object []dataFlight = new Object[5];
        dataFlight[0] = spinnerAdapter;
        dataFlight[1] = textFrom;
        dataFlight[2] = textTo;
        dataFlight[3] = Constants.URL_AIRPORTS;
        dataFlight[4] = "Airport";
        http.execute(dataFlight);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        int id = view.getId();
        if (id == R.id.imageButtonSource) {
            showDialog(999);
        } else if (id == R.id.imageButtonDest) {
            showDialog(899);
        }
        Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListenerFrom, yearFrom, monthFrom, dayFrom);
        } else if (id == 899) {
            return new DatePickerDialog(this, myDateListenerTo, yearTo, monthTo, dayTo);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListenerFrom = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDateFrom(arg1, arg2+1, arg3);
        }
    };
    private DatePickerDialog.OnDateSetListener myDateListenerTo = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDateTo(arg1, arg2+1, arg3);
        }
    };

    private void showDateFrom(int year, int month, int day) {
        FromEditText.setText(new StringBuilder().append(year).append("/")
                .append(month).append("/").append(day));
    }

    private void showDateTo(int year, int month, int day) {
        ToEditText.setText(new StringBuilder().append(year).append("/")
                .append(month).append("/").append(day));
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.searchFlight) {
            Intent intentFlight = new Intent(getApplicationContext(), ListFlightActivity.class);
            //get source and destination
            int idCommaFrom = fromAirport.indexOf(",");
            int idCommaTo = toAirport.indexOf(",");
            String iataCodeFrom = fromAirport.substring(0, idCommaFrom);
            String nameFrom = fromAirport.substring(idCommaFrom + 1);
            String iataCodeTo = toAirport.substring(0, idCommaTo);
            String nameTo= toAirport.substring(idCommaTo + 1);
            departureTime = FromEditText.getText().toString().replace('/', '-');
            arrivalTime = ToEditText.getText().toString().replace('/', '-');
            intentFlight.putExtra(Constants.FROM_AIRPORT, nameFrom);
            intentFlight.putExtra(Constants.FROM_AIRPORT_CODE, iataCodeFrom);
            intentFlight.putExtra(Constants.TO_AIRPORT, nameTo);
            intentFlight.putExtra(Constants.TO_AIRPORT_CODE, iataCodeTo);
            intentFlight.putExtra(Constants.DATAFROM, departureTime);
            intentFlight.putExtra(Constants.DATATO, arrivalTime);
            startActivity(intentFlight);
        }
    }


}

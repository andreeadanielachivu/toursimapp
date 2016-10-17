package com.bachelor.degree.travel.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailsAccountActivity extends BaseNavigationActivity {
    private SQLiteHandler db;
    private SessionManager session;
    private String name = "", email = "";
    final String[] items = {" Attractions "," Restaurants ", " Hotels ", " All "};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_account);
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        TextView welcomeTextView = (TextView) findViewById(R.id.titleDetailsAccount);
        Button backupBtn = (Button) findViewById(R.id.activateBackupBtn);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        name = user.get("name");
        email = user.get("email");

        //noinspection ConstantConditions
        welcomeTextView.setText(String.format("%s%s", getString(R.string.welcome), name));
        // Logout button click event
        //noinspection ConstantConditions
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayWindow();
            }
        });
        // Activate backup btn
        //noinspection ConstantConditions
        backupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMenuTypes(items, "You can activate history for one of the options below.");
            }
        });
    }


    public void displayWindow() {
        final Dialog dialog = new Dialog(DetailsAccountActivity.this);
        dialog.setContentView(R.layout.logout_dialog);

        dialog.setTitle("Welcome  " + name);
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //get text view logout
        TextView textView1Logout = (TextView)dialog.findViewById(R.id.textViewLogout);
        textView1Logout.setText(getString(R.string.logout_text, name, email));

        //set up button
        Button buttonCancel = (Button) dialog.findViewById(R.id.Button02);
        Button buttonOk = (Button) dialog.findViewById(R.id.Button01);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }


    public void displayMenuTypes(final String[] listData, String messageTitle) {
        // Strings to Show In Dialog with Radio Buttons
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(messageTitle);
        builder.setSingleChoiceItems(listData, -1 , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                session.setHistoryOption( listData[item]);
                dialog.dismiss();
            }
        });
        final AlertDialog levelDialog = builder.create();
        levelDialog.show();
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        if (session.isLoggedInFb()) {
            //delete from db

            deleteUser();
        }
        // Launching the login activity
        Intent intent = new Intent(DetailsAccountActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteUser() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Constants.URL_LOGINFB, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Login Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLoginFacebook(false);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("name", name);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    protected int getNavigationItemID() {
        return R.id.accountItem;
    }
}

package com.bachelor.degree.travel.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Andreea on 6/7/2016.
 */
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_IS_LOGGEDIN_FB = "isLoggedInFb";
    private static final String KEY_IS_ATTRACTIONS = "Attractions";
    private static final String KEY_IS_RESTAURANTS = "Restaurants";
    private static final String KEY_IS_HOTELS = "Hotels";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // set key login
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    //set history option
    public void  setHistoryOption(String nameOption) {
        nameOption = nameOption.trim();
        if (nameOption.compareTo("Attractions") == 0) {
            editor.putBoolean(KEY_IS_ATTRACTIONS, true);
        } else if (nameOption.compareTo("Restaurants") == 0) {
            editor.putBoolean(KEY_IS_RESTAURANTS, true);
        } else if (nameOption.compareTo("Hotels") == 0) {
            editor.putBoolean(KEY_IS_HOTELS, true);
        } else if (nameOption.compareTo("All") == 0) {
            editor.putBoolean(KEY_IS_RESTAURANTS, true);
            editor.putBoolean(KEY_IS_HOTELS, true);
            editor.putBoolean(KEY_IS_ATTRACTIONS, true);
        } else {
            editor.putBoolean(KEY_IS_HOTELS, false);
            editor.putBoolean(KEY_IS_RESTAURANTS, false);
            editor.putBoolean(KEY_IS_ATTRACTIONS, false);
        }
        // commit changes
        editor.commit();
    }

    public void setLoginFacebook(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN_FB, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    //check login status
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public boolean isLoggedInFb(){
        return pref.getBoolean(KEY_IS_LOGGEDIN_FB, false);
    }

    public boolean isHistoryAttractionsActivate() {
        return pref.getBoolean(KEY_IS_ATTRACTIONS, false);
    }

    public boolean isHistoryRestaurantActivate() {
        return pref.getBoolean(KEY_IS_RESTAURANTS, false);
    }

    public boolean isHistoryHotelsActivate() {
        return pref.getBoolean(KEY_IS_HOTELS, false);
    }

    public boolean isHistoryActivated() {
        return pref.getBoolean(KEY_IS_ATTRACTIONS, false) || pref.getBoolean(KEY_IS_RESTAURANTS, false)
                || pref.getBoolean(KEY_IS_HOTELS, false);
    }

    public boolean isHistoryActivatedByCategory(String input) {
        String category = input.substring(0, 1).toUpperCase() + input.substring(1);
        if (input.compareTo("all") == 0) {
            boolean value = isHistoryActivated();
            return value;
        } else {
            return pref.getBoolean(category, false);
        }
    }
}

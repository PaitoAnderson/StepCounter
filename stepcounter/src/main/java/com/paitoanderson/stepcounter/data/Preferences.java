package com.paitoanderson.stepcounter.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Paito Anderson on 2014-04-26.
 */
public class Preferences {

    // Identify Shared Preference Store
    public final static String PREFS_NAME = "stepcounter_prefs";

    // How many steps have I walked?
    public static String getStepCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return String.format("%,d", prefs.getInt("stopCount", 0));
    }

    // Set how many steps I have walked.
    public static void setStepCount(Context context, Integer newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt("stopCount", newValue);
        prefsEditor.commit();
    }
}

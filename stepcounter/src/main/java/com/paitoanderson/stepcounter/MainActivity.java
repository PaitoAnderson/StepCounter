package com.paitoanderson.stepcounter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;

import com.paitoanderson.stepcounter.data.Preferences;
import com.paitoanderson.stepcounter.services.StepCounter;

/**
 * Created by Paito Anderson on 2014-04-26.
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the service is running
        if (!isServiceRunning()) {

            // Set system boot time as last sync unless last sync is more recent
            // The reason for doing this is Android *could* start counting steps when the system starts up and not sync'd them yet
            if (Preferences.getFitbitSyncDate(this) < SystemClock.elapsedRealtime())
            {
                // If no steps are recorded don't bother
                if (Preferences.getStepCount(this) == 0) {
                    Preferences.setFitbitSyncDate(this, System.currentTimeMillis());
                } else {
                    Preferences.setFitbitSyncDate(this, SystemClock.elapsedRealtime());
                }
            }

            // Start Step Counting service
            Intent serviceIntent = new Intent(this, StepCounter.class);
            startService(serviceIntent);
        }

        // Exit Activity
        finish();
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.paitoanderson.stepcounter.services.StepCounter".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

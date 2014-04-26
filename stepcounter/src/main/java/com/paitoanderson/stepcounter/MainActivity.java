package com.paitoanderson.stepcounter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

package com.paitoanderson.stepcounter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.paitoanderson.stepcounter.data.Preferences;
import com.paitoanderson.stepcounter.services.StepCounter;

/**
 * Created by Paito Anderson on 2014-04-27.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Clear Steps
        Preferences.clearStepCount(context);

        // Start Step Counter service
        Intent myIntent = new Intent(context, StepCounter.class);
        context.startService(myIntent);
    }
}

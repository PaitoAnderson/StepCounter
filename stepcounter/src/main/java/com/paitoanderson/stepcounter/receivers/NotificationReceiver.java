package com.paitoanderson.stepcounter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.paitoanderson.stepcounter.data.Preferences;
import com.paitoanderson.stepcounter.services.StepCounter;

/**
 * Created by Paito Anderson on 2014-04-26.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("CLOSE")) {

            // Stop Service
            Intent stopIntent = new Intent(context, StepCounter.class);
            context.stopService(stopIntent);
        } else if (intent.getAction().equals("SHARE")) {

            // Share Intent
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I walked " + Preferences.getStepCount(context) + " steps!");

            // Chooser Intent
            Intent shareChooser = Intent.createChooser(shareIntent, "Share with");
            shareChooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(shareChooser);
        }
    }
}

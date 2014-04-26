package com.paitoanderson.stepcounter.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.paitoanderson.stepcounter.R;
import com.paitoanderson.stepcounter.data.Preferences;
import com.paitoanderson.stepcounter.receivers.NotificationReceiver;

/**
 * Created by Paito Anderson on 2014-04-26.
 */
public class StepCounter extends Service implements SensorEventListener {

    private static final Integer NOTIFICATION_ID = 7837;
    private NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
    private NotificationManager mNotificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Get Notification Manager
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Setup Step Counter
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            Toast.makeText(this, "Started Counting Steps", Toast.LENGTH_LONG).show();
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Device not Compatible!", Toast.LENGTH_LONG).show();
            this.stopSelf();
        }

        // Setup First Notification
        updateNotification(true, 0);

        // Restart the service if its killed
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Update Notification
        updateNotification(false, (int) event.values[0]);

        // Record Step Count
        Preferences.setStepCount(this, (int) event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateNotification(boolean firstTime, int stepCount) {

        // Update Step Count
        mBuilder.setContentText("Step Count: " + String.format("%,d", stepCount));

        // First Time Setup
        if (firstTime) {
            mBuilder.setSmallIcon(R.drawable.ic_stat_feet);
            mBuilder.setPriority(-1);
            mBuilder.setContentTitle("Step Counter - Counting");
            mBuilder.setOngoing(true);
            mBuilder.setAutoCancel(false);
            mBuilder.setOnlyAlertOnce(true);

            // Share Button
            Intent shareIntent = new Intent(this, NotificationReceiver.class);
            shareIntent.setAction("SHARE");
            PendingIntent pShareIntent = PendingIntent.getBroadcast(this, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_stat_share, "Share", pShareIntent);

            // Reset Button
            //Intent resetIntent = new Intent(this, NotificationReceiver.class);
            //resetIntent.setAction("RESET");
            //PendingIntent pResetIntent = PendingIntent.getBroadcast(this, 0, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //mBuilder.addAction(R.drawable.ic_stat_reset, "Reset", pResetIntent);

            // Close Button
            Intent closeIntent = new Intent(this, NotificationReceiver.class);
            closeIntent.setAction("CLOSE");
            PendingIntent pCloseIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_stat_cancel, "Close", pCloseIntent);

            // Set Service to run in the Foreground
            startForeground(NOTIFICATION_ID, mBuilder.build());
        }

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

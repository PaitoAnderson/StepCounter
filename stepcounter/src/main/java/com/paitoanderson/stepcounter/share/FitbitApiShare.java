package com.paitoanderson.stepcounter.share;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.paitoanderson.stepcounter.api.FitbitApi;
import com.paitoanderson.stepcounter.data.Preferences;
import com.paitoanderson.stepcounter.oauth.OAuthActivity;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.text.SimpleDateFormat;

/**
 * Created by Paito Anderson on 14-08-01.
 */
public class FitbitApiShare {

    private long _lastUpdate = 0;
    private long _lastFetch = 0;

    private String _token = null;
    private String _secret = null;

    public void sendSteps(final Context context, final int steps) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        long now = System.currentTimeMillis();

        if (now - this._lastUpdate > 1000 * 60) {
            this._lastUpdate = now;

            final FitbitApiShare me = this;

            this._token = prefs.getString("oauth_token", null);
            this._secret = prefs.getString("oauth_secret", null);

            if (this._token == null || this._secret == null) {
                Runnable r = new Runnable() {
                    public void run() {
                        me.fetchAuth(context);
                    }
                };

                Thread t = new Thread(r);
                t.start();
            } else {
                if (now - this._lastFetch > 1000 * 60 * 5) {
                    this._lastFetch = now;

                    Runnable r = new Runnable() {
                        public void run() {
                            try {
                                Token accessToken = new Token(me._token, me._secret);

                                ServiceBuilder builder = new ServiceBuilder();
                                builder = builder.provider(FitbitApi.class);
                                builder = builder.apiKey(FitbitApi.Key);
                                builder = builder.apiSecret(FitbitApi.Secret);
                                final OAuthService service = builder.build();

                                OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.fitbit.com/1/user/-/activities.json");
                                request.addBodyParameter("activityId", "90013");
                                request.addBodyParameter("date", new SimpleDateFormat("yyyy-MM-dd").format(Preferences.getFitbitSyncDate(context)));
                                request.addBodyParameter("startTime", new SimpleDateFormat("HH:mm").format(Preferences.getFitbitSyncDate(context)));
                                request.addBodyParameter("durationMillis", String.valueOf(System.currentTimeMillis() - Preferences.getFitbitSyncDate(context)));
                                request.addBodyParameter("distance", Integer.toString(steps));
                                request.addBodyParameter("distanceUnit", "Steps");

                                Log.i("FitbitApiShare", request.getBodyContents());

                                service.signRequest(accessToken, request);

                                Response response = request.send();

                                switch (response.getCode()) {
                                    case 200:
                                    case 201:
                                    case 204:
                                        // Reset so we don't send the same steps twice
                                        Preferences.resetStepCount(context);

                                        // Set now as the last FitBit Sync date
                                        Preferences.setFitbitSyncDate(context, System.currentTimeMillis());

                                        Log.i("FitbitApiShare", "Successfully updated FitBit!");
                                        //Toast.makeText(context, "Successfully updated FitBit!", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        Log.i("FitbitApiShare", "Failed to update FitBit!");
                                        Log.d("FitbitApiShare", response.getBody());

                                        //Toast.makeText(context, "Failed to update FitBit!", Toast.LENGTH_LONG).show();
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Thread t = new Thread(r);
                    t.start();
                }
            }
        }

    }

    private void fetchAuth(Context context) {
        Intent intent = new Intent(context, OAuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(OAuthActivity.SETUP_KEY, "YES");
        context.startActivity(intent);
    }
}

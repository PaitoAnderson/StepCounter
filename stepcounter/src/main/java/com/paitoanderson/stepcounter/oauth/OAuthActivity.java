package com.paitoanderson.stepcounter.oauth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.paitoanderson.stepcounter.api.FitbitApi;

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.List;

/**
 * Created by Paito Anderson on 14-07-31.
 */
public class OAuthActivity extends Activity {

    public static final String SETUP_KEY = "SETUP_KEY";

    public void onResume() {
        super.onResume();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final OAuthActivity me = this;

        Bundle extras = this.getIntent().getExtras();

        if (extras.containsKey(OAuthActivity.SETUP_KEY)) {

            ServiceBuilder builder = new ServiceBuilder();
            builder = builder.provider(FitbitApi.class);
            builder = builder.apiKey(FitbitApi.Key);
            builder = builder.apiSecret(FitbitApi.Secret);
            builder = builder.callback(FitbitApi.CallbackUrl);

            final OAuthService service = builder.build();

            Runnable r = new Runnable() {
                public void run() {
                    try {
                        Token token = service.getRequestToken();

                        SharedPreferences.Editor e = prefs.edit();
                        e.putString("request_token", token.getToken());
                        e.putString("request_secret", token.getSecret());
                        e.apply();

                        String url = service.getAuthorizationUrl(token);

                        Intent intent = new Intent(me, OAuthWebActivity.class);
                        intent.setData(Uri.parse(url));
                        Log.d("OAuthActivity", "Url #2: " + url);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        me.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread t = new Thread(r);
            t.start();

            this.finish();
        } else {

            Uri incomingUri = this.getIntent().getData();

            if ("http".equals(incomingUri.getScheme())) {
                List<String> segments = incomingUri.getPathSegments();

                if (segments.get(0).equals("oauth")) {

                    String verifier = incomingUri.getQueryParameter("oauth_verifier");

                    if (verifier == null)
                        verifier = incomingUri.getQueryParameter("code");

                    if (verifier != null) {
                        final Token requestToken = new Token(prefs.getString("request_token", ""), prefs.getString("request_secret", ""));

                        final Verifier v = new Verifier(verifier);


                        ServiceBuilder builder = new ServiceBuilder();
                        builder = builder.provider(FitbitApi.class);
                        builder = builder.apiKey(FitbitApi.Key);
                        builder = builder.apiSecret(FitbitApi.Secret);
                        builder = builder.callback(FitbitApi.CallbackUrl);

                        final OAuthService service = builder.build();

                        Runnable r = new Runnable() {
                            public void run() {
                                try {
                                    Token accessToken = service.getAccessToken(requestToken, v);

                                    SharedPreferences.Editor e = prefs.edit();
                                    e.putString("oauth_secret", accessToken.getSecret());
                                    e.putString("oauth_token", accessToken.getToken());
                                    e.commit();

                                    //SanityManager.getInstance(me).refreshState();

                                    me.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(me, "Connected to FitBit!", Toast.LENGTH_LONG).show();
                                            me.finish();
                                        }
                                    });
                                } catch (OAuthConnectionException e) {
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
    }
}

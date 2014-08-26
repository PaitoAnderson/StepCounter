package com.paitoanderson.stepcounter.oauth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.paitoanderson.stepcounter.R;

import java.util.Locale;

/**
 * Created by Paito Anderson on 14-08-01.
 */
public class OAuthWebActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

        this.setContentView(R.layout.layout_web_activity);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    protected void onResume() {
        super.onResume();

        WebView webView = (WebView) this.findViewById(R.id.webview);

        Uri uri = this.getIntent().getData();

        if (uri != null && uri.getScheme() != null && uri.getScheme().toLowerCase(Locale.ENGLISH).startsWith("http")) {
            final OAuthWebActivity me = this;

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);

            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    me.setProgress(progress * 1000);
                }

                public void onCloseWindow(WebView window) {
                    me.finish();
                }

                public void onReceivedTitle(WebView view, String title) {
                    me.getActionBar().setTitle(title);
                }
            });

            webView.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(me, description, Toast.LENGTH_LONG).show();
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    boolean oauth = false;

                    if (url.toLowerCase(Locale.getDefault()).startsWith("http://paitoanderson.com/oauth"))
                        oauth = true;

                    if (oauth) {
                        Intent intent = new Intent(me, OAuthActivity.class);
                        intent.setData(Uri.parse(url));
                        intent.putExtras(new Bundle());

                        me.startActivity(intent);
                        me.finish();
                    }

                    return oauth;
                }
            });

            webView.loadUrl(uri.toString());
        }
    }

    public void onBackPressed() {
        WebView webView = (WebView) this.findViewById(R.id.webview);

        if (webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }
}

package com.sisindia.ai.mtrainer.android.features.trainingkit;

import static com.sisindia.ai.mtrainer.android.constants.Constant.EXTRA_LINK_URL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.sisindia.ai.mtrainer.android.R;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    private String url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            url = bundle.getString(EXTRA_LINK_URL);
        }

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webSettings.setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDomStorageEnabled(true);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Default behavior for caching
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");
        webSettings.setBuiltInZoomControls(false);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        System.out.println("URL = " + url);
        webView.loadUrl(url);
    }
}

package com.sisindia.ai.mtrainer.android.features.report;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.Constant;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        WebView webView = findViewById(R.id.webView);
        AppCompatImageView crossButton = findViewById(R.id.closeReportWV);

        crossButton.setOnClickListener(v -> finish());

        String webUrl = getIntent().getStringExtra(Constant.WEB_VIEW_URL_KEY);
        if (webUrl != null && !webUrl.isEmpty()) {

            /*WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Default behavior for caching
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    Toast.makeText(ReportActivity.this, "SSL Error" + error.getUrl(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                @Nullable
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    return super.shouldInterceptRequest(view, request);
                }
            });
            webView.loadUrl(webUrl);*/

            final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36";
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webSettings.setBuiltInZoomControls(true); // Enable zoom controls
            webSettings.setDomStorageEnabled(true); // Enable DOM storage
            webSettings.setUserAgentString(DESKTOP_USER_AGENT); // Set User-Agent string
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // Allow JavaScript to open windows
            webSettings.setAllowFileAccessFromFileURLs(true); // Allow file access from file URLs
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // Allow mixed content
            //webSettings.setAppCacheEnabled(true);
            //webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(webUrl);
        }
    }
}




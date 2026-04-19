package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingKitRedirectBinding;

public class TrainingKitRedirectActivity extends AppCompatActivity {

    ActivityTrainingKitRedirectBinding binding;
    ProgressDialog pd;
    Message message;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = (Message) getIntent().getParcelableExtra("messagevalue");
        url = getIntent().getStringExtra("redirecturl");
        binding = (ActivityTrainingKitRedirectBinding) bindActivityView(this, getLayoutResource());
        initwebview(savedInstanceState);
    }

    private void initwebview(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            WebSettings webSettings = binding.webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Default behavior for caching
            webSettings.setAllowFileAccess(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setLoadsImagesAutomatically(true);
            binding.webview.loadUrl(url);
            WebView.WebViewTransport webViewTransport = (WebView.WebViewTransport) message.obj;
            webViewTransport.setWebView(binding.webview);
            message.sendToTarget();

            binding.webview.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onPermissionRequest(PermissionRequest request) {
                    runOnUiThread(() -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (request.getOrigin().toString() == "file:///") {
                                request.grant(request.getResources());
                            }
                        }
                    });
                }
            });

            binding.webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Nullable
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    return super.shouldInterceptRequest(view, request);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    /*if (pd.isShowing()) {
                        pd.dismiss();
                    }*/
/*
                    if (!iswebviewreloaded) {
                        if (url.equals("https://mtrainer.sisindia.com/app/index.html?token=")) {
                            Log.i("test", "onPageFinished, ${url}");
                            iswebviewreloaded = true;
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.webview.loadUrl("https://mtrainer.sisindia.com/app/index.html?token="+ Prefs.getString("LoginToken", "")+"#/");
                                }
                            }, 10);
                        }
                    }
*/
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                   /* if (!pd.isShowing()) {
                        pd.show();
                    }*/
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                    super.onReceivedSslError(view, handler, error);
                }
            });

           /* pd = new ProgressDialog(this);
            pd.setMessage("Please wait Loading...");
            pd.show();*/
            binding.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            if (Build.VERSION.SDK_INT >= 19) {
                binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                binding.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }


        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.webview.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        binding.webview.restoreState(savedInstanceState);
    }

    protected ViewDataBinding bindActivityView(Activity activity, @LayoutRes int layoutResource) {
        return DataBindingUtil.setContentView(activity, layoutResource);
    }

    protected int getLayoutResource() {
        return R.layout.activity_training_kit_redirect;
    }
}
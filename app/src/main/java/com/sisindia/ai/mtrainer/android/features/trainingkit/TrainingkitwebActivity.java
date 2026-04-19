package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingkitwebBinding;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;

public class TrainingkitwebActivity extends AppCompatActivity {

    ActivityTrainingkitwebBinding binding;
    boolean iswebviewreloaded = false;
    AuthApi authApi;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //progressDialog = new ProgressDialog(this);
        //progressDialog.show();
        //initRetrofit();
        binding = (ActivityTrainingkitwebBinding) bindActivityView(this, getLayoutResource());
        checkDownloadPermission();
        /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },10);*/
        initwebview(savedInstanceState);
    }

    private void checkDownloadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

/*
    private void initRetrofit() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(*/
    /*message -> Timber.d(message)*//*
);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        builder.addInterceptor(loggingInterceptor);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://mtrainer.sisindia.com/").client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        authApi = retrofit.create(AuthApi.class);
        Date date = new Date();
        long currentdate = date.getTime();
        Log.d("sadfsadf", Prefs.getLong("TokenTime")+" , "+currentdate);
        Log.d("sadfsadf",Prefs.getLong("ExpiryTokenTime")+" , "+currentdate);
        if (Prefs.getLong("ExpiryTokenTime")<currentdate){
            SaveTokenRequest saveTokenRequest = new SaveTokenRequest();
            saveTokenRequest.setUserName("ApiAdmin");
            saveTokenRequest.setPassword("India@123");
            saveTokenRequest.setLoginCode(PrefsConstants.EMPLOYEE_REG_NO);
            authApi.gettoken(saveTokenRequest).enqueue(new Callback<SaveTokenResponse>() {
                @Override
                public void onResponse(Call<SaveTokenResponse> call, Response<SaveTokenResponse> response) {
                    Prefs.putString("LoginToken",response.body().getLoginCodeToken());
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.HOUR,8);
                    long formatdate = date.getTime();
                    Date date1 = calendar.getTime();
                    long expirydate = date1.getTime();
                    Prefs.putLong("TokenTime",formatdate);
                    Prefs.putLong("ExpiryTokenTime",expirydate);
                    binding.webview.loadUrl("https://lmsqc3.gc-solutions.net/SISIndia/app/index.html?token=" + response.body().getLoginCodeToken());
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<SaveTokenResponse> call, Throwable t) {

                }
            });
        }else {
            progressDialog.dismiss();
        }
    }
*/

    private void initwebview(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            WebSettings webSettings = binding.webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Default behavior for caching
//            webSettings.setAppCacheEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setSupportMultipleWindows(true);
            webSettings.setLoadsImagesAutomatically(true);
            binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            binding.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            binding.webview.getSettings().setAllowFileAccessFromFileURLs(true);
            binding.webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
            binding.webview.getSettings().setSupportMultipleWindows(true);
            binding.webview.getSettings().setAllowFileAccess(true);
            binding.webview.getSettings().setJavaScriptEnabled(true);
            binding.webview.getSettings().setDomStorageEnabled(true);
            binding.webview.getSettings().setPluginState(WebSettings.PluginState.ON);

            // binding.webview.getSettings().setSupportMultipleWindows(true);
            binding.webview.addJavascriptInterface(new WebAppInterface(this), "window");
            binding.webview.loadUrl("https://mtrainer.sisindia.com/app/index.html?token=" + Prefs.getString("LoginToken", ""));
            binding.webview.setWebChromeClient(new WebChromeClient() {

                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    Log.d("webviewredirectingurl:", view.getUrl());

                   /* WebView newWebView = new WebView(TrainingkitwebActivity.this);
                    newWebView.getSettings().setJavaScriptEnabled(true);
                    newWebView.getSettings().setSupportZoom(true);
                    newWebView.getSettings().setBuiltInZoomControls(true);
                    newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                    newWebView.getSettings().setSupportMultipleWindows(true);
                    view.addView(newWebView);
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(newWebView);
                    resultMsg.sendToTarget();

                    newWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });

                    return true;*/
                   /* WebView newWebView = new WebView(TrainingkitwebActivity.this);
                    view.addView(newWebView);
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(newWebView);
                    resultMsg.sendToTarget();

                    newWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Log.d("webviewredirectingurl:",url);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.google.com"));
                            browserIntent.setData(Uri.parse(url));
                            startActivity(browserIntent);
                            return false;
                        }
                    });*/
                    /*WebView.HitTestResult result = view.getHitTestResult();
                    String data = result.getExtra();
                    Log.d("webviewurl:",data);
                    Context context = view.getContext();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                    context.startActivity(browserIntent);*/
                   /* WebView.HitTestResult result = view.getHitTestResult();
                    String data = result.getExtra();
                    Context context = view.getContext();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                    context.startActivity(browserIntent);
                    return true;*/

//                    WebView newWebView = new WebView(TrainingkitwebActivity.this);
//                    newWebView.getSettings().setJavaScriptEnabled(true);
//                    newWebView.getSettings().setSupportZoom(true);
//                    newWebView.getSettings().setBuiltInZoomControls(true);
//                    newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
//                    newWebView.getSettings().setSupportMultipleWindows(true);
//                    view.addView(newWebView);
//                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//                    transport.setWebView(newWebView);
//                    resultMsg.sendToTarget();
//
//                    newWebView.setWebViewClient(new WebViewClient() {
//                        @Override
//                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                            view.loadUrl(url);
//                            return true;
//                        }
//                    });
//
//                    return true;

                    WebView newWebView = new WebView(TrainingkitwebActivity.this);
                    WebSettings webSettings = newWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);

                    // Other configuration comes here, such as setting the WebViewClient

                    final Dialog dialog = new Dialog(TrainingkitwebActivity.this);
                    dialog.setContentView(newWebView);
                    dialog.show();

                    newWebView.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public void onCloseWindow(WebView window) {
                            dialog.dismiss();
                        }
                    });

                    ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
                    resultMsg.sendToTarget();
                    return true;

                }

                @Override
                public void onPermissionRequest(PermissionRequest request) {
                    runOnUiThread(() -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (request.getOrigin().toString().equals("file:///")) {
                                request.grant(request.getResources());
                            }
                        }
                    });
                }
            });

            //                BroadcastReceiver onComplete = new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        Toast.makeText(getApplicationContext(), "Downloading Complete", Toast.LENGTH_SHORT).show();
//                    }
//                };
            binding.webview.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
//                    DownloadManager.Request request =new DownloadManager.Request(Uri.parse(url));
//                    request.setTitle(URLUtil.guessFileName(url,contentDisposition,mimetype));
//                    request.setDescription("Downloading file...");
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url, contentDisposition, mimetype));
//                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                    dm.enqueue(request);
//                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//
//                    request.setMimeType(mimeType);
//                    //------------------------COOKIE!!------------------------
//                    String cookies = CookieManager.getInstance().getCookie(url);
//                    request.addRequestHeader("cookie", cookies);
//                    //------------------------COOKIE!!------------------------
//                    request.addRequestHeader("User-Agent", userAgent);
//                    request.setDescription("Downloading file...");
//                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
//                    request.allowScanningByMediaScanner();
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
//                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                    dm.enqueue(request);
//                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                startActivity(intent);
                //intent.setType(mimeType);
                //startActivity(intent);


            });

            binding.webview.setWebViewClient(new WebViewClient() {

//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                    Log.d("webviewurl",request.getUrl().getHost());
//                    if (!request.getUrl().getHost().contains("https://mtrainer.sisindia.com/PrintCertificate/PrintCertificate.aspx?")){
//                        return false;
//                    }
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(request.getUrl());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setPackage("com.android.chrome");
//                    startActivity(intent);
//                    return true;
//                }

//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    if (url.contains("https://mtrainer.sisindia.com/app/index.html?token=")){
//                        return false;
//                    }
//                    //view.loadUrl(url);
//                    Log.d("webviewurl",url);
//                    //if (){}
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    view.getContext().startActivity(intent);
//                    return true;
//
//                }

//                @Nullable
//                @Override
//                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                    return super.shouldInterceptRequest(view, request);
//                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    if (!iswebviewreloaded) {
                        if (url.equals("https://mtrainer.sisindia.com/app/index.html?token=")) {
                            Log.d("weburltest", Prefs.getString("LoginToken", ""));
                            iswebviewreloaded = true;
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.webview.loadUrl("https://mtrainer.sisindia.com/app/index.html?token=" + Prefs.getString("LoginToken", "") + "#/");
                                }
                            }, 10);
                        } else if (url.startsWith("https://mtrainer.sisindia.com/PrintCertificate/PrintCertificate.aspx?")) {
                            Log.d("weburltestredirect", url);
                        }
                    } else if (url.startsWith("https://mtrainer.sisindia.com/PrintCertificate/PrintCertificate.aspx?")) {
                        Log.d("weburltestredirect1", url);
                    }
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (!pd.isShowing()) {
                        pd.show();
                    }
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                    super.onReceivedSslError(view, handler, error);
                }
            });


            pd = new ProgressDialog(this);
            pd.setMessage("Please wait Loading...");
            pd.show();
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
        return R.layout.activity_trainingkitweb;
    }


}
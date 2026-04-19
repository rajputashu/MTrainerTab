package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.features.server.VideoEnabledWebChromeClient;
import com.sisindia.ai.mtrainer.android.features.server.VideoEnabledWebView;
import com.sisindia.ai.mtrainer.android.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VideoPlayerActivity extends AppCompatActivity {
    VideoEnabledWebView webView;
    String video_name, videoId;
    private VideoEnabledWebChromeClient webChromeClient;
    int position = 0;
    Utils mUtil;
    private String videoStartTime;

    Future longRunningTaskFuture;
    Runnable timerRunnable;
    Handler timerHandler;
    boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        webView = findViewById(R.id.webView);
        mUtil = new Utils(this);
        String documentRoot = mUtil.getDocRoot();
//        String documentRoot = mUtil.getDocRootV2();
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);

        String videoDuration = getIntent().getStringExtra("duration");
        Log.e("durat", videoDuration);

        if (getIntent() != null) {
            video_name = getIntent().getStringExtra("videoname").replaceAll("\\\\", "/");
            videoId = getIntent().getStringExtra("videoId");

            Log.e("videoname", "its" + video_name);
        }
        Log.e("ip", mUtil.getIpAddressAndUrl());


        if (video_name != null) {
            //getting file and passing file to internal socket
            File file = new File(documentRoot + video_name);
            Log.e("filepath", file.getPath());
            if (file.exists()) {
                webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
                {
                    // Subscribe to standard events, such as onProgressChanged()...
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        Log.v("Player ", progress + "");
                        if (progress == 100) {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a", Locale.ENGLISH);
                            videoStartTime = sdf.format(c.getTime());
                            Log.v("Player videoStartTime", videoStartTime + "");
                        }
                    }

                    @Nullable
                    @Override
                    public View getVideoLoadingProgressView() {
                        if (loadingView != null) {
                            loadingView.setVisibility(View.VISIBLE);
                            return loadingView;
                        } else {
                            return super.getVideoLoadingProgressView();
                        }
                    }

                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            if (loadingView != null) {
                                loadingView.setVisibility(View.VISIBLE);
                            }
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            if (loadingView != null) {
                                loadingView.setVisibility(View.GONE);
                            }
                        }
                        return super.onInfo(mediaPlayer, what, i1);
                    }
                };

                webChromeClient.setOnToggledFullscreen(fullscreen -> {
                    // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                    if (fullscreen) {
                        WindowManager.LayoutParams attrs = getWindow().getAttributes();
                        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        getWindow().setAttributes(attrs);
                        if (android.os.Build.VERSION.SDK_INT >= 14) {
                            //noinspection all
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        }
                    } else {
                        WindowManager.LayoutParams attrs = getWindow().getAttributes();
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        getWindow().setAttributes(attrs);
                        if (android.os.Build.VERSION.SDK_INT >= 14) {
                            //noinspection all
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        }
                    }
                });
                webView.setWebChromeClient(webChromeClient);
                webView.clearCache(true);
                webView.clearHistory();
                webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                // Call private class InsideWebViewClient
                // webView.setWebViewClient(new InsideWebViewClient());
                webView.loadUrl(mUtil.getIpAddressAndUrl() + video_name);
                timerHandler = new Handler();
                //this updates the textbox
                timerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        running = true;
                        if (running && position <= Integer.parseInt(videoDuration)) {
                            position = position + 1;
                            Log.e("position", String.valueOf(position));
                            timerHandler.postDelayed(this, 1000);
                        } else {
                            timerHandler.removeCallbacks(timerRunnable);
                        }

                    }

                };

                timerHandler.postDelayed(timerRunnable, 1000);
                ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
                longRunningTaskFuture = threadPoolExecutor.submit(timerRunnable);

            } else {
                new Utils(this).showAlert("File not found");
            }
            // video = Uri.parse(tempurl + "LearningMantra-MTRAINER/PHYSICALTRAINING/6Nishkash.mp4");
            // video = Uri.parse("/storage/emulated/0/MTrainerVideo/" + video_name.trim() + ".mp4");
        } else {
            new Utils(this).showAlert("File not found");
        }
    }

    public void stopRunning() {
        running = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        webView.onResume();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a", Locale.ENGLISH);
        String endDate = sdf.format(c.getTime());
        if (!webChromeClient.onBackPressed()) {
            if (webView.canGoBack()) {
                //    dataBase.getMtrainerVideoDao().insertVideoClick(video_name, "1", String.valueOf(position),videoStartTime ,endDate, Prefs.getString(PrefsConstants.ROTA_ID),videoId);
                Log.e("playing", String.valueOf(position));
                //videoClickTable();
                timerHandler.removeCallbacks(timerRunnable);
                timerHandler.removeCallbacksAndMessages(null);
                stopRunning();
                webView.goBack();
                finish();

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                // Standard back button implementation (for example this could close the app)

                // Log.e("playing", String.valueOf(webChromeClient.playingPosition()));

                //  dataBase.getMtrainerVideoDao().insertVideoClick(video_name, "1", String.valueOf(position), videoStartTime, endDate, Prefs.getString(PrefsConstants.ROTA_ID),videoId);
                Log.e("playing", String.valueOf(position));
                //   videoClickTable();
                timerHandler.removeCallbacks(timerRunnable);
                timerHandler.removeCallbacksAndMessages(null);
                stopRunning();
                longRunningTaskFuture.cancel(true);
                super.onBackPressed();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                finish();
            }
        }
        /* MTrainerApplication.getInstance().getSyncAdapterInstance().startForceSyncing(new Bundle());*/
    }
}

package com.sisindia.ai.mtrainer.android.features.videoscormplayer;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;

import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityVideoScormBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.TrainingCourseUpdateRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingCourseUpdateResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import org.apache.cordova.CordovaActivity;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

public class VideoScorm extends CordovaActivity {
    TrainingTopicsDataModel trainingTopicsModel;
    TrainingTopicsDataModel trainingLastseen;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    DashBoardApi dashboardapi;
    Dialog progressDialog;
    String localurl;
    ActivityVideoScormBinding binding = null;
    MtrainerDataBase dataBase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_video_scorm);
            dataBase = MtrainerDataBase.getDatabase(this);
            initRetrofit();

            trainingTopicsModel = getIntent().getExtras().getParcelable("trainingTopic");
            compositeDisposable.add(dataBase.getMasterAttendanceDao()
                    .getCourseLastSeenById(trainingTopicsModel.getCourseId(),
                            trainingTopicsModel.getCourseTopicId(),
                            trainingTopicsModel.getCourseContentId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onTrainingData, this::onApiError2));

            //binding.offlineScoPlayerWebView.getSettings().setLoadsImagesAutomatically(true);
            //binding.offlineScoPlayerWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            //binding.offlineScoPlayerWebView.clearCache(true);
            if (trainingTopicsModel.getCourseContentType().equals("Video")) {
                //localurl= getIntent().getExtras().getString("VIdeoMP4url");
                Log.d("MP4VIdeo", "Running");
                binding.videoPlayerView.setVisibility(View.VISIBLE);
                binding.offlineScoPlayerWebView.setVisibility(View.GONE);
                progressDialog = new Dialog(this);
                progressDialog.setContentView(R.layout.custom_progress_dialo);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                ProgressBar progressBar = progressDialog.findViewById(R.id.progressBar);
                LinearLayoutCompat ll_progrss = progressDialog.findViewById(R.id.ll_progrss);
                LinearLayout ll_text = progressDialog.findViewById(R.id.ll_text);
                LinearLayout ll_progrss1 = progressDialog.findViewById(R.id.ll_progrss1);
                ll_progrss1.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                ll_text.setVisibility(View.GONE);
                ll_progrss.setVisibility(View.VISIBLE);
                progressDialog.show();

                binding.videoPlayerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Set the video URI and start playback
                        Uri videoUri = Uri.parse(trainingTopicsModel.getFileURL() + "?" + Prefs.getString(PrefsConstants.SAS_TOKEN));
                        Log.d("VIDEOURL", String.valueOf(videoUri));
                        binding.videoPlayerView.setVideoURI(videoUri);
                        binding.videoPlayerView.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                }
                            }
                        }, 2500);

                       /* Uri videoUri = Uri.parse(trainingTopicsModel.getFileURL()+"?"+ Prefs.getString(PrefsConstants.SAS_TOKEN );
                        Log.d("VIDEOURL", String.valueOf(trainingTopicsModel.getFileURL()+"?"+ Prefs.getString(PrefsConstants.SAS_TOKEN)));
                        binding.videoPlayerView.setVideoURI(videoUri);
                        binding.videoPlayerView.start();
                        */

                    }
                }, 1000);
            } else {
                localurl = getIntent().getExtras().getString("scromdownloadedurl");
                Log.d("SCORMVIDEO", localurl);
                binding.videoPlayerView.setVisibility(View.GONE);
                binding.offlineScoPlayerWebView.setVisibility(View.VISIBLE);

                binding.offlineScoPlayerWebView.getSettings().setAllowFileAccess(true);
                binding.offlineScoPlayerWebView.getSettings().setAllowFileAccessFromFileURLs(true);
                binding.offlineScoPlayerWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                binding.offlineScoPlayerWebView.getSettings().setAllowContentAccess(true);
                binding.offlineScoPlayerWebView.getSettings().setJavaScriptEnabled(true);

                binding.offlineScoPlayerWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("SCORMVIDEO", localurl + "/story.html");
                        binding.offlineScoPlayerWebView.loadUrl(localurl + "/story.html");
                    }
                }, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*binding.offlineScoPlayerWebView.setWebViewClient(new WebViewClient(){
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
        });

        binding.offlineScoPlayerWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return true;
            }
        });*/
        //loadUrl(localurl+"/story.html");
    }

    private void onTrainingData(TrainingTopicsDataModel trainingTopicLastSeen) {

        //if(trainingTopicLastSeen!=null && trainingTopicLastSeen.getLastseen()!=null){
        trainingLastseen = trainingTopicLastSeen;
        trainingLastseen.setSyncStatus(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trainingLastseen.setStartTime(LocalDateTime.now().toString());
        }
    }

    @Override
    protected void extractBundle() {

    }

    private void initRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(/*message -> Timber.d(message)*/);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(headerInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://mtrainer2-uat.azurewebsites.net/").client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        dashboardapi = retrofit.create(DashBoardApi.class);
    }

    /*@Override
    public void onBackPressed() {
        *//*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            compositeDisposable.add(dashboardapi.sendcoursecontenttracker(new TrainingCourseUpdateRequest(
                            Prefs.getInt(PrefsConstants.COMPANY_ID),Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                            "",
                            trainingTopicsModel.getCourseId(),
                            trainingTopicsModel.getCourseTopicId(),
                            trainingTopicsModel.getCourseContentId(),1, LocalDateTime.now().toString(),LocalDateTime.now().toString(),
                            LocalDateTime.now().toString()
                    )).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onTrainingTopicsList, this::onApiError));
        }*//*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (trainingLastseen != null) {
                trainingLastseen.setLastseen(LocalDateTime.now().toString());
                trainingLastseen.setSyncStatus(0);
                //trainingLastseen.setSession(UUID.randomUUID().toString());
                compositeDisposable.add(dataBase.getMasterAttendanceDao()
                        .updatelastseen(trainingLastseen.getCourseId(),
                                trainingTopicsModel.getCourseTopicId(),
                                trainingTopicsModel.getCourseContentId(),
                                trainingLastseen.getStartTime(),
                                trainingTopicsModel.getLastseen(),
                                trainingLastseen.getSession())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onTrainingupdatelist, this::onGettingError));
            } else {
                trainingLastseen = trainingTopicsModel;
                trainingLastseen.setSyncStatus(0);
                trainingLastseen.setStartTime(LocalDateTime.now().toString());
                trainingLastseen.setLastseen(LocalDateTime.now().toString());
                trainingLastseen.setSession(UUID.randomUUID().toString());
                compositeDisposable.add(dataBase.getMasterAttendanceDao()
                        .insertCoursesList(trainingLastseen)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onTrainingupdatelist, this::onGettingError));
            }
        }
        super.onBackPressed();
    }*/

    private void onTrainingupdatelist() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            compositeDisposable.add(dashboardapi.sendcoursecontenttracker(new TrainingCourseUpdateRequest(
                            1,//  Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)),
                            Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                            trainingLastseen.getSession(),
                            trainingTopicsModel.getCourseId(),
                            trainingTopicsModel.getCourseTopicId(),
                            trainingTopicsModel.getCourseContentId(),
                            1,
                            trainingLastseen.getStartTime(),
                            trainingLastseen.getLastseen(),
                            LocalDateTime.now().toString()
                    )).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onTrainingTopicsList, this::onGettingError));
        }
    }

    private void onTrainingTopicsList(TrainingCourseUpdateResponse trainingCourseUpdateResponse) {
        compositeDisposable.add(dataBase.getMasterAttendanceDao()
                .updatestatus(trainingLastseen.getCourseId(),
                        trainingLastseen.getCourseTopicId(),
                        trainingLastseen.getCourseContentId(),
                        trainingLastseen.getSession())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingStatusUpdate, this::onGettingError));
    }

    private void onTrainingStatusUpdate() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::sendResultIntent, 1000);
    }

    private void onGettingError(Throwable throwable) {
        sendResultIntent();
    }

    private void sendResultIntent() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception ignored) {}

//        Intent resultIntent = new Intent();
//        resultIntent.putExtra("trainingTopic", trainingTopicsModel);
//        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void onApiError(Throwable throwable) {
        Log.d("Test143143123", throwable.toString());
    }

    private void onApiError3(Throwable throwable) {
        Log.d("teste123", throwable.toString());
    }

    private void onApiError2(Throwable throwable) {
        trainingLastseen = trainingTopicsModel;
        trainingLastseen.setSyncStatus(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trainingLastseen.setStartTime(LocalDateTime.now().toString());
        }
        trainingLastseen.setSession(UUID.randomUUID().toString());
        compositeDisposable.add(dataBase.getMasterAttendanceDao()
                .insertCoursesList(trainingLastseen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->{}, this::onApiError3));
    }

    @Override
    public void onBackPressed() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (trainingTopicsModel == null) {
                super.onBackPressed();
                return;
            }

            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }

            if (trainingLastseen != null) {

                Log.d("LASTSEEN", "UPDATE FLOW");

                trainingLastseen.setLastseen(LocalDateTime.now().toString());
                trainingLastseen.setSyncStatus(0);

                compositeDisposable.add(
                        dataBase.getMasterAttendanceDao()
                                .updatelastseen(
                                        trainingLastseen.getCourseId(),
                                        trainingTopicsModel.getCourseTopicId(),
                                        trainingTopicsModel.getCourseContentId(),
                                        trainingLastseen.getStartTime(),
//                                        trainingTopicsModel.getLastseen(), // commenting by Ashu
                                        trainingLastseen.getLastseen(),
                                        trainingLastseen.getSession()
                                )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        this::onTrainingupdatelist,
                                        this::onGettingError
                                )
                );
            }

            else {
                Log.d("LASTSEEN", "INSERT FLOW");
                trainingLastseen = trainingTopicsModel;
                trainingLastseen.setSyncStatus(0);

                trainingLastseen.setStartTime(LocalDateTime.now().toString());
                trainingLastseen.setLastseen(LocalDateTime.now().toString());
                trainingLastseen.setSession(UUID.randomUUID().toString());

                compositeDisposable.add(
                        dataBase.getMasterAttendanceDao()
                                .insertCoursesList(trainingLastseen)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        this::onTrainingupdatelist,
                                        this::onGettingError
                                )
                );
            }
        }

//        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
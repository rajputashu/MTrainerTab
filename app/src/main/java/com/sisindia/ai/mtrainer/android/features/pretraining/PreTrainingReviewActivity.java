package com.sisindia.ai.mtrainer.android.features.pretraining;

import static com.droidcommons.base.timer.CountUpTimer.REVIEW_INFORMATION_TIME_SPENT;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.IRQ_ON_START_TRAINING;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_LOCATION_PERMISSION;
import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.TRAINING_CANCEL;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_CANCEL_TRAINING_CLICK;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_START_TRAINING_CLICK;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_VIEW_PAGER;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_VIEW_PAGER1;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import com.droidcommons.preference.Prefs;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PreTrainingConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityPreTrainingReviewBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;
import com.sisindia.ai.mtrainer.android.features.starttraining.StartTraningActivity;
import com.sisindia.ai.mtrainer.android.features.syncadapter.GpsTracker;
import com.sisindia.ai.mtrainer.android.models.CancelTrainingRequest;
import com.sisindia.ai.mtrainer.android.models.CancelTrainingResponse;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PreTrainingReviewActivity extends MTrainerBaseActivity {
    private static final String TAG = "PreTrainingReviewActivi";
    private ActivityPreTrainingReviewBinding binding;
    private PreTrainingReviewViewModel viewModel;
    // Have alternative
    private int clickedView;
    private boolean isClicked = false;
    GpsTracker gpsTracker;
    // Have alternative
    public static HashSet<String> selectedAssessmentEmpId = new HashSet<>();
    private MtrainerDataBase dataBase;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean isCancelingTraining = false;
    private boolean isGoingBack = false;


    @Inject
    DashBoardApi dashBoardApi;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, PreTrainingReviewActivity.class);
        return intent;
    }

    @Override
    protected void extractBundle() {
    }

    private void onTaskCreatedSuccess() {
        finish();
    }


    @Override
    protected void initViewState() {
        liveTimerEvent.observe(this, message -> {
            switch (message.what) {
                case REVIEW_INFORMATION_TIME_SPENT:
                    bindReviewInformationTime(message.arg1);
                    break;
            }
        });

        liveData.observe(this, message -> {
            switch (message.what) {
                case ON_START_TRAINING_CLICK:
                    if (!isClicked) {
                        viewModel.setIsLoading(true);
                        isClicked = true;
                        initStartTrainingRequest();
                        // openStartTrainingScreen();
                    }
                    break;

                case ON_CANCEL_TRAINING_CLICK:
                    if (!isClicked) {
                        viewModel.setIsLoading(true);
                        isClicked = true;
                        clickedView = ON_CANCEL_TRAINING_CLICK;
                        initCancelTraining();
                    }
                    break;

                case NavigationConstants.ON_TRAINING_TOPIC_ITEM_CLICK:
                    openTrainingTopicScreen();
                    break;

                case NavigationConstants.ON_OPEN_TRAINING_DETAIL:
                    openTrainingDetailScreen();
                    break;

                case OPEN_VIEW_PAGER:
/*
                    binding.llViewpager.setVisibility(( binding.llViewpager.getVisibility() == View.VISIBLE)
                            ? View.GONE : View.VISIBLE);*/
                    binding.llViewpager.setVisibility(View.VISIBLE);
                    binding.arrow1.setVisibility(View.VISIBLE);
                    binding.arrow.setVisibility(View.GONE);
                    binding.viewDashLine.setVisibility(View.VISIBLE);

                    break;
                case OPEN_VIEW_PAGER1:
                    binding.llViewpager.setVisibility(View.GONE);
                    binding.arrow1.setVisibility(View.GONE);
                    binding.arrow.setVisibility(View.VISIBLE);
                    binding.viewDashLine.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void initCancelTraining() {
        onLocationPermissionRequest();
    }


    private void openCancelTrainingBottomSheet() {
        startActivityForResult(CancelTrainingBottomSheetFragment.newIntent(this), TRAINING_CANCEL);

    }

    private void openTrainingDetailScreen() {
        startActivityForResult(TrainingItemDetailActivity.newIntent(this), IntentRequestCodes.IRQ_ON_TRAINING_DETAIL);
    }

    private void openTrainingTopicScreen() {
        if (getSupportFragmentManager().findFragmentByTag(TrainingTopicsBottomSheetFragment.class.getSimpleName()) == null) {
            TrainingTopicsBottomSheetFragment.newInstance().show(getSupportFragmentManager(), TrainingTopicsBottomSheetFragment.class.getSimpleName());
        }
    }

    private void initStartTrainingRequest() {
        selectedAssessmentEmpId.clear();
        onLocationPermissionRequest();
    }

    private void onSucessDBSaveTest() {
        isClicked = false;
        viewModel.setIsLoading(false);
        startActivityForResult(StartTraningActivity.newIntent(this), IRQ_ON_START_TRAINING);
    }

    @Override
    protected void onCreated() {
        Log.d(TAG, "onCreated: Called");
        dataBase = MtrainerDataBase.getDatabase(getApplicationContext());

        gpsTracker = new GpsTracker(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    mLastLocation = locationResult.getLastLocation();
                    Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                    Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                    if (clickedView == ON_CANCEL_TRAINING_CLICK)
                        openCancelTrainingBottomSheet();
                    else
                        openStartTrainingScreen();
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    Log.d("got location123", "Sucess");
                } else {
                    isClicked = false;
                    viewModel.setIsLoading(false);
                    showToast("Error fetching location");
                }
            }
        };
        setupToolBarForBackArrow(binding.tbPreTrainingReview);
        initTabLayout();
        viewModel.initViewModel();
        viewModel.getPreviousTraining();
        viewModel.timer.start();
        viewModel.getChooseTopics();
        binding.navigateBtn.setOnClickListener(v -> {
            showToast("No GPS ping found");
        });
        if (Prefs.getInt(PrefsConstants.TRAINING_TYPE_ID) == 2) {
            binding.trainingType.setText("");
            binding.trainingType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_van, 0, 0, 0);
        } else {
            binding.trainingType.setText("");
            binding.trainingType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tab, 0, 0, 0);
        }
    }


    //u r code calling here
    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getMasterList().observe(this, new Observer<List<ChooseTopicsResponse.TopicsResponse>>() {
            @Override
            public void onChanged(List<ChooseTopicsResponse.TopicsResponse> topicsResponses) {
                if (topicsResponses.size() != 0) {
                    String rotaTopicId = Prefs.getString(PrefsConstants.TOPIC_ID);
                    ArrayList<ChooseTopicsResponse.TopicsResponse> rotaTopicList = new ArrayList<>();
                    if (rotaTopicId.length() != 1) {
                        StringTokenizer tokenizer = new StringTokenizer(rotaTopicId, ",");
                        while (tokenizer.hasMoreElements()) {
                            int topicId = Integer.parseInt(tokenizer.nextToken().trim());
                           /* ChooseTopicsResponse.TopicsResponse topicsResponse = topicsResponses.stream().filter(new Predicate<ChooseTopicsResponse.TopicsResponse>() {
                                @Override
                                public boolean test(ChooseTopicsResponse.TopicsResponse topicsResponse) {
                                    if(topicsResponse.topicId == topicId)
                                        return true;
                                    return false;
                                }
                            })
                                    .findAny().orElse(null);*/
                            ChooseTopicsResponse.TopicsResponse topicsResponse = null;
                            for (ChooseTopicsResponse.TopicsResponse e : topicsResponses) {
                                if (topicId == e.topicId)
                                    topicsResponse = e;

                            }
                            if (topicsResponse != null)
                                rotaTopicList.add(topicsResponse);
                        }
                    } else {
                        int topicId = Integer.parseInt(rotaTopicId);
                       /* ChooseTopicsResponse.TopicsResponse topicsResponse = topicsResponses.stream().filter(new Predicate<ChooseTopicsResponse.TopicsResponse>() {
                            @Override
                            public boolean test(ChooseTopicsResponse.TopicsResponse topicsResponse) {
                                if(topicsResponse.topicId == topicId)
                                    return true;
                                return false;
                            }
                        })
                                .findAny().orElse(null);*/
                        ChooseTopicsResponse.TopicsResponse topicsResponse = null;

                        for (ChooseTopicsResponse.TopicsResponse e : topicsResponses) {
                            if (topicId == e.topicId)
                                topicsResponse = e;
                        }

                        if (topicsResponse != null)
                            rotaTopicList.add(topicsResponse);
                    }
                    viewModel.setRotaData(rotaTopicList);
                }
            }
        });
    }

    private void initTabLayout() {

        new TabLayoutMediator(binding.tlPreviousTraining, binding.vpPreviousTraining, (tab, position) -> {
            View view = getLayoutInflater().inflate(R.layout.layout_tab_header_indicator, null);
            tab.setCustomView(view);
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_pre_training_review, menu);
        return super.onCreateOptionsMenu(menu);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionVideo:
                //showToast("Video Clicked");
                break;

            case android.R.id.home: {
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void initViewBinding() {
        binding = (ActivityPreTrainingReviewBinding) bindActivityView(this, getLayoutResource());
        viewModel.setPicasso();
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    private void bindReviewInformationTime(int seconds) {
        // binding.tvDuration.setText(TimeUtils.convertIntSecondsToMM(seconds)+" "+"min ago");
        if (Prefs.getString(PrefsConstants.PRE_TIME_SAVE) != null && Prefs.getString(PrefsConstants.PRE_TIME_SAVE).length() > 0) {
            binding.tvDuration.setText(Prefs.getString(PrefsConstants.PRE_TIME_SAVE));
        } else {
            binding.tvDuration.setText(TimeUtils.convertIntSecondsToMM(seconds));

        }
    }

    @Override
    protected void initViewModel() {
        viewModel = (PreTrainingReviewViewModel) getAndroidViewModel(PreTrainingReviewViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_pre_training_review;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IRQ_ON_START_TRAINING:
                Timber.e("On Start training finish");
                finish();
                break;
            case REQUEST_CODE_RESOLUTION_REQUIRED:
                if (resultCode == Activity.RESULT_OK) {
                    onLocationRequest();
                } else {
                    isClicked = false;
                    viewModel.setIsLoading(false);
                    showToast("GPS is needed");
                }
                break;
            case TRAINING_CANCEL:
                if (resultCode == RESULT_OK) {

                    String data1 = data.getStringExtra("Value");
                    String data2 = data.getStringExtra("Val");
                    Log.e("value", "it is" + data1 + " " + data2);

                    //below logic written for cancel training api calling
                    String empId = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
                    double lat = Prefs.getDouble(PrefsConstants.LAT);
                    double longi = Prefs.getDouble(PrefsConstants.LONGI);
                    String trainingid = String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID));
                    CancelTrainingRequest request = new CancelTrainingRequest(trainingid, empId, "12", data1 + " " + data2, lat, longi);

                    dashBoardApi.getCancelTraining(request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::onCancelTrainingSuccess);
                    String reason = String.valueOf(data1 + "" + data2);

                    //below table updation for cancel training
                    dataBase.getTrainingCalenderDao().updateCancelTraining(12, reason, Prefs.getInt(PrefsConstants.ROTA_ID))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                    Prefs.putInt(PrefsConstants.PRE_TRAINING_STATE, PreTrainingConstants.STARTED_FOR_SITE);
                    isCancelingTraining = true;

                    Date date = new Date();

                    String duty = "Training Cancelled";
                    String km = Prefs.getString(PrefsConstants.UNIT_NAME);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                    String currenttime = sdf2.format(date);

                    TimeLineEntity timeLineResponse = new TimeLineEntity();
                    timeLineResponse.dutyon = duty;
                    timeLineResponse.time = currenttime;
                    timeLineResponse.kmmeter = km;

                    dataBase.getTimeLineDao().insertTimeline(timeLineResponse)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    finish();
                } else {
                    Prefs.putInt(PrefsConstants.PRE_TRAINING_STATE, PreTrainingConstants.STARTED_FOR_SITE);
                    isCancelingTraining = true;
                    finish();
                }
        }
    }

    private void onCancelTrainingSuccess(CancelTrainingResponse response) {
        showToast(response.statusMessage);
        if (response.statusCode == SUCCESS_RESPONSE) {

        }
    }


    public void onLocationPermissionRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {

            if (gpsTracker.canGetLocation()) {
                if (gpsTracker.getLatitude() != 0.0 && gpsTracker.getLongitude() != 0.0) {
                    Log.v("got location lat lon", "Sucess " + gpsTracker.getLocation().getLatitude() + "," + gpsTracker.getLocation().getLongitude());
                    mLastLocation = gpsTracker.getLocation();
                    Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                    Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                    if (clickedView == ON_CANCEL_TRAINING_CLICK)
                        openCancelTrainingBottomSheet();
                    else
                        openStartTrainingScreen();
                    //mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    Log.v("got location", "Sucess");
                } else {
                    checkLocationSetting();
                }
            } else {
                isClicked = false;
                viewModel.setIsLoading(false);
                showToast("Error fetching location");
            }

            //checkLocationSetting();
            Log.d("Pre Training", "getLocation: permissions granted");

        }
    }

    private void onLocationRequest() {
        final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    mLastLocation = locationResult.getLastLocation();
                    Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                    Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                    if (clickedView == ON_CANCEL_TRAINING_CLICK)
                        openCancelTrainingBottomSheet();
                    else
                        openStartTrainingScreen();
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                    Log.v("got location", "Sucess");
                } else {
                    isClicked = false;
                    viewModel.setIsLoading(false);
                    showToast("Error fetching location");
                }
            }
        };
        mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
    }

    // Setting the type of Request
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(200);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void checkLocationSetting() {
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(getLocationRequest());
        LocationSettingsRequest locationSettingsRequest = builder.build();
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Timber.i("All location settings are satisfied.");
                    onLocationRequest();
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Timber.i("Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(PreTrainingReviewActivity.this, REQUEST_CODE_RESOLUTION_REQUIRED);
                            } catch (IntentSender.SendIntentException sie) {
                                Timber.i("PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            isClicked = false;
                            viewModel.setIsLoading(false);
                            Timber.e(errorMessage);
                            Toast.makeText(PreTrainingReviewActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openStartTrainingScreen() {
        Log.d(TAG, "openStartTrainingScreen: Called");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String startTime = sdf.format(date);
        TrainingFinalSubmitResponse.TrainingSubmitResponse trainingFinalSubmitResponse = new TrainingFinalSubmitResponse.TrainingSubmitResponse();
        trainingFinalSubmitResponse.setRotaid(Prefs.getInt(PrefsConstants.ROTA_ID));

        // TODO: Its a Dirty Fix need concreate solution
        if (Prefs.getString(PrefsConstants.STARTED_TIME_FOR_SERVER, "-1").equals("-1")) {
            trainingFinalSubmitResponse.startTime = startTime;
            Prefs.putStringOnMainThread(PrefsConstants.STARTED_TIME_FOR_SERVER, startTime);
        } else
            trainingFinalSubmitResponse.startTime = Prefs.getString(PrefsConstants.STARTED_TIME_FOR_SERVER);

        trainingFinalSubmitResponse.startLat = String.valueOf(Prefs.getDouble(PrefsConstants.LAT));
        trainingFinalSubmitResponse.startLong = String.valueOf(Prefs.getDouble(PrefsConstants.LONGI));

        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String time1 = sdf2.format(date);

        // TODO: Its a Dirty Fix need concreate solution
        if (Prefs.getString(PrefsConstants.STARTED_TIME, "-1").equals("-1"))
            Prefs.putStringOnMainThread(PrefsConstants.STARTED_TIME, time1);
        else
            time1 = Prefs.getString(PrefsConstants.STARTED_TIME);


        Log.d(TAG, "openStartTrainingScreen: New Time : " + time1);

        MtrainerDataBase.getDatabase(this).getTrainingCalenderDao().updateStartTime(time1, Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        Log.d(TAG, "openStartTrainingScreen: Time Saved in ShardPref : " + time1);
        MtrainerDataBase.getDatabase(this).getTrainingFinalSubmitDao().insertTrainingFinalSubmit(trainingFinalSubmitResponse)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSucessDBSaveTest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //checkLocationSetting();
                    if (gpsTracker.canGetLocation()) {
                        if (gpsTracker.getLatitude() != 0.0 && gpsTracker.getLongitude() != 0.0) {
                            mLastLocation = gpsTracker.getLocation();
                            Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                            Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                            if (clickedView == ON_CANCEL_TRAINING_CLICK)
                                openCancelTrainingBottomSheet();
                            else
                                openStartTrainingScreen();
                            //mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                            Log.v("got location", "Sucess");
                        } else {
                            checkLocationSetting();
                        }
                    } else {
                        isClicked = false;
                        viewModel.setIsLoading(false);
                        showToast("Error fetching location");
                    }
                } else {
                    isClicked = false;
                    viewModel.setIsLoading(false);
                    Toast.makeText(this,
                            "Location Permission denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    // Added for Pre-Training

    @Override
    protected void onPause() {
        super.onPause();
        if (!(isCancelingTraining || isGoingBack)) {
            Integer currentState = viewModel.preTrainingStateObs.get();
            if (currentState != null)
                Prefs.putInt(PrefsConstants.PRE_TRAINING_STATE, currentState);

            else
                Prefs.putInt(PrefsConstants.PRE_TRAINING_STATE, PreTrainingConstants.STARTED_FOR_SITE);

        }


    }

    @Override
    public void onBackPressed() {
        Integer currentState = viewModel.preTrainingStateObs.get();
        if (currentState != null) {
            if (currentState == PreTrainingConstants.START_TRAINING) {
                final AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to end the session?")
                        .setPositiveButton("InProgress", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Prefs.putInt(PrefsConstants.PRE_TRAINING_STATE, PreTrainingConstants.START_TRAINING);


                                //below table updation for InProgress training
                                Prefs.putInt(PrefsConstants.IS_PROGRESS_ROTAID, Prefs.getInt(PrefsConstants.ROTA_ID));

                                MtrainerDataBase.getDatabase(PreTrainingReviewActivity.this).getTrainingCalenderDao().markInProgress(Prefs.getInt(PrefsConstants.ROTA_ID))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            isGoingBack = true;
                                            PreTrainingReviewActivity.super.onBackPressed();
                                        });

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openCancelTrainingBottomSheet();
                                dialog.dismiss();
                            }
                        }).setCancelable(false)
                        .create();
                dialog.show();
            } else
                super.onBackPressed();


        } else {
            Prefs.putInt(PrefsConstants.PRE_TRAINING_STATE, PreTrainingConstants.STARTED_FOR_SITE);
            // binding.tvDuration.setText(Prefs.getString(PrefsConstants.PRE_TIME_SAVE));
            isGoingBack = true;
            super.onBackPressed();
        }

    }


}
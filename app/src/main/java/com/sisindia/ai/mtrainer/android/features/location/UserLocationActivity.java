package com.sisindia.ai.mtrainer.android.features.location;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.droidcommons.permissions.RunTimePermissions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.features.services.LocationService;

import timber.log.Timber;

import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED;

public class UserLocationActivity extends MTrainerBaseActivity {

    private static final String INTENT_EXTRA_READ_DUTY = "is_duty_on";

    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationRequest locationRequest;

    private Boolean isOnDuty = false;

    public static Intent newIntent(Activity activity, Boolean isDutyOn) {
        Intent intent = new Intent(activity, UserLocationActivity.class);
        intent.putExtra(INTENT_EXTRA_READ_DUTY, isDutyOn);
        return intent;
    }

    @Override
    protected void extractBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(INTENT_EXTRA_READ_DUTY)) {
            isOnDuty = bundle.getBoolean(INTENT_EXTRA_READ_DUTY);
        }

    }

    @Override
    protected void initViewState() {

    }

    @Override
    protected void onCreated() {
        if (!isOnDuty) {
            // TODO: 2020-03-25 Stop Service and finish
            failedResult();
        } else {
            settingsClient = LocationServices.getSettingsClient(this);

            createLocationRequest();
            buildLocationSettings();
            fetchUserLocation();
        }

    }




    private void buildLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void fetchUserLocation() {
        if (RunTimePermissions.checkLocationPermissions(this)) {

            // Begin by checking if the device has the necessary location settings.
            settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(this, locationSettingsResponse -> {
                        Timber.i("All location settings are satisfied.");

                        //noinspection MissingPermission
                        startLocationService();

                    })
                    .addOnFailureListener(this, e -> {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Timber.i("Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(UserLocationActivity.this, REQUEST_CODE_RESOLUTION_REQUIRED);
                                } catch (IntentSender.SendIntentException sie) {
                                    Timber.i("PendingIntent unable to execute request.");
                                    failedResult();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Timber.e(errorMessage);
                                Toast.makeText(UserLocationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                failedResult();
                        }
                    });
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(RunTimePermissions.getLocationPermissions(), RunTimePermissions.RC_LOCATION_PERMISSION);
            } else {
                showToast("Permission Needed");
            }
        }
    }





    @Override
    protected void initViewBinding() {

    }

    @Override
    protected void initViewModel() {

    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RunTimePermissions.RC_LOCATION_PERMISSION) {
            if (RunTimePermissions.checkLocationPermissions(this)) {
                fetchUserLocation();
            } else {
                Toast.makeText(this, "Unable to fetch Location ... please enable in App Settings", Toast.LENGTH_SHORT).show();
                failedResult();
            }
        }
    }



    void failedResult() {
        stopService();
        setResult(Activity.RESULT_CANCELED);
        this.finish();
    }

    void startLocationService() {
        startService(new Intent(this, LocationService.class));
        setResult(Activity.RESULT_OK);
        this.finish();
    }

    void stopService() {
        stopService(new Intent(this, LocationService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RESOLUTION_REQUIRED) {
            if (resultCode == Activity.RESULT_OK) {
                fetchUserLocation();
            } else {
                failedResult();
            }
        }
    }
}


package com.sisindia.ai.mtrainer.android.features.onboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.droidcommons.permissions.RunTimePermissions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityOnBoardBinding;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardActivity;
import com.sisindia.ai.mtrainer.android.features.login.LoginWithMobileNumberFragment;
import com.sisindia.ai.mtrainer.android.features.otp.LoginOtpFragment;
import com.sisindia.ai.mtrainer.android.features.splash.SplashFragment;

import java.io.File;
import java.util.Objects;

import timber.log.Timber;

public class OnBoardActivity extends MTrainerBaseActivity {

    private OnBoardViewModel viewModel;
    private BroadcastReceiver onComplete;
    //    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private boolean isReturnedFromSettings = false;

    @Override
    protected void extractBundle() {
        /*int savedVersionCode = Prefs.getInt(PrefsConstants.CURRENT_VERSION_CODE, 0);
        if(savedVersionCode < BuildConfig.VERSION_CODE) {
            String dutyTime = Prefs.getString(PrefsConstants.DUTY_ON_UI_TIME, "");
            String gpsKey = Prefs.getString(PrefsConstants.LAST_GPS_PAIRING_KEY, "");
            boolean isDutyOn = Prefs.getBoolean(PrefsConstants.IS_ON_DUTY, false);
            Prefs.clear();
            Prefs.putStringOnMainThread(PrefsConstants.DUTY_ON_UI_TIME, dutyTime);
            Prefs.putStringOnMainThread(PrefsConstants.LAST_GPS_PAIRING_KEY, gpsKey);
            Prefs.putBooleanOnMainThread(PrefsConstants.IS_ON_DUTY, isDutyOn);
            Prefs.putIntOnMainThread(PrefsConstants.CURRENT_VERSION_CODE, BuildConfig.VERSION_CODE);
        }*/
    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            switch (message.what) {

                case NavigationConstants.OPEN_LOGIN:
                    openLoginScreen();
                    break;

                case NavigationConstants.OPEN_DASH_BOARD:
                    openDashBoardScreen();
                    break;

                case NavigationConstants.ON_LOGIN_SUBMIT_MOBILE_NUMBER:
                    openOtpScreen();
                    break;

                case NavigationConstants.INSTALL_NEW_APK:
                    installLatestApk((String) message.obj);
                    break;
            }
        });
    }

    private void openDashBoardScreen() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.putExtra("IS_FROM_DASHBOARD", true);
        startActivity(intent);
        finish();
    }

    private void openOtpScreen() {
        loadFragment(R.id.flOnBoard, LoginOtpFragment.newInstance(), FRAGMENT_REPLACE, false);
    }

    void openSplashScreen() {
        initUpdateReceiver();
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        loadFragment(R.id.flOnBoard, SplashFragment.newInstance(), FRAGMENT_REPLACE, false);
    }

    private void openLoginScreen() {
        loadFragment(R.id.flOnBoard, LoginWithMobileNumberFragment.newInstance(), FRAGMENT_REPLACE, false);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreated() {
        if (RunTimePermissions.hasAppPermissions(this)) {
            if (RunTimePermissions.hasBGLocationGranted(this))
                requestForPostNotification();
            else
                ActivityCompat.requestPermissions(this, RunTimePermissions.getBGLocationPermissions(), RunTimePermissions.BG_LOCATION_PERMISSION);
        } else
            ActivityCompat.requestPermissions(this, RunTimePermissions.getAllAppPermissions(), RunTimePermissions.RC_APP_PERMISSION);
    }

    private void installLatestApk(String path) {
        OnBoardViewModel.appName = path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
            } else {
                installAPK();
            }
        } else {
            installAPK();
        }
    }

    @Override
    protected void initViewBinding() {
        ActivityOnBoardBinding binding = (ActivityOnBoardBinding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (OnBoardViewModel) getAndroidViewModel(OnBoardViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_on_board;
    }

    private void requestForPostNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (RunTimePermissions.hasPostNotificationGranted(this))
                enableLocationIfRequired();
            else
                ActivityCompat.requestPermissions(this, RunTimePermissions.getPostNotificationPermissions(), RunTimePermissions.POST_NOTIFICATION_PERMISSION);
        } else
            enableLocationIfRequired();
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder
                .setMessage(message)
                .setPositiveButton("Proceed", okListener)
                .create();

        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null)
            positiveButton.setTextColor(Color.BLACK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RunTimePermissions.RC_APP_PERMISSION)
            checkIsAllPermissionGranted();
        else if (requestCode == RunTimePermissions.BG_LOCATION_PERMISSION)
            checkBGLocationPermissionGranted();
        else if (requestCode == RunTimePermissions.POST_NOTIFICATION_PERMISSION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                checkPostNotificationPermissionGranted();
    }


    public void enableLocationIfRequired() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(10000 / 2)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    openSplashScreen();
                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(OnBoardActivity.this, IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Timber.e(sendEx);
                            onBackPressed();
                        }
                    }
                });
    }

    private void checkIsAllPermissionGranted() {
        if (RunTimePermissions.hasAppPermissions(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                onCreated();
            else
                enableLocationIfRequired();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                String message = "Important Message!\n\niOPS2.0 needs all permission. Kindly allow required permission without denial";
                openLocationPermissionSettingDialog(false, message);
            } else {
                requestPermissions(RunTimePermissions.getAllAppPermissions(), RunTimePermissions.RC_APP_PERMISSION);
            }
        }
    }

    private void checkBGLocationPermissionGranted() {
        if (RunTimePermissions.hasBGLocationGranted(this)) {
            requestForPostNotification();
        } else {
            String message = "Important Message!\n\niOPS2.0 need full access of location service. Kindly choose\n\nPermission -> Location -> Allow all the time";
            openLocationPermissionSettingDialog(true, message);
        }
    }

    private void checkPostNotificationPermissionGranted() {
        if (RunTimePermissions.hasPostNotificationGranted(this)) {
            enableLocationIfRequired();
        } else {
            String message = "Notification permission is required to mark your duty on and off, Kindly grant";
            showToast(message);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ActivityCompat.requestPermissions(this, RunTimePermissions.getPostNotificationPermissions(), RunTimePermissions.POST_NOTIFICATION_PERMISSION);
        }
    }


    private void openLocationPermissionSettingDialog(boolean isForLocation, String message) {

        showDialogOK(message, (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (isForLocation) {
                        openSettingScreen();
                    } else {
                        ActivityCompat.requestPermissions(OnBoardActivity.this, RunTimePermissions.getAllAppPermissions(), RunTimePermissions.RC_APP_PERMISSION);
                    }

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    finish();
                    break;
            }
        });
    }

    private void openSettingScreen() {
        isReturnedFromSettings = true;
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){
            Intent fallbackIntent = new Intent(Settings.ACTION_SETTINGS);
            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(fallbackIntent);
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.SET_ALARM, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WAKE_LOCK, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, PackageManager.PERMISSION_GRANTED);
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.SET_ALARM) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {


                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SET_ALARM) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WAKE_LOCK) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {

                        showDialogOK("Call, Imei, Recording, Folder, Camera and Location Services Permission required for this app",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkAndRequestPermissions();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:

                                            finish();
                                            break;
                                    }
                                });
                    }

                }
            }
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            if (getPackageManager().canRequestPackageInstalls())
                installAPK();
        } else if (requestCode == IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED) {
            if (resultCode == Activity.RESULT_OK)
                openSplashScreen();
            else
                onBackPressed();
        }
    }

    private void installAPK() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        Log.v("Version Code :", "Log " + BuildConfig.VERSION_CODE);
        String path = Objects.requireNonNull(getExternalFilesDir("apk")).getAbsolutePath();
        Log.v("Path : ", " " + path);
        File file = new File(OnBoardViewModel.appName);
        Uri apkURI = FileProvider.getUriForFile(
                this,
                this.getApplicationContext()
                        .getPackageName() + ".provider", file);
        intent.setDataAndType(apkURI, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void initUpdateReceiver() {
        onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == OnBoardViewModel.id) {
                    installLatestApk(OnBoardViewModel.appName);
                    unregisterReceiver(onComplete);
                    //finish();
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (onComplete != null)
                unregisterReceiver(onComplete);
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReturnedFromSettings) {
            isReturnedFromSettings = false;
            checkIsAllPermissionGranted();
        }
    }
}

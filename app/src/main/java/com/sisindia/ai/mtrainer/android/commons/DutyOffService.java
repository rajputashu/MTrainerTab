package com.sisindia.ai.mtrainer.android.commons;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.droidcommons.preference.Prefs;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.DutyStatus;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.LocationDetailsEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DutyOffService extends Service {
    private static final String TAG = "DutyOffService";
    private static final int NOTIFICATION_ID = 12345678;
    private static final String CHANNEL_ID = "channel_01";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private MtrainerDataBase dataBase;
    private CompositeDisposable disposable = new CompositeDisposable();
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        dataBase = MtrainerDataBase.getDatabase(this);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        if (Prefs.getBoolean(PrefsConstants.IS_ON_DUTY, false)) {
            turnOffDuty();
            return START_STICKY;
        }
        else
            stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void turnOffDuty() {
            holdWakeLock();
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    || !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                saveDataLocally(false);
                Log.d(TAG, "onReceive: + null" );
            } else {
                onLocationRequest();
                Log.d(TAG, "onReceive: + non-null" );
            }
    }



    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText("Turning Off Duty")
                .setContentTitle("Please don't turn off the device")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_mtrainer_white)
                .setAutoCancel(false);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    private void onLocationRequest() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location mLastLocation = locationResult.getLastLocation();
                    Prefs.putDouble(PrefsConstants.LAT, mLastLocation.getLatitude());
                    Prefs.putDouble(PrefsConstants.LONGI, mLastLocation.getLongitude());
                    Log.d(TAG, "onLocationResult: ");
                    saveDataLocally(true);
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        };
        mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(200);
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void saveDataLocally(boolean hasLocation) {
        Log.d(TAG, "saveDataLocally: ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        LocationDetailsEntity ping = new LocationDetailsEntity();
        ping.dateTime = sdf.format(new Date());
        ping.latitude = hasLocation ? Prefs.getDouble(PrefsConstants.LAT) : -500;
        ping.longitude = hasLocation ? Prefs.getDouble(PrefsConstants.LONGI) : -500;
        ping.status = DutyStatus.DUTY_OFF;
        ping.pairingToken = Prefs.getLong(PrefsConstants.LAST_DUTY_ON_id, -1);

        disposable.add(dataBase.getGpsPingDao().insertLocationDetail(ping)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                            Log.d(TAG, "saveDataLocally: RxJava");
                            Prefs.putBooleanOnMainThread(PrefsConstants.IS_ON_DUTY, false);
                            releaseWakeLock();
                            stopSelf();
                        },
                        throwable -> {}));
    }

    private void holdWakeLock() {
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getSimpleName() + ":WakeLock");
        wakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld())
            wakeLock.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }
}
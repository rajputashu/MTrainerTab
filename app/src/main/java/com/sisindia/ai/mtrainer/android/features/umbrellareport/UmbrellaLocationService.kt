package com.sisindia.ai.mtrainer.android.features.umbrellareport

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.droidcommons.preference.Prefs
import com.google.android.gms.location.*
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants
import timber.log.Timber
import java.util.*

class UmbrellaLocationService : Service() {
    lateinit var fusedLocClient: FusedLocationProviderClient
    lateinit var notificationManager : NotificationManager
    private val NOTIFICATION_ID = 1289

    val LOCATION_CALLBACK: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                Timber.i("Got Location lastLocation $result")
                result?.lastLocation?.let {
                    Prefs.putDouble(PrefsConstants.LAT, it.latitude)
                    Prefs.putDouble(PrefsConstants.LONGI, it.longitude)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isDutyOn = Prefs.getBoolean(PrefsConstants.IS_ON_DUTY, false)
        //if (isDutyOn) {
            startForeground(NOTIFICATION_ID, getNotification())
            getLocation()
            return START_STICKY
        //} else {
            stopSelf()
            return START_NOT_STICKY
    //    }
    }

    private fun getLocationRequest(): LocationRequest = LocationRequest().apply {
        interval = 2 * 60 * 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun getLocation() {
        Timber.i("getLocation Called")
        fusedLocClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Timber.i("Got Location lastLocation")
            fusedLocClient.lastLocation
                .addOnSuccessListener {
                    Timber.i("Got Location lastLocation $it")
                    it?.let {
                        if ((Date().time - it.time) < 120000) {
                            Prefs.putDouble(PrefsConstants.LAT, it.latitude)
                            Prefs.putDouble(PrefsConstants.LONGI, it.longitude)
                            fusedLocClient.requestLocationUpdates(
                                getLocationRequest(),
                                LOCATION_CALLBACK,
                                null
                            )
                            Timber.i("Got Location lastLocation")
                        } else {
                            Timber.i("Request location update")
                            fusedLocClient.requestLocationUpdates(
                                getLocationRequest(),
                                LOCATION_CALLBACK,
                                null
                            )
                        }
                    } ?: fusedLocClient.requestLocationUpdates(
                        getLocationRequest(),
                        LOCATION_CALLBACK,
                        null
                    )
                }
                .addOnFailureListener {
                    Timber.i("Got Location lastLocation Error -> $it")
                    fusedLocClient.requestLocationUpdates(
                        getLocationRequest(),
                        LOCATION_CALLBACK,
                        null
                    )
                }

        }
    }

    private fun getNotification(): Notification {
        val builder = NotificationCompat.Builder(this)
            .setContentText("Getting Location")
            .setContentTitle("Mtrainer")
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_mtrainer_white)
            .setAutoCancel(false)

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("Mtrainer Location Service")  // Channel ID
        }
        return builder.build()
    }

    private fun createChannel() {
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Mtrainer"
            // Create the channel for the notification
            val mChannel = NotificationChannel(
                "Mtrainer Location Service",
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel)
        }
    }


    override fun onBind(intent: Intent): IBinder?{
        return null
    }
}

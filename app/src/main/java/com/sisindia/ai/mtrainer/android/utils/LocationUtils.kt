package com.sisindia.ai.mtrainer.android.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.droidcommons.preference.Prefs
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants
import com.sisindia.ai.mtrainer.android.features.umbrellareport.UmbrellaLocationService
import timber.log.Timber
import java.util.*
const val LOCATION_SETTING_RESOLUTION_CODE = 3098
const val LOCATION_PERMISSION_RESOLUTION_CODE = 3099

class LocationUtils(val onSuccess: () -> Unit) {
    companion object {
        private const val TAG = "LocationUtils"
        lateinit var fusedLocClient: FusedLocationProviderClient

        val LOCATION_CALLBACK: LocationCallback by lazy {
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    Timber.i("Got Location lastLocation $result")
                    result?.lastLocation?.let {
                        Prefs.putDouble(PrefsConstants.LAT, it.latitude)
                        Prefs.putDouble(PrefsConstants.LONGI, it.longitude)
                        fusedLocClient.removeLocationUpdates(LOCATION_CALLBACK)
//                        onSuccess.invoke()
                    } //?: onFailure.invoke()
                }
            }
        }
//        private lateinit var onSuccess: () -> Unit
//        private lateinit var onFailure: () -> Unit
        //private var isForService = false

        @JvmStatic
        private fun checkPermission(activity: Activity) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                checkLocationSetting(activity)
            } else if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSION_RESOLUTION_CODE
                )
            else if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_RESOLUTION_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_RESOLUTION_CODE
                )
        }

        @JvmStatic
        fun getLocationData(activity: Activity /*onSuccess: () -> Unit, onFailure: () -> Unit,*/ /*isForService: Boolean = false*/) {
//            this.onSuccess = onSuccess
//            this.onFailure = onFailure
            // this.isForService = isForService
            checkPermission(activity)
        }

        private fun checkLocationSetting(activity: Activity) {
            val settingsClient = LocationServices.getSettingsClient(activity)
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(getLocationRequest())
            val locationSettingsRequest = builder.build()
            settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener {
                    Timber.i("All location settings are satisfied.")
                    //if(isForService)
                    Timber.i("Services -> " + UmbrellaLocationService::class.qualifiedName)
                    if(!isServiceRunning(UmbrellaLocationService::class.qualifiedName ?: "testing", activity))
                        activity.startService(Intent(activity, UmbrellaLocationService::class.java))
                    //else
                    //  getLocation(activity)
                }
                .addOnFailureListener {
                    val statusCode = (it as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Timber.i(
                                "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings "
                            )
                            try {
                                val rae = it as ResolvableApiException
                                rae.startResolutionForResult(
                                    activity,
                                    LOCATION_SETTING_RESOLUTION_CODE
                                )
                            } catch (sie: SendIntentException) {
                                Timber.i("PendingIntent unable to execute request.")
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings."
                            //viewModel.setIsLoading(false)
                            Timber.e(errorMessage)
                            Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
        }

        private fun getLocationRequest(): LocationRequest = LocationRequest().apply {
            interval = 500
            fastestInterval = 200
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        private fun isServiceRunning(serviceName: String, context: Context): Boolean {
            var serviceRunning = false
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val l = am.getRunningServices(50)
            for (runningServiceInfo in l) {
                Log.d(
                    TAG,
                    "isServiceRunning: " + runningServiceInfo.service.className
                )
                if (runningServiceInfo.service.className == serviceName) {
                    serviceRunning = true
                    break

                    /*if(runningServiceInfo.foreground)
                {
                    //service run in foreground
                }*/
                }
            }
            Log.d(TAG, "isServiceRunning: $serviceRunning")
            return serviceRunning
        }
    }
}
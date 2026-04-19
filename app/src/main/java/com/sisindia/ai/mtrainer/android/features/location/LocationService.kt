package com.sisindia.ai.mtrainer.android.features.location

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.sisindia.ai.mtrainer.android.R
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class LocationService : Service() {

    private val TAG = "LocationService"
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mLocationManager: LocationManager
    private val NOTIFICATION_ID = 1234
    private lateinit var locationCallback: LocationCallback
    private lateinit var gpsStatusReciver: GpsStatusReciver

    override fun onCreate() {
        mNotificationManager =
            getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager
        gpsStatusReciver = GpsStatusReciver()
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val filter = IntentFilter()
        filter.addAction("android.location.PROVIDERS_CHANGED")
        registerReceiver(gpsStatusReciver, filter)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(results: LocationResult?) {
                Log.d(TAG, "onLocationResult: $results")
                results ?: return
                val loc = results.locations[0]
                writeDataToFile(loc)
                Log.d(TAG, "onLocationResult: ${loc.time} ->  ${loc.latitude}  :  ${loc.longitude}")
            }
        }
        // Android O requires a Notification Channel.
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Mtrainer"
            // Create the channel for the notification
            val mChannel = NotificationChannel(
                "Mtrainer_Locaion_Service",
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val status = intent?.getIntExtra("STATUS", 1)
        if (status == 0) {
            writeEventToFile("Stop Service Called")
            stopSelf()
            return START_NOT_STICKY
        } else {
            writeEventToFile("onStartCommand Called")
            startForeground(NOTIFICATION_ID, getNotification())
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "onStartCommand: Don't have Permission")
                writeEventToFile("onStartCommand: Don't have Permission")
            } else {
                mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    15 * 60 * 1000,
                    0f,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            Log.d(
                                TAG,
                                "initLocationListener: ${location.longitude} : ${location.latitude}  ${location.time}"
                            )
                            writeDataToFile(location)
                        }

                        override fun onProviderDisabled(provider: String) {
                        }

                        override fun onProviderEnabled(provider: String) {
                        }

                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: Bundle?
                        ) {
                        }
                    })
            }
            writeEventToFile("onStartCommand : Foreground Service Started")
            return START_NOT_STICKY
        }
    }

    override fun onDestroy() {
        writeEventToFile("On onDestroy called -- unregisterReceiver")
        unregisterReceiver(gpsStatusReciver)
    }

    private fun getNotification(): Notification? {
        val builder = NotificationCompat.Builder(this)
            .setContentText("Getting Location")
            .setContentTitle("Location POC")
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_mtrainer_white)
            .setAutoCancel(false)

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("Locaion_POC_Service")  // Channel ID
        }
        return builder.build()
    }

    private fun writeEventToFile(eventData: String) {
        val rootPath = getExternalFilesDir("Files")!!.absolutePath
        val file = File(rootPath, "events_logs.txt")
        var outputStream: FileWriter? = null
        try {
            outputStream = FileWriter(file, true)
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val data = sdf.format(Date()) + "  : " + eventData
            outputStream.append(
                """
                $data
                
                """.trimIndent()
            )
            Log.i(
                TAG,
                "writeEventToFile: Written $data"
            )
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun writeDataToFile(location: Location?) {
        val rootPath = getExternalFilesDir("Files")!!.absolutePath
        val file = File(rootPath, "location_logs.txt")
        var outputStream: FileWriter? = null
        try {
            outputStream = FileWriter(file, true)
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val data =
                sdf.format(Date()) + if (location == null) "No Location" else "  " + location.latitude + "  " + location.longitude
            outputStream.append(
                """
                $data
                
                """.trimIndent()
            )
            Log.i(
                TAG,
                "writeDataToFile: Written $data"
            )
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
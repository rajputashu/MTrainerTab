package com.sisindia.ai.mtrainer.android.features.location

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.sisindia.ai.mtrainer.android.R


class GpsStatusReciver : BroadcastReceiver() {
    private val TAG = "GpsStatusReciver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Location Providers State Checking ...")
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!isGpsEnabled) {
            try {
                val dialog = Dialog(context.applicationContext)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                else
                    dialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                val view = View.inflate(context.applicationContext, R.layout.gps_alert_dialog, null)
                view.findViewById<Button>(R.id.btn).setOnClickListener {
                    val isGpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    if(isGpsOn) {
                        if (dialog.isShowing)
                            dialog.dismiss()
                    }
                }
                dialog.setContentView(view);
                dialog.setCancelable(false);
                dialog.show()
            }catch (e:Exception){}
        }
    }

}
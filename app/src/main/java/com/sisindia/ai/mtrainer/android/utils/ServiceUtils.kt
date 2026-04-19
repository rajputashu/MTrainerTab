package com.sisindia.ai.mtrainer.android.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log

class ServiceUtils {
    companion object {
        private  val TAG = "ServiceUtils"

        fun isServiceRunning(serviceName: String, context: Context): Boolean {
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
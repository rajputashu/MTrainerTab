package com.sisindia.ai.mtrainer.android.features.location;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.AlarmManagerCompat;

import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class AutoStartServiceReciver extends BroadcastReceiver {
    private static final String TAG = "AutoStartServiceReciver";
    final int PENDING_INTENT_REQUEST_CODE = 1213;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent receiverIntent = new Intent(context, AutoStartServiceReciver.class);
        if (intent.getAction().matches("android.intent.action.BOOT_COMPLETED")) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(context, PENDING_INTENT_REQUEST_CODE, receiverIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!isServiceRunning(LocationService.class.getName(), context)) {
                    Intent serviceIntent = new Intent(context, LocationService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                }
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis() + (10 * 60 * 1000), pendingIntent);
            }
        }  else {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(context, PENDING_INTENT_REQUEST_CODE, receiverIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!isServiceRunning(LocationService.class.getName(), context)) {
                    Intent serviceIntent = new Intent(context, LocationService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                }
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis() + (10 * 60 * 1000), pendingIntent);
            } else {
                if (!isServiceRunning(LocationService.class.getName(), context)) {
                    Intent serviceIntent = new Intent(context, LocationService.class);
                    context.startService(serviceIntent);
                }
            }
        }
    }

    private boolean isServiceRunning(String serviceName, Context context) {
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : l) {
            Log.d(TAG, "isServiceRunning: " + runningServiceInfo.service.getClassName());
            if (runningServiceInfo.service.getClassName().equals(serviceName)) {
                serviceRunning = true;
                break;

                /*if(runningServiceInfo.foreground)
                {
                    //service run in foreground
                }*/
            }
        }
        return serviceRunning;
    }
}

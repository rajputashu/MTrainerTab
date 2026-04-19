package com.sisindia.ai.mtrainer.android.features.location;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sisindia.ai.mtrainer.android.utils.ServiceUtils;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class LocationWorker extends Worker {
    private Context context;
    private static final String TAG = "LocationWorker";

    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!ServiceUtils.Companion.isServiceRunning(LocationService.class.getName(), context)) {
            Intent serviceIntent = new Intent(context, LocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
        return Result.success();
    }
}

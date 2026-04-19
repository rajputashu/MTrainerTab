package com.sisindia.ai.mtrainer.android.features.starttraining;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.Constant;

public class StartTrainingNotificationService extends Service {
    private IBinder serviceBinder = new ServiceBinder();
    private final String CHANNEL_ID = "1000";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    class ServiceBinder extends Binder {
        StartTrainingNotificationService getService() {
            return StartTrainingNotificationService.this;
        }
    }

    private void createNotification() {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mtrainer_white)
                .setContentTitle("Mtrainer")
                .setContentText("Training Started")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(Constant.FOREGROUND_SERVICE_ID, builder.build());
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Mtrainer", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Mtrainer Notification Channel");
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

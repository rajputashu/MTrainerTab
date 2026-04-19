package com.sisindia.ai.mtrainer.android.features.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sisindia.ai.mtrainer.android.utils.NotificationUtil;

public class NotificationRequestReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil.createNotification(context, "Training starting soon",intent.getStringExtra("SITE_NAME"), intent.getIntExtra("ROTA_ID", 12));
    }
}

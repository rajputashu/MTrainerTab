package com.sisindia.ai.mtrainer.android.features.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sisindia.ai.mtrainer.android.commons.DutyOffService;

public class DutyStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "DutyStatusReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        context.startService(new Intent(context, DutyOffService.class));
        Log.d(TAG, "onReceive: Service Started");
    }
}
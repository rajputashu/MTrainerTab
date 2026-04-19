package com.sisindia.ai.mtrainer.android.features.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServerRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(ServerRestarterBroadcastReceiver.class.getSimpleName(), "Server Stops! Oooooooooooooppppssssss!!!!");

    }
}
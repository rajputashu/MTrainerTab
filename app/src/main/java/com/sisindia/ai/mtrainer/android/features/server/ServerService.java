package com.sisindia.ai.mtrainer.android.features.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.constants.Constant;
import com.sisindia.ai.mtrainer.android.features.rota.DashBoardRotaFragment;
import com.sisindia.ai.mtrainer.android.features.trainingkit.VideoLandingActivity;

import java.util.Objects;

//server service class
public class ServerService extends Service {

    private final int NOTIFICATION_ID = 4711;
    private static final int NOTIFICATION_LED_ON_MS = 100;
    private static final int NOTIFICATION_LED_OFF_MS = 1000;
    private static final int NOTIFICATION_ARGB_COLOR = 0xff1EB6E1;
    private NotificationManager mNM;
    //    private String message;
    private Notification notification;
    private NotificationCompat.Builder notifactionBuilder;
    private Server server;

    private boolean isRunning = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void updateNotification(String message) {
        String channelId = "Sessions";
        Intent intent = new Intent(this, DashBoardRotaFragment.class);

        // Use FLAG_IMMUTABLE for API level 31 and above
        PendingIntent pIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        /*PendingIntent pIntent = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), intent, 0);*/

        if (notification == null) {
            notifactionBuilder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Webserver")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_mtrainer_white)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setLights(
                            ServerService.NOTIFICATION_ARGB_COLOR,
                            ServerService.NOTIFICATION_LED_ON_MS,
                            ServerService.NOTIFICATION_LED_OFF_MS)
                    .setContentIntent(pIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(false);

            notification = notifactionBuilder.build();
            assert mNM != null;
            mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Sessions about to begin",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNM.createNotificationChannel(channel);
            }
            mNM.notify(NOTIFICATION_ID, notification);
        } else {
            // hide the notification after its selected
            notifactionBuilder.setContentTitle("Webserver");
            notifactionBuilder.setContentText(message);
            notifactionBuilder.setSmallIcon(R.drawable.ic_mtrainer_white);
            notifactionBuilder.setColor(getResources().getColor(R.color.colorPrimary));
            notifactionBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            notifactionBuilder.setLights(
                    ServerService.NOTIFICATION_ARGB_COLOR,
                    ServerService.NOTIFICATION_LED_ON_MS,
                    ServerService.NOTIFICATION_LED_OFF_MS);
            notifactionBuilder.setPriority(Notification.PRIORITY_MAX);
            notifactionBuilder.setAutoCancel(false);
            notifactionBuilder.setContentIntent(pIntent);
            notification = notifactionBuilder.build();
            assert mNM != null;
            mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Sessions about to begin",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNM.createNotificationChannel(channel);
            }
            mNM.notify(NOTIFICATION_ID, notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startServer(Handler handler, String documentRoot, int port) {
        try {
            Log.v("socket trainner ", "temp count = " + VideoLandingActivity.tempCount++);

            isRunning = true;
//            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
//            assert wifiManager != null;
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            //  String ipAddress = intToIp(wifiInfo.getIpAddress());

            String ipAddress = Constant.IP_ADDRESS;

//            if (wifiInfo.getSupplicantState() != SupplicantState.COMPLETED) {
//                new AlertDialog.Builder(this).setTitle("Error").setMessage("Please connect to a WIFI-network for starting the webserver.").setPositiveButton("OK", null).show();
//                throw new Exception("Please connect to a WIFI-network.");
//            }

            server = new Server(handler, documentRoot, ipAddress, port, getApplicationContext(), isRunning);
            server.start();

//            Intent i = new Intent(this, DashBoardRotaFragment.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

            updateNotification("Webserver is running on port " + ipAddress + ":" + port);
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("msg", "Webserver is running on port " + ipAddress + ":" + port);
            msg.setData(b);
            handler.sendMessage(msg);

        } catch (Exception e) {
            isRunning = false;
            Log.e("Webserver", Objects.requireNonNull(e.getMessage()));
            updateNotification("Error: " + e.getMessage());
        }
    }

    /*public static String intToIp(int i) {
        return ((i) & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }*/

    public void stopServer() {
        if (null != server) {
            server.stopServer();
            server.interrupt();
            isRunning = false;
        }
    }

    public void removeNotification() {
        Handler handler = new Handler();
        long delayInMilliseconds = 200;
        handler.postDelayed(() -> mNM.cancel(NOTIFICATION_ID), delayInMilliseconds);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {
        updateNotification("");
        startForeground(NOTIFICATION_ID, notification);
    }

    public class LocalBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    /*@TargetApi(26)
    private void createChannel(NotificationManager notificationManager, CharSequence description) {
        String name = "Webserver";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(name, name, importance);
        mChannel.setDescription(String.valueOf(description));
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }*/

    private final IBinder mBinder = new LocalBinder();

}

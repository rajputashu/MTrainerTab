package com.sisindia.ai.mtrainer.android.features;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.droidcommons.preference.Prefs;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
import com.sisindia.ai.mtrainer.android.constants.FcmServiceConstant;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.sisindia.ai.mtrainer.android.constants.FcmServiceConstant.*;

public class MtrainerFcmService extends FirebaseMessagingService {
    private static final String TAG = "MtrainerFcmService";
    private static final String CHANNEL_ID = "mtrainer_fcm_notification_channel";
    private static final int NOTIFICATION_ID =2233;
    private final String IS_DEBUG = "isDebug";
    private final String CLEAR_DATA = "clear_data";
    private NotificationCompat.Builder builder;
    private MtrainerDataBase dataBase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        dataBase = MtrainerDataBase.getDatabase(this);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());
        Map<String, String> map = remoteMessage.getData();
        String isDebug = map.get(IS_DEBUG);
        String clearData = map.get(CLEAR_DATA);
        if(isDebug !=null && isDebug.equals("1")) {
            Prefs.putBooleanOnMainThread(PrefsConstants.IS_DEBUG, true);
            //new Logger(TAG).log("Is Debug Activated");
            buildNotification();
            showNotification();
        } else {
            Prefs.putBooleanOnMainThread(PrefsConstants.IS_DEBUG, false);
            //new Logger(TAG).log("Is Debug Deactivated");
            closeNotification();
        }
        if(clearData !=null && clearData.equals("1")) {
            clearAllTable();
        }
    }

    void clearAllTable() {
        dataBase.clearAllTables();
        Prefs.clear();
        Logger logger = new Logger("MtrainerFcmService");
        //logger.log("clearAllTable - All Data Cleared");
    }

    void showNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    void buildNotification() {
        Log.d(TAG, "buildNotification: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mtrainer FCM";
            String description = "Mtrainer Channel for Showing Debug Status";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mtrainer_white)
                .setContentTitle("Mtrainer")
                .setContentText("Started Logging")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    void closeNotification() {
        Log.d(TAG, "closeNotification: ");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);
    }

   /* void getData(int type) {
        switch (type) {
            case GET_SAVED_ROTA_DATA :
                break;
            case GET_TRAINING_IMAGES:
                break;
            case GET_ASSESSMENT_DATA:
                break;
        }
    }*/
}
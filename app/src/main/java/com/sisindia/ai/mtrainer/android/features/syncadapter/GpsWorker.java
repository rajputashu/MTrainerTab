package com.sisindia.ai.mtrainer.android.features.syncadapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.GpsTokenEntity;
import com.sisindia.ai.mtrainer.android.db.entities.LocationDetailsEntity;
import com.sisindia.ai.mtrainer.android.models.gps.GpsRequest;
import com.sisindia.ai.mtrainer.android.models.gps.GpsResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

public class GpsWorker extends Worker {
    private MtrainerDataBase dataBase;
    private DashBoardApi dashBoardApi;

    private static final String TAG = "GpsWorker";

    public GpsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dataBase = MtrainerDataBase.getDatabase(context);
        initRetrofit();
    }

    @NonNull
    @Override
    public Result doWork() {
      //  List<LocationDetailsEntity> dutyOnPings = dataBase.getGpsPingDao().getDutyOnDetails();
       // sendDataToServer(dutyOnPings);
       // List<LocationDetailsEntity> dutyOffPings = dataBase.getGpsPingDao().getDutyOffDetails();
      //  sendDataToServer(dutyOffPings);
        return Result.success();
    }

    private synchronized GpsRequest prepareRequest(LocationDetailsEntity ping) {
        String token = dataBase.getGpsTokenDao().getPairingKey(ping.pairingToken);
        if(ping.status == 2 && token == null)
            return null;
        GpsRequest request = new GpsRequest();
        request.setDateTime(ping.dateTime);
        request.setDutyOnOffId(ping.status == 1? "" : token);
        request.setLatitude(String.valueOf(ping.latitude));
        request.setLongitude(String.valueOf(ping.longitude));
        request.setTrainerId(String.valueOf(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)));
        request.setStatusId(String.valueOf(ping.status));
        return request;
    }

    private synchronized void sendDataToServer(List<LocationDetailsEntity> pings) {
        for(LocationDetailsEntity ping : pings) {
            // Preparing request
            GpsRequest request  = prepareRequest(ping);
            if(request == null)
                continue;
            // Doing Network Call
            GpsResponse response = dashBoardApi.sendGpsPing(request).blockingGet();
            Log.d(TAG, "doWork: " + response);
            if(response!=null && response.getStatusCode() == 200) {
                if(response.getStatusId() == 1) {
                    Log.d(TAG, "doWork: update " + ping.id + "   Key : " + response.getKey() );
                    GpsTokenEntity gpsTokenEntity = new GpsTokenEntity();
                    gpsTokenEntity.id = ping.id;
                    gpsTokenEntity.pairingKey = String.valueOf(response.getKey());
                    dataBase.getGpsTokenDao().insertGpsToken(gpsTokenEntity);
                } else if(response.getStatusId() == 2)
                    dataBase.getGpsTokenDao().removeGpsToken((int)ping.pairingToken);
                dataBase.getGpsPingDao().removeSyncedLocationDetail(response.getDataTime());
            }
        }
    }

    private void initRetrofit() {
        final int CONNECT_TIMEOUT_MILLIS = 1500 * 1000; // 15s
        final int READ_TIMEOUT_MILLIS = 2000 * 1000; // 20s

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Timber.d(message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(0, TimeUnit.MINUTES)
                .connectTimeout(0, TimeUnit.MINUTES)
                .readTimeout(0, TimeUnit.MINUTES);


        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        MtrainerLogIntercepter mtrainerLogIntercepter = new MtrainerLogIntercepter();
        builder.addInterceptor(headerInterceptor);
        builder.addInterceptor(mtrainerLogIntercepter);



        builder.addInterceptor(loggingInterceptor);


        Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.MTRAINER_HOST).client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        dashBoardApi = retrofit.create(DashBoardApi.class);

    }
}
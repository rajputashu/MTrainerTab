package com.sisindia.ai.mtrainer.android.features.syncadapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.models.DataResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class NetworkClient {
    private static final String RELEASE_BASEURL = "https://sisapplications.azurewebsites.net/MTrainer_API/api/";
    private static final String VIDEO_BASEURL = "http://mtrainer2-uat.azurewebsites.net/api/Modules/";
    private static NetworkClient networkClient = null;
    private static boolean isDownloadUrlCalled = false;
    private static RestServiceTempData restServiceTempData;

    private NetworkClient() {
    }

    public static NetworkClient getNetworkClientInstance() {
        if (networkClient == null) {
            networkClient = new NetworkClient();
            isDownloadUrlCalled = false;
            initializeRetrofitLib();
        }
        return networkClient;
    }

    /*public static NetworkClient getNetworkClientInstanceForDownload() {
        networkClient = new NetworkClient();
        isDownloadUrlCalled = true;
        initializeRetrofitLib();
        return networkClient;
    }*/

    private static void initializeRetrofitLib() {

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String finalURL = "";
        if (isDownloadUrlCalled)
            finalURL = "http://50.31.147.142/IOPS/Presentations/REV/1/";
        else
            //finalURL = "http://111.93.216.167:8080/MTrainerAPI/api/";
            finalURL = RELEASE_BASEURL;

        //                .baseUrl("http://111.93.216.167:8080/SIS/api/")
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://111.93.216.167:8080/SIS/api/")
                .baseUrl(finalURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Retrofit retrofit2 = new Retrofit.Builder().baseUrl(VIDEO_BASEURL)
                .client(client).addConverterFactory(GsonConverterFactory.create(gson)).build();
        restServiceTempData = retrofit2.create(RestServiceTempData.class);
    }
/*    http://mtrainer2-uat.azurewebsites.net/api/Modules/ModulesList
    http://mtrainer2-uat.azurewebsites.net/api/Modules/SubModulesList
    http://mtrainer2-uat.azurewebsites.net/api/Modules/SubModulesVideosList
    http://mtrainer2-uat.azurewebsites.net/api/Modules/PPTImages*/


    public interface RestServiceTempData {
        @GET("ModulesList")
        Call<DataResponse> getModules();

        @GET("SubModulesList")
        Call<DataResponse> getSubModules();

        @GET("SubModulesVideosList")
        Call<DataResponse> getVideos();

        @GET("MLPModuleVideoList")
        Call<DataResponse> getMLPVideos();

        @GET("PPTImages")
        Call<DataResponse> getSlides();
    }

    public RestServiceTempData getTempNoccCall() {
        return restServiceTempData;
    }
}
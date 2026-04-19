package com.sisindia.ai.mtrainer.android.di.module;

import com.droidcommons.dagger.module.BaseRetrofitModule;
import com.google.gson.Gson;

import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepterKt;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.SyncApi;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class AppRetrofitModule extends BaseRetrofitModule {

    private final int CONNECT_TIMEOUT_MILLIS = 1500 * 1000; // 15s
    private final int READ_TIMEOUT_MILLIS = 2000 * 1000; // 20s

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder().baseUrl(BuildConfig.MTRAINER_HOST).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(RequestHeaderInterceptor headerInterceptor,
                                     HttpLoggingInterceptor loggingInterceptor, MtrainerLogIntercepter mtrainerLogIntercepter) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(80,TimeUnit.SECONDS)
                .connectTimeout(80, TimeUnit.SECONDS)
                .readTimeout(80, TimeUnit.SECONDS).retryOnConnectionFailure(true)
                /*.protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(0, 5,TimeUnit.MINUTES))*/;

        builder.addInterceptor(headerInterceptor);
        builder.addInterceptor(mtrainerLogIntercepter);

        // below commited for chunch msg
        if (BuildConfig.DEBUG) {
         //   builder.addInterceptor(chuckInterceptor);

        }
        builder.addInterceptor(loggingInterceptor);
        return builder.build();
    }


    @Provides
    @Singleton
    DashBoardApi provideSampleApi(Retrofit retrofit) {
        return retrofit.create(DashBoardApi.class);
    }

    @Provides
    @Singleton
    AuthApi provideAuthApi(Retrofit retrofit) {
        return retrofit.create(AuthApi.class);
    }

    @Provides
    @Singleton
    SyncApi provideSyncApi(Retrofit retrofit) {
        return retrofit.create(SyncApi.class);
    }
}
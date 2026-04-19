package com.sisindia.ai.mtrainer.android.base;

import android.text.TextUtils;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestHeaderInterceptor implements Interceptor {
    //    private static final String TAG = "RequestHeaderIntercepto";
    @Inject
    public RequestHeaderInterceptor() {
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {

        Request.Builder requestBuilder = chain.request().newBuilder();
        // TODO: Need to revert
//        Log.d("RequestInterceptor", chain.request().url().toString());
//        Log.d("Request Headers", chain.request().headers().toString());
        //requestBuilder.addHeader(RestConstants.CONTENT_TYPE, RestConstants.APPLICATION_JSON);

        String authToken = Prefs.getString(PrefsConstants.AUTH_TOKEN_KEY);

        if (!TextUtils.isEmpty(authToken)) {
            requestBuilder.addHeader(RestConstants.AUTHORIZATION_KEY, authToken);
        }

        Request request = requestBuilder.build();

//        Log.d(TAG, "intercept: Procced" + System.currentTimeMillis() + "Connection TimeOut Before -> " + chain.connectTimeoutMillis());
        if (chain.request().url().toString().trim().contains("UploadFile")) {
//            Log.d(TAG, "intercept: Is Binary File");
            return chain.withConnectTimeout(0, TimeUnit.MILLISECONDS)
                    .withReadTimeout(0, TimeUnit.MILLISECONDS)
                    .withWriteTimeout(0, TimeUnit.MILLISECONDS)
                    .proceed(request);

        } else
            return chain.proceed(request);
    }
}

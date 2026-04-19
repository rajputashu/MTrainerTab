package com.sisindia.ai.mtrainer.android.base;

import android.app.Application;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.droidcommons.base.BaseViewModel;
import com.droidcommons.base.SingleLiveEvent;
import com.google.gson.Gson;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
import com.sisindia.ai.mtrainer.android.models.ApiError;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class MTrainerViewModel extends BaseViewModel {
    final protected Logger logger = new Logger(this.getClass().getSimpleName());

    @Inject
    protected SingleLiveEvent<Message> liveData;
    @Inject
    public Picasso picasso;

    @Inject
    public MTrainerViewModel(@NonNull Application application) {
        super(application);
    }

    protected void onApiError(Throwable throwable) {
        setIsLoading(false);
        Timber.e(throwable);
        try {
            if (throwable instanceof HttpException) {
                Response errorResponse = ((HttpException) throwable).response();
                if (errorResponse != null) {
                    ResponseBody errorResponseBody = errorResponse.errorBody();
                    if (errorResponseBody != null) {
                        ApiError response = new Gson().fromJson(errorResponseBody.charStream(), ApiError.class);
                        if (response != null) {
                            showToast(TextUtils.isEmpty(response.statusMessage) ? response.description : response.statusMessage);
                        }
                  /*  if(response.statusCode==500){
                        showToast("server error");
                    }
*/
                        else {
                            showToast(R.string.something_went_wrong);
                        }
                    } else {
                        showToast(R.string.something_went_wrong);
                    }
                }

                else {
                    showToast(R.string.something_went_wrong);
                }
            } else {
                showToast(R.string.something_went_wrong);
            }
        }catch (Exception e){
            //logger.log("Apicall exception",e.getCause());
        }
    }
}

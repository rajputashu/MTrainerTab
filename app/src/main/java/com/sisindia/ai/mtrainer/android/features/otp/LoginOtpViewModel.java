package com.sisindia.ai.mtrainer.android.features.otp;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;

import javax.inject.Inject;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_LOGIN;

public class LoginOtpViewModel extends MTrainerViewModel {
    @Inject
    public LoginOtpViewModel(@NonNull Application application) {
        super(application);
    }
    public void onEditNumberClick(View view) {
        message.what = OPEN_LOGIN;
        liveData.postValue(message);
    }


}

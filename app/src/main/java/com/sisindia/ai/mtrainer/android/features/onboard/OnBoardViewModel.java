package com.sisindia.ai.mtrainer.android.features.onboard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;

import javax.inject.Inject;

public class OnBoardViewModel extends MTrainerViewModel {

    // TODO: 2020-03-04 inject api interface
    public static String appName;
    public static long id = -2;
    //public static boolean apiCalled = false;

    @Inject
    public OnBoardViewModel(@NonNull Application application) {
        super(application);
    }
}
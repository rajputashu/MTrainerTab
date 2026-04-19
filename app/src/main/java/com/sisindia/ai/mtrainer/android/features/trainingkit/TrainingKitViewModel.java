package com.sisindia.ai.mtrainer.android.features.trainingkit;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;

import javax.inject.Inject;

public class TrainingKitViewModel extends MTrainerViewModel {

    @Inject
    public TrainingKitViewModel(@NonNull Application application) {
        super(application);
    }
}

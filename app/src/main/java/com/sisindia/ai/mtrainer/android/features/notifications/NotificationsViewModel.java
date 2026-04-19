package com.sisindia.ai.mtrainer.android.features.notifications;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;

import javax.inject.Inject;

public class NotificationsViewModel extends MTrainerViewModel {

    @Inject
    public NotificationsViewModel(@NonNull Application application) {
        super(application);
    }
}

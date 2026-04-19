package com.sisindia.ai.mtrainer.android.features.tickets;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;

import javax.inject.Inject;

public class TicketsViewModel extends MTrainerViewModel {

    @Inject
    public TicketsViewModel(@NonNull Application application) {
        super(application);
    }
}

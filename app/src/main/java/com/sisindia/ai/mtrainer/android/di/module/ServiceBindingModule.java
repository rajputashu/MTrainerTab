package com.sisindia.ai.mtrainer.android.di.module;

import com.droidcommons.dagger.qualifier.ServiceScoped;
import com.sisindia.ai.mtrainer.android.features.syncadapter.ForcedSyncService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBindingModule {
    @ContributesAndroidInjector
    @ServiceScoped
    abstract ForcedSyncService bindForcedSyncService();
}

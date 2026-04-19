package com.sisindia.ai.mtrainer.android.di.component;

import com.droidcommons.dagger.module.BaseRetrofitModule;
import com.sisindia.ai.mtrainer.android.di.module.AppRetrofitModule;
import com.sisindia.ai.mtrainer.android.di.module.SyncModule;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.SyncApi;

import javax.inject.Singleton;

import dagger.Component;
@Singleton
@Component(modules = {AppRetrofitModule.class, BaseRetrofitModule.class, SyncModule.class})
public interface MTrainerSyncComponent {
    SyncApi getSyncApi();
    DashBoardApi getDashBoardApi();
}

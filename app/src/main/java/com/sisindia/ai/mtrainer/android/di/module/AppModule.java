package com.sisindia.ai.mtrainer.android.di.module;

import android.os.Message;

import androidx.lifecycle.ViewModelProvider;

import com.droidcommons.base.SingleLiveEvent;
import com.droidcommons.base.timer.CountUpTimer;
import com.droidcommons.base.timer.SingleLiveTimerEvent;
import com.sisindia.ai.mtrainer.android.MTrainerApplication;
import com.sisindia.ai.mtrainer.android.di.component.AppViewModelSubComponent;
import com.sisindia.ai.mtrainer.android.di.factory.AppViewModelFactory;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = {AppViewModelSubComponent.class})
public class AppModule {

    @Provides
    @Singleton
    SingleLiveEvent<Message> provideSingleLiveEvent() {
        return new SingleLiveEvent<>();
    }

    @Provides
    @Singleton
    SingleLiveTimerEvent<Message> provideSingleLiveTimerEvent() {
        return new SingleLiveTimerEvent<>();
    }

    @Provides
    @Singleton
    CountUpTimer provideCountDownTimer(SingleLiveTimerEvent<Message> liveData) {
        return new CountUpTimer(liveData);
    }

    @Singleton
    @Provides
    ViewModelProvider.Factory provideFactory(AppViewModelSubComponent.Builder builder) {
        return new AppViewModelFactory(builder.build());
    }

    @Singleton
    @Provides
    static Picasso providePicasso(MTrainerApplication application) {
        return new Picasso.Builder(application).loggingEnabled(true).build();
    }
}

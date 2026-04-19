package com.sisindia.ai.mtrainer.android.di.component;

import com.droidcommons.dagger.module.BaseRetrofitModule;
import com.sisindia.ai.mtrainer.android.MTrainerApplication;
import com.sisindia.ai.mtrainer.android.di.module.ActivityBindingModule;
import com.sisindia.ai.mtrainer.android.di.module.AppModule;
import com.sisindia.ai.mtrainer.android.di.module.AppRetrofitModule;
import com.sisindia.ai.mtrainer.android.di.module.BottomSheetDialogFragmentModule;
import com.sisindia.ai.mtrainer.android.di.module.FragmentBindingModule;
import com.sisindia.ai.mtrainer.android.di.module.MTrainerUiModule;
import com.sisindia.ai.mtrainer.android.di.module.ServiceBindingModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityBindingModule.class,
        FragmentBindingModule.class,
        AppModule.class,
        BaseRetrofitModule.class,
        BottomSheetDialogFragmentModule.class,
        AppRetrofitModule.class,
        ServiceBindingModule.class,
        MTrainerUiModule.class,

})
public interface MTrainerApplicationComponent extends AndroidInjector<MTrainerApplication> {

    void inject(MTrainerApplication mtrainerApplication);
    //void inject();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(MTrainerApplication application);

        MTrainerApplicationComponent build();
    }
}

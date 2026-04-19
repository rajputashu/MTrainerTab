package com.sisindia.ai.mtrainer.android.di.module;

import com.droidcommons.dagger.bottomsheet.AndroidBottomSheetDialogInjectionModule;
import com.sisindia.ai.mtrainer.android.features.pretraining.TrainingTopicsBottomSheetFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module(includes = {AndroidBottomSheetDialogInjectionModule.class})
public abstract class BottomSheetDialogFragmentModule {

    @ContributesAndroidInjector
    abstract TrainingTopicsBottomSheetFragment bindTrainingTopicsBottomSheetFragment();

}

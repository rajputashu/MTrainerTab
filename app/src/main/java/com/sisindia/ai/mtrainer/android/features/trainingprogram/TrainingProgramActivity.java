package com.sisindia.ai.mtrainer.android.features.trainingprogram;

import android.content.Intent;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingProgramBinding;
import com.sisindia.ai.mtrainer.android.features.trainingcourses.TrainingCoursesActivity;

public class TrainingProgramActivity extends MTrainerBaseActivity {

    private TrainingProgramViewModel viewModel;

    @Override
    protected void extractBundle() {
    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            if (message.what == NavigationConstants.OPEN_ONLINE_TRAINING_COURSES_SCREEN) {
                Intent in = new Intent(this, TrainingCoursesActivity.class);
                startActivity(in);
            }
        });
    }

    @Override
    protected void onCreated() {
        clearUserData();
    }

    @Override
    protected void initViewBinding() {
        ActivityTrainingProgramBinding binding = (ActivityTrainingProgramBinding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingProgramViewModel) getAndroidViewModel(TrainingProgramViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_program;
    }

    private void clearUserData() {
        Prefs.remove(PrefsConstants.SELECTED_PROGRAM_ID);
        Prefs.remove(PrefsConstants.SELECTED_COURSE_TYPE_ID);
        Prefs.remove(PrefsConstants.SELECTED_COURSE_ID);
    }
}
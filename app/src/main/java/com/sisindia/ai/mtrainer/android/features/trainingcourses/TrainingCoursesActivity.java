package com.sisindia.ai.mtrainer.android.features.trainingcourses;

import static com.sisindia.ai.mtrainer.android.constants.Constant.SELECTED_TRAINING_TOPICS_BODY_MO;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.widget.SearchView;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingCourses1Binding;
import com.sisindia.ai.mtrainer.android.features.topicslist.TrainingTopicsActivityV2;

public class TrainingCoursesActivity extends MTrainerBaseActivity {

    ActivityTrainingCourses1Binding binding;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_courses1;
    }

    private TrainingCoursesViewmodel viewModel;

    @Override
    protected void extractBundle() {
        /*Intent intent = getIntent();
        int programId = intent.getIntExtra(Constant.SELECTED_PROGRAM_ID, -1);
        int courseTypeId = intent.getIntExtra(Constant.SELECTED_COURSE_TYPE_ID, -1);
        viewModel.selectedProgramId.set(programId);
        viewModel.selectedCourseTypeId.set(courseTypeId);*/
    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            Intent intent = new Intent(this, TrainingTopicsActivityV2.class);
            /*Bundle bundle = new Bundle();
            bundle.putParcelable(SELECTED_TRAINING_TOPICS_BODY_MO, (Parcelable) message.obj);
            intent.putExtras(bundle);*/
            startActivity(intent);
        });

        binding.seachview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.filterCourses(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.filterCourses(newText);
                return true;
            }
        });
    }

    @Override
    protected void onCreated() {
        viewModel.getTrainingCourses();
    }

    @Override
    protected void initViewBinding() {
        binding = (ActivityTrainingCourses1Binding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingCoursesViewmodel) getAndroidViewModel(TrainingCoursesViewmodel.class);
    }
}
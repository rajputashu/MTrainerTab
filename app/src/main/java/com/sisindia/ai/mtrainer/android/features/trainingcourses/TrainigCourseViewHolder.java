package com.sisindia.ai.mtrainer.android.features.trainingcourses;

import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemTrainingCoursesBinding;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesDataResponse;

class TrainigCourseViewHolder extends BaseViewHolder<TrainingCoursesDataResponse> {
    RecyclerAdapterItemTrainingCoursesBinding binding;

    public TrainigCourseViewHolder(@NonNull RecyclerAdapterItemTrainingCoursesBinding itemBinding) {
        super(itemBinding);
        binding = itemBinding;

    }

    @Override
    public void onBind(TrainingCoursesDataResponse item) {
        binding.setAdapterItem(item);
        //binding.tvDuration.setText(item.getCourseDuration()+"");
        Glide.with(binding.tvDuration.getContext()).load(item.getCourseThumbnailURL()+"?"+ Prefs.getString(PrefsConstants.SAS_TOKEN)).into(binding.imvCourseimg);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}


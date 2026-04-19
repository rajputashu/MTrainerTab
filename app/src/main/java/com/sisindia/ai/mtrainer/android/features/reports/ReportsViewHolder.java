package com.sisindia.ai.mtrainer.android.features.reports;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.droidcommons.base.recycler.BaseViewHolder;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemEmployeeReportBinding;
import com.sisindia.ai.mtrainer.android.databinding.RecyclerAdapterItemTrainingCoursesBinding;
import com.sisindia.ai.mtrainer.android.models.EmployeeReportsResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesDataResponse;

class ReportsViewHolder extends BaseViewHolder<EmployeeReportsResponse> {
    RecyclerAdapterItemEmployeeReportBinding binding;

    public ReportsViewHolder(@NonNull RecyclerAdapterItemEmployeeReportBinding itemBinding) {
        super(itemBinding);
        binding = itemBinding;
    }

    @Override
    public void onBind(EmployeeReportsResponse item) {
        binding.tvDoj.setText(item.getDateOfJoining().split("T")[0]);
        binding.setAdapterItem(item);
        //binding.tvDuration.setText(item.getCourseDuration()+"");
    }
}


package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBottomSheetDialogFragment;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.databinding.BottomSheetTrainigTopicBinding;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_CANCEL_PHOTO_BOTTOM_FRAGMENT;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_CANCEL_TRAINING_CLICK;



public class TrainingTopicsBottomSheetFragment extends MTrainerBottomSheetDialogFragment {

    private BottomSheetTrainigTopicBinding binding;
    private TrainingTopicsViewModel viewModel;

    public static TrainingTopicsBottomSheetFragment newInstance() {
        TrainingTopicsBottomSheetFragment fragment = new TrainingTopicsBottomSheetFragment();

        return fragment;
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void applyStyle() {

    }


    @Override
    protected void initViewModel() {
        viewModel = (TrainingTopicsViewModel) getAndroidViewModel(TrainingTopicsViewModel.class);
    }



    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (BottomSheetTrainigTopicBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {




    }


    @Override
    protected void onCreated() {
        viewModel.initViewModel();
        binding.cacelBottomsomSheet.setOnClickListener(v -> this.dismissAllowingStateLoss());

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.bottom_sheet_trainig_topic;
    }
}

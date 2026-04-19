package com.sisindia.ai.mtrainer.android.features.feedback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBottomSheetDialogFragment;
import com.sisindia.ai.mtrainer.android.databinding.ClientFeedbackBottomsheetBinding;
import com.sisindia.ai.mtrainer.android.utils.StringUtils;

public class FeedbackBottomSheet extends MTrainerBottomSheetDialogFragment {

    private ClientFeedbackBottomsheetBinding binding;
    private FeedbackBottomSheetViewModel viewModel;

    public static FeedbackBottomSheet newInstance() {
        return new FeedbackBottomSheet();
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void applyStyle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (FeedbackBottomSheetViewModel) getAndroidViewModel(FeedbackBottomSheetViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (ClientFeedbackBottomsheetBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
    }

    @Override
    protected void onCreated() {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.client_feedback_bottomsheet;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.caFabBtn.setOnClickListener(v -> {
            FeedbackActivity.selectedFeedbackDetails.clear();
            if (binding.feedbackInputName.getText().toString().trim().equals("") ||
                    binding.feedbackInputEmail.getText().toString().trim().equals("") || (viewModel.companyID.equals("2") && binding.feedbackInputNumber.getText().toString().trim().equals(""))
                    || (viewModel.companyID.equals("7") && binding.feedbackInputNumber.getText().toString().trim().equals("")) || (viewModel.companyID.equals("9") && binding.feedbackInputNumber.getText().toString().trim().equals("")))
                Toast.makeText(getContext(), "Need to fill all Details", Toast.LENGTH_SHORT).show();
            else if (!StringUtils.isEmailValid(binding.feedbackInputEmail.getText().toString().trim()))
                Toast.makeText(getContext(), "Enter Correct Email", Toast.LENGTH_SHORT).show();
            else {
                FeedbackActivity.selectedFeedbackDetails.add(binding.feedbackInputName.getText().toString().trim());
                FeedbackActivity.selectedFeedbackDetails.add(binding.feedbackInputEmail.getText().toString().trim());
                FeedbackActivity.selectedFeedbackDetails.add(binding.feedbackInputNumber.getText().toString().trim());
                FeedbackActivity.selectedFeedbackDetails.add("NA");
                viewModel.openRatingFragment();
                dismissAllowingStateLoss();
            }
        });
        binding.exitClientFeedback.setOnClickListener(v -> dismissAllowingStateLoss());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });
    }
}
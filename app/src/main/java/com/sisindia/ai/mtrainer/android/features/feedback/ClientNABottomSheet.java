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
import com.sisindia.ai.mtrainer.android.databinding.ClientNaBottomsheetBinding;
import com.sisindia.ai.mtrainer.android.models.FeedbackReasonQuestionItem;

public class ClientNABottomSheet extends MTrainerBottomSheetDialogFragment {

    private ClientNaBottomsheetBinding binding;
    private ClientNAViewModel viewModel;

    public static ClientNABottomSheet newInstance() {
        ClientNABottomSheet fragment = new ClientNABottomSheet();

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
        viewModel = (ClientNAViewModel) getAndroidViewModel(ClientNAViewModel.class);


    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (ClientNaBottomsheetBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
        viewModel.initViewModel();
    }

    @Override
    protected void onCreated() {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.client_na_bottomsheet;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.exitClientNa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        binding.clientNaFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.selectedNAOption() != null) {
                    FeedbackReasonQuestionItem feedbackReasonQuestionItem = new FeedbackReasonQuestionItem();
                    feedbackReasonQuestionItem.question = viewModel.selectedNAOption();
                    FeedbackActivity.selectedReasonList.add(feedbackReasonQuestionItem);
                    viewModel.canFinishActivity();
                }
                else
                    Toast.makeText(getContext(), "Please select option", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
// androidx should use: com.google.android.material.R.id.design_bottom_sheet
                FrameLayout bottomSheet = (FrameLayout)
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });
    }
}

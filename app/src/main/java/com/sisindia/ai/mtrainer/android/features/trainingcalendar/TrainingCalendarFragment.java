package com.sisindia.ai.mtrainer.android.features.trainingcalendar;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_SEND_MAIL_DIALOG;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.droidcommons.base.BaseFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentTrainingCalendarBinding;
import com.sisindia.ai.mtrainer.android.features.dashboard.DashBoardActivity;

public class TrainingCalendarFragment extends MTrainerBaseFragment {

    private TrainingCalendarViewModel viewModel;

    public static BaseFragment newInstance() {
        return new TrainingCalendarFragment();
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingCalendarViewModel) getAndroidViewModel(TrainingCalendarViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        FragmentTrainingCalendarBinding binding = (FragmentTrainingCalendarBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {

        /*viewModel.getCalendarRota().observe(this, calendarRotaResponses -> {
            viewModel.refreshCalendarView(calendarRotaResponses);

            if (!calendarRotaResponses.isEmpty()) {
                binding.tvNoRota.setText("" + View.GONE);
                binding.tvNoRota.setText("");
            } else {
                binding.tvNoRota.setText("" + View.VISIBLE);
                binding.tvNoRota.setText("Data not found");
            }
        });*/

        liveData.observe(this, message -> {
            if (message.what == OPEN_SEND_MAIL_DIALOG) {
                showSendEmailDialogConfirmation();
            }
        });
    }

    @Override
    protected void onCreated() {
        ((DashBoardActivity) requireContext()).hideRotaButton();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_training_calendar;
    }

    private void showSendEmailDialogConfirmation() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity(),
                R.style.AlertDialogTheme)
                .setTitle("Alert")
                .setMessage("Are you sure you want to send the mail?")
                .setPositiveButton("Yes", (positiveDialog, which) -> viewModel.sendEmailViaAPI())
                .setNegativeButton("No", (negativeDialog, i) -> negativeDialog.dismiss())
                .setCancelable(false)
                .create();
        dialog.show();
    }
}
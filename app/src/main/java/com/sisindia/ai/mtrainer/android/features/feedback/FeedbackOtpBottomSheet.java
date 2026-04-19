package com.sisindia.ai.mtrainer.android.features.feedback;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBottomSheetDialogFragment;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
import com.sisindia.ai.mtrainer.android.databinding.OtpBottomSheetBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FeedbackOtpBottomSheet extends MTrainerBottomSheetDialogFragment {

    private OtpBottomSheetBinding binding;
    private FeedbackActivityViewModel viewModel;
    final Logger logger = new Logger(this.getClass().getSimpleName());

    public static FeedbackOtpBottomSheet newInstance() {
        return new FeedbackOtpBottomSheet();
    }

    @Override
    protected void extractBundle() {
    }

    @Override
    protected void applyStyle() {
    }

    @Override
    protected void initViewModel() {
        viewModel = (FeedbackActivityViewModel) getAndroidViewModel(FeedbackActivityViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        try {
            binding = (OtpBottomSheetBinding) bindFragmentView(getLayoutResource(), inflater, container);
            binding.setVm(viewModel);
            binding.executePendingBindings();
        } catch (Exception e) {
            logger.log("Exception in feedbackotpbottomsheet initViewBinding", e.getCause());
        }
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {

    }

    public static String dutyOnOffWaitingTime(int sec) {
        Date d = new Date(sec * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("'Wait 'mm:ss"); // HH for 0-23
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    @Override
    protected void onCreated() {

        //Triggering FEEDBACK OTP
        viewModel.feedbackOtpV2();

        binding.feedbackCloseSheet.setOnClickListener(v -> dismissAllowingStateLoss());

        binding.feedbackOtpSubmitFab.setOnClickListener(v -> {
            String otp = binding.feedbackOtp.getText().toString();
            viewModel.verifyOtp(otp);
        });

        binding.resendFeedbackOtp.setOnClickListener(view -> {

            if (binding.resendFeedbackOtp.getText().toString().equalsIgnoreCase("resend otp")) {

                viewModel.feedbackOtpV2();
                Toast.makeText(getActivity(), "Otp Resent successfully", Toast.LENGTH_LONG).show();

                new CountDownTimer(120000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int sec = (int) ((millisUntilFinished) / 1000);
                        binding.resendFeedbackOtp.setText(dutyOnOffWaitingTime(sec));
                    }

                    @Override
                    public void onFinish() {
                        binding.resendFeedbackOtp.setText("Resend otp");
                    }
                }.start();
            }
        });

        /*try {
            if (binding.resendFeedbackOtp.getText().toString().equalsIgnoreCase("resend otp")) {
                Toast.makeText(getActivity(), "Otp Resent successfully", Toast.LENGTH_LONG).show();
                new CountDownTimer(120000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int sec = (int) ((millisUntilFinished) / 1000);
                        binding.resendFeedbackOtp.setText(dutyOnOffWaitingTime(sec));
                    }

                    @Override
                    public void onFinish() {
                        binding.resendFeedbackOtp.setText("Resend otp");
                    }
                }.start();
            }
        } catch (Exception ignored) {
        }*/
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.otp_bottom_sheet;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*try {
            binding.feedbackOtpSubmitFab.setOnClickListener(v -> {
                String otp = binding.feedbackOtp.getText().toString();
                viewModel.verifyOtp(otp);
            });
        } catch (Exception e) {
            logger.log("Exception in feedbackotpbottomsheet onResume", e.getCause());
        }*/
    }

    // below code for showing bottom view completely
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            });
        } catch (Exception e) {
            logger.log("Exception in feedbackotpbottomsheet onViewCreated", e.getCause());
        }
    }
}
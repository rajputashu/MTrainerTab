package com.sisindia.ai.mtrainer.android.features.otp;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.HIDE_KEYBOARD;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_UPDATING_LOADING_TIME;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentEnterOtpBinding;
import com.sisindia.ai.mtrainer.android.features.bordcastrecever.SMSListener;
import com.sisindia.ai.mtrainer.android.features.login.UserViewModel;

import java.util.Objects;

public class LoginOtpFragment extends MTrainerBaseFragment {

    private FragmentEnterOtpBinding binding;
    private UserViewModel userViewModel;

    public static Fragment newInstance() {
        return new LoginOtpFragment();
    }

    @Override
    protected void extractBundle() {
        new SMSListener().getOtp(otp -> userViewModel.setOtp(otp));
    }

    @Override
    protected void initViewModel() {
        userViewModel = (UserViewModel) getAndroidViewModel(UserViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentEnterOtpBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setUserViewModel(userViewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }


    @Override
    protected void initViewState() {
        liveData.observe(getViewLifecycleOwner(), message -> {
            if (message.what == ON_UPDATING_LOADING_TIME) {
                Objects.requireNonNull(binding.loadingPercentageView).setProgress(message.arg1);
                binding.loadingPercentageView.setFinishedStrokeColor(R.color.colorLightRed);
                binding.loadingPercentageView.setUnfinishedStrokeColor(R.color.colorRed_30opct);
                binding.loadingPercentageView.setBottomText("SYNCING");
                binding.loadingPercentageView.setBottomTextSize(30f);
            }
            else if (message.what == HIDE_KEYBOARD) {
                hideKeyboard();
            }
        });

        SMSListener.getOtp(new SMSListener.OtpListner() {
            @Override
            public void onOptRecever(String otp) {
                userViewModel.setOtp(otp);
            }
        });
    }

    @Override
    protected void onCreated() {
        new SMSListener().getOtp(otp -> {
            userViewModel.setOtp(otp);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.otpSubmitClick.performClick();
                }
            }, 2000);
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_enter_otp;
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userViewModel.cancelTimer();
    }
}

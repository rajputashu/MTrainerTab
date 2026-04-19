package com.sisindia.ai.mtrainer.android.features.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentLoginWithMobileNumberBinding;

public class LoginWithMobileNumberFragment extends MTrainerBaseFragment {

    private FragmentLoginWithMobileNumberBinding binding;
    private UserViewModel userViewModel;

    public static Fragment newInstance() {
        return new LoginWithMobileNumberFragment();
    }

    @Override
    protected void extractBundle() {
        if (getActivity() != null && getActivity().getWindow() != null)
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void initViewModel() {
        userViewModel = (UserViewModel) getAndroidViewModel(UserViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentLoginWithMobileNumberBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setUserViewModel(userViewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {}

    @Override
    protected void onCreated() {
       // userViewModel.getPreAuthToken();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_login_with_mobile_number;
    }
}

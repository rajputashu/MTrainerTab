package com.sisindia.ai.mtrainer.android.features.splash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentSplashBinding;

public class SplashFragment extends MTrainerBaseFragment {

    private SplashViewModel viewModel;

    public static Fragment newInstance() {
        return new SplashFragment();
    }

    @Override
    protected void extractBundle() {
        if (getActivity() != null && getActivity().getWindow() != null)
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void initViewModel() {
        viewModel = (SplashViewModel) getAndroidViewModel(SplashViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        FragmentSplashBinding binding = (FragmentSplashBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            switch (message.what) {
                case NavigationConstants.SHOW_INSTALL_DIALOG:
                    //showDownloadDialog((VersionCheckResponse) message.obj);
                    break;
                case NavigationConstants.UPDATE_DOWNLOAD_PROGRESS:
                    //binding.pieProgress.setValue(message.arg1);
                    break;
            }
        });
    }

    @Override
    protected void onCreated() {
        clearPrefValues();
        viewModel.checkUserState();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_splash;
    }

    private void clearPrefValues() {
        Prefs.remove(PrefsConstants.SELECTED_PROGRAM_ID);
        Prefs.remove(PrefsConstants.SELECTED_COURSE_TYPE_ID);
        Prefs.remove(PrefsConstants.SELECTED_COURSE_ID);
    }
}

package com.sisindia.ai.mtrainer.android.features.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidcommons.base.BaseFragment;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentNotificaitonsBinding;

public class NotificationsFragment extends MTrainerBaseFragment {

    private FragmentNotificaitonsBinding binding;
    private NotificationsViewModel viewModel;

    public static BaseFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {

    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentNotificaitonsBinding) bindFragmentView(getLayoutResource(), inflater, container);
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
        return R.layout.fragment_notificaitons;
    }
}

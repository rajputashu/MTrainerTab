package com.sisindia.ai.mtrainer.android.features.myattendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidcommons.base.BaseFragment;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentMyAttendanceBinding;

public class MyAttendanceFragment extends MTrainerBaseFragment {

    private FragmentMyAttendanceBinding binding;
    private MyAttendanceViewModel viewModel;

    public static BaseFragment newInstance() {
        MyAttendanceFragment fragment = new MyAttendanceFragment();
        return fragment;
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (MyAttendanceViewModel) getAndroidViewModel(MyAttendanceViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentMyAttendanceBinding) bindFragmentView(getLayoutResource(), inflater, container);
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
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_my_attendance;
    }
}

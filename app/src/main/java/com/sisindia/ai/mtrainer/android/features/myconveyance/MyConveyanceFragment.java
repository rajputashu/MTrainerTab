package com.sisindia.ai.mtrainer.android.features.myconveyance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidcommons.base.BaseFragment;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentMyConveyanceBinding;
import com.sisindia.ai.mtrainer.android.utils.TimeUtils;

public class MyConveyanceFragment extends MTrainerBaseFragment {

    private FragmentMyConveyanceBinding binding;
    private MyConveyanceViewModel viewModel;

    public static BaseFragment newInstance() {
        MyConveyanceFragment fragment = new MyConveyanceFragment();
        return fragment;
    }

    @Override
    protected void extractBundle() {
    }

    @Override
    protected void initViewModel() {
        viewModel = (MyConveyanceViewModel) getAndroidViewModel(MyConveyanceViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentMyConveyanceBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        //viewModel.selectedmonth.set(TimeUtils.getMonthNum()+1);
        viewModel.selectedyear.set(TimeUtils.getYear());
        viewModel.fetchconveyencemonthlydata(true);
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
        return R.layout.fragment_my_conveyance;
    }
}

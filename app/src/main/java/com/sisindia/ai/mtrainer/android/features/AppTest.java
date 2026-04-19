package com.sisindia.ai.mtrainer.android.features;

import com.sisindia.ai.mtrainer.android.AppTestViewModel;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.databinding.ActivityAppTestBinding;

public class AppTest extends MTrainerBaseActivity {

    private ActivityAppTestBinding binding;
    private AppTestViewModel viewModel;


    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewState() {

    }

    @Override
    protected void onCreated() {
        viewModel.setTestText("hellooo");
    }

    @Override
    protected void initViewBinding() {
        binding = (ActivityAppTestBinding) bindActivityView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }


    @Override
    protected void initViewModel() {
        viewModel = (AppTestViewModel) getAndroidViewModel(AppTestViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_app_test;
    }
}

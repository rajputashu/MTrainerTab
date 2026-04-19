package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.databinding.ActivityTrainingItemDetailBinding;

public class TrainingItemDetailActivity extends MTrainerBaseActivity {

    private ActivityTrainingItemDetailBinding binding;
    private TrainingItemDetailViewModel viewModel;

    public static Intent newIntent(Activity activity) {
        Intent intent = new Intent(activity, TrainingItemDetailActivity.class);

        return intent;
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewState() {

    }

    @Override
    protected void onCreated() {
        setupToolBarForBackArrow(binding.tbTrainingItemDetail);

        initTabLayout();

        viewModel.initViewModel();
    }

    private void initTabLayout() {

        new TabLayoutMediator(binding.tlTrainingItemDetail, binding.vpTrainingItemDetail, (tab, position) -> {
            View view = getLayoutInflater().inflate(R.layout.layout_tab_header_indicator, null);
            tab.setCustomView(view);
        }).attach();
    }

    @Override
    protected void initViewBinding() {
        binding = (ActivityTrainingItemDetailBinding) bindActivityView(this, getLayoutResource());
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_training_detail, menu);
//        return super.onCreateOptionsMenu(menu);
        return false;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViewModel() {
        viewModel = (TrainingItemDetailViewModel) getAndroidViewModel(TrainingItemDetailViewModel.class);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_training_item_detail;
    }
}

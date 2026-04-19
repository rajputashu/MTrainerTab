package com.sisindia.ai.mtrainer.android.features.timeline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.droidcommons.base.BaseFragment;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentTimeLineBinding;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;

import java.util.List;

public class TimeLineFragment extends MTrainerBaseFragment {

    private FragmentTimeLineBinding binding;
    private TimeLineViewModel viewModel;

    public static BaseFragment newInstance() {
        TimeLineFragment fragment = new TimeLineFragment();
        return fragment;
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (TimeLineViewModel) getAndroidViewModel(TimeLineViewModel.class);

    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentTimeLineBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
        viewModel.getTimelineDetails1().observe(this, new Observer<List<TimeLineEntity>>() {
            @Override
            public void onChanged(List<TimeLineEntity> timeLineEntities) {
                viewModel.refreshPerfRecylerView1(timeLineEntities);
            }
        });

    }

    @Override
    protected void onCreated() {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_time_line;
    }
}

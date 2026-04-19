package com.sisindia.ai.mtrainer.android.features.tickets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidcommons.base.BaseFragment;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.FragmentTicketsBinding;

public class TicketsFragment extends MTrainerBaseFragment {

    private FragmentTicketsBinding binding;
    private TicketsViewModel viewModel;

    public static BaseFragment newInstance() {
        TicketsFragment fragment = new TicketsFragment();
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
        binding = (FragmentTicketsBinding) bindFragmentView(getLayoutResource(), inflater, container);
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
        return R.layout.fragment_tickets;
    }
}

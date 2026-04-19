package com.sisindia.ai.mtrainer.android.features.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.droidcommons.base.BaseFragment;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.constants.FeedbackConstants;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentFeedbackMainBinding;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.ContactListResponse;

import java.util.List;

public class MainFeedbackFragment extends MTrainerBaseFragment {

    private FragmentFeedbackMainBinding binding;
    private MainFeedbackViewModel viewModel;

    public static BaseFragment newInstance() {
        MainFeedbackFragment fragment = new MainFeedbackFragment();
        return fragment;
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (MainFeedbackViewModel) getAndroidViewModel(MainFeedbackViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentFeedbackMainBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
        liveData.observe(this, message -> {
            if(message.what == NavigationConstants.CLEAR_VIEW) {
                if(binding.feedbackRadioBtn.isChecked())
                    binding.feedbackRadioBtn.setChecked(false);
            }
        });
    }

    @Override
    protected void onCreated() {
        viewModel.getClientList().observe(this, new Observer<List<ContactListResponse.ClientData>>() {
            @Override
            public void onChanged(List<ContactListResponse.ClientData> clientDataList) {
                viewModel.onClientListDbSuccess(clientDataList);
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_feedback_main;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.feedbackClientNotPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.feedbackRadioBtn.isChecked()) {
                    binding.feedbackRadioBtn.setChecked(false);
                    FeedbackActivity.selectedFeedback.clear();
                } else {
                    binding.feedbackRadioBtn.setChecked(true);
                    FeedbackActivity.selectedFeedback.clear();
                    viewModel.feedbackActivityRecylerAdapter.notifyDataSetChanged();
                    FeedbackActivity.selectedFeedback.add(FeedbackConstants.CLIENT_NOT_PRESENT);
                }
            }
        });
    }
}


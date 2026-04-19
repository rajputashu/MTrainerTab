package com.sisindia.ai.mtrainer.android.features.takeassessment;

import androidx.databinding.DataBindingUtil;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseActivity;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.FragmentChooseAssessmentBinding;

import java.util.Objects;

public class ChooseAssessmentFragment extends MTrainerBaseActivity {

    private FragmentChooseAssessmentBinding binding;
    private ChooseAssessmentViewModel viewModel;

    public static ChooseAssessmentFragment newInstance() {
        return new ChooseAssessmentFragment();
    }

    @Override
    protected void extractBundle() {

    }

    @Override
    protected void initViewModel() {
        viewModel = (ChooseAssessmentViewModel) getAndroidViewModel(ChooseAssessmentViewModel.class);
    }

   /* @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }*/

    @Override
    protected void initViewState() {

    }

    @Override
    protected void onCreated() {
        setupToolBarForBackArrow(binding.tbChooseAssessment);
        Objects.requireNonNull(binding.includeTimeSpent).tvTimeSpent.setText(Prefs.getString(PrefsConstants.STARTED_TIME));
        viewModel.getAttendanceCount();
    }

    @Override
    protected void initViewBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResource());
        binding.setVm(viewModel);
        binding.executePendingBindings();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_choose_assessment;
    }
}

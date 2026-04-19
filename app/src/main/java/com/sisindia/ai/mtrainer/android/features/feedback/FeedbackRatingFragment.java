package com.sisindia.ai.mtrainer.android.features.feedback;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.databinding.ClientFeedbackBottomsheetBinding;
import com.sisindia.ai.mtrainer.android.databinding.FragmentFeedbackRatingBinding;

public class FeedbackRatingFragment extends MTrainerBaseFragment {

    private FragmentFeedbackRatingBinding binding;
    private FeedbackActivityViewModel viewModel;

    public static FeedbackRatingFragment newInstance() {
        FeedbackRatingFragment fragment = new FeedbackRatingFragment();
        return fragment;
    }

    @Override
    protected void extractBundle() {
    }

    @Override
    protected void initViewModel() {
        viewModel = (FeedbackActivityViewModel) getAndroidViewModel(FeedbackActivityViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentFeedbackRatingBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
    }

    @Override
    protected void onCreated() {
        if(FeedbackActivity.selectedFeedbackDetails.size() == 4) {
            binding.feedbackRattingName.setText(FeedbackActivity.selectedFeedbackDetails.get(0));
            binding.feedbackRattingEmail.setText(FeedbackActivity.selectedFeedbackDetails.get(1));
            binding.feedbackRattingNumber.setText(FeedbackActivity.selectedFeedbackDetails.get(2));
            binding.feedbackRattingUnitName.setText(Prefs.getString(PrefsConstants.UNIT_NAME));
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_feedback_rating;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.feedbackRatting.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                FeedbackActivity.selectedRating = rating;
            }
        });
    }
}
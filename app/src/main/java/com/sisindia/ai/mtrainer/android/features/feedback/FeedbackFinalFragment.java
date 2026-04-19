package com.sisindia.ai.mtrainer.android.features.feedback;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerBaseFragment;
import com.sisindia.ai.mtrainer.android.databinding.ClientFeedbackBottomsheetBinding;
import com.sisindia.ai.mtrainer.android.databinding.FragmentFeedbackFinalBinding;
import com.sisindia.ai.mtrainer.android.databinding.FragmentFeedbackRatingBinding;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.RatingMasterResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FeedbackFinalFragment extends MTrainerBaseFragment {

    private FragmentFeedbackFinalBinding binding;
    private FeedbackFinalViewModel viewModel;
    private boolean isDtss = false;

    public static FeedbackFinalFragment newInstance() {
        FeedbackFinalFragment fragment = new FeedbackFinalFragment();

        return fragment;
    }


    @Override
    protected void extractBundle() {
        if(getActivity() != null)
            isDtss = getActivity().getIntent().getBooleanExtra("IS_DTSS", false);

    }

    @Override
    protected void initViewModel() {
        viewModel = (FeedbackFinalViewModel) getAndroidViewModel(FeedbackFinalViewModel.class);
    }

    @Override
    protected View initViewBinding(LayoutInflater inflater, ViewGroup container) {
        binding = (FragmentFeedbackFinalBinding) bindFragmentView(getLayoutResource(), inflater, container);
        binding.setVm(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    protected void initViewState() {
        viewModel.isDtss.set(isDtss);
        viewModel.initViewModel();
    }

    @Override
    protected void onCreated() {
        binding.feedbackRattingShow.setNumStars((int)Math.ceil(FeedbackActivity.selectedRating));
        binding.feedbackRattingShow.setRating(FeedbackActivity.selectedRating);
        binding.feedbackRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                FeedbackActivity.feedbackRemarks = s.toString();
            }
        });
        int ratingValue = (int) FeedbackActivity.selectedRating;
        if(ratingValue == 0)
            ratingValue = 1;
        MtrainerDataBase.getDatabase(getContext()).getRatingDataDao().getRatingData(ratingValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRatingDataDbSuccess, this::onDbError);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_feedback_final;
    }

    private void onRatingDataDbSuccess(RatingMasterResponse.RatingData ratingData){
        binding.ratingHeader.setText(ratingData.ratingHeader);
        binding.ratingQuestionHeader.setText(ratingData.ratingQuestion);
    }

    private void onDbError(Throwable throwable) {
        Toast.makeText(getContext(), "Error Ocurred", Toast.LENGTH_SHORT).show();
    }
}
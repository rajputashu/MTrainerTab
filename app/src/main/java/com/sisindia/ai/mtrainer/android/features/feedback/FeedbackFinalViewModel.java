package com.sisindia.ai.mtrainer.android.features.feedback;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.FeedbackReasonQuestionItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FeedbackFinalViewModel extends MTrainerViewModel {
    public FeedbackFinalRecylerAdapter feedbackFinalRecylerAdapter = new FeedbackFinalRecylerAdapter();
    private MtrainerDataBase dataBase;
    public ObservableBoolean isDtss = new ObservableBoolean(false);

    @Inject
    public FeedbackFinalViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }


    public void initViewModel() {
        /*List<String> list = new ArrayList<String>();
        list.add("Training content was not good");
        list.add("Test 2");
        list.add("Test 3");*/
        int ratingValue = (int) FeedbackActivity.selectedRating;
        if(ratingValue == 0)
            ratingValue = 1;
        if(!isDtss.get())

        dataBase.getRatingQuestionDao().getRatingQuestion(ratingValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onQuestionRatingDbSuccess, this::onApiError);

    }

    private void onQuestionRatingDbSuccess(List<FeedbackReasonQuestionItem> questionList) {
        feedbackFinalRecylerAdapter.clearAndSetItems(questionList);
    }
}

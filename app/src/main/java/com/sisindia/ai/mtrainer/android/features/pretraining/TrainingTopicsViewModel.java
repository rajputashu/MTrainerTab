package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;


import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TrainingTopicsViewModel extends MTrainerViewModel {

    public TrainingTopicItemViewPagerAdapter topicItemViewPagerAdapter = new TrainingTopicItemViewPagerAdapter();

    public PreTrainingViewListeners viewListeners = new PreTrainingViewListeners() {

        @Override
        public void onTopicItemClick() {

        }

        @Override
        public void openTrainingDetail() {
            message.what = NavigationConstants.ON_OPEN_TRAINING_DETAIL;
            liveData.postValue(message);
        }
    };

    @Inject
    public TrainingTopicsViewModel(@NonNull Application application) {
        super(application);
    }

    public void initViewModel() {
        List<Object> list = new ArrayList<>();
        list.add("asdfasd");
        list.add("asdfasd");
        list.add("asdfasd");
        topicItemViewPagerAdapter.clearAndSetItems(list);
    }









}

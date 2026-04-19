package com.sisindia.ai.mtrainer.android.features.topicslist;

import com.sisindia.ai.mtrainer.android.models.online.TrainingTopicDataResponseMO;

public interface TrainingTopicsViewListeners {
    //        void onCourseClick( TrainingTopicsDataModel item);
    void onTopicClick(TrainingTopicDataResponseMO item);
}

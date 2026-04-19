package com.sisindia.ai.mtrainer.android.features.trainingcourses;

import com.sisindia.ai.mtrainer.android.models.TrainingCoursesDataResponse;
import com.sisindia.ai.mtrainer.android.models.online.TrainingCourseDataResponseMO;
import com.sisindia.ai.mtrainer.android.models.online.TrainingCoursesResponseMO;

public interface TrainingCourseViewListeners {
    void onCourseClick(TrainingCoursesDataResponse item);
    void onCourseClickV2(TrainingCourseDataResponseMO item);
}

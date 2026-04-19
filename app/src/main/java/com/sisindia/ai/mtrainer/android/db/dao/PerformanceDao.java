package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.models.TrainerPerformanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitResponse;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface PerformanceDao {

    @Query("SELECT * from performance_table")
    LiveData<List<TrainerPerformanceResponse.PerformanceResponse>> getPerformanceList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPerformance(List<TrainerPerformanceResponse.PerformanceResponse> performanceList);

    @Query("UPDATE performance_table SET actualTrainingTime = :actualTrainingTime, actualAvgRating = :actualAvgRating, actualFeedbackTaken = :actualFeedbackTaken , actualTrainingCount = :actualTrainingCount, actualGuardTrained = :actualGuardTrained, actualUnitCoverd = :actualUnitCoverd WHERE typeId = :typeId")
    Completable updatePerformance(String actualTrainingTime, String actualAvgRating, String actualFeedbackTaken, String actualTrainingCount, String actualGuardTrained, String actualUnitCoverd, int typeId);
}

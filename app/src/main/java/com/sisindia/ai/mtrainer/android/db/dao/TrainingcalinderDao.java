package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.SavedTopic;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingCalenderEntity;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TrainingcalinderDao {

    @Query("SELECT * from training_calender_table ORDER BY traningStatusId ASC")
    LiveData<List<TrainingCalendarResponse.TrainingCalendar>> getTrainingCalenderList();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertTrainingCalender(List<TrainingCalendarResponse.TrainingCalendar> trainingCalenderEntityList);

    @Query("DELETE from training_calender_table WHERE rotaId = :id")
    Completable deleteTrainingCalender(int id);

    @Query("DELETE from training_calender_table WHERE isInProgress != 1")
    Completable flushTrainingCalender();

    @Query("DELETE from training_calender_table WHERE rotaId = :rotaId")
    Completable deleteRotaFromMaster(int rotaId);

    @Query("SELECT topicId from training_calender_table WHERE rotaId = :rotaId")
    Single<List<String>> getTopicID(int rotaId);

    @Query("UPDATE training_calender_table SET savedStartTime = :startTime, isInProgress = 1 WHERE rotaid = :rotaId")
    Completable updateStartTime(String startTime, int rotaId);


    @Query("UPDATE training_calender_table SET isInProgress = 0 WHERE rotaid = :rotaId")
    Completable updateInProgressStatus(int rotaId);

    @Query("UPDATE training_calender_table SET savedEndTime = :endTime, givenRating = :rating, totalTrained = :totalCount, traningStatusId = :statusId WHERE rotaid = :rotaId")
    Completable updateData(String endTime, String rating, String totalCount, int statusId, int rotaId);


    @Query("UPDATE training_calender_table SET traningStatusId = :statusId,reason = :reason WHERE rotaid = :rotaId")
    Completable updateCancelTraining(int statusId,String reason, int rotaId);


    @Query("UPDATE training_calender_table SET traningStatusId = :statusId WHERE rotaid = :rotaId")
    Completable updateInprogress(int statusId, int rotaId);

    @Query("UPDATE training_calender_table SET isInProgress = 1 WHERE rotaid = :rotaId")
    Completable markInProgress(int rotaId);


}

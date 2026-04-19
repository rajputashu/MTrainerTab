package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sisindia.ai.mtrainer.android.models.TrainingFinalSubmitResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TrainingFinalSubmitDao {

    @Query("SELECT rotaid from training_final_submit_table ")
    LiveData<List<Integer>> getTrainingFinalSubmitResponse();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertTrainingFinalSubmit(TrainingFinalSubmitResponse.TrainingSubmitResponse trainingSubmitResponses);

    @Query("DELETE from training_final_submit_table")
    Completable flushTrainingFinalSubmit();

    @Query("UPDATE training_final_submit_table SET taskEndTime = :endTime, endLat = :endLat, endLong = :endLong, remarks=:remarks WHERE rotaid = :rotaId ")
    Completable updateEndTime(String endTime, String endLat, String endLong , int rotaId,String remarks);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Completable updateTrainingFinalSubmit(TrainingFinalSubmitResponse.TrainingSubmitResponse trainingSubmitResponses);


    @Query("SELECT * from training_final_submit_table WHERE taskEndTime != 'NA' ORDER BY rotaid DESC")
    Single<List<TrainingFinalSubmitResponse.TrainingSubmitResponse>> getSavedRota();

    @Query("DELETE from training_final_submit_table WHERE rotaid = :rotaId")
    Completable deleteSyncedRota(int rotaId);

    @Query("SELECT count(rotaid) from training_final_submit_table WHERE taskEndTime != 'NA'")
    Single<Integer> getPendingRotaCount();

}

package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedback;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface SavedFeedbackDao {

    @Query("SELECT * from saved_feedback_table WHERE rotaId = :rotaId")
    SavedFeedback getFeedback(String rotaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFeedback(SavedFeedback savedFeedback);

    @Query("DELETE from saved_feedback_table WHERE rotaId = :rotaId")
    Completable deleteFeedback(String rotaId);

}

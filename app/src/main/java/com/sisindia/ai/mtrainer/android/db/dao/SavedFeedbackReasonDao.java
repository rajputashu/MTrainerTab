package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedback;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedbackReason;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface SavedFeedbackReasonDao {

    @Query("SELECT reasonId from feedback_reason_table WHERE rotaId = :rotaId")
    List<Integer> getReasonList(String rotaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertReason(List<SavedFeedbackReason> feedbackReasonList);

    @Query("DELETE from feedback_reason_table WHERE rotaId = :rotaId")
    Completable deleteReason(String rotaId);
}